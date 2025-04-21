package org.migdb.migdbclient.models.dto;

public class ConnectorDTO {
	
	private String connectionName;
	private String mysqlHostName;
	private String mongoHostName;
	private int mysqlPort;
	private int mongoPort;
	private String userName;
	private String password;
	private String schemaName;
	
	public ConnectorDTO() {
	}

	/**
	 * Constructor method
	 * @param connectionName
	 * @param mysqlHostName
	 * @param mongoHostName
	 * @param mysqlPort
	 * @param mongoPort
	 * @param userName
	 * @param password
	 * @param schemaName
	 */
	public ConnectorDTO(String connectionName, String mysqlHostName, String mongoHostName, int mysqlPort, int mongoPort,
			String userName, String password, String schemaName) {
		super();
		this.connectionName = connectionName;
		this.mysqlHostName = mysqlHostName;
		this.mongoHostName = mongoHostName;
		this.mysqlPort = mysqlPort;
		this.mongoPort = mongoPort;
		this.userName = userName;
		this.password = password;
		this.schemaName = schemaName;
	}

	/**
	 * Get connection name method
	 * @return
	 */
	public String getConnectionName() {
		return connectionName;
	}

	/**
	 * Set connection name method
	 * @param connectionName
	 */
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	/**
	 * Get MySQL host name method
	 * @return
	 */
	public String getMysqlHostName() {
		return mysqlHostName;
	}

	/**
	 * Set MySQL host name method
	 * @param mysqlHostName
	 */
	public void setMysqlHostName(String mysqlHostName) {
		this.mysqlHostName = mysqlHostName;
	}

	/**
	 * Get MongoDB host name method
	 * @return
	 */
	public String getMongoHostName() {
		return mongoHostName;
	}

	/**
	 * Set MongoDB host name method
	 * @param mongoHostName
	 */
	public void setMongoHostName(String mongoHostName) {
		this.mongoHostName = mongoHostName;
	}

	/**
	 * Get MySQL port method
	 * @return
	 */
	public int getMysqlPort() {
		return mysqlPort;
	}

	/**
	 * Set MySQL port method
	 * @param mysqlPort
	 */
	public void setMysqlPort(int mysqlPort) {
		this.mysqlPort = mysqlPort;
	}

	/**
	 * Get MongoDB port method
	 * @return
	 */
	public int getMongoPort() {
		return mongoPort;
	}

	/**
	 * Set MongoDB port method
	 * @param mongoPort
	 */
	public void setMongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
	}

	/**
	 * Get user name method
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set user name method
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get password method
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password method
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get schema name method
	 * @return
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * Set schema name method
	 * @param schemaName
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}
