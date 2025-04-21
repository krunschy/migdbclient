package org.migdb.migdbclient.controllers.mapping.changemapping;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;

public class ChangeEmbedding {

	JSONObject collectionStructure;
	public String child;
	public String parent;

	public ChangeEmbedding() {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			this.collectionStructure = (JSONObject) obj;
			System.out.println("*****Embedding to Referencing Started**********");

		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
	}

	public void changeEmbeddingToReferencing(String parent, String child) {
		try {
			this.child = child;
			this.parent = parent;
			System.out.println("Parent:" + parent + " Child:" + child);
			JSONArray collectionArray = new JSONArray();
			JSONArray returnReferencingObjects = new JSONArray();
			JSONObject parentCollection = new JSONObject();
			collectionArray = (JSONArray) collectionStructure.get("collections");
			for (int i = 0; i < collectionArray.size(); i++) {
				parentCollection = (JSONObject) collectionArray.get(i);
				if (parentCollection.get("collectionName").toString().equalsIgnoreCase(parent)) {
					JSONArray dataArray = (JSONArray) parentCollection.get("data");

					for (int j = 0; j < dataArray.size(); j++) {
						JSONObject dataObject = (JSONObject) dataArray.get(j);
						if (dataObject.containsKey(child)) {

							if (dataObject.get(child) instanceof JSONArray) {
								JSONArray tempArray = (JSONArray) dataObject.get(child);
								ObjectId id = new ObjectId();
								dataObject.remove(child);
								dataObject.put(child, id.toString());
								JSONObject rowValue = new JSONObject();
								rowValue.put("_id", id.toString());
								returnReferencingObjects.add(rowValue);

							} else if (dataObject.get(child) instanceof JSONObject) {
								JSONObject tempObject = (JSONObject) dataObject.get(child);
								ObjectId id = new ObjectId();
								dataObject.remove(child);
								dataObject.put(child, id.toString());
								tempObject.put("_id", id.toString());
								returnReferencingObjects.add(tempObject);

							}

						}
						dataArray.remove(j);
						dataArray.add(dataObject);
					}
					parentCollection.remove("data");
					parentCollection.put("data", dataArray);
					collectionArray.remove(i);

				}

			}

			collectionArray.add(parentCollection);
			collectionArray.add(buildCollection(returnReferencingObjects));

			collectionStructure.remove("collections");
			collectionStructure.put("collections", collectionArray);
			writeChangedJson();
		} catch (Exception ex) {
			System.out.println("Recheck Embedding to referencing mapping change" + ex);
		}
	}

	public JSONObject buildCollection(JSONArray collectionData) {
		JSONObject newCollection = new JSONObject();
		newCollection.put("collectionName", child);
		newCollection.put("data", collectionData);
		return newCollection;

	}

	public void writeChangedJson() {
		FileWriter file;
		try {
			file = new FileWriter(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath());
			file.write(collectionStructure.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
