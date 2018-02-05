
const async = require("async");
const config = require("config.json")("./config/config.json");
const request = require("request");


const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');


exports.adjustBrightness = function(gatewayObject, unit, unitId, command, callback){
  console.log("adjustBrightness");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var brightness = constants.DEFAULT_BRIGHTNESS;
  var preBrightness = constants.DEFAULT_BRIGHTNESS;
  var preOnOff = constants.DEFAULT_POWER;
  var prePowerLevel = constants.DEFAULT_POWER_LEVEL;
  var code = constants.DEFAULT_CODE;

  switch (command) {
    case constants.INCREASE_COMMAND:
      code = constants.INCREASE_CODE;
      break;
    case constants.DECREASE_COMMAND:
      code = constants.DECREASE_CODE;
      break;
    default:
      break;
  }

  async.waterfall([
    function(callback){
      // Request query
      var requestURL = gatewayURL + "/" + unit + "/" + unitId;
      switch (unit) {
        case constants.UNIT_LIGHT:
          requestURL += "/light";
          break;
        case constants.UNIT_GROUP:
          requestURL += "/dstatus";
          break;
        default:
      }

      var data = {
        url: requestURL,
        json: true
      }

      // request gateway
      request.get(data, function(error, httpResponse, body){
        switch (unit) {
          case constants.UNIT_LIGHT:
            preOnOff = body.result_data.onoff;
            prePowerLevel = body.result_data.level;
            preBrightness = body.result_data.brightness;

            break;
          case constants.UNIT_GROUP:
            preOnOff = body.result_data.device_list[0].onoff;
            prePowerLevel = body.result_data.device_list[0].level;
            preBrightness = body.result_data.device_list[0].brightness;

            break;
          default:
            break;
        }

        callback(null);
      });
    },
    function(callback){
      // TODO modify equation
      switch (code) {
        case constants.INCREASE_CODE:
          brightness = Math.floor(preBrightness * constants.INCREASE_RATE);

          break;
        case constants.DECREASE_CODE:
          brightness = Math.floor(preBrightness * constants.DECREASE_RATE);

          break;
        default:
          brightness = Math.floor(preBrightness * constants.DEFAULT_RATE);

          break;
      }

      var requestURL = gatewayURL + "/" + unit + "/" + unitId + "/light";

      var body = {};
      // TODO modify gateway
      //body.onoff = preOnOff;
      //body.level = prePowerLevel;

      // brightness
      body.level = Number(brightness);
      //body.brightness = brightness;

      if(unit == constants.UNIT_GROUP){
        body.onlevel = constants.DEFAULT_POWER_LEVEL;
      }

      var data = {
        url: requestURL,
        json: true,
        body: body
      }

      // request gateway
      request.put(data, function(error, httpResponse, body){
        callback(null, true);
      });
    }
  ], function(error, result){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "success";

    var data = {
      brightness: brightness
    }

    resultObject.data = data;

    callback(null, resultObject);
  });
};// adjustColorTemperature

exports.setBrightness = function(gatewayObject, uSpaceId, unit, unitId, brightness, callback){
  console.log("setBrightness");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  // Request query
  const gatewayURL = makeGatewayURL(ip, port, version);
  var requestURL = gatewayURL;

  if(uSpaceId == undefined){
    requestURL += "/" + unit + "/" + unitId + "/light";
  }else{
    requestURL += "/uspace/" + uSpaceId + "/" + unit + "/" + unitId + "/light"
  }

  const onoff = constants.SL_API_POWER_ON;
  const level = constants.DEFAULT_POWER_LEVEL;

  var body = {};
  // TODO modify gateway
  //body.onoff = onoff;
  //body.level = level;

  // brightness
  body.level = Number(brightness);
  //body.brightness = brightness;

  if(unit == constants.UNIT_GROUP){
    body.onlevel = constants.DEFAULT_POWER_LEVEL;
  }

  var data = {
    url: requestURL,
    json: true,
    body: body
  }

  // request gateway
  request.put(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "success";

    callback(null, resultObject);
  });
};// setBrightness
