package com.etri.sl.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.etri.sl.configs.Config;
import com.etri.sl.configs.ConstantData;
import com.etri.sl.models.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
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
	private RestTemplate restTemplate = new RestTemplate();

    public static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.AP_NORTHEAST_2)
            .withCredentials(new ProfileCredentialsProvider(Config.PROFILE))
            .build();

    private DynamoDB dynamoDB = new DynamoDB(ddb);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GatewayService gatewayService; //Service which will do all data retrieval/manipulation work


    // -------------------IBM Watson conversation API---------------------------------------------
    // Use only this route
    @RequestMapping(value = "/watson/{wid}", method = RequestMethod.POST, produces = "application/json; charset=utf8")
    public @ResponseBody String UnderstandText(@PathVariable("wid") String wid, @RequestBody String data) {
        String message = "";

    	Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
    	service.setUsernameAndPassword(Config.USERNAME, Config.PASSWORD);
    	service.setEndPoint(Config.ENDPOINT);

        //System.err.println(data);

        Table table = dynamoDB.getTable(Config.TABLE_NAME);


        Context context = null;
        Boolean isFirst = false;
        try {

            Item item = table.getItem(Config.KEY_NAME, wid);

            System.out.println("Printing item after retrieving it....");
            //System.out.println("item : " + item.toJSONPretty());

            Object o = item.get(Config.CONTEXT);

            //System.out.println("context : " + o.toString());

            String contextStr = mapper.writeValueAsString(o);

            //System.out.println("contextStr : " + contextStr);

            context = mapper.readValue(contextStr, Context.class);

        }
        catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }

        InputData input = new InputData.Builder(data).build();
        MessageOptions options = new MessageOptions.Builder(Config.WORKSPACE_ID)
                .input(input)
                .context(context)
                .build();

        MessageResponse response = service.message(options).execute();


        if(context == null){
            isFirst = true;
        }

        //System.out.println("isFirst : " + isFirst);

        context = response.getContext();

        //System.out.println("\n" + context);

        if(isFirst){
            //System.out.println("first");
            Item item = new Item()
                    .withPrimaryKey(Config.KEY_NAME, wid)
                    .with(Config.CONTEXT, context);

            try {
                table.putItem(item);
            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The table \"%s\" can't be found.\n", Config.TABLE_NAME);
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
                System.exit(1);
            } catch (AmazonServiceException e) {
                System.err.println(e.getMessage());
                System.exit(2);
            }
        }else {
            //System.out.println("not first");
            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(Config.KEY_NAME, wid)
                    .withUpdateExpression("set context = :c")
                    .withValueMap(new ValueMap().with(":c", context))
                    .withReturnValues(ReturnValue.ALL_NEW);

            try {
                System.out.println("Updating the item...");
                UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
                System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The table \"%s\" can't be found.\n", Config.TABLE_NAME);
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
                System.exit(3);
            } catch (AmazonServiceException e) {
                System.err.println(e.getMessage());
                System.exit(4);
            }
        }

        Boolean actionCheck = response.getOutput().containsKey(ConstantData.ACTION);

        if(actionCheck){
            Object o = response.getOutput().get(ConstantData.ACTION);
            Action action = new Action();
            String name = "";
            String url = "";

            try{
                String actionStr = mapper.writeValueAsString(o);

                System.out.println("actionStr : " + actionStr);

                action = mapper.readValue(actionStr, Action.class);

                name = action.getName();
            }catch (Exception e){
                System.err.println(e.getMessage());
                System.exit(5);
            }

            // HTTP request body
            Map<String, String> body = new HashMap<>();

            // HTTP request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpMethod method = HttpMethod.PUT;

            ResponseEntity<String> responseEntity;
            HttpEntity<Map> entity;

            switch (name){
                case ConstantData.ACTION_LOAD:{
                    System.out.println("load");

                    String uspace = action.getUspace();
                    String unit = action.getUnit();
                    int unitId = (int) action.getUnitId();

                    if(uspace != null && unit != null && unitId > 0){
                        // Load unitspace unit unitId
                        logger.info("1", uspace, unit, unitId);
                    }else if(uspace != null && unit != null && unitId == 0){
                        // Load unitspace unit
                        logger.info("2", uspace, unit);
                    }else if(uspace == null && unit != null && unitId > 0){
                        // Load unit unitId
                        logger.info("3", unit, unitId);

                        if(unit.equals(ConstantData.DEVICE)){
                            url = Config.BASE_URI + "/" + unit + "/" + unitId + "/light";
                        }else{
                            url = Config.BASE_URI + "/" + unit + "/" + unitId + "/status";
                        }
                    }else if(uspace == null && unit != null && unitId == 0){
                        // Load unit
                        logger.info("4", unit);

                        if(unit.equals(ConstantData.DEVICE)){
                            url = Config.BASE_URI + "/" + unit;
                        }else{
                            url = Config.BASE_URI + "/" + unit;
                        }
                    }else{
                        // Load unit space
                        logger.info("5", uspace);
                        if(uspace.equals(ConstantData.USPACE)){
                            url = Config.BASE_URI + "/uspace";
                        }else{
                            // TODO handle uspace name
                        }
                    }

                    method = HttpMethod.GET;

                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    System.out.println(responseEntity.toString());

                    message = responseEntity.getBody();

                    try{
                        String json = responseEntity.getBody();

                        JsonNode jsonNode = mapper.readTree(json);

                        if(uspace != null && unit != null && unitId > 0){
                            // Load unitspace unit unitId

                        }else if(uspace != null && unit != null && unitId == 0){
                            // Load unitspace unit
                        }else if(uspace == null && unit != null && unitId > 0){
                            // Load unit unitId
                            if(unit.equals(ConstantData.DEVICE)){


                            }else{

                            }
                        }else if(uspace == null && unit != null && unitId == 0){
                            // Load unit
                            if(unit.equals(ConstantData.DEVICE)){
                                message = parsingList(jsonNode, ConstantData.DEVICE_LIST, ConstantData.DEVICE_ID);
                            }else{
                                message = parsingList(jsonNode, ConstantData.GROUP_LIST, ConstantData.GROUP_ID);
                            }
                        }else{
                            // Load unit space
                            message = parsingList(jsonNode, ConstantData.USPACE_LIST, ConstantData.L_USPACE_ID);
                        }


                    }catch (Exception e){
                        System.err.println(e.getMessage());
                        System.exit(6);
                    }



                    break;
                }
                case ConstantData.ACTION_TURN_ON:{
                    System.out.println("turn_on");

                    // TODO modify unitspace part
                    String uspace = action.getUspace();
                    String unit = action.getUnit();
                    int unitId = (int) action.getUnitId();

                    if(unit.equals(ConstantData.DEVICE)){
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/light";
                    }else{
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/status";
                    }

                    body.put(ConstantData.BODY_ONOFF, ConstantData.LIGHT_ON);

                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    // System.out.println(responseEntity.toString());

                    // message = responseEntity.getBody();
                    message = response.getOutput().getText().get(0);

                    break;
                }
                case ConstantData.ACTION_TURN_OFF:{
                    System.out.println("turn_off");

                    // TODO modify unitspace part
                    String uspace = action.getUspace();
                    String unit = action.getUnit();
                    int unitId = (int) action.getUnitId();

                    if(unit.equals(ConstantData.DEVICE)){
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/light";
                    }else{
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/status";
                    }

                    body.put(ConstantData.BODY_ONOFF, ConstantData.LIGHT_OFF);


                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    System.out.println(responseEntity.toString());

                    // message = responseEntity.getBody();
                    message = response.getOutput().getText().get(0);

                    break;
                }
                case ConstantData.ACTION_SET:{
                    System.out.println("set");

                    // TODO modify unitspace part
                    String uspace = action.getUspace();
                    String unit = action.getUnit();
                    int unitId = (int) action.getUnitId();

                    if(unit.equals(ConstantData.DEVICE)){
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/light";
                    }else{
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/status";
                    }

                    String attribute = action.getAttribute();

                    int value = 0;
                    String color = "";


                    if(attribute.equals(ConstantData.BRIGHTNESS)) {
                        attribute = ConstantData.BODY_LEVEL;
                    }


                    if(attribute.equals(ConstantData.COLOR)){
                        color = action.getColor();
                        // TODO Change color name (value variable) to RGB value
                    }else {
                        value = (int) action.getValue();

                        body.put(attribute, "" + value);
                    }



                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    // System.out.println(responseEntity.toString());

                    // message = responseEntity.getBody();
                    message = response.getOutput().getText().get(0);

                    break;
                }
                case ConstantData.ACTION_ADJUST:{
                    System.out.println("adjust");

                    // TODO modify unitspace part
                    String uspace = action.getUspace();
                    String unit = action.getUnit();
                    int unitId = (int) action.getUnitId();

                    if(unit.equals(ConstantData.DEVICE)){
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/light";
                    }else{
                        url = Config.BASE_URI + "/" + unit + "/" + unitId + "/status";
                    }

                    String command = action.getCommand();
                    String attribute = action.getAttribute();

                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    method = HttpMethod.GET;

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    System.out.println(responseEntity.toString());

                    Double value = 0.0;

                    try{
                        String json = responseEntity.getBody();

                        JsonNode jsonNode = mapper.readTree(json);

                        value = jsonNode.get(ConstantData.RESULT_DATA).get(ConstantData.VALUE).doubleValue();
                    }catch (Exception e){
                        System.err.println(e.getMessage());
                        System.exit(6);
                    }

                    if(command.equals(ConstantData.INCREASE_CMD)){
                        value = Math.floor(value * ConstantData.INCREASE_RATE);
                    }else if(command.equals(ConstantData.DECREASE_CMD)){
                        value = Math.floor(value * ConstantData.DECREASE_RATE);
                    }

                    body.put(attribute, "" + value.intValue());

                    method = HttpMethod.PUT;

                    System.out.println(message);

                    entity = new HttpEntity<>(body, headers);

                    System.out.println(url + " " + body.toString());

                    responseEntity = this.restTemplate.exchange(url, method, entity, String.class);

                    message = response.getOutput().getText().get(0);
                    break;
                }
                default:{
                    break;
                }
            }

        }

        System.out.println(response.getOutput().getText());
        System.out.println(message);

        if(!actionCheck){
            message = response.getOutput().getText().get(0);
        }


    	return message;
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
    	String url = Config.BASE_URI + "/device/" + did + "/light";


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
    	String url = Config.BASE_URI + "/device/" + did + "/light";

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
    	String url = Config.BASE_URI + "/group";

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
    	String url = Config.BASE_URI + "/group/" + gid;

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
    	String url = Config.BASE_URI + "/group/" + gid + "/light";

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
    	String url = Config.BASE_URI + "/uspace";

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
    	String url = Config.BASE_URI + "/uspace/" + uid;

    	ResponseEntity<String> responseEntity;

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Map> entity = new HttpEntity<Map>(null, headers);

    	responseEntity = this.restTemplate.getForEntity(url, String.class, entity);

    	return responseEntity;
    }

    private String parsingList(JsonNode jsonNode, String listName, String idName){
        String message;

        JsonNode groupNode = jsonNode.get(ConstantData.RESULT_DATA).get(listName);

        List<String> groupList = new ArrayList<>();

        int length = groupNode.size();

        String unitName = "";
        switch (listName){
            case ConstantData.DEVICE_LIST:{
                unitName = "조명";
                break;
            }
            case ConstantData.GROUP_LIST:{
                unitName = "그룹";
                break;
            }
            case ConstantData.USPACE_LIST:{
                unitName = "단위 공간";
                break;
            }
        }

        if(length > 0){
            message = unitName + "을 찾았습니다.";

            if(listName.equals(ConstantData.USPACE_LIST)){
                unitName = "";
            }

            for(int i = 0; i < length; i++){
                groupList.add(i, unitName + " " + groupNode.get(i).get(idName));
                if(i < length - 1){
                    message += " " + groupList.get(i) + ",";
                }else{
                    message += " " + groupList.get(i) + "!";
                }
            }
            message += "!";

            logger.info(groupList.toString());
        }else{
            message = unitName + "이 없습니다.";
        }
        return message;
    }
}
