
const async = require("async");
const config = require("config.json")("./config/config.json");
var request = require("request");


const constants = require('../../lib/constants');
const makeGatewayURL = require('../../js/make_gateway_URL');


exports.createGroup = function(gatewayObject, groupId, callback){
  //console.log("createGroup");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/group";

  const groupName = constants.GROUP_NAME_PREFIX + groupId;
  const lightList = [];

  var body = {};
  body.group_name = groupName;
  body.device_list = lightList;

  var data = {
    url: requestURL,
    json: true,
    body: body
  }

  // request gateway
  request.post(data, function(error, httpResponse, body){
    console.log(body);
    var data = {
      groupName : groupName,
      gdid: body.result_data.gdid
    }

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    resultObject.data = data;

    callback(null, resultObject);
  });
};// createGroup


exports.loadGroupList = function(gatewayObject, callback){
  //console.log("loadGroupList");

  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/group";

  var data = {
    url: requestURL,
    json: true
  }

  // request gateway
  request.get(data, function(error, httpResponse, body){
    var groupList = body.result_data.group_list;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    var data = {};

    data.groupList = groupList;

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadGroupList

exports.removeGroup = function(gatewayObject, groupId, callback){
  var resultObject = {};

  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);

  var requestURL = gatewayURL + "/group/" + groupId;

  var data = {
    url: requestURL,
    json: true
  }

  request.delete(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    callback(null, resultObject);
  });
};// removeGroup

exports.addLightToGroup = function(gatewayObject, deviceObject, groupId, callback){
  //console.log("addLightToGroup");

  var resultObject = {};

  const lightId = deviceObject.did;


  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/group/" + groupId;

  const groupName = constants.GROUP_NAME_PREFIX + groupId;
  const lightList = [];

  lightList.push(deviceObject);

  var body = {};
  body.group_name = groupName;
  body.add_list = lightList;

  var data = {
    url: requestURL,
    json: true,
    body: body
  }

  // request gateway
  request.put(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    callback(null, resultObject);
  });
};// addLightToGroup


exports.loadLightListFromGroup = function(gatewayObject, groupId, callback){
  //console.log("loadLightFromGroup");

  var resultObject = {};


  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  var requestURL = gatewayURL + "/group/" + groupId;

  var data = {
    url: requestURL,
    json: true
  }

  request.get(data, function(error, httpResponse, body){
    var deviceList = body.result_data.basic.device_list;

    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    var data = {};
    data.deviceList = deviceList;

    resultObject.data = data;

    callback(null, resultObject);
  });
};// loadLightFromGroup


exports.removeLightFromGroup = function(gatewayObject, deviceObject, groupId, callback){
  //console.log("removeLightFromGroup");

  var resultObject = {};

  const lightId = deviceObject.did;


  const ip = gatewayObject.ip;
  const port = gatewayObject.tcp_port;
  const version = config.sl.gw.version;

  const gatewayURL = makeGatewayURL(ip, port, version);
  const requestURL = gatewayURL + "/group/" + groupId;

  const groupName = constants.GROUP_NAME_PREFIX + groupId;
  const lightList = [];

  lightList.push(deviceObject);

  var body = {};
  body.group_name = groupName;
  body.del_list = lightList;

  var data = {
    url: requestURL,
    json: true,
    body: body
  }

  // request gateway
  request.put(data, function(error, httpResponse, body){
    resultObject.code = constants.SL_API_SUCCESS_CODE;
    resultObject.message = "Success";

    callback(null, resultObject);
  });
};// removeLightFromGroup
