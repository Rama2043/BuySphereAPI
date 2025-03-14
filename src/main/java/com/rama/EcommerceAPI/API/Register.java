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

import static com.rama.EcommerceAPI.Classes.FormatValidations.*;
import static com.rama.EcommerceAPI.Classes.MYSQLAccess.selectQuery;
import static com.rama.EcommerceAPI.Classes.Utilities.emEncrypt;
import static com.rama.EcommerceAPI.Classes.Utilities.getCurrentDateTime;

@RestController
@RequestMapping("/API")
public class Register {
    @RequestMapping(value = "/Register")
    @ResponseBody
    public static String Register(@RequestBody JSONObject RequestJSON, HttpServletRequest request){
        JSONObject metaJSON = new JSONObject();
        JSONObject registerDetails = new JSONObject();
        JSONParser parser = new JSONParser();
        int count = 0;
        long lastID = 0L;
        String version = "";
        String timeStamp = "";
        String txnId = "";
        String name = "";
        String Email = "";
        String Username  = "";
        String Password = "";
        String phoneNumber = "";
        String Gender = "";
        String Country = "";
        String Address = "";
        String countQuery = "";
        String query = "";
        String ipAddress = com.rama.EcommerceAPI.Classes.Utilities.getClientIpAddress(request);
        JSONObject responseJson = new JSONObject();
        JSONObject responseMeta = new JSONObject();
        JSONObject jsonResponseFinal = new JSONObject();
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(RequestJSON));
            metaJSON = (JSONObject) jsonObject.get("meta");
            version = (String) metaJSON.get("ver");
            timeStamp = (String) metaJSON.get("requestTs");
            txnId = (String) metaJSON.get("txn");
            registerDetails = (JSONObject) jsonObject.get("registrationDetails");
            name = (String) registerDetails.get("name");
            Email = (String) registerDetails.get("emailID");
            Username = (String) registerDetails.get("userName");
            Password = (String) registerDetails.get("password");
            phoneNumber = (String) registerDetails.get("mobileNumber");
            Gender = (String) registerDetails.get("gender");
            Country = (String) registerDetails.get("country");
            Address = (String) registerDetails.get("address");

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
            else if(name.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Name cannot be empty");
            }
            else if(!alphaSpaceValidation(name)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Name must be valid");
            }
            else if(Email.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","EmailID cannot be empty");
            }
            else if(!emailIdValidation(Email)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","EmailID must be valid");
            }
            else if(phoneNumber.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Mobile Number cannot be empty");
            }
            else if(!phoneValidation(phoneNumber)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Mobile Number must be valid");
            }
            else if(Gender.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Gender cannot be empty");
            }
            else if(Country.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Country cannot be empty");
            }
            else if(Username.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Username cannot be empty");
            }
            else if(!usernameValidation(Username)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Username must be valid");
            }
            else if(Password.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Password cannot be empty");
            }
            else if(!passwordValidation(Password)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Password must be valid");
            }
            else if(Address.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Address cannot be empty");
            }
            else{
                countQuery = "select count(ID) as count from registration where Username = ?";
                try (Connection newcon = MYSQLAccess.dataSourcePool.getConnection();
                     PreparedStatement newstm = newcon.prepareStatement(countQuery, Statement.RETURN_GENERATED_KEYS)) {
                    newstm.setString(1, Username);
                    ResultSet rs = selectQuery(countQuery, newstm);
                    if (rs.next()) {
                        count = rs.getInt("count");
                    }
                }
                if (count > 0) {
                    responseJson.put("status","failure");
                    responseJson.put("errorMessage","Username already exists");
                }
                else {
                    query = "INSERT INTO registration (Name,EmailID,MobileNumber,Gender,Country,Address,Password,CreatedDate,CreatedIP,Username,userType) values (?,?,?,?,?,?,?,?,?,?,?)";
                    try (Connection con = MYSQLAccess.dataSourcePool.getConnection();
                         PreparedStatement stm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        stm.setString(1, name);
                        stm.setString(2, Email);
                        stm.setString(3, phoneNumber);
                        stm.setString(4, Gender);
                        stm.setString(5, Country);
                        stm.setString(6, Address);
                        stm.setString(7, emEncrypt(Password));
                        stm.setString(8, getCurrentDateTime());
                        stm.setString(9, ipAddress);
                        stm.setString(10, Username);
                        stm.setString(11, "2");
                        lastID = MYSQLAccess.insertQuery(query, stm);
                        if (lastID > 0) {
                            responseJson.put("status","success");
                        }
                        else {
                            responseJson.put("status","failure");
                            responseJson.put("errorMessage","Unable to submit the form.");
                        }
                    }
                }

            }
            jsonResponseFinal.put("meta",responseMeta);
            jsonResponseFinal.put("registrationDetailsResponse",responseJson);

        }
        catch(Exception e){

        }
        return jsonResponseFinal.toString();
    }
}
