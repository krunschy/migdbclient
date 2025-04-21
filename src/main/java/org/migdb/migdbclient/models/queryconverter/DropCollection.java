package org.migdb.migdbclient.models.queryconverter;

public class DropCollection {

	String collectionName;

	public DropCollection(String collectionName) {
		this.collectionName = collectionName;
	}

	@Override
	public String toString() {
		return "db."+ collectionName +".drop()";
	}
	
	
}
