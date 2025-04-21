package org.migdb.migdbclient.models.queryconverter;

import java.util.LinkedHashMap;

public class InsertDocument {

	private String collectionName;
	private LinkedHashMap<String, Object> pairs;

	public InsertDocument(String collectionName) {
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
	
	public void addPair(String key, Object value) {
		this.pairs.put(key, value);
		if(value instanceof Integer) {
			System.out.println("int");
		}
	}

	public void addPair(int index, Object value) {
		this.pairs.put("<Key"+index+">", value);
	}

	@Override
	public String toString() {
		String str = pairs.toString().replace("=", ": ").replace(", ", ",\n\t").replace("{", "{\n\t")
				.replace("}", "\n}");
		return "db."+collectionName+".insert("+str+")";
	}
	
	
	
	
}
