package com.etri.sl.configs;

public class ConstantData {
    // System light gateway API
    public static final String BODY_ONOFF = "onoff";
    public static final String BODY_LEVEL = "level";

    public static final String LIGHT_ON = "on";
    public static final String LIGHT_OFF = "off";

    public static final String DEVICE_ID = "did";
    public static final String GROUP_ID = "gdid";
    public static final String USPACE_ID = "uid";

    // response
    public static final String RESULT_CODE = "result_code";
    public static final String RESULT_MESSAGE = "result_msg";
    public static final String RESULT_DATA = "result_data";

    public static final String DEVICE_LIST = "device_list";
    public static final String GROUP_LIST = "group_list";
    public static final String USPACE_LIST = "uspace_list";

    public static final String L_USPACE_ID = "uspace_id";

    // Adjust
    public static final double INCREASE_RATE = 1.2;
    public static final double DECREASE_RATE = 0.8;

    // IBM Watson conversation API
    // Intent Action name
    public static final String ACTION = "action";
    public static final String ACTION_LOAD = "load";
    public static final String ACTION_TURN_ON = "turn_on";
    public static final String ACTION_TURN_OFF = "turn_off";
    public static final String ACTION_SET = "set";
    public static final String ACTION_ADJUST = "adjust";

    // Entity
    public static final String INCREASE_CMD = "increase";
    public static final String DECREASE_CMD = "decrease";
    public static final String DEVICE = "device";
    public static final String USPACE = "uspace";

    // Slot
    public static final String BRIGHTNESS = "brightness";
    public static final String COLOR = "color";
    public static final String VALUE = "value";

}
