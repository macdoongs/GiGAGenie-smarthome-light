
module.exports = Object.freeze({
  // constants
  BACKGROUND_IMAGE : "https://s3.amazonaws.com/etri-system-light/maxresdefault.jpg",
  ECHO_SHOW_DISPLAY_WIDTH : 1659,
  ECHO_SHOW_DISPLAY_HEIGHT : 935,

  INCREASE_CODE : 1,
  DECREASE_CODE : -1,
  DEFAULT_CODE : 0,

  INCREASE_RATE : 1.1,
  DECREASE_RATE : 0.9,
  DEFAULT_RATE : 1,

  UNIT_LIGHT : "device",
  UNIT_GROUP : "group",
  UNIT_SPACE : "unit space",

  GROUP_NAME_PREFIX : "Group ",

  INCREASE_COMMAND : "increase",
  DECREASE_COMMAND : "decrease",

  // Response
  RESPONSE_SPEAK : 0,
  RESPONSE_SPEAK_AND_LISTEN : 1,

  // AWS DynamoDB Table name
  TABLE_USER_GATEWAY_FLAG : "user/gateway/flag",
  TABLE_USER_GATEWAY : "user/gateway",
  TABLE_USER_LIGHT_FLAG : "user/light/flag",
  TABLE_USER_LIGHT_LIST : "user/light/list",
  TABLE_USER_GROUP_PREFIX : "user/group/",
  TABLE_USER_GROUP_LIST : "user/group/list",
  TABLE_USER_UNIT_SPACE_PREFIX : "user/unitspace/",
  TABLE_USER_UNIT_SPACE_LIST : "user/unitspace/list",

  // system light API parameters
  SL_API_POWER_ON : "on",
  SL_API_POWER_OFF : "off",

  DEFAULT_POWER : "off",
  DEFAULT_POWER_LEVEL : 100,

  DEFAULT_BRIGHTNESS : 70,

  DEFAULT_COLOR_TEMPERATURE : 5000,

  // system light GW API response code
  SL_API_SUCCESS_CODE : 200,
  SL_API_FAILURE_CODE : 400,

  // MQTT
  MQTT_RESPONSE_TOPIC_PREFIX : "systemlight/response/",
  MQTT_REQUEST_TOPIC : "systemlight/request"
});
