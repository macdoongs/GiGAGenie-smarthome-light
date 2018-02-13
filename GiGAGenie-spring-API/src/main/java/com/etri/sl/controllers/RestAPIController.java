package com.etri.sl.controllers;

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
import com.etri.sl.models.Action;
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
            .withCredentials(new ProfileCredentialsProvider(Config.profile))
            .build();

    private DynamoDB dynamoDB = new DynamoDB(ddb);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GatewayService gatewayService; //Service which will do all data retrieval/manipulation work


    // -------------------IBM Watson conversation API---------------------------------------------

    @RequestMapping(value = "/watson/{wid}", method = RequestMethod.POST, produces = "application/json; charset=utf8")
    public @ResponseBody String UnderstandText(@PathVariable("wid") String wid, @RequestBody String data) {


    	Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
    	service.setUsernameAndPassword(Config.username, Config.password);
    	service.setEndPoint(Config.endpoint);

        //System.err.println(data);

        Table table = dynamoDB.getTable(Config.tableName);


        Context context = null;
        Boolean isFirst = false;
        try {

            Item item = table.getItem("id", wid);

            System.out.println("Printing item after retrieving it....");
            //System.out.println("item : " + item.toJSONPretty());

            Object o = item.get("context");

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
        MessageOptions options = new MessageOptions.Builder(Config.workspaceId)
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
                    .withPrimaryKey(Config.keyName, wid)
                    .with("context", context);

            try {
                table.putItem(item);
            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The table \"%s\" can't be found.\n", Config.tableName);
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
                System.exit(1);
            } catch (AmazonServiceException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }else {
            //System.out.println("not first");
            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(Config.keyName, wid)
                    .withUpdateExpression("set context = :c")
                    .withValueMap(new ValueMap().with(":c", context))
                    .withReturnValues(ReturnValue.ALL_NEW);

            try {
                System.out.println("Updating the item...");
                UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
                System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The table \"%s\" can't be found.\n", Config.tableName);
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
                System.exit(1);
            } catch (AmazonServiceException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        if(response.getOutput().containsKey("action")){
            Object o = response.getOutput().get("action");
            Action action;

            try{
                String actionStr = mapper.writeValueAsString(o);

                System.out.println("actionStr : " + actionStr);

                action = mapper.readValue(actionStr, Action.class);

                String name = action.getName();
                if(name.equals("load")){
                    System.out.println("load");
                }else if(name.equals("turn_on")){
                    System.out.println("turn_on");
                }else if(name.equals("turn_off")){
                    System.out.println("turn_off");
                }else if(name.equals("set")){
                    System.out.println("set");
                } else if (name.equals("adjust")) {
                    System.out.println("adjust");
                }
            }catch (Exception e){
                System.err.println(e.getMessage());
                System.exit(1);
            }

        }





        String message = "";
        if(!response.getOutput().getText().isEmpty()){
            message = response.getOutput().getText().get(0);
        }


        //System.out.println(response);

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
}
