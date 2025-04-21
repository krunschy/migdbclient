package org.migdb.migdbclient.controllers.mapping.changemapping;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;

public class ChangeReferencing {

//	JSONObject dbStructure;
	JSONObject collectionStructure;

	public ChangeReferencing() {
		super();
		JSONParser parser = new JSONParser();
		try {
//			Object obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
//			this.dbStructure = (JSONObject) obj;
			Object obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			this.collectionStructure = (JSONObject) obj;

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void change(String parentTableName, String childTableName) {

		// System.out.println(dbStructure);
		 System.out.println(collectionStructure);
		JSONArray parentTable = getTable(collectionStructure, parentTableName);
		JSONArray childTable = getTable(collectionStructure, childTableName);
		// System.out.println(parentTable);
		// System.out.println(childTable);
		for (int i = 0; i < parentTable.size(); i++) {
			JSONObject parentData = (JSONObject) parentTable.get(i);
//			System.out.println(parentData);
			// System.out.println(parentData.get(childTableName));
			if (parentData.get(childTableName) instanceof JSONArray) {
				System.out.println("JSONArray");
				JSONArray idArr = (JSONArray) parentData.get(childTableName);
				JSONArray childDataObjects = getDataObjects(childTable, idArr);
				parentData.put(childTableName, childDataObjects);
				
			} else if (parentData.get(childTableName) instanceof String) {
				System.out.println("String");
				String id = parentData.get(childTableName).toString();
				JSONObject childDataObject = getDataObject(childTable, id);
				parentData.put(childTableName, childDataObject);
				// System.out.println(childDataObject);
			}

		}
		deleteChildTable(collectionStructure, childTableName);
		 System.out.println(collectionStructure);
		 writeChangedJson();

	}

	private JSONArray getTable(JSONObject tableData, String name) {
		JSONArray collections = new JSONArray();
		collections = (JSONArray) tableData.get("collections");
		for (int i = 0; i < collections.size(); i++) {
			JSONObject collection = (JSONObject) collections.get(i);
			if (collection.get("collectionName").equals(name)) {
				return (JSONArray) collection.get("data");
			}
		}
		return null;
	}

	private JSONObject getDataObject(JSONArray dataArray, String id) {
		// System.out.println(dataArray);
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = (JSONObject) dataArray.get(i);
			if (data.get("_id").equals(id)) {
				return data;
			}
		}

		return null;
	}
	private JSONArray getDataObjects(JSONArray dataArray, JSONArray idArr){
		JSONArray childDataObjects = new JSONArray();
		for (int i = 0; i < idArr.size(); i++) {
			String id = idArr.get(i).toString();
			childDataObjects.add(getDataObject(dataArray, id));
		}
		return childDataObjects;
	}
	private void deleteChildTable(JSONObject tableData,String name){
		JSONArray collections = new JSONArray();
		collections = (JSONArray) tableData.get("collections");
		for (int i = 0; i < collections.size(); i++) {
			JSONObject collection = (JSONObject) collections.get(i);
			if (collection.get("collectionName").equals(name)) {
				collections.remove(i);
			}
		}
	}
	private void writeChangedJson() {
		FileWriter file;
		try {
			file = new FileWriter(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath());
			file.write(collectionStructure.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
