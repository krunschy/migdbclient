package org.migdb.migdbclient.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.fromResources.DataSetUpdateRequestMessage;
import org.migdb.migdbclient.config.fromResources.MappingRequestMessage;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ServiceAccessor {

	public JSONObject getMappingModel(String clientId, String requestId, String columnCount, String numericCount, String stringCount,
			String calenderCount){
		MappingRequestMessage messageBody = new MappingRequestMessage(clientId, requestId, columnCount, numericCount,stringCount, calenderCount);
		JSONObject body = messageBody.getMessageBody();
		Client client = Client.create();

		WebResource webResource = client
		   .resource("http://localhost:8080/migdbserver/services/mappingrequest");

		String input = body.toJSONString();

		client.addFilter(new HTTPBasicAuthFilter("fhgi8598ugh985yhob580uojg0t", "dfjgn984u608jb950o9bipj0945yjpbjmgi"));
		ClientResponse response = webResource.type("application/json")
				.accept("application/json")
				.post(ClientResponse.class, input);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}

		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		System.out.println(output);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(output);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 return json;

	}

	public static JSONObject getCollectionStructureJSON(String name){
		String filename = "a90ed2a4-f28d-43af-a5cf-4803b6e47ca5";
		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:8080/migdbserver/services/collectionstructure/get/"+filename);
		client.addFilter(new HTTPBasicAuthFilter("fhgi8598ugh985yhob580uojg0t", "dfjgn984u608jb950o9bipj0945yjpbjmgi"));
		ClientResponse response = webResource.type("application/json")
				.accept("application/json")
				.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		JSONParser parser = new JSONParser();
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		try {
			jsonArray = (JSONArray) parser.parse(output);
			json.put("collectionstructure",jsonArray);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;

	}

	public static String saveCollectionStructureJSON(JSONObject object){
		Client client = Client.create();
		WebResource webResource = client
				.resource("http://localhost:8080/migdbserver/services/collectionstructure/save");
		String input = object.toJSONString();
		client.addFilter(new HTTPBasicAuthFilter("fhgi8598ugh985yhob580uojg0t", "dfjgn984u608jb950o9bipj0945yjpbjmgi"));
		ClientResponse response = webResource.type("application/json")
				.accept("application/json")
				.post(ClientResponse.class, input);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		String output = response.getEntity(String.class);
		JSONParser parser = new JSONParser();
		JSONObject json;
		String filename = "";
		try {
			json = (JSONObject) parser.parse(output);
			filename = (String) json.get("response");
			System.out.println("JSON file name retrieved:"+filename);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return filename;
	}

	public static JSONObject updatedDataSet(DataSetUpdateRequestMessage message){

		Client client = Client.create();

		WebResource webResource = client
		   .resource("http://localhost:8080/migdbserver/services/learnnetwork");

		String input = message.getMessageBody().toJSONString();

		client.addFilter(new HTTPBasicAuthFilter("fhgi8598ugh985yhob580uojg0t", "dfjgn984u608jb950o9bipj0945yjpbjmgi"));
		ClientResponse response = webResource.type("application/json")
				.accept("application/json")
				.post(ClientResponse.class, input);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}

		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		System.out.println(output);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(output);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 return json;
	}
}
