package org.migdb.migdbclient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.JsonConstants;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ColumnsDTO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.models.dto.RelationshiCardinalityDTO;
import org.migdb.migdbclient.models.dto.TableDTO;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;
import org.migdb.migdbclient.config.fromResources.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MySQLParser {
	
	String host = ConnectionParameters.SESSION.getMysqlHostName();
	int port = ConnectionParameters.SESSION.getMysqlPort();
	String username = ConnectionParameters.SESSION.getUserName();
	String password = ConnectionParameters.SESSION.getPassword();
	String database = Session.INSTANCE.getActiveDB();
	
	public void sqlParser() throws ParserConfigurationException, SAXException, IOException{
		
		File fXmlFile = new File(FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("table_data");
		
		try {
			MysqlDAO dao = new MysqlDAO();
			SqliteDAO sDao = new SqliteDAO();
			ArrayList<RelationshiCardinalityDTO> dto = sDao.getReferencedList();
			ArrayList<TableDTO> arrTab = dao.getTables(host, port, database, username, password);
			ArrayList<ColumnsDTO> arrCol = dao.getColumnsAccordingTable(host, port, database, username, password);
			ArrayList<ReferenceDTO> arr = dao.getReferencedList(host, port, database, username, password);
			File jsonFiel = new File(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath());
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArr = new JSONArray();
			
			ChangeStructure structure = ChangeStructure.getInstance();
			structure.nodeDataArray.clear();
			/*jw = new JsonWriter(new FileWriter(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			*//*jw.beginObject();

			jw.name("tables");

			jw.beginArray();*/

			if (!arrTab.isEmpty()) {
				for (TableDTO dtoTable : arrTab) {
					/*jw.beginObject();// begin
*/					JSONObject jo = new JSONObject();
					jo.put(JsonConstants.TABLENAME.getJsonContant(), dtoTable.getTableName());
					int colCount = dataTypeObject( dtoTable.getTableName().toString()).get("STRING") + dataTypeObject( dtoTable.getTableName().toString()).get("NUMERIC") + dataTypeObject( dtoTable.getTableName().toString()).get("DATE");
					jo.put(JsonConstants.COLUMNCOUNT.getJsonContant(), colCount);
					jo.put(JsonConstants.PRIMARYKEY.getJsonContant(), dao.getPrimaryKey(host, port, database, username, password, dtoTable.getTableName().toString()));
					
					JSONObject dataTypeObject = new JSONObject();
					dataTypeObject.put("STRING_COUNT", dataTypeObject( dtoTable.getTableName().toString()).get("STRING"));
					dataTypeObject.put("NUMERIC_COUNT", dataTypeObject( dtoTable.getTableName().toString()).get("NUMERIC"));
					dataTypeObject.put("DATE_COUNT", dataTypeObject( dtoTable.getTableName().toString()).get("DATE"));
					jo.put("dataTypeCount", dataTypeObject);
					
					// Collects tables for collection structure gui
					JSONObject ChangeStructureObject = new JSONObject();
					JSONArray attributesArr = new JSONArray();
					
					ChangeStructureObject.put("key", dtoTable.getTableName());
					
					/*jw.name("name");
					jw.value(dtoTable.getTableName());*/
					/*jw.name("colCount");
					jw.value(dao.getColumnCount(host, port, database, username, password, dtoTable.getTableName().toString()));*/
					/*jw.name("primaryKey");
					jw.value(dao.getPrimaryKey(host, port, database, username, password, dtoTable.getTableName().toString()));*/
					JSONArray columns = new JSONArray();
					
					/*jw.name("columns");
					jw.beginArray(); // begin columns array
*/
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							/*jw.beginObject();*/
							JSONObject columnObj = new JSONObject();
							columnObj.put(JsonConstants.COLUMNNAME.getJsonContant(), dtoColumn.getCOLUMN_NAME());
							columnObj.put(JsonConstants.DATATYPE.getJsonContant(), dtoColumn.getDATA_TYPE());
							/*jw.name("colName");
							jw.value(dtoColumn.getCOLUMN_NAME());*/
							/*jw.name("dataType");
							jw.value(dtoColumn.getDATA_TYPE());*/
							/*jw.endObject();*/
							columns.add(columnObj);
							
							JSONObject attrubuteObj = new JSONObject();
							attrubuteObj.put("name", dtoColumn.getCOLUMN_NAME());
							attrubuteObj.put("iskey", false);
							attrubuteObj.put("figure", "Cube1");
							attrubuteObj.put("color", "bluegrad");
							
							attributesArr.add(attrubuteObj);
						}
					}
					jo.put(JsonConstants.COLUMNOBJECT.getJsonContant(), columns);
					ChangeStructureObject.put("items", attributesArr);
					
					structure.nodeDataArray.add(ChangeStructureObject);
					/*jw.endArray(); // End columns array
					 * 
*/
					/*jw.name("referencingFrom");
					jw.beginArray(); // Begin reference
*/
					JSONArray referencingFrom = new JSONArray();
					
					
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							for (ReferenceDTO dtoReference : arr) {
								if (dtoTable.getTableName().equals(dtoReference.getTableName())
										&& dtoColumn.getCOLUMN_NAME().equals(dtoReference.getColumnName())) {
									/*jw.beginObject();*/
									JSONObject referencingFromObj = new JSONObject();
									referencingFromObj.put(JsonConstants.REFERENCEDCOLUMN.getJsonContant(), dtoReference.getReferencedColumnName());
									referencingFromObj.put(JsonConstants.REFERENCEDTABLE.getJsonContant(), dtoReference.getReferencedTableName());
									referencingFromObj.put(JsonConstants.REFERENCINGCOLUMN.getJsonContant(), dtoReference.getColumnName());
									
									for(RelationshiCardinalityDTO rDto : dto){
										if(dtoReference.getReferencedTableName().equals(rDto.getREFERENCED_TABLE_NAME()) && dtoReference.getReferencedColumnName().equals(rDto.getREFERENCED_COLUMN_NAME()) && dtoTable.getTableName().equals(rDto.getTABLE_NAME()) && dtoColumn.getCOLUMN_NAME().equals(rDto.getCOLUMN_NAME())){
											referencingFromObj.put(JsonConstants.RELATIONSHIPTYPE.getJsonContant(),rDto.getRELATIONSHIP_TYPE());
										}
									}
									
									/*jw.name("referencedCol");
									jw.value(dtoColumn.getCOLUMN_NAME());*/
									/*jw.name("referencedTab");
									jw.value(dtoReference.getReferencedTableName());*/
									/*jw.name("referencingCol");
									jw.value(dtoReference.getReferencedColumnName());*/
									// System.out.println(dao.getRelationshipType(database,
									// dtoTable.getTableName()));
									/*jw.name("relationshipType");
									jw.value("----- Coming soon ----");*/
									/*jw.endObject();*/
									referencingFrom.add(referencingFromObj);
								}
							}
						}
					}

					/*jw.endArray(); // End reference*/
					jo.put(JsonConstants.REFERENCINGFROMOBJECT.getJsonContant(), referencingFrom);
					
					/*jw.name("referencedBy");
					jw.beginArray(); // Begin reference*/
					JSONArray referencedBy = new JSONArray();
					
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							for (ReferenceDTO dtoReference : arr) {
								if (dtoTable.getTableName().equals(dtoReference.getReferencedTableName()) && dtoColumn
										.getCOLUMN_NAME().equals(dtoReference.getReferencedColumnName())) {
									/*jw.beginObject();*/
									JSONObject referencedByObj = new JSONObject();
									referencedByObj.put(JsonConstants.REFERENCEDCOLUMN.getJsonContant(), dtoColumn.getCOLUMN_NAME());
									referencedByObj.put(JsonConstants.REFERENCINGTABLE.getJsonContant(), dtoReference.getTableName());
									referencedByObj.put(JsonConstants.REFERENCINGCOLUMN.getJsonContant(), dtoReference.getColumnName());
									
									for(RelationshiCardinalityDTO rDto : dto){
										if(dtoReference.getTableName().equals(rDto.getTABLE_NAME()) && dtoReference.getColumnName().equals(rDto.getCOLUMN_NAME()) && dtoTable.getTableName().equals(rDto.getREFERENCED_TABLE_NAME()) && dtoColumn.getCOLUMN_NAME().equals(rDto.getREFERENCED_COLUMN_NAME())){
											referencedByObj.put(JsonConstants.RELATIONSHIPTYPE.getJsonContant(),rDto.getRELATIONSHIP_TYPE());
										}
									}
									
									/*jw.name("referencedCol");
									jw.value(dtoColumn.getCOLUMN_NAME());*/
									/*jw.name("referencingTab");
									jw.value(dtoReference.getTableName());*/
									/*jw.name("referencingCol");
									jw.value(dtoReference.getColumnName());*/
									/*jw.name("relationshipType");
									jw.value("----- Coming soon ----");*/
									/*jw.endObject();*/
									referencedBy.add(referencedByObj);
								}
							}
						}
					}

					/*jw.endArray(); // End reference*/
					jo.put(JsonConstants.REFERENCEDBYOBJECT.getJsonContant(), referencedBy);
					
					/*jw.name("data");
					jw.beginArray(); // Data begin array*/	
					JSONArray data = new JSONArray();
					
					for (int temp = 0; temp < nList.getLength(); temp++) {

						Node nNode = nList.item(temp);

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element eElement = (Element) nNode;
							
							if(dtoTable.getTableName().equals(eElement.getAttribute("name"))) {
							
							//jw.beginObject(); // Data begin object
							/*jw.name("tableName");
							jw.value(eElement.getAttribute("name"));*/
							//jw.name("values");
							//jw.beginArray(); // Values begin array

							NodeList rowlist = nList.item(temp).getChildNodes();
							for (int l = 0; l < rowlist.getLength(); l++) {
								Node rowChildList = rowlist.item(l);
								if ("row".equals(rowChildList.getNodeName())) {
									NodeList fieldList = rowlist.item(l).getChildNodes();

									/*jw.beginObject();*/
									JSONObject dataObj = new JSONObject();
									for (int k = 0; k < fieldList.getLength(); k++) {
										Node fieldChildList = fieldList.item(k);
										if ("field".equals(fieldChildList.getNodeName())) {
											Element fElement = (Element) fieldChildList;

											/*jw.name(fElement.getAttribute("name"));
											jw.value(fieldList.item(k).getTextContent().trim());*/
											dataObj.put(fElement.getAttribute("name"), fieldList.item(k).getTextContent().trim());

										}
									}
									/*jw.endObject();*/
									data.add(dataObj);

								}
							}

							//jw.endArray(); // Values end array
							//jw.endObject(); // Data end object
						}
						}

					}
					
					/*jw.endArray(); // Data end array*/
					jo.put(JsonConstants.DATA.getJsonContant(), data);
					
					/*jw.endObject();// begin-end*/		
					jsonArr.add(jo);
					}
			}

			jsonObj.put(JsonConstants.TABLESOBJECT.getJsonContant(), jsonArr);

			/*jw.endObject();*/
			JSONObject json = new JSONObject();
			String updatedson = json.toJSONString(jsonObj);
			FileUtils.writeStringToFile(jsonFiel, updatedson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Map<String,Integer> dataTypeObject(String table){
		ArrayList<ColumnsDTO> dataType = null;
		Map<String,Integer> requestParam = new HashMap<String,Integer>();
		int stringCount = 0;
		int nymericCount = 0;
		int dateCount = 0;
		try {
			MysqlDAO dataDAO = new MysqlDAO();
			dataType = dataDAO.getDataTypeCount(host, port, database, username, password, table);
			for(ColumnsDTO colDto : dataType){
				String key = colDto.getDATA_TYPE();
				if(key.equals("date") || key.equals("datetime") || key.equals("timestamp") || key.equals("time") || key.equals("year")){
					dateCount = dateCount + colDto.getDATA_TYPE_COUNT();
				}else if(key.equals("int") || key.equals("tinyint") || key.equals("smallint") || key.equals("mediumint") || key.equals("bigint") || key.equals("float") || key.equals("double") || key.equals("decimal")){
					nymericCount = nymericCount + colDto.getDATA_TYPE_COUNT();
				} else {
					stringCount = stringCount + colDto.getDATA_TYPE_COUNT();
				}
			}
			
			requestParam.put("STRING", stringCount);
			requestParam.put("NUMERIC", nymericCount);
			requestParam.put("DATE", dateCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestParam;
	}
	

}
