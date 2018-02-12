package com.etri.sl.controllers;

import java.util.List;
import java.util.Map;

import com.etri.sl.configs.Config;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.etri.sl.models.Gateway;
import com.etri.sl.services.GatewayService;

@RestController
@RequestMapping("/api")
public class RestAPIController {
		RestTemplate restTemplate = new RestTemplate();

		// TEST API URI
		private String BASE_URI = Config.BASE_URI;

    public static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    @Autowired
    GatewayService gatewayService; //Service which will do all data retrieval/manipulation work


    // -------------------IBM Watson conversation API---------------------------------------------

    @RequestMapping(value = "/watson", method = RequestMethod.POST)
    public @ResponseBody String UnderstandText(@RequestBody String data) {
    	Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
    	service.setUsernameAndPassword(Config.username, Config.password);
    	service.setEndPoint(Config.endpoint);

    	logger.info(data);

    	InputData input = new InputData.Builder(data).build();
    	MessageOptions options = new MessageOptions.Builder(Config.workspaceId).input(input).build();
    	MessageResponse response = service.message(options).execute();
    	System.out.println(response);

    	return response.toString();

    }


    // -------------------Load Gateway list---------------------------------------------

    @RequestMapping(value = "/gateway/0/discovery", method = RequestMethod.GET)
    public ResponseEntity<List<Gateway>> listAllGateways() {
        List<Gateway> gateways = gatewayService.findAllGateways();
        if (gateways.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Gateway>>(gateways, HttpStatus.OK);
    }

    // -------------------Load Device status---------------------------------------------

    @RequestMapping(value = "/device/{did}/light", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> LoadDeviceStatus(@PathVariable("did") int did) {
    	String url = BASE_URI + "/device/" + did + "/light";


    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }

    // -------------------Update Device status---------------------------------------------

    @RequestMapping(value = "/device/{did}/light", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<String> UpdateDeviceStatus(@PathVariable("did") int did, @RequestBody String data) {
    	String url = BASE_URI + "/device/" + did + "/light";

    	// TODO modify
    	String body = data;
    	/*
    	Map<String, String> body = new HashMap<String, String>();
    	body.put("onoff", "on");
    	*/

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	//HttpEntity<Map> entity = new HttpEntity<Map>(body, headers);
    	HttpEntity<String> entity = new HttpEntity<String>(body, headers);

    	responseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

    	return responseEntity;
    }

    // -------------------Load Group list---------------------------------------------

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> LoadGroupList() {
    	String url = BASE_URI + "/group";

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }

    // -------------------Load Group---------------------------------------------

    @RequestMapping(value = "/group/{gid}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> LoadGroup(@PathVariable("gid") int gid) {
    	String url = BASE_URI + "/group/" + gid;

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }

    // -------------------Update Group status---------------------------------------------

    @RequestMapping(value = "/group/{gid}/light", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<String> UpdateGroupStatus(@PathVariable("gid") int gid, @RequestBody String data) {
    	String url = BASE_URI + "/group/" + gid + "/light";

    	// TODO modify
    	String body = data;

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<String> entity = new HttpEntity<String>(body, headers);

    	responseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

    	return responseEntity;
    }

    // -------------------Load Unit Space list---------------------------------------------

    @RequestMapping(value = "/uspace", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> LoadUnitSpace() {
    	String url = BASE_URI + "/uspace";

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }

    // -------------------Load Unit Space---------------------------------------------

    @RequestMapping(value = "/uspace/{uid}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> LoadUnitSpace(@PathVariable("uid") int uid) {
    	String url = BASE_URI + "/uspace/" + uid;

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }
}
