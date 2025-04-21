package org.migdb.migdbclient.models.queryconverter;

public class CreateCollection {

	String collectionName;

	public CreateCollection(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	@Override
	public String toString() {
		return "db.createCollection(\""+collectionName+"\")";
	}
	
	
}
