package com.rama.EcommerceAPI.API;

import com.rama.EcommerceAPI.Classes.MYSQLAccess;
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
import static com.rama.EcommerceAPI.Classes.MYSQLAccess.updateQuery;
import static com.rama.EcommerceAPI.Classes.Utilities.*;

@RestController
@RequestMapping("/API")
public class ChangePassword {
    @RequestMapping(value = "/ChangePassword")
    @ResponseBody
    public static String changePassword(@RequestBody JSONObject RequestJSON, HttpServletRequest request, HttpSession session){
        JSONObject metaJSON = new JSONObject();
        JSONObject changePasswordDetails = new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = new JSONObject();
        JSONObject responseMeta = new JSONObject();
        JSONObject jsonResponseFinal = new JSONObject();
        String version = "";
        String timeStamp = "";
        String txnId = "";
        String currentPassword = "";
        String newPassword = "";
        String reEnterPassword = "";
        String sQuery = "";
        String DBpassword = "";
        String query = "";
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(RequestJSON));
            metaJSON = (JSONObject) jsonObject.get("meta");
            version = (String) metaJSON.get("ver");
            timeStamp = (String) metaJSON.get("requestTs");
            txnId = (String) metaJSON.get("txn");
            changePasswordDetails = (JSONObject) jsonObject.get("ChangePasswordDetails");
            currentPassword = (String) changePasswordDetails.get("currentPassword");
            newPassword = (String) changePasswordDetails.get("newPassword");
            reEnterPassword = (String) changePasswordDetails.get("reEnterPassword");
            String userID = (String)session.getAttribute("userID");
            String ipAddress = com.rama.EcommerceAPI.Classes.Utilities.getClientIpAddress(request);

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
            else if(currentPassword.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Current Password cannot be empty");
            }
            else if(newPassword.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","New Password cannot be empty");
            }
            else if(currentPassword.equals(newPassword)){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Current Password and New Password should not be the same");
            }
            else if(reEnterPassword.equals("")){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Re entered Password cannot be empty");
            }
            else if(!(reEnterPassword.equals(newPassword))){
                responseJson.put("status","failure");
                responseJson.put("errorMessage","Both New Password and Re-enter password must be same");
            }
            else{
                sQuery = "select Password from registration where ID = ?";
                try(Connection newcon = MYSQLAccess.dataSourcePool.getConnection();
                    PreparedStatement newstm = newcon.prepareStatement(sQuery, Statement.RETURN_GENERATED_KEYS);){
                    newstm.setString(1,userID);
                    ResultSet rs = selectQuery(sQuery,newstm);
                    if(rs.next()){
                        DBpassword = emDecrypt(rs.getString("Password")).trim();
                    }
                    if(DBpassword.equals(currentPassword)){
                        try{
                            query = "update registration set Password = ?,updatedDate = ?,updatedIP=? where ID = ?";
                            try(Connection con = MYSQLAccess.dataSourcePool.getConnection();
                                PreparedStatement stm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                                stm.setString(1,emEncrypt(newPassword));
                                stm.setString(2,getCurrentDateTime());
                                stm.setString(3,ipAddress);
                                stm.setString(4,userID);
                                boolean updated = updateQuery(query,stm);
                                if(updated==true){
                                    responseJson.put("status","success");
                                }
                                else{
                                    responseJson.put("status","failure");
                                    responseJson.put("errorMessage","Retry Again.");
                                }
                            }

                        }
                        catch(Exception e){

                        }
                    }
                    else{
                        responseJson.put("status","failure");
                        responseJson.put("errorMessage","Invalid Current Password");
                    }
                }
                catch (Exception e){

                }
            }
            jsonResponseFinal.put("meta",responseMeta);
            jsonResponseFinal.put("ChangePasswordResponse",responseJson);
        }
        catch(Exception e)
        {

        }
        return jsonResponseFinal.toString();
    }
}