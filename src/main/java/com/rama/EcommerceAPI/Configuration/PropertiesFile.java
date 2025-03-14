package com.rama.EcommerceAPI.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.annotation.PostConstruct;

@Configuration
@PropertySources({@PropertySource("file:${Ecommerce}/DB.properties")})
public class PropertiesFile {
    @Value("${MYSQL_DB_USERNAME}")
    private String MYSQL_DB_USERNAME;

    @Value("${MYSQL_DB_PASSWORD}")
    private String MYSQL_DB_PASSWORD;

    @Value("${MYSQL_DB_HOST}")
    private String MYSQL_DB_HOST;

    @Value("${MYSQL_DB_PORT}")
    private String MYSQL_DB_PORT;

    @Value("${MYSQL_DB_NAME}")
    private String MYSQL_DB_NAME;

    private static String MYSQL_DB_USERNAME_STATIC;
    private static String MYSQL_DB_PASSWORD_STATIC;
    private static String MYSQL_DB_HOST_STATIC;
    private static String MYSQL_DB_PORT_STATIC;
    private static String MYSQL_DB_NAME_STATIC;

    public static String getMYSQL_DB_USERNAME() {
        return MYSQL_DB_USERNAME_STATIC;
    }

    public static String getMYSQL_DB_PASSWORD() {
        return MYSQL_DB_PASSWORD_STATIC;
    }

    public static String getMYSQL_DB_HOST() {
        return MYSQL_DB_HOST_STATIC;
    }

    public static String getMYSQL_DB_PORT() {
        return MYSQL_DB_PORT_STATIC;
    }

    public static String getMYSQL_DB_NAME() {
        return MYSQL_DB_NAME_STATIC;
    }
    @PostConstruct
    public void initialize() throws Exception {
        MYSQL_DB_USERNAME_STATIC = com.rama.EcommerceAPI.Classes.Utilities.emDecrypt(MYSQL_DB_USERNAME).trim();
        MYSQL_DB_PASSWORD_STATIC = com.rama.EcommerceAPI.Classes.Utilities.emDecrypt(MYSQL_DB_PASSWORD).trim();
        MYSQL_DB_HOST_STATIC = MYSQL_DB_HOST;
        MYSQL_DB_PORT_STATIC = MYSQL_DB_PORT;
        MYSQL_DB_NAME_STATIC = MYSQL_DB_NAME;
    }
}
