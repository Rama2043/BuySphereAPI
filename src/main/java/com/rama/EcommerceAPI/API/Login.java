package com.rama.EcommerceAPI.API;

import com.rama.EcommerceAPI.Classes.MYSQLAccess;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.rama.EcommerceAPI.Classes.MYSQLAccess.selectQuery;
import static com.rama.EcommerceAPI.Classes.Utilities.emDecrypt;
import static com.rama.EcommerceAPI.Classes.Utilities.getCurrentDateTime;

@RestController
@RequestMapping("/API")
public class Login {
    @RequestMapping(value = "/Login")
    @ResponseBody
    public static String login(@RequestBody JSONObject RequestJSON, HttpSession session){
        JSONObject metaJSON = new JSONObject();
        JSONParser parser = new JSONParser();
        String version = "";
        String timeStamp = "";
        String txnId = "";
        JSONObject loginDetails = new JSONObject();
        String userName = "";
        String password = "";
        String query = "";
        int count = 0;
        String pwdQuery = "";
        String dbPassword = "";
        String userID = "";
        String UserType = "";
        JSONObject responseJson = new JSONObject();
        JSONObject responseMeta = new JSONObject();
        JSONObject jsonResponseFinal = new JSONObject();
        try
        {
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(RequestJSON));
            metaJSON = (JSONObject) jsonObject.get("meta");
            version = (String) metaJSON.get("ver");
            timeStamp = (String) metaJSON.get("requestTs");
            txnId = (String) metaJSON.get("txn");
            loginDetails = (JSONObject) jsonObject.get("LoginDetails");
            userName = (String) loginDetails.get("userName");
            password = (String) loginDetails.get("password");

            // Create the 'meta' JSONObject
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
            else if(userName.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Username cannot be empty");
            }
            else if(password.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Password cannot be empty");
            }
            else{

                query = "Select count(ID) as count from registration where Username = ?; ";
                try (Connection newcon = MYSQLAccess.dataSourcePool.getConnection();
                     PreparedStatement newstm = newcon.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    newstm.setString(1, userName);
                    ResultSet rs = selectQuery(query, newstm);
                    if (rs.next()) {
                        count = rs.getInt("count");
                    }
                }
                if(count==0){
                    responseJson.put("status","failure");
                    responseJson.put("errorMessage","Username doesn't exist");
                }
                else{
                    try{
                        pwdQuery = "select Password,ID,userType from registration where Username = ?;";
                        try (Connection pwdcon = MYSQLAccess.dataSourcePool.getConnection();
                             PreparedStatement pwdstm = pwdcon.prepareStatement(pwdQuery, Statement.RETURN_GENERATED_KEYS)) {
                            pwdstm.setString(1, userName);
                            ResultSet result = selectQuery(pwdQuery, pwdstm);
                            if(result.next()){
                                dbPassword = emDecrypt(result.getString("Password")).trim();
                                userID  = result.getString("ID");
                                UserType = result.getString("userType");
                            }
                        }
                        if(dbPassword.equals(password)){
                            session.setAttribute("userID",userID);
                            session.setAttribute("UserTypeID",UserType);
                            responseJson.put("status","success");
                        }
                        else{
                            responseJson.put("status","failure");
                            responseJson.put("errorMessage","Password doesn't exist");
                        }
                    }
                    catch(Exception e){

                    }
                }
            }

            jsonResponseFinal.put("meta",responseMeta);
            jsonResponseFinal.put("LoginDetailsResponse",responseJson);

        }
        catch(Exception e){

        }
        return jsonResponseFinal.toString();
    }
}
