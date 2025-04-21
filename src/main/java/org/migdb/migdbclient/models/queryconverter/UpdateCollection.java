package org.migdb.migdbclient.models.queryconverter;

import java.util.LinkedHashMap;

public class UpdateCollection {

	private String collectionName;
	private LinkedHashMap<String, Object> pairs;

	public UpdateCollection(String collectionName) {
		this.collectionName = collectionName;
		this.pairs = new LinkedHashMap<String, Object>();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public LinkedHashMap<String, Object> getPairs() {
		return pairs;
	}

	public void setPairs(LinkedHashMap<String, Object> pairs) {
		this.pairs = pairs;
	}
	
	public void addColumn(String key, String dataType) {
		LinkedHashMap<String, Object> pair = new LinkedHashMap<String, Object>();
		if(dataType.equalsIgnoreCase("int")) {
			pair.put(key, "NumberInt(\"<Value>\")");
		} else if(dataType.equalsIgnoreCase("bigint")) {
			pair.put(key, "NumberLong(\"<Value>\")");
		} else if(dataType.equals("numeric")) {
			pair.put(key, "<Value>");
		} else if (dataType.equals("date")) {
			pair.put(key, "new Date()");
		} else {
			pair.put(key, "\"<Value>\"");
		}
		this.pairs.put("$set", pair);
	}
	
	public void dropColumn(String key) {
		LinkedHashMap<String, Object> pair = new LinkedHashMap<String, Object>();
		pair.put(key, "\"\"");
		this.pairs.put("$unset", pair);
	}

	@Override
	public String toString() {
		String str = pairs.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }");
		return "db."+collectionName+".update( \n\t{ }, \n\t"+str+", \n\t{ multi: true } \n)";
	}
	
	
 }
