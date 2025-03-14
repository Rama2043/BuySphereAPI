package com.rama.EcommerceAPI.Classes;

import java.util.regex.*;
public class FormatValidations {
    public static boolean emailIdValidation(String value) {
        String regex = "^[A-Z0-9\\._%-]+@[A-Z0-9\\.-]+\\.[A-Z]{2,8}(?:(?:[,;][A-Z0-9\\._%-]+@[A-Z0-9\\.-]+))*$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    public static boolean alphaSpaceValidation(String value) {
        String regex = "^[a-zA-Z ]*$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    public static boolean phoneValidation(String value) {
        String regex = "^\\d{10}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    public static boolean usernameValidation(String value) {
        String regex = "^(?=.*[A-Z])(?=.*[\\W_])[A-Za-z0-9\\W_]{7,10}$";
        Pattern pattern = Pattern.compile(regex); // Removed CASE_INSENSITIVE
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean passwordValidation(String value) {
        String regex = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    public static boolean alphaNumericSpaceValidation(String value) {
        String regex = "^[A-Za-z0-9\\s]+$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }


}
