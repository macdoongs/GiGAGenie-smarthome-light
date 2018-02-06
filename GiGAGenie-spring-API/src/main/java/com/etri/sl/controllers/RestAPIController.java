package com.etri.sl.controllers;

import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.etri.sl.models.Gateway;
import com.etri.sl.services.GatewayService;
import com.etri.sl.utils.CustomErrorType;
 
@RestController
@RequestMapping("/api")
public class RestAPIController {
 
    public static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);
 
    @Autowired
    GatewayService gatewayService; //Service which will do all data retrieval/manipulation work

    // -------------------Retrieve All Users---------------------------------------------
    
    @RequestMapping(value = "/gateway/0/discovery", method = RequestMethod.GET)
    public ResponseEntity<List<Gateway>> listAllGateways() {
        List<Gateway> gateways = gatewayService.findAllGateways();
        if (gateways.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Gateway>>(gateways, HttpStatus.OK);
    }
 
}