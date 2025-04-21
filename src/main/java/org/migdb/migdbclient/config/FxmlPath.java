package org.migdb.migdbclient.config;

public enum FxmlPath {
	
	ROOTLAYOUT("/views/root/RootLayout.fxml"),
	CONNECTIONMANAGER("/views/connectionmanager/ConnectionManager.fxml"),
	MODIFICATIONEVALUATOR("/views/modificationevaluator/ModificationEvaluator.fxml"),
	DATAMANAGER("/views/mongodatamanager/MongoDataManager.fxml"),
	NEWDBCONNECTION("/views/connectionmanager/NewDBConnection.fxml"),
	COLLECTIONMANAGER("/views/mongodatamanager/CollectionManager.fxml"),
	DOCUMENTMANAGER("/views/mongodatamanager/DocumentManager.fxml"),
	QUERYCONVERTER("/views/queryconverter/QueryConverter.fxml"),
	QUERYGENERATOR("/views/queryGenerator/QueryGenerator.fxml"),
	MONGODBPATHBROWSE("/views/filechooser/FileChooserDBPath.fxml"),
	INTERNETCONNECTIVITY("/views/connectivity/InternetConnectivity.fxml"),
	COLLECTIONSTRUCTURE("/views/collectionstructure/CollectionStructure.fxml"),
	NEW_DOC("/views/mongodatamanager/NewDocument.fxml"),
	DBMIGRATOR("/views/dbmigrate/DbMigrater.fxml"),
	MAINWINDOW("/views/connectionmanager/MainWindow.fxml");
	
	private String path;
	
	FxmlPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
