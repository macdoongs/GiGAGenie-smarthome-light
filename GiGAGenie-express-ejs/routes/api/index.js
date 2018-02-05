var express = require('express');
var router = express.Router();


const powerCTRL = require('../../controllers/light/power');

const constants = require('../../lib/constants');

var gatewayObject = require('../../demo/gateway.json');


/*
  GET
*/
router.get('/', function(req, res, next) {
  var resultObject = {};

  resultObject.test = "test";

  res.json(resultObject);
});


/*
  PUT
*/
router.put('/device/:did/light', function(req, res, next) {
  var resultObject = {};

  var uSpaceId = undefined;
  var unit = "device";
  var unitId = req.params.did;

  powerCTRL.handlePower(gatewayObject, uSpaceId, unit, unitId, constants.SL_API_POWER_ON, constants.DEFAULT_POWER_LEVEL, function(error, resultObject){
    res.json(resultObject);
  });

});

module.exports = router;
