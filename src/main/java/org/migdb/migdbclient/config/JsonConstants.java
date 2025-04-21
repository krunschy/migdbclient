package org.migdb.migdbclient.config;

public enum JsonConstants {
	
	DATABASE("database"),
	TABLENAME("name"),
	COLUMNNAME("colName"),
	DATATYPE("dataType"),
	COLUMNCOUNT("colCount"),
	PRIMARYKEY("primaryKey"),
	COLUMNOBJECT("columns"),
	REFERENCINGFROMOBJECT("referencingFrom"),
	REFERENCEDBYOBJECT("referencedBy"),
	TABLESOBJECT("tables"),
	REFERENCEDCOLUMN("referencedCol"),
	REFERENCEDTABLE("referencedTab"),
	REFERENCINGTABLE("referencingTab"),
	REFERENCINGCOLUMN("referencingCol"),
	RELATIONSHIPTYPE("relationshipType"),
	DATA("data");
	
	private String jsonContant;
	
	JsonConstants(String path) {
		this.jsonContant = path;
	}

	public String getJsonContant() {
		return jsonContant;
	}

}
