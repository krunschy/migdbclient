package org.migdb.migdbclient.controllers;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;
import org.migdb.migdbclient.config.fromResources.DataSetUpdateRequestMessage;
import org.migdb.migdbclient.utils.ServiceAccessor;

public class UpdateDataSet {

	public ChangeStructure changeStructure = ChangeStructure.getInstance();

	public List<String> getChanges(){
	List<String> changes = new ArrayList<String>();
	for(int i = 0; i < changeStructure.linkDataArray.size(); i++){
		JSONObject relationship = (JSONObject) changeStructure.linkDataArray.get(i);
		String original = relationship.get("originalvalue").toString();
		String modified = relationship.get("toText").toString();
		if(!original.equalsIgnoreCase(modified)){
			String code = relationship.get("from").toString()+"#"+
												relationship.get("toText");
			changes.add(code);
		}
	}

	return changes;
	}

	public void updateDataSet(){
		List<String> changes = getChanges();
		if(changes.size() > 0){
			try{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			JSONObject sqlJson = (JSONObject) obj;
			JSONArray sqlTables = (JSONArray) sqlJson.get("tables");
				for(int j = 0; j < changes.size(); j++){
					String[] tableNameAndMapping = changes.get(j).split("#",2);
					for(int i = 0; i < sqlTables.size(); i++){
						JSONObject table = (JSONObject)sqlTables.get(i);
						if(table.get("name").equals(tableNameAndMapping[0])){
							String columnCount = table.get("colCount").toString();
							JSONObject dataTypeCounts = (JSONObject)table.get("dataTypeCount");
							String stringCount = dataTypeCounts.get("STRING_COUNT").toString();
							String calenderCount = dataTypeCounts.get("DATE_COUNT").toString();
							String numericCount = dataTypeCounts.get("NUMERIC_COUNT").toString();
							String mappingModel = tableNameAndMapping[1];
							DataSetUpdateRequestMessage message = new DataSetUpdateRequestMessage("alfaclient", columnCount,
									numericCount, stringCount, calenderCount, mappingModel);
							ServiceAccessor.updatedDataSet(message);
						}
					}
				}
			}
			catch(Exception ex){

			}
		}

	}


}
