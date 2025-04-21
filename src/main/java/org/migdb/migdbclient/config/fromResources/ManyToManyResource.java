package org.migdb.migdbclient.config.fromResources;

import org.json.simple.JSONObject;

public enum ManyToManyResource {

	INSTANCE;
	private JSONObject table1;
	private JSONObject table1Info;
	private JSONObject table2;
	private JSONObject table2Info;
	private JSONObject mappingTable;
	
	
	public JSONObject getTable1() {
		return table1;
	}
	public void setTable1(JSONObject table1) {
		this.table1 = table1;
	}
	public JSONObject getTable2() {
		return table2;
	}
	public void setTable2(JSONObject table2) {
		this.table2 = table2;
	}
	public JSONObject getMappingTable() {
		return mappingTable;
	}
	public void setMappingTable(JSONObject mappingTable) {
		this.mappingTable = mappingTable;
	}
	public JSONObject getTable1Info() {
		return table1Info;
	}
	public void setTable1Info(JSONObject table1Info) {
		this.table1Info = table1Info;
	}
	public JSONObject getTable2Info() {
		return table2Info;
	}
	public void setTable2Info(JSONObject table2Info) {
		this.table2Info = table2Info;
	}
	
	
}
