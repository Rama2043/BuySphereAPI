package com.rama.EcommerceAPI.Classes;

import com.rama.EcommerceAPI.BC.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class Utilities {
    private static String unknown = "unknown";
    public static String emDecrypt(String ecryptedString) throws Exception {
        try {
            byte[] message = Base64.decode(ecryptedString);
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest("%#$%$^".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8; ) {
                keyBytes[k++] = keyBytes[j++];
            }
            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            byte[] IV = {(byte) 240, (byte) 3, (byte) 45, (byte) 29, (byte) 0, (byte) 76, (byte) 173, (byte) 59};
            final IvParameterSpec iv = new IvParameterSpec(IV);
            final Cipher decipher = Cipher.getInstance("DESede/CBC/NoPadding");
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            final byte[] plainText = decipher.doFinal(message);

            return new String(plainText);
        } catch (Exception ex) {
            return null;
        }
    }
    public static String getCurrentDateTime() {
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        return currentTime;
    }

    public static String getClientIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String emEncrypt(String plainText) {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest("%#$%$^".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8; ) {
                keyBytes[k++] = keyBytes[j++];
            }

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            byte[] IV = {(byte) 240, (byte) 3, (byte) 45, (byte) 29, (byte) 0, (byte) 76, (byte) 173, (byte) 59};
            final IvParameterSpec iv = new IvParameterSpec(IV);
            final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            final byte[] plainTextBytes = plainText.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);

            return new String(Base64.encode(cipherText));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String GenarateRandomNumberforTewelve() {
        // Using numeric values
        String numbers = "123456789";
        // Using random method
        Random rndm_method = new Random();
        String txn = "";
        for (int i = 0; i < 12; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            txn += numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return txn;
    }

    public static String sha512(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-512");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
