package org.migdb.migdbclient.config;

public enum DbConnector {
	
	SQLITECONNECTIONURL("jdbc:sqlite:INTERNAL_DB.db");
	
	private String connector;
	
	DbConnector(String connector) {
		this.connector = connector;
	}

	public String getConnector() {
		return connector;
	}

}