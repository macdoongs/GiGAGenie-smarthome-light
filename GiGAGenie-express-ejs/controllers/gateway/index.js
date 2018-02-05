
const async = require("async");
const config = require("config.json")("./config/config.json");
const request = require("request");

const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');

var responseDemo = require('../../response/gateway/discovery.json');


exports.discoverGateway = function(callback){
  console.log("discoverGateway");

  var resultObject = {};

  // TODO Add multicast request
  // TEMP data


  var response = responseDemo;

  var gatewayObject = response.result_data;
  gatewayObject.ip = config.sl.gw.ip;
  gatewayObject.tcp_port = config.sl.gw.port;


  var response = {};

  if(gatewayObject === null){
    response.type = constants.RESPONSE_SPEAK;
    response.speechOutput = "Don't find the gateway...";
  }else{
    response.type = constants.RESPONSE_SPEAK_AND_LISTEN;
    response.speechOutput = "Find the gateway, " + gatewayObject.gid + "!";
    response.reprompt = 'What do you want to control the gateway?';
  }

  var data = {};

  data.gateway = gatewayObject;
  data.response = response;

  resultObject.code = constants.SL_API_SUCCESS_CODE;
  resultObject.message = "Success";
  resultObject.data = data;


  callback(null, resultObject);
};// discoverGateway
