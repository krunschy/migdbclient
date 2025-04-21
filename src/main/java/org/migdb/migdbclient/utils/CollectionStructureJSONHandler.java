package org.migdb.migdbclient.utils;

import org.json.simple.JSONObject;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;


public class CollectionStructureJSONHandler {

    public synchronized void save(ChangeStructure changeStructure){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodeDataArray",changeStructure.nodeDataArray);
        jsonObject.put("linkDataArray",changeStructure.linkDataArray);
        String jsonString = jsonObject.toJSONString();
        jsonObject.put("jsonContent",jsonString);
        String filename = ServiceAccessor.saveCollectionStructureJSON(jsonObject);
        if(filename.length() > 0){
            changeStructure.jsonFileName = filename;
        }
    }


}
