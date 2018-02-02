
const async = require("async");
const config = require("config.json")("./config/config.json");
const request = require("request");


const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');

exports.createLight = function(gatewayObject, lightId, callback){
  console.log("createLight");

  var resultObject = {};


  resultObject.code = constants.SL_API_SUCCESS_CODE;
  resultObject.message = "Success";

  callback(null, resultObject);
};// createLight

exports.loadLight = function(gatewayObject, lightId, callback){
  console.log("loadLight");

  var resultObject = {};


  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  var requestURL = gatewayURL + "/device/" + lightId;

  var data = {
    url: requestURL,
    json: true
  }

  request.get(data, function(error, httpResponse, body){
    var data = body.result_data;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadLight


exports.removeLight = function(gatewayObject, lightId, callback){
  console.log("removeLight");

  var resultObject = {};


  resultObject.code = constants.SL_API_SUCCESS_CODE;
  resultObject.message = "Success";

  callback(null, resultObject);
};// removeLight

exports.loadLightList = function(gatewayObject, callback){
  console.log("loadLightList");

  var resultObject = {};

  var response = {};

  if(gatewayObject === null){
    response.type = constants.RESPONSE_SPEAK;
    response.speechOutput = "Don't find the gateway...";
  }else{
    response.type = constants.RESPONSE_SPEAK_AND_LISTEN;
    response.speechOutput = "Find the gateway, " + gatewayObject.gid + "!";
    response.reprompt = 'What do you want to control the gateway?';
  }

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/device";

  var data = {
    url: requestURL,
    json: true
  }

  // request gateway
  request.get(data, function(error, httpResponse, body){
    var lightList = body.result_data.device_list;

    const lightNum = lightList.length;

    var response = {};

    if(lightNum > 1){
      response.type = constants.RESPONSE_SPEAK_AND_LISTEN;
      response.speechOutput = "Find the lights";

      for(let i = 0; i < lightList.length; i++){
        response.speechOutput += ", Light " + lightList[i].did;
      }
      response.speechOutput += "!";

      response.reprompt = 'What do you want to control the lights?';
    }else if(lightNum == 1){
      response.type = constants.RESPONSE_SPEAK_AND_LISTEN;
      response.speechOutput = "Find the light, " + lightList[0].did + "!";
      response.reprompt = 'What do you want to control the light?';
    }else{
      response.type = constants.RESPONSE_SPEAK;
      response.speechOutput = "Don't find any light.";
    }


    var data = {};

    data.response = response;
    data.lightList = lightList;


    resultObject.code = 0;
    resultObject.message = "Success";
    resultObject.data = data;

    callback(null, resultObject);
  });
}//loadLightList
