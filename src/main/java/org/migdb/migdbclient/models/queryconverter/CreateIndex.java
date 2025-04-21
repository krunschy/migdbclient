package org.migdb.migdbclient.models.queryconverter;

import java.util.LinkedHashMap;

public class CreateIndex {

	private String collectionName;
	private String indexName;
	private LinkedHashMap<String, Object> indexPairs;

	public CreateIndex(String collectionName, String indexName) {
		this.collectionName = collectionName;
		this.indexName = indexName;
		this.indexPairs = new LinkedHashMap<String, Object>(); 
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public LinkedHashMap<String, Object> getIndexPairs() {
		return indexPairs;
	}

	public void setIndexPairs(LinkedHashMap<String, Object> indexPairs) {
		this.indexPairs = indexPairs;
	}
	
	public void addIndex(String colName, int order) {
		this.indexPairs.put(colName, order);
	}

	@Override
	public String toString() {
		String str = indexPairs.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }");
		return "db."+collectionName+".createIndex(\n\t"+str+",\n\t{ name: \""+indexName+"\" }\n)";
	}
	
	
	
}
