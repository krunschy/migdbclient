/**
 * 
 */
package org.migdb.migdbclient.controllers.mapping.onetoone;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;


/**
 * @author Kani
 *
 */
public class OneToOneMap {
	
	public void oneToOneMapper(){
		
		JSONParser parser = new JSONParser();

		Object obj;
		JSONObject dataObj;
		JSONArray collectionArray = new JSONArray();
		JSONObject collectionObject = new JSONObject();
		String collectionName = null;

		try {

			File collectionFile = new File(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath());
			obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray tableList = (JSONArray) jsonObject.get("tables");
			Iterator<JSONObject> iterator = tableList.iterator();
			JSONObject valueObject = new JSONObject();
			JSONArray valueArray = new JSONArray();
			//JSONArray values = new JSONArray();
			
			ChangeStructure structure = ChangeStructure.getInstance();
			//structure.nodeDataArray.clear();
			structure.linkDataArray.clear();
			structure.jsonFileName = null;

			while (iterator.hasNext()) {
				JSONObject tbl = (JSONObject) iterator.next();

				JSONArray referencedBy = (JSONArray) tbl.get("referencedBy");
				Iterator<JSONObject> iterateReferencedBy = referencedBy.iterator();

				// Iterate referencingFrom array
				while (iterateReferencedBy.hasNext()) {
					JSONObject referencingObj = iterateReferencedBy.next();
					String referencedCol = (String) referencingObj.get("referencedCol");
					String referencingTab = (String) referencingObj.get("referencingTab");
					String referencingCol = (String) referencingObj.get("referencingCol");
					String relationshipType = (String) referencingObj.get("relationshipType");

					System.out.println("DEBUG - Referenced Column: " + referencedCol);
					System.out.println("DEBUG - Referencing Table: " + referencingTab);
					System.out.println("DEBUG - Referencing Column: " + referencingCol);
					System.out.println("DEBUG - Relationship Type: " + relationshipType);

					if (relationshipType == null || relationshipType.equals("OneToOne")) {
						
						JSONObject ChangeStructureObject = new JSONObject();
						
						ChangeStructureObject.put("from", tbl.get("name"));
						ChangeStructureObject.put("to", referencingObj.get("referencingTab"));
						ChangeStructureObject.put("toText", "EMBEDDING");
						ChangeStructureObject.put("text", "");
						
						structure.linkDataArray.add(ChangeStructureObject);
						
						/*System.out.println("Table name : " + tbl.get("name"));
						System.out.println("Referenced Column : " + referencedCol);
						System.out.println("Referencing Table : " + referencingTab);
						System.out.println("Referencing Column : " + referencingCol);
						System.out.println("Relationship Type : " + relationshipType);*/
						collectionName = (String) tbl.get("name");

						JSONArray data = (JSONArray) tbl.get("data");
						Iterator<JSONObject> iterateData = data.iterator();
						while (iterateData.hasNext()) {
							dataObj = iterateData.next();
							String column = (String) dataObj.get(referencedCol);
							JSONObject getReturn = getReferencedBy(referencingTab, referencingCol, column);
							getReturn.remove(referencingCol);
							if(!getReturn.isEmpty()){
								dataObj.put(referencingTab, getReturn);
							}
							
							//values.add(dataObj);
							/*System.out.println(dataObj);*/
							valueArray.add(dataObj);
							/*System.out.println();*/
						}

						/*System.out.println();*/
						/*JSONArray collections = new JSONArray();
						JSONObject collectionObj = new JSONObject();*/
						valueObject.put("data", valueArray);
						valueObject.put("collectionName", collectionName);
						collectionArray.add(valueObject);
						
					}
				}
			}
			
			
			
			collectionObject.put("collections", collectionArray);
			JSONObject json = new JSONObject();
			String updatedson = json.toJSONString(collectionObject);
			FileUtils.writeStringToFile(collectionFile, updatedson);
			
			System.out.println("One to one map");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static JSONObject getReferencedBy(String referencingTab, String referencingCol, String value) {
		JSONObject dataReturn = null;
		JSONObject mappingType = null;
		JSONParser parser2 = new JSONParser();
		Object obj2;
		try {
			obj2 = parser2.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			JSONObject jsonObject = (JSONObject) obj2;
			JSONArray tableList = (JSONArray) jsonObject.get("tables");
			Iterator<JSONObject> iterator = tableList.iterator();
			while (iterator.hasNext()) {
				JSONObject tbl = (JSONObject) iterator.next();
				String referencingTable = (String) tbl.get("name");
				if (referencingTab.equals(referencingTable)) {
					JSONArray referencedBy = (JSONArray) tbl.get("referencingFrom");

					JSONArray data = (JSONArray) tbl.get("data");
					Iterator<JSONObject> iterateData = data.iterator();

					// Iterate referencingFrom array
					while (iterateData.hasNext()) {
						JSONObject dataObj = iterateData.next();
						String referencingDataCol = (String) dataObj.get(referencingCol);
						if (referencingDataCol.equals(value)) {
							/*if (!referencedBy.isEmpty()) {
								ObjectId id = new ObjectId();
								dataObj.put("isReferencedBy", "YES");
								dataObj.put("_id", id);
							} else {
								dataObj.put("isReferencedBy", "NO");
							}*/
							dataReturn = dataObj;
							break;
						} else {
							dataObj.clear();
							dataObj.put(referencingCol, null);
							dataReturn = dataObj;
						}
					}
				}
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataReturn;
	}

}
