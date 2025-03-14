package com.rama.EcommerceAPI.API;

import com.rama.EcommerceAPI.Classes.MYSQLAccess;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.rama.EcommerceAPI.Classes.MYSQLAccess.selectQuery;
import static com.rama.EcommerceAPI.Classes.Utilities.getCurrentDateTime;

@RestController
@RequestMapping("/API")
public class UserPurchaseDetails {
    @RequestMapping(value = "/UserProductDetails")
    @ResponseBody
    public static String userPurchaseDetails(@RequestBody JSONObject RequestJSON, HttpServletRequest request, HttpSession session){
        JSONObject metaJSON = new JSONObject();
        JSONObject userPurchaseDetails = new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = new JSONObject();
        JSONObject responseMeta = new JSONObject();
        JSONObject jsonResponseFinal = new JSONObject();
        String version = "";
        String timeStamp = "";
        String txnId = "";
        String ProductName = "";
        String ProductPrice = "";
        String ProductQuantity = "";
        String Username = "";
        String UserId = "";
        String idQuery = "";
        String cartQuery = "";
        String purchasedQuery = "";
        String cartCount = "";
        String purchasedCount = "";
        JSONArray CartProducts = new JSONArray();
        JSONArray PurchasedProducts = new JSONArray();
        JSONObject productdetails = new JSONObject();
        int count = 0;
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(RequestJSON));
            metaJSON = (JSONObject) jsonObject.get("meta");
            version = (String) metaJSON.get("ver");
            timeStamp = (String) metaJSON.get("requestTs");
            txnId = (String) metaJSON.get("txn");
            userPurchaseDetails = (JSONObject) jsonObject.get("userDetails");
            Username = (String) userPurchaseDetails.get("userName");

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
            else if(Username.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Username cannot be empty");
            }
            else{
                idQuery = "Select ID from registration where Username = ?;";
                try(Connection idCon = MYSQLAccess.dataSourcePool.getConnection();
                    PreparedStatement idStm = idCon.prepareStatement(idQuery, Statement.RETURN_GENERATED_KEYS)){
                    idStm.setString(1,Username);
                    ResultSet id = selectQuery(idQuery,idStm);
                    if(id.next()){
                        UserId = id.getString("ID");
                        cartCount = "select count(ID) as count from temp_productdetails where UserId = ? and isPurchased = 0";
                        try(Connection cartCountCon = MYSQLAccess.dataSourcePool.getConnection();
                            PreparedStatement cartCountStm = cartCountCon.prepareStatement(cartCount, Statement.RETURN_GENERATED_KEYS)){
                            cartCountStm.setString(1,UserId);
                            ResultSet CartCount = selectQuery(cartCount,cartCountStm);
                            if(CartCount.next()){
                                count = CartCount.getInt("count");
                            }
                        }
                        if(count>0){
                            cartQuery = "select t.ProductName,ProductPrice,t1.Quantity from temp_productdetails t1 left join productdetails t on t1.ProductDetailsId = t.ID where t1.UserId = ? and isPurchased = 0";
                            try(Connection cartCon = MYSQLAccess.dataSourcePool.getConnection();
                                PreparedStatement cartStm = cartCon.prepareStatement(cartQuery, Statement.RETURN_GENERATED_KEYS)){
                                cartStm.setString(1,UserId);
                                ResultSet cart = selectQuery(cartQuery,cartStm);
                                while(cart.next()){
                                    ProductName = cart.getString("ProductName");
                                    ProductPrice = cart.getString("ProductPrice");
                                    ProductQuantity = cart.getString("Quantity");
                                    productdetails.put("ProductName",ProductName);
                                    productdetails.put("ProductPrice",ProductPrice);
                                    productdetails.put("ProductQuantity",ProductQuantity);
                                    CartProducts.add(productdetails);
                                    productdetails = new JSONObject();
                                }
                            }
                        }
                        else{
                            productdetails.put("Message","No products found in cart");
                            CartProducts.add(productdetails);
                            productdetails = new JSONObject();
                        }
                        purchasedCount = "select count(ID) as count from temp_productdetails where UserId = ? and isPurchased = 1";
                        try(Connection purchasedCountCon = MYSQLAccess.dataSourcePool.getConnection();
                            PreparedStatement purchasedCountStm = purchasedCountCon.prepareStatement(purchasedCount, Statement.RETURN_GENERATED_KEYS)){
                            purchasedCountStm.setString(1,UserId);
                            ResultSet PurchasedCount = selectQuery(purchasedCount,purchasedCountStm);
                            if(PurchasedCount.next()){
                                count = PurchasedCount.getInt("count");
                            }
                        }
                        if(count>0){
                            purchasedQuery = "select t.ProductName,ProductPrice,t1.Quantity from temp_productdetails t1 left join productdetails t on t1.ProductDetailsId = t.ID where t1.UserId = ? and isPurchased = 1";
                            try(Connection purchasedCon = MYSQLAccess.dataSourcePool.getConnection();
                                PreparedStatement purchasedStm = purchasedCon.prepareStatement(purchasedQuery, Statement.RETURN_GENERATED_KEYS)){
                                purchasedStm.setString(1,UserId);
                                ResultSet purchased = selectQuery(purchasedQuery,purchasedStm);
                                while(purchased.next()){
                                    ProductName = purchased.getString("ProductName");
                                    ProductPrice = purchased.getString("ProductPrice");
                                    ProductQuantity = purchased.getString("Quantity");
                                    productdetails.put("ProductName",ProductName);
                                    productdetails.put("ProductPrice",ProductPrice);
                                    productdetails.put("ProductQuantity",ProductQuantity);
                                    PurchasedProducts.add(productdetails);
                                    productdetails = new JSONObject();
                                }
                            }
                        }
                        else{
                            productdetails.put("Message","No products purchased");
                            PurchasedProducts.add(productdetails);
                            productdetails = new JSONObject();
                        }
                        responseJson.put("status","success");
                        responseJson.put("cartProducts",CartProducts);
                        responseJson.put("PurchasedProducts",PurchasedProducts);
                    }
                    else{
                        responseJson.put("status","failure");
                        responseJson.put("errorMessage","No user found for given Username");
                    }
                }
            }
            jsonResponseFinal.put("meta",responseMeta);
            jsonResponseFinal.put("UserProductResponse",responseJson);
        }
        catch(Exception e){

        }
        return jsonResponseFinal.toString();
    }
}