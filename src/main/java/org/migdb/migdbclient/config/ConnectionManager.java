package org.migdb.migdbclient.config;

public enum ConnectionManager {
	
	MYSQLBACKLABELID("mysqlBackLabel"),MONGOBACKLABELID("mongoBackLabel");
	
	private String connManager;
	
	ConnectionManager(String connManager){
		this.connManager = connManager;
	}

	public String getConnManager() {
		return connManager;
	}

}
