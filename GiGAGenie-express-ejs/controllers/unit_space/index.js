
const async = require("async");
const config = require("config.json")("./config/config.json");
const request = require("request");


const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');


exports.createUnitSpace = function(gatewayObject, uSpaceName, callback){
  console.log("createUnitSpace");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/uspace";


  // TEMP data
  const uSpaceId = Math.floor(Math.random() * 10000);
  const uSpaceType = Math.floor(Math.random() * 10);
  const uSpaceMin = Math.floor(Math.random() * 10);
  const uSpaceMax = Math.floor(Math.random() * 100) + 10;
  const lightList = [];

  var body = {};
  body.uspace_id = uSpaceId;
  body.uspace_name = uSpaceName;
  body.uspace_type = uSpaceType;
  body.uspace_min = uSpaceMin;
  body.uspace_max = uSpaceMax;
  body.device_list = lightList;

  var data = {
    url: requestURL,
    json: true,
    body: body
  }

  // request gateway
  request.post(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "success";

    var data = {
      uSpaceId : uSpaceId,
      uSpaceName : uSpaceName
    }

    resultObject.data = data;

    callback(null, resultObject);
  });
};// createUnitSpace


exports.loadUnitSpaceList = function(gatewayObject, callback){
  console.log("loadUnitSpaceList");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/uspace";

  var data = {
    url: requestURL,
    json: true
  }

  // request gateway
  request.get(data, function(error, httpResponse, body){
    var uSpaceList = body.result_data.uspace_list;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "success";

    var data = {
      uSpaceList: uSpaceList
    }

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadUnitSpaceList


exports.removeUnitSpace = function(gatewayObject, uSpaceId, callback){
  console.log("removeUnitSpace");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var requestURL = gatewayURL + "/uspace/" + uSpaceId;

  var data = {
    url: requestURL,
    json: true
  }

  request.delete(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    callback(null, resultObject);
  });
};// removeUnitSpace


exports.addLightToUnitSpace = function(gatewayObject, lightId, uSpaceId, callback){
  console.log("addLightToUnitSpace");

  var resultObject = {};

  resultObject.code = SL_API_SUCCESS_CODE;
  resultObject.message = "Success";

  callback(null, resultObject);
};// addLightToUnitSpace


exports.addGroupToUnitSpace = function(gatewayObject, groupId, uSpaceId, callback){
  console.log("addLightToUnitSpace");

  var resultObject = {};

  resultObject.code = SL_API_SUCCESS_CODE;
  resultObject.message = "Success";

  callback(null, resultObject);
};// addLightToUnitSpace


exports.loadLightListFromUnitSpace = function(gatewayObject, uSpaceId, callback){
  console.log("loadLightFromUnitSpace");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var requestURL = gatewayURL + "/uspace/" + uSpaceId + "/device"

  var data = {
    url: requestURL,
    json: true
  }

  request.get(data, function(error, httpResponse, body){
    var deviceList = body.result_data.device_list;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    var data = {};
    data.deviceList = deviceList;

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadLightFromUnitSpace


exports.loadGroupLightListFromUnitSpace = function(gatewayObject, groupId, uSpaceId, callback){
  console.log("loadLightFromUnitSpace");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var requestURL = gatewayURL + "/uspace/" + uSpaceId + "/group/" + groupId;

  var data = {
    url: requestURL,
    json: true
  }

  request.get(data, function(error, httpResponse, body){
    var deviceList = body.result_data.device_list;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    var data = {};
    data.deviceList = deviceList;

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadLightFromUnitSpace


exports.removeLightFromUnitSpace = function(gatewayObject, lightId, uSpaceId, callback){
  console.log("removeLightFromUnitSpace");

  var resultObject = {};

  resultObject.code = constants.SL_API_SUCCESS_CODE;
  resultObject.message = "Success";

  callback(null, resultObject);
};// removeLightFromUnitSpace


exports.removeGroupFromUnitSpace = function(gatewayObject, groupId, uSpaceId, callback){
  console.log("removeGroupFromUnitSpace");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var requestURL = gatewayURL + "/uspace/" + uSpaceId + "/group/" + groupId;

  var data = {
    url: requestURL,
    json: true
  }

  request.delete(data, function(error, httpResponse, body){
    console.log(body);

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    callback(null, resultObject);
  });
};// removeGroupFromUnitSpace
