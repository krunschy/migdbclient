package org.migdb.migdbclient.models.queryconverter;

import java.util.LinkedHashMap;

public class CreateDocumentCollection extends CreateCollection{

	private LinkedHashMap<String, Object> pairs;

	public CreateDocumentCollection(String collectionName) {
		super(collectionName);
		this.pairs = new LinkedHashMap<String, Object>();
	}

	public LinkedHashMap<String, Object> getPairs() {
		return pairs;
	}

	public void setPairs(LinkedHashMap<String, Object> pairs) {
		this.pairs = pairs;
	}
	
	public void addPair(String key, String dataType) {
		if(dataType.equalsIgnoreCase("int")) {
			this.pairs.put(key, "NumberInt(\"<Value>\")");
		} else if(dataType.equalsIgnoreCase("bigint")) {
			this.pairs.put(key, "NumberLong(\"<Value>\")");
		} else if(dataType.equals("numeric")) {
			this.pairs.put(key, "<Value>");
		} else if (dataType.equals("date")) {
			this.pairs.put(key, "new Date(\"<Value>\")");
		} else {
			this.pairs.put(key, "\"<Value>\"");
		}
	}
	
	public void addReferencePair(String referencedCollection, String key) {
		LinkedHashMap<String, Object> refPairs = new LinkedHashMap<String, Object>();
		refPairs.put("$ref", referencedCollection);
		refPairs.put("$id", "ObjectId(\"<Id_Value>\")");
		this.pairs.put(key, refPairs);
	}

	@Override
	public String toString() {
		String str = pairs.toString().replace("=", ": ").replace(", ", ",\n\t").replace("$", "\t$");
		return "db."+collectionName+".insert("+str.replace("{", "{\n\t").replace("}", "\n}")
				.replace("},", "\t},")+")";
	}
	
	
}
