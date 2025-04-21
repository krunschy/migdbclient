/**
 * 
 */
package org.migdb.migdbclient.controllers;

import java.io.IOException;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.CenterLayout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * @author Kani
 *
 */
public class MigrationProcess {
	
	@FXML
	public void initialize() throws IOException {
		
		DumpGenerator obj = new DumpGenerator();
		obj.generateDump();
		
		CardinalityMap cmObj = new CardinalityMap();
		cmObj.cardinalityAnalyze();
		
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.MODIFICATIONEVALUATOR.getPath()));
		AnchorPane modificationEvaluator = loader.load();
		root.getChildren().clear();
		root.getChildren().add(modificationEvaluator);
		
	}
	
}
