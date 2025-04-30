package org.migdb.migdbclient.controllers.mapping.manytomany;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;
import org.migdb.migdbclient.config.fromResources.ManyToManyResource;
import org.migdb.migdbclient.utils.CollectionStructureJSONHandler;
import org.migdb.migdbclient.utils.ServiceAccessor;

public class ManyToMany {

	JSONObject sqlJson;
	JSONArray manyToMany = new JSONArray();
	JSONObject mapped = new JSONObject();
	JSONArray mappedDataArray;
	JSONObject mappingMethod;

	/**
	 * reads intermediate JSON files
	 */
	public ManyToMany() {
		super();

		JSONParser parser = new JSONParser();
		JSONParser parser1 = new JSONParser();
		Object obj;
		Object obj1;
		try {
			obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			sqlJson = (JSONObject) obj;
			obj1 = parser1.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			mapped = (JSONObject) obj1;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * identify the many-to-many relationships in the dbStructure file
	 */
	public void identifyM2M() { //diese methode findet tables, die genau 2 andere tables referencen mit , also potentiell einfach nur links sind

		JSONArray sqlTables = (JSONArray) sqlJson.get("tables");
		for (int i = 0; i < sqlTables.size(); i++) { //für alle tables also
			int m2mCount = 0;
			JSONObject sqlTable = (JSONObject) sqlTables.get(i);
			if (sqlTable.containsKey("referencingFrom")) {
				JSONArray referencingFrom = (JSONArray) sqlTable.get("referencingFrom");
				if (referencingFrom.size() == 2) { //wenn der table genau 2 sachen referenced

					for (int j = 0; j < referencingFrom.size(); j++) {
						JSONObject referenceInfo = (JSONObject) referencingFrom.get(j);
						String string = referenceInfo.get("relationshipType").toString();
						if(string != null) {
							if (string.equals("ManyToMany")) { //und bei beiden der relationshiptype many to many is
								m2mCount++;
							}
						}
					}
				}
			}
			if (m2mCount == 2) { //also wenn wir so nen connector table haben
				mappedDataArray = new JSONArray();
				setMapInstance(sqlTable); //setz ma mal den table als das was wir brauchen und...
				if (mappingMethod.get("method").toString().equals("EMBEDDING")) {
					embed();
				} else if (mappingMethod.get("method").toString().equals("REFERENCING")) {
					reference();
				}

				writeMappedJson();

			}
		}

	}

	/**
	 * get the mapping decision from neural network & set parent and child table
	 * instances
	 * 
	 * @param mappingTable
	 */
	public void setMapInstance(JSONObject mappingTable) {
		removeMappingTable(mappingTable.get("name").toString());
		ManyToManyResource.INSTANCE.setMappingTable(mappingTable);
		mappingMethod = getMappingDecision();
		JSONArray referenceInfo = (JSONArray) mappingTable.get("referencingFrom");
		JSONObject object1 = (JSONObject) referenceInfo.get(0);
		String string1 = (String) object1.get("referencedTab");
		if (string1.equals(mappingMethod.get("parent").toString())) {

			ManyToManyResource.INSTANCE.setTable1Info(object1);
			JSONObject object2 = (JSONObject) referenceInfo.get(1);
			ManyToManyResource.INSTANCE.setTable2Info(object2);
			String string2 = (String) object2.get("referencedTab");

			JSONArray sqlTables = (JSONArray) sqlJson.get("tables");

			for (int i = 0; i < sqlTables.size(); i++) {
				JSONObject table = (JSONObject) sqlTables.get(i);
				if (table.get("name").toString().equals(string1)) {
					ManyToManyResource.INSTANCE.setTable1(table);
				} else if (table.get("name").toString().equals(string2)) {
					ManyToManyResource.INSTANCE.setTable2(table);
				}
			}
		} else {
			ManyToManyResource.INSTANCE.setTable2Info(object1);
			JSONObject object2 = (JSONObject) referenceInfo.get(1);
			ManyToManyResource.INSTANCE.setTable1Info(object2);
			String string2 = (String) object2.get("referencedTab");

			JSONArray sqlTables = (JSONArray) sqlJson.get("tables");

			for (int i = 0; i < sqlTables.size(); i++) {
				JSONObject table = (JSONObject) sqlTables.get(i);
				if (table.get("name").toString().equals(string1)) {
					ManyToManyResource.INSTANCE.setTable2(table);
				} else if (table.get("name").toString().equals(string2)) {
					ManyToManyResource.INSTANCE.setTable1(table);
				}
			}

		}

	}

	/**
	 * get the mapping decision from neural network comparing complexity select
	 * the parent table and select mapping method add many-to-many relationship
	 * tables to node array
	 * 
	 * @return JSONObject contains mapping decision
	 */
	public JSONObject getMappingDecision() {

		JSONArray sqlTables = (JSONArray) sqlJson.get("tables");
		JSONObject mappingTable = ManyToManyResource.INSTANCE.getMappingTable();
		JSONArray referencingFrom = (JSONArray) mappingTable.get("referencingFrom");
		JSONObject referencingTable1Info = (JSONObject) referencingFrom.get(0);
		JSONObject referencingTable2Info = (JSONObject) referencingFrom.get(1);
		System.out.println("referencingFrom : " + referencingFrom);

		JSONObject referencingTable1 = new JSONObject();
		JSONObject referencingTable2 = new JSONObject();

		for (int i = 0; i < sqlTables.size(); i++) {
			JSONObject table = (JSONObject) sqlTables.get(i);
			String tableName = table.get("name").toString();
			System.out.println("Checking table: " + tableName);

			if (tableName.equals(referencingTable1Info.get("referencedTab"))) {
				referencingTable1 = table;
				System.out.println("Matched referencingTable1: " + tableName);
			} else if (tableName.equals(referencingTable2Info.get("referencedTab"))) {
				referencingTable2 = table;
				System.out.println("Matched referencingTable2: " + tableName);
			}
		}


		JSONObject referencingTable1ColInfo = (JSONObject) referencingTable1.get("dataTypeCount");
		JSONObject referencingTable2ColInfo = (JSONObject) referencingTable2.get("dataTypeCount");


		ServiceAccessor accessor = new ServiceAccessor();
		JSONObject table1Response = accessor.getMappingModel("ClientId", "requestId",
				referencingTable1.get("colCount").toString(), referencingTable1ColInfo.get("NUMERIC_COUNT").toString(),
				referencingTable1ColInfo.get("STRING_COUNT").toString(),
				referencingTable1ColInfo.get("DATE_COUNT").toString());
		JSONObject table2Response = accessor.getMappingModel("ClientId", "requestId",
				referencingTable2.get("colCount").toString(), referencingTable2ColInfo.get("NUMERIC_COUNT").toString(),
				referencingTable2ColInfo.get("STRING_COUNT").toString(),
				referencingTable2ColInfo.get("DATE_COUNT").toString());

		// Debug: Mapping service responses
		System.out.println("Mapping Model Response - Table 1: " + table1Response.toJSONString());
		System.out.println("Mapping Model Response - Table 2: " + table2Response.toJSONString());

		JSONObject summary = new JSONObject();
		JSONObject ChangeStructureObject = new JSONObject();
		ChangeStructureObject.put("text", "");

		if (Double.parseDouble(table1Response.get("complexity").toString()) > Double
				.parseDouble(table2Response.get("complexity").toString())) {
			summary.put("parent", referencingTable1Info.get("referencedTab"));
			summary.put("method", table1Response.get("mappingModel"));

			ChangeStructureObject.put("from", referencingTable1Info.get("referencedTab"));
			ChangeStructureObject.put("to", referencingTable2Info.get("referencedTab"));
			ChangeStructureObject.put("toText", table1Response.get("mappingModel"));
		} else {
			summary.put("parent", referencingTable2Info.get("referencedTab"));
			summary.put("method", table2Response.get("mappingModel"));

			ChangeStructureObject.put("from", referencingTable2Info.get("referencedTab"));
			ChangeStructureObject.put("to", referencingTable1Info.get("referencedTab"));
			ChangeStructureObject.put("toText", table2Response.get("mappingModel"));
		}

		ChangeStructure structure = ChangeStructure.getInstance();
		structure.linkDataArray.add(ChangeStructureObject);

		return summary;
	}


	/**
	 * use to embed the child table to parent table
	 */
	public void embed() {
		JSONObject table1 = ManyToManyResource.INSTANCE.getTable1();

		if (!isTableMapped(table1)) {
			insertTable(table1);
		}
		setTable2();
		JSONArray table1Data = (JSONArray) table1.get("data");
		JSONObject mappingTable = ManyToManyResource.INSTANCE.getMappingTable();
		JSONObject table1MappingInfo = ManyToManyResource.INSTANCE.getTable1Info();
		JSONArray mappingData = (JSONArray) mappingTable.get("data");
		String referencingCol = table1MappingInfo.get("referencingCol").toString();
		String referencedCol = table1MappingInfo.get("referencedCol").toString();
		String referencedTable = table1MappingInfo.get("referencedTab").toString();
		JSONObject table2MappingInfo = ManyToManyResource.INSTANCE.getTable2Info();
		String referencingTable = table2MappingInfo.get("referencedTab").toString();

		for (Object object : table1Data) {
			JSONObject table1DataObject = (JSONObject) object;
			String table1Value = table1DataObject.get(referencedCol).toString();
			JSONArray referencingDataArray = new JSONArray();
			for (Object object2 : mappingData) {
				JSONObject mappingObject = (JSONObject) object2;
				System.out.println("potentially missing Col: " + referencedCol);
				String referencedValue = mappingObject.get(referencingCol).toString();
				if (referencedValue.equals(table1Value)) {
					JSONObject referencingObject = findTable2Value(mappingObject);
					referencingDataArray.add(referencingObject);
				}
			}
			generateMappedDataArray(referencedTable, referencingTable, referencedCol, table1Value,
					referencingDataArray);
		}

		System.out.println("embed result : " + mapped);
	}

	/**
	 * set child table instance
	 */
	public void setTable2() {
		JSONObject table = ManyToManyResource.INSTANCE.getTable2();
		if (isTableMapped(table)) {
			JSONArray array = (JSONArray) mapped.get("collections");
			for (int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				if (object.get("collectionName").equals(table.get("name"))) {
					ManyToManyResource.INSTANCE.setTable2(object);
				}
			}
		} else {
			JSONArray array = (JSONArray) sqlJson.get("tables");
			JSONArray tableData = new JSONArray();
			for (int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				if (object.get("name").equals(table.get("name"))) {
					tableData = (JSONArray) object.get("data");
				}
			}
			JSONObject newCollection = new JSONObject();
			newCollection.put("collectionName", table.get("name").toString());
			newCollection.put("data", tableData);
			ManyToManyResource.INSTANCE.setTable2(newCollection);
		}
	}

	public JSONObject findTable2Value(JSONObject mappingObject) {

		JSONObject table2 = ManyToManyResource.INSTANCE.getTable2();
		JSONArray table2Data = (JSONArray) table2.get("data");
		JSONObject table2MappingInfo = ManyToManyResource.INSTANCE.getTable2Info();
		String referencingCol = table2MappingInfo.get("referencingCol").toString();
		String referencedCol = table2MappingInfo.get("referencedCol").toString();

		String referencedValue = mappingObject.get(referencingCol).toString();
		for (Object object : table2Data) {
			JSONObject table2DataObject = (JSONObject) object;
			String table2Value = table2DataObject.get(referencedCol).toString();
			if (referencedValue.equals(table2Value)) {
				return table2DataObject;
			}
		}

		return null;
	}
/**
 * get matching child data array and embed that array into parent table
 * @param referencedCollection
 * @param referencingTable
 * @param referencingCol
 * @param referencingColData
 * @param referencingDataArray
 */
	public void generateMappedDataArray(String referencedCollection, String referencingTable, String referencingCol,
			String referencingColData, JSONArray referencingDataArray) {
		JSONArray mappedData = (JSONArray) mapped.get("collections");
		for (int i = 0; i < mappedData.size(); i++) {
			JSONObject mappedCollection = (JSONObject) mappedData.get(i);
			String collectionName = mappedCollection.get("collectionName").toString();
			if (referencedCollection.equals(collectionName)) {
				JSONArray mappedValues = (JSONArray) mappedCollection.get("data");
				for (int j = 0; j < mappedValues.size(); j++) {
					JSONObject mappedDataObject = (JSONObject) mappedValues.get(j);
					String mappedDataValue = (String) mappedDataObject.get(referencingCol);
					if (mappedDataValue.equals(referencingColData)) {
						mappedDataObject.put(referencingTable, referencingDataArray);
					}
				}
			}

		}

	}

	/**
	 * use to reference the child table to parent table
	 */
	public void reference() {
		JSONObject table1 = ManyToManyResource.INSTANCE.getTable1();

		if (!isTableMapped(table1)) {
			insertTable(table1);
		}
		insertReferencedTable();
		JSONObject table2 = ManyToManyResource.INSTANCE.getTable2();

		JSONArray table1Data = (JSONArray) table1.get("data");
		JSONObject table1MappingInfo = ManyToManyResource.INSTANCE.getTable1Info();
		String referencingCol = table1MappingInfo.get("referencingCol").toString();
		String referencedCol = table1MappingInfo.get("referencedCol").toString();
		String referencedTable = table1MappingInfo.get("referencedTab").toString();
		JSONObject mappingTable = ManyToManyResource.INSTANCE.getMappingTable();
		JSONObject table2MappingInfo = ManyToManyResource.INSTANCE.getTable2Info();
		String referencingTable = table2MappingInfo.get("referencedTab").toString();

		JSONArray mappingData = (JSONArray) mappingTable.get("data");

		for (Object object : table1Data) {
			JSONObject table1DataObject = (JSONObject) object;
			String table1Value = table1DataObject.get(referencedCol).toString();
			JSONArray referencingIdArray = new JSONArray();
			for (Object object2 : mappingData) {
				JSONObject mappingObject = (JSONObject) object2;
				String referencedValue = mappingObject.get(referencingCol).toString();
				if (referencedValue.equals(table1Value)) {
//					System.out.println(mappingObject + "@@@" + table1DataObject);
					String referencingId = findReferencedId(mappingObject);
					referencingIdArray.add(referencingId);
				}
			}
			System.out.println(referencingIdArray);
			generateReferencedDataArray(referencedTable, referencingTable, referencedCol, table1Value,
					referencingIdArray);
		}

	}
/**
 * get matching child object id array and embed that array into parent table
 * @param referencedCollection
 * @param referencingTable
 * @param referencingCol
 * @param referencingColData
 * @param referencingDataArray
 */
	public void generateReferencedDataArray(String referencedCollection, String referencingTable, String referencingCol,
			String referencingColData, JSONArray referencingDataArray) {
		JSONArray mappedData = (JSONArray) mapped.get("collections");
		for (int i = 0; i < mappedData.size(); i++) {
			JSONObject mappedCollection = (JSONObject) mappedData.get(i);
			String collectionName = mappedCollection.get("collectionName").toString();
			if (referencedCollection.equals(collectionName)) {
				JSONArray mappedValues = (JSONArray) mappedCollection.get("data");
				for (int j = 0; j < mappedValues.size(); j++) {
					JSONObject mappedDataObject = (JSONObject) mappedValues.get(j);
					String mappedDataValue = (String) mappedDataObject.get(referencingCol);
					if (mappedDataValue.equals(referencingColData)) {
						mappedDataObject.put(referencingTable, referencingDataArray);
						System.out.println(collectionName);
					}
				}
			}

		}
		System.out.println("referenced " + mapped);

	}

	public String findReferencedId(JSONObject mappingObject) {
		JSONObject table2 = ManyToManyResource.INSTANCE.getTable2();
		JSONArray table2Data = (JSONArray) table2.get("data");
		JSONObject table2MappingInfo = ManyToManyResource.INSTANCE.getTable2Info();

		String referencingCol = table2MappingInfo.get("referencingCol").toString();
		String referencedCol = table2MappingInfo.get("referencedCol").toString();

		String referencedValue = mappingObject.get(referencingCol).toString();

		for (Object object : table2Data) {
			JSONObject table2DataObject = (JSONObject) object;
			System.out.println("DEBUG: Attempting to access key '" + referencedCol + "' in: " + table2DataObject);

			Object table2ValueRaw = table2DataObject.get(referencedCol);

			if (table2ValueRaw == null && referencedCol.endsWith("_id")) {
				// Try nested lookup
				String nestedDocName = referencedCol.substring(0, referencedCol.length() - 3); // remove "_id"
				JSONObject nested = (JSONObject) table2DataObject.get(nestedDocName);
				if (nested != null) {
					table2ValueRaw = nested.get(referencedCol);
					System.out.println("DEBUG: Fallback to nested '" + nestedDocName + "' for key '" + referencedCol + "'");
				} else {
					System.out.println("DEBUG: Nested object '" + nestedDocName + "' not found.");
				}
			}
			if (table2ValueRaw == null) {
				System.out.println("DEBUG: Could not find value for '" + referencedCol + "' in object.");
				continue; // skip this one
			}

			String table2Value = table2ValueRaw.toString();
			if (referencedValue.equals(table2Value)) {
				return table2DataObject.get("_id").toString();
			}
		}

		return null;
	}

	public void insertReferencedTable() {
		JSONObject table = ManyToManyResource.INSTANCE.getTable2();

		if (isTableMapped(table)) {
			JSONArray array = (JSONArray) mapped.get("collections");
			for (int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				if (object.get("collectionName").equals(table.get("name"))) {
					JSONArray tableData = (JSONArray) object.get("data");
					for (int j = 0; j < tableData.size(); j++) {
						JSONObject tableValue = (JSONObject) tableData.get(j);
						if (!tableData.contains("_id")) {
							ObjectId id = new ObjectId();
							tableValue.put("_id", id.toString());
						}
					}
					ManyToManyResource.INSTANCE.setTable2(object);
				}

			}
			mapped.put("collections", array);
		} else {
			JSONArray array = (JSONArray) sqlJson.get("tables");
			JSONArray newDataArray = new JSONArray();
			for (int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				if (object.get("name").equals(table.get("name"))) {
					JSONArray tableData = (JSONArray) object.get("data");
					for (int j = 0; j < tableData.size(); j++) {
						JSONObject tableValue = (JSONObject) tableData.get(j);
						ObjectId id = new ObjectId();
						tableValue.put("_id", id.toString());
						newDataArray.add(tableValue);

					}
				}

			}
			JSONObject newCollection = new JSONObject();
			newCollection.put("collectionName", table.get("name").toString());
			newCollection.put("data", newDataArray);
			ManyToManyResource.INSTANCE.setTable2(newCollection);
			// System.out.println("###"+newCollection);
			JSONArray mappedArray = (JSONArray) mapped.get("collections");
			mappedArray.add(newCollection);
			System.out.println(mapped);

		}
	}

	public void writeMappedJson() {
		FileWriter file;
		try {
			file = new FileWriter(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath());
			file.write(mapped.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveChangeStructure() {
		ChangeStructure structure = ChangeStructure.getInstance();
		CollectionStructureJSONHandler handler = new CollectionStructureJSONHandler();
		handler.save(structure);
	}

	public boolean isTableMapped(JSONObject table) {
		JSONArray array = (JSONArray) mapped.get("collections");
		boolean isExist = false;
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = (JSONObject) array.get(i);
			if (object.get("collectionName").equals(table.get("name"))) {
				// System.out.println(table.get("name"));
				isExist = true;
			}

		}
		return isExist;
	}

	public void insertTable(JSONObject table) {
		JSONObject newCollection = new JSONObject();
		newCollection.put("collectionName", table.get("name"));
		JSONArray collectionData = new JSONArray();
		collectionData = (JSONArray) table.get("data");
		newCollection.put("data", collectionData);
		JSONArray array = (JSONArray) mapped.get("collections");
		array.add(newCollection);
		// ManyToManyResource.INSTANCE.setTable1(newCollection);
		mapped.put("collections", array);

		// System.out.println(mapped);

	}

	/**
	 * used to remove many-to-many mapping table from node array
	 * 
	 * @param name
	 *            table name
	 */
	private void removeMappingTable(String name) {
		JSONArray nodeArray = new JSONArray();
		nodeArray = ChangeStructure.getInstance().nodeDataArray;
		for (int i = 0; i < nodeArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) nodeArray.get(i);
			if (jsonObject.get("key").toString().equals(name)) {
				nodeArray.remove(i);
			}
		}

	}

	/*
	 * public void insertToMapped(String referencedCollection) { JSONArray
	 * mappedData = (JSONArray) mapped.get("collections"); JSONObject object =
	 * new JSONObject(); for (int i = 0; i < mappedData.size(); i++) {
	 * JSONObject mappedCollection = (JSONObject) mappedData.get(i); String
	 * collectionName = mappedCollection.get("collectionName").toString(); if
	 * (referencedCollection.equals(collectionName)) { //
	 * object.put("collectionName", referencedCollection); //
	 * object.put("values", mappedDataArray); }
	 *
	 * } // mappedData.add(object); System.out.println("before" + mapped); //
	 * mapped.put("collections", mappedData); System.out.println("after" +
	 * mapped); FileWriter file; try { file = new
	 * FileWriter("C:\\Users\\Lakshan1\\Desktop\\Resources\\mapped.json");
	 * file.write(mapped.toJSONString()); file.flush(); file.close(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * }
	 *
	 * }
	 */

}
