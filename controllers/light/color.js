
const colorConverter = require("color-convert");
const config = require("config.json")("./config/config.json");
const request = require("request");

const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');



exports.handleColor = function(gatewayObject, uSpaceId, unit, unitId, color, callback){
  console.log("handleColorControl");

  var resultObject = {};

  // Request query
  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

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


  // color
  try {
    const colorHSL = colorConverter.keyword.hsl(color);

    console.log("colorHSL : ", colorHSL);

    body.hue = colorHSL[0];
    body.saturation = colorHSL[1];
    body.brightness = colorHSL[2];

    if(unit == constants.UNIT_GROUP){
      body.onlevel = constants.DEFAULT_POWER_LEVEL;
    }

    var data = {
      url: requestURL,
      json: true,
      body: body
    }

    console.log(requestURL);
    console.log(data);

    // request gateway
    request.put(data, function(error, httpResponse, body){
      resultObject.code = constants.SL_API_SUCCESS_CODE;
      resultObject.message = "Success";

      callback(null, resultObject);
    });

  } catch(error){
    console.log("Error", error);

    resultObject.code = constants.SL_API_FAILURE_CODE;
    resultObject.message = "Failure";

    callback(true, resultObject);
  }
};// handleColorControl
