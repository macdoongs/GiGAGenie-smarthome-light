package com.etri.sl.configs;

public class Config {
    // TODO modify Gateway Object
    // TEST API URI
    public static String BASE_URI = "http://192.168.0.2:9000/gw/v1";

    // IBM watson conversation API
    public static String endpoint = "https://gateway.watsonplatform.net/conversation/api";
    public static String username = "username";
    public static String password = "password";
    public static String workspaceId = "workspaceId";

    // AWS DynamoDB
    public static String tableName = "tableName";
    public static String keyName = "id";
    public static String profile = "profile";
}
