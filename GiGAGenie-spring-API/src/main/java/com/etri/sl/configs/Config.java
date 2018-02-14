package com.etri.sl.configs;

public class Config {
    // TODO modify Gateway Object
    // TEST API URI
    public static String BASE_URI = "http://192.168.0.2:9000/gw/v1";


    // IBM watson conversation API
    public static String ENDPOINT = "https://gateway.watsonplatform.net/conversation/api";
    public static String USERNAME = "448c149b-849c-4525-901f-efa734******";
    public static String PASSWORD = "Ov46ffwJ****";
    public static String WORKSPACE_ID = "068cc140-51b8-****-****-ac06439ac69c";
    public static String CONTEXT = "context";
    // Intent Action name
    public static String ACTION_LOAD = "load";
    public static String ACTION_TURN_ON = "turn_on";
    public static String ACTION_TURN_OFF = "turn_off";
    public static String ACTION_SET = "set";
    public static String ACTION_ADJUST = "adjust";


    // AWS DynamoDB
    public static String TABLE_NAME = "watson";
    public static String KEY_NAME = "id";
    public static String PROFILE = "ma*";
}
