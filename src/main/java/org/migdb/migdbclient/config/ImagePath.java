package org.migdb.migdbclient.config;

public enum ImagePath {
	
	FAVICON("/images/MigDB.png"),
	DBICON("/images/dbicon.png"),
	TABDBCONNECTION("/images/DBConn.png"),
	TABMIGRATION("/images/Migrate.jpg"),
	TABCONVERTER("/images/Convert.jpeg"),
	TABGENERATOR("/images/Generator.png"),
	TABDATAMANAGER("/images/Manager.jpeg"),
	SPLASHIMAGE("/images/Splash_Image.jpg");
	
	private String path;
	
	ImagePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
