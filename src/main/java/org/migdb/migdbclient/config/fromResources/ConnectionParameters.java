package org.migdb.migdbclient.config.fromResources;

public enum ConnectionParameters {
	SESSION;
	
	private String connectionName;
	private String mysqlHostName;
	private String mongoHostName;
	private int mysqlPort;
	private int mongoPort;
	private String userName;
	private String password;
	private String schemaName;
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getMysqlHostName() {
		return mysqlHostName;
	}
	public void setMysqlHostName(String mysqlHostName) {
		this.mysqlHostName = mysqlHostName;
	}
	public String getMongoHostName() {
		return mongoHostName;
	}
	public void setMongoHostName(String mongoHostName) {
		this.mongoHostName = mongoHostName;
	}
	public int getMysqlPort() {
		return mysqlPort;
	}
	public void setMysqlPort(int mysqlPort) {
		this.mysqlPort = mysqlPort;
	}
	public int getMongoPort() {
		return mongoPort;
	}
	public void setMongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}
