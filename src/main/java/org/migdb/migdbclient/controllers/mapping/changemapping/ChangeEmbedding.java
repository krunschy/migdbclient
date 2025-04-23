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
			System.out.println("Parent: " + parent + " | Child: " + child);

			JSONArray collections = (JSONArray) collectionStructure.get("collections");
			JSONObject parentCollection = null;

			for (Object obj : collections) {
				JSONObject collection = (JSONObject) obj;
				if (collection.get("collectionName").toString().equalsIgnoreCase(parent)) {
					parentCollection = collection;
					break;
				}
			}

			if (parentCollection == null) {
				System.out.println("Parent collection not found.");
				return;
			}

			JSONArray parentDataArray = (JSONArray) parentCollection.get("data");
			JSONArray newChildCollectionData = new JSONArray();

			for (int i = 0; i < parentDataArray.size(); i++) {
				JSONObject parentRecord = (JSONObject) parentDataArray.get(i);

				if (!parentRecord.containsKey(child)) continue;

				Object embeddedValue = parentRecord.get(child);

				if (embeddedValue instanceof JSONObject) {
					JSONObject embeddedObject = (JSONObject) embeddedValue;
					String newId = new ObjectId().toString();
					embeddedObject.put("_id", newId);
					parentRecord.put(child, newId);
					newChildCollectionData.add(embeddedObject);

				} else if (embeddedValue instanceof JSONArray) {
					JSONArray embeddedArray = (JSONArray) embeddedValue;
					JSONArray refIdArray = new JSONArray();

					for (Object embeddedObj : embeddedArray) {
						if (embeddedObj instanceof JSONObject) {
							JSONObject embeddedChild = (JSONObject) embeddedObj;
							String newId = new ObjectId().toString();
							embeddedChild.put("_id", newId);
							refIdArray.add(newId);
							newChildCollectionData.add(embeddedChild);
						}
					}

					parentRecord.put(child, refIdArray);
				}
			}
			parentCollection.put("data", parentDataArray);
			collections.removeIf(c -> ((JSONObject) c).get("collectionName").toString().equalsIgnoreCase(parent));
			collections.add(parentCollection);
			JSONObject newChildCollection = new JSONObject();
			newChildCollection.put("collectionName", child);
			newChildCollection.put("data", newChildCollectionData);
			collections.add(newChildCollection);

			collectionStructure.put("collections", collections);

			writeChangedJson();

		} catch (Exception ex) {
			System.out.println("Error during embedding to referencing conversion: " + ex.getMessage());
			ex.printStackTrace();
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
