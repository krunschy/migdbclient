package org.migdb.migdbclient.controllers.mapping.onetomany;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.models.dto.Reference;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;
import org.migdb.migdbclient.utils.ServiceAccessor;

public class OneToManyMapper {

	String desktopPath = System.getProperty("user.home") + "\\" + "Desktop";
	JSONParser parser = new JSONParser();
	Object obj;
	JSONObject jsonObject;
	File jsonFile = new File(FilePath.DOCUMENT.getPath()+ FilePath.DBSTRUCTUREFILE.getPath());
	File collectionFile = new File(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath());
	boolean exist = collectionFile.exists();
	Object collectionObject;
	JSONObject collectionJsonObject;
	JSONArray tableList;
	JSONArray collectionList;
	ArrayList<Reference> connectedTables = new ArrayList<Reference>();
	int refObjLimit = 5;


	public void mapOneToMany(){

		try {
			obj = parser.parse(new FileReader(jsonFile));
			jsonObject = (JSONObject) obj;

			if(exist) {
				collectionObject = parser.parse(new FileReader(collectionFile));
				collectionJsonObject = (JSONObject) collectionObject;
				collectionList = (JSONArray) collectionJsonObject.get("collections");
			} else {
				collectionJsonObject = new JSONObject();
				collectionList = new JSONArray();
			}

			tableList = (JSONArray) jsonObject.get("tables");

			for(int i=0; i<tableList.size(); i++) {

				JSONObject table = (JSONObject) tableList.get(i);
				String tableName = (String) table.get("name");

				JSONArray referencedByArr = (JSONArray) table.get("referencedBy");
				JSONArray columns = (JSONArray) table.get("columns");

				if(!referencedByArr.isEmpty()) {
					for(int k=0; k<referencedByArr.size(); k++) {
						JSONObject referencedBy = (JSONObject) referencedByArr.get(k);
						String relationshipType = (String) referencedBy.get("relationshipType");
						String referencingTab = (String) referencedBy.get("referencingTab");
						String referencingCol = (String) referencedBy.get("referencingCol");
						if(relationshipType!=null) {
							if (relationshipType.equals("OneToMany")) {
								String referencedCol = (String) referencedBy.get("referencedCol");
								for (int x = 0; x < columns.size(); x++) {
									JSONObject col = (JSONObject) columns.get(x);
									String colName = (String) col.get("colName");
									if (colName.equals(referencedCol)) {
										int[] ref = getPairIndex(referencingTab, referencingCol, tableName);
										Reference reference = new Reference(i, x, ref[0], ref[1]);
										connectedTables.add(reference);
									}
								}
							}
						}
					}
				}

			}

			System.out.println("connectedTables "+connectedTables);

			JSONObject documentObj = new JSONObject();

			for(int i =0; i<connectedTables.size(); i++) {
				Reference ref = connectedTables.get(i);
				int referencedTblIndex = ref.getOneSideTbl();
				int referencedColIndex = ref.getOneSideCol();
				int referencingTblIndex = ref.getManySideTbl();
				int referencingColIndex = ref.getManySideCol();

				System.out.println(referencedTblIndex+" "+referencedColIndex+" "+referencingTblIndex+" "+referencingColIndex);

				JSONObject referencedTab = (JSONObject) tableList.get(referencedTblIndex);
				String referencedTabName = (String) referencedTab.get("name");
				JSONArray referencedColumns = (JSONArray) referencedTab.get("columns");
				JSONObject referencedCol = (JSONObject) referencedColumns.get(referencedColIndex);
				String referencedColName = (String) referencedCol.get("colName");
				JSONObject referencedDataType = (JSONObject) referencedTab.get("dataTypeCount");

				JSONObject referencingTab = (JSONObject) tableList.get(referencingTblIndex);
				String referencingTabName = (String) referencingTab.get("name");
				JSONArray referencingColumns = (JSONArray) referencingTab.get("columns");
				JSONObject referencingCol = (JSONObject) referencingColumns.get(referencingColIndex);
				String referencingColName = (String) referencingCol.get("colName");
				Long referencingColCount = (Long) referencingTab.get("colCount");
				JSONObject referencingDataType = (JSONObject) referencingTab.get("dataTypeCount");

				JSONArray referencedArr = (JSONArray) referencingTab.get("referencedBy");
				JSONArray referencingArr = (JSONArray) referencingTab.get("referencingFrom");

				JSONArray dataObj= new JSONArray();
				JSONArray referencedDataArr = (JSONArray) referencedTab.get("data");
				JSONArray referencingDataArr = (JSONArray) referencingTab.get("data");

				String mappingType = "";
				float oneSideComplexity = -1;
				float manySideComplexity = -1;

				JSONObject ChangeStructureObject = new JSONObject();
				ChangeStructure structure = ChangeStructure.getInstance();

				try {

					ServiceAccessor accessor = new ServiceAccessor();
					JSONObject oneSideResponse = accessor.getMappingModel("client123456", "req123456",String.valueOf((Long)referencedTab.get("colCount")), String.valueOf((Long) referencedTab.get("NUMERIC_COUNT")),String.valueOf((Long) referencedTab.get("STRING_COUNT")),String.valueOf((Long) referencedTab.get("DATE_COUNT")));
					JSONObject manySideResponse = accessor.getMappingModel("client123456", "req123456",String.valueOf((Long)referencingTab.get("colCount")),String.valueOf((Long)referencingTab.get("NUMERIC_COUNT")),String.valueOf((Long)referencingTab.get("STRING_COUNT")),String.valueOf((Long)referencingTab.get("DATE_COUNT")));

					oneSideComplexity = Float.parseFloat((String)oneSideResponse.get("complexity"));
					manySideComplexity = Float.parseFloat((String)manySideResponse.get("complexity"));

					if(oneSideComplexity >= manySideComplexity) {
						String mappingModel = (String)manySideResponse.get("mappingModel");
						if(mappingModel.equals("EMBEDDING") && referencedArr.isEmpty() && (referencingArr.size() == 1)) {
							mappingType = "Embedding";

							ChangeStructureObject.put("from", referencedTabName);
							ChangeStructureObject.put("to", referencingTabName);
							ChangeStructureObject.put("toText", "EMBEDDING");
							ChangeStructureObject.put("text", "");

						} else {
							mappingType = "Referencing";

							ChangeStructureObject.put("from", referencingTabName);
							ChangeStructureObject.put("to", referencedTabName);
							ChangeStructureObject.put("toText", "REFERENCING");
							ChangeStructureObject.put("text", "");

						}
					} else {
						mappingType = "Referencing";

						ChangeStructureObject.put("from", referencingTabName);
						ChangeStructureObject.put("to", referencedTabName);
						ChangeStructureObject.put("toText", "REFERENCING");

					}

				} catch (Exception e) {

					if((referencedArr.isEmpty()) && (referencingArr.size() == 1)) {
						mappingType = "Embedding";
						manySideComplexity = 0;
						oneSideComplexity = 1;

						ChangeStructureObject.put("from", referencedTabName);
						ChangeStructureObject.put("to", referencingTabName);
						ChangeStructureObject.put("toText", "EMBEDDING");

					} else {
						mappingType = "Referencing";
						int objectCount = getObjCount(referencedDataArr, referencingDataArr, referencedColName, referencingColName);
						if(objectCount > refObjLimit) {
							manySideComplexity = 1;
							oneSideComplexity = 0;

							ChangeStructureObject.put("from", referencingTabName);
							ChangeStructureObject.put("to", referencedTabName);
							ChangeStructureObject.put("toText", "REFERENCING");

						} else {
							manySideComplexity = 0;
							oneSideComplexity = 1;

							ChangeStructureObject.put("from", referencedTabName);
							ChangeStructureObject.put("to", referencingTabName);
							ChangeStructureObject.put("toText", "EMBEDDING");

						}
					}

				}

				ChangeStructureObject.put("text", "");
				structure.linkDataArray.add(ChangeStructureObject);


				for(int k=0; k<referencedDataArr.size(); k++) {
					JSONObject referencedData = (JSONObject) referencedDataArr.get(k);
					String referencedVal = (String) referencedData.get(referencedColName).toString();
					JSONArray manySideData = new JSONArray();

					ObjectId referencedId = null;
					if(mappingType.equals("Referencing") && manySideComplexity>oneSideComplexity) {
						referencedId = new ObjectId();
					}

					referencingDataArr = (JSONArray) referencingTab.get("data");
					for(int l=0; l<referencingDataArr.size(); l++) {
						JSONObject referencingData = (JSONObject) referencingDataArr.get(l);
						String value = null;
						if(referencingData.containsKey(referencingColName)) {
							value =(String) referencingData.get(referencingColName).toString();
						}
						if(value != null) {
							if(value.equals(referencedVal)) {
								referencingData.remove(referencingColName);
								if(mappingType.equals("Embedding")) {
									manySideData.add(referencingData);
								} else {
									ObjectId referencingId = null;
									if(manySideComplexity>oneSideComplexity) {
										referencingData.put(referencedTabName, referencedId.toString());
									} else {
										referencingId = new ObjectId();
										referencingData.put("_id", referencingId.toString());
										manySideData.add(referencingId.toString());
									}

									int referencingIndex = checkCollection(referencingTabName);
									if(referencingIndex == -1) {
										JSONObject manySideDocObj = new JSONObject();
										manySideDocObj.put("collectionName", referencingTabName);
										JSONArray valArray = new JSONArray();
										valArray.add(referencingData);
										manySideDocObj.put("data", valArray);
										collectionList.add(manySideDocObj);
									} else {
										JSONObject collection = (JSONObject) collectionList.get(referencingIndex);
										JSONArray values = (JSONArray) collection.get("data");
										if(values != null){
											if(values.size()>l) {
												JSONObject valObj = (JSONObject) values.get(l);
												if(manySideComplexity>oneSideComplexity) {
													valObj.put(referencedTabName, referencedId.toString());
												} else {
													valObj.put("_id", referencingId.toString());
												}
											}else {
												if(manySideComplexity>oneSideComplexity) {
													referencingData.put(referencedTabName, referencedId.toString());
												} else {
													referencingData.put("_id", referencingId.toString());
												}
												values.add(referencingData);
											}
										} else {
											JSONArray valArray = new JSONArray();
											valArray.add(referencingData);
											collection.put("data", valArray);
										}
									}

									System.out.println();
									System.out.println("collectionlist "+collectionList);
									System.out.println();
								}
							}
						}
					}

					if(mappingType.equals("Embedding")) {
						referencedData.put(referencingTabName, manySideData);
						int index = checkCollection(referencedTabName);
						if(index == -1) {
							dataObj.add(referencedData);
							System.out.println(dataObj);
							documentObj.put("collectionName", referencedTabName);
							documentObj.put("data", dataObj);
							System.out.println("document "+ documentObj);
							collectionList.add(documentObj);
							//System.out.println(collectionList);
						} else {
							JSONObject collection = (JSONObject) collectionList.get(index);
							JSONArray values = (JSONArray) collection.get("data");
							if(values != null){
								if(values.size()>k) {
									JSONObject valObj = (JSONObject) values.get(k);
									valObj.put(referencingTabName, manySideData);
								} else {
									values.add(referencedData);
								}
							} else {
								JSONArray valArray = new JSONArray();
								valArray.add(referencedData);
								collection.put("data", valArray);
							}
							//System.out.println(collectionList);
						}
					} else {
						if(manySideComplexity>oneSideComplexity) {
							referencedData.put("_id", referencedId.toString());
						} else {
							referencedData.put(referencingTabName, manySideData);
						}
						int referencedIndex = checkCollection(referencedTabName);
						System.out.println("referencedIndex "+referencedIndex+" referencedTabName "+referencedTabName);
						if(referencedIndex == -1) {
							JSONObject oneSideDocObj = new JSONObject();
							oneSideDocObj.put("collectionName", referencedTabName);
							JSONArray valArray = new JSONArray();
							valArray.add(referencedData);
							oneSideDocObj.put("data", valArray);
							collectionList.add(oneSideDocObj);
							//System.out.println(collectionList);
						} else {
							JSONObject collection = (JSONObject) collectionList.get(referencedIndex);
							JSONArray values = (JSONArray) collection.get("data");
							if(values != null){
								if(values.size()>k) {
									JSONObject valObj = (JSONObject) values.get(k);
									if(manySideComplexity>oneSideComplexity) {
										valObj.put("_id", referencedId.toString());
									} else {
										valObj.put(referencingTabName, manySideData);
									}
								} else {
									values.add(referencedData);
								}
							} else {
								JSONArray valArray = new JSONArray();
								valArray.add(referencedData);
								collection.put("data", valArray);
							}
							//System.out.println(collectionList);
						}
					}

				}

			}

			if (!exist) {
				collectionJsonObject.put("collections", collectionList);
			}

			JSONObject json = new JSONObject();

			String updatedJson = json.toJSONString(collectionJsonObject);
			FileUtils.writeStringToFile(collectionFile, updatedJson);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}


	public int[] getPairIndex(String referencingTab, String referencingCol, String refTable) {
		int[] ref = new int[2];
		for(int i =0; i<tableList.size(); i++) {
			JSONObject table = (JSONObject) tableList.get(i);
			String name = (String) table.get("name");
			if(name.equals(referencingTab)) {
				JSONArray cols = (JSONArray) table.get("columns");
				if(!cols.isEmpty()) {
					for(int k=0; k<cols.size(); k++) {
						JSONObject column = (JSONObject) cols.get(k);
						String colName = (String) column.get("colName");
						if(colName.equals(referencingCol)) {
							ref[0] = i;
							ref[1] = k;
						}
					}
				}
			}
		}
		return ref;
	}

	public int checkCollection(String name) {
		int index = -1;
		if(!collectionList.isEmpty()) {
			for(int i=0; i<collectionList.size(); i++){
				JSONObject collection = (JSONObject) collectionList.get(i);
				String collName = (String) collection.get("collectionName");
				if(collName.equals(name)) {
					index = i;
				}
			}
		}
		return index;
	}

	public int getObjCount(JSONArray referencedDataArr, JSONArray referencingDataArr, String referencedColName, String referencingColName) {
		int count=0;
		for(int i=0; i<referencedDataArr.size(); i++) {
			JSONObject referencedData = (JSONObject) referencedDataArr.get(i);
			String referencedVal = (String) referencedData.get(referencedColName).toString();
			for(int k=0; k<referencingDataArr.size(); k++) {
				JSONObject referencingData = (JSONObject) referencingDataArr.get(k);
				String value = (String) referencingData.get(referencingColName).toString();
				if(value != null) {
					if(value.equals(referencedVal)) {
						count++;
					}
				}
			}
		}

		return count;
	}

}
