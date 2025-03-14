package com.rama.EcommerceAPI.API;

import com.rama.EcommerceAPI.Classes.MYSQLAccess;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import static com.rama.EcommerceAPI.Classes.FormatValidations.alphaNumericSpaceValidation;
import static com.rama.EcommerceAPI.Classes.MYSQLAccess.selectQuery;
import static com.rama.EcommerceAPI.Classes.Utilities.getCurrentDateTime;

@RestController
@RequestMapping("/API")
public class AddProduct {
    @RequestMapping(value = "/AddProduct")
    @ResponseBody
    public static String addProduct(@RequestBody JSONObject RequestJSON, HttpServletRequest request)
    {
        JSONObject metaJSON = new JSONObject();
        JSONObject addProductDetails = new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = new JSONObject();
        JSONObject responseMeta = new JSONObject();
        JSONObject jsonResponseFinal = new JSONObject();
        String version = "";
        String timeStamp = "";
        String txnId = "";
        String ProductName = "";
        String ProductCategory = "";
        String ProductQuantity = "";
        String ProductPrice = "";
        String ProductDescription = "";
        String countQuery = "";
        int count = 0;
        String query = "";
        long lastID = 0L;
        String base64EncodedData = null;
        String ipAddress = com.rama.EcommerceAPI.Classes.Utilities.getClientIpAddress(request);

        try{
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(RequestJSON));
            metaJSON = (JSONObject) jsonObject.get("meta");
            version = (String) metaJSON.get("ver");
            timeStamp = (String) metaJSON.get("requestTs");
            txnId = (String) metaJSON.get("txn");
            addProductDetails = (JSONObject) jsonObject.get("AddProductDetails");
            ProductCategory = (String) addProductDetails.get("ProductCategory");
            ProductName = (String) addProductDetails.get("ProductName");
            ProductPrice = (String) addProductDetails.get("ProductPrice");
            ProductQuantity = (String) addProductDetails.get("ProductQuantity");
            ProductDescription = (String) addProductDetails.get("ProductDescription");
            base64EncodedData = (String) addProductDetails.get("ProductImage");



            responseMeta.put("ver", version);
            responseMeta.put("responseTs", getCurrentDateTime());
            responseMeta.put("txn", txnId);

            if(version.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Version cannot be empty");
            }
            else if(timeStamp.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Timestamp cannot be empty");
            }
            else if(txnId.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Transaction ID cannot be empty");
            }
            else if(ProductCategory.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Category cannot be empty");
            }
            else if(ProductName.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Name cannot be empty");
            }
            else if(!alphaNumericSpaceValidation(ProductName)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Name must be valid");
            }
            else if(ProductPrice.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Price cannot be empty");
            }
            else if(ProductQuantity.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Quantity cannot be empty");
            }
            else if(ProductDescription.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Description cannot be empty");
            }
            else if(base64EncodedData.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Product Image cannot be empty");
            }
            else{
                countQuery = "select count(ID) as count from productdetails where ProductName = ?";
                try (Connection newcon = MYSQLAccess.dataSourcePool.getConnection();
                     PreparedStatement newstm = newcon.prepareStatement(countQuery, Statement.RETURN_GENERATED_KEYS)) {
                    newstm.setString(1, ProductName);
                    ResultSet rs = selectQuery(countQuery, newstm);
                    if (rs.next()) {
                        count = rs.getInt("count");
                    }
                }
                if (count > 0) {
                    responseJson.put("status","failure");
                    responseJson.put("errorMessage","Product already exists");
                } else {
                    try {
                        byte[] fileBytes = Base64.getDecoder().decode(base64EncodedData);
                        query = "Insert into productdetails(ProductName,ProductPrice,ProductQuantity,ProductDescription,CreatedDate,CreatedIP,ProductCategoryID,FileDetails) values(?,?,?,?,?,?,?,?);";
                        try (Connection con = MYSQLAccess.dataSourcePool.getConnection();
                             PreparedStatement stm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                            stm.setString(1, ProductName);
                            stm.setString(2, ProductPrice);
                            stm.setString(3, ProductQuantity);
                            stm.setString(4, ProductDescription);
                            stm.setString(5, getCurrentDateTime());
                            stm.setString(6, ipAddress);
                            stm.setInt(7, Integer.parseInt(ProductCategory));
                            stm.setBytes(8,fileBytes);
                            lastID = MYSQLAccess.insertQuery(query, stm);
                            if (lastID > 0) {
                                responseJson.put("status","success");
                            } else {
                                responseJson.put("status","failure");
                                responseJson.put("errorMessage","Unable to add product.Please try again.");
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }
            jsonResponseFinal.put("meta",responseMeta);
            jsonResponseFinal.put("AddProductResponse",responseJson);

        }
        catch(Exception e){

        }
        return jsonResponseFinal.toString();
    }
}
