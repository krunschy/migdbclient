package org.migdb.migdbclient.views.root;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.LayoutInstance;
import org.migdb.migdbclient.views.mongodatamanager.MongoDataManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RootLayoutController implements Initializable {

	@FXML
	private AnchorPane rootContainerAncpane;
	@FXML
	private VBox sideBarAnchorpane;
	@FXML
	private Label datamanagerLabel;
	@FXML
	private Label connectionManagerLabel;
	@FXML
	private Label modificationEvaluatorLabel;
	@FXML
	private Label queryConverterLabel;
	@FXML
	private Label queryGeneratorLabel;
	@FXML
	private ListView<String> mongoDatabaseList;
	@FXML
	private ContextMenu mongoDatabaseContextMenu;
	@FXML
	private MenuItem menubarCloseMenuItem;
	@FXML
	private Hyperlink menubarManualHyperlink;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {

		CenterLayout.INSTANCE.setRoot(rootContainerAncpane);
		LayoutInstance.INSTANCE.setSidebar(sideBarAnchorpane);
		
		/*connectionManagerLabel.setGraphic(new ImageView(ImagePath.TABDBCONNECTION.getPath()));
		modificationEvaluatorLabel.setGraphic(new ImageView(ImagePath.TABMIGRATION.getPath()));
		queryConverterLabel.setGraphic(new ImageView(ImagePath.TABCONVERTER.getPath()));
		queryGeneratorLabel.setGraphic(new ImageView(ImagePath.TABGENERATOR.getPath()));
		datamanagerLabel.setGraphic(new ImageView(ImagePath.TABDATAMANAGER.getPath()));*/
		
		
		showConnectionManager();

//		ObservableList<String> list;
//		try {
//			list = FXCollections.observableArrayList(getDatabaseNames());
//			mongoDatabaseList.setItems(list);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// Data manager navigation label click event
		datamanagerLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				MongoDBResource.INSTANCE.setDB(ConnectionParameters.SESSION.getSchemaName());
				showDataManager();
			}
		});

		// Connection manager navigation label click event
		connectionManagerLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showConnectionManager();
			}
		});

		// Connection manager navigation label click event
		modificationEvaluatorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				
				showDbMigrator();
			}
		});

		// Query converter navigation label click event
		queryConverterLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showQueryConverter();
			}
		});

		// Query generator navigation label click event
		queryGeneratorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showQueryGenerator();
			}
		});
		
		// Menu bar manual hyperlink click event
		menubarManualHyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
					openManualWebpage("http://migdb.org/help.html");
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
	}

	/**
	 * Method for add connection manager layout to the root container anchor
	 * pane
	 */
	public void showConnectionManager() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.CONNECTIONMANAGER.getPath()));
			AnchorPane connectionManager = loader.load();
			root.getChildren().clear();
			root.getChildren().add(connectionManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add modification evaluator layout to the root container anchor
	 * pane
	 */
	public void showDbMigrator() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.DBMIGRATOR.getPath()));
			AnchorPane dbMigrator = loader.load();
			root.getChildren().clear();
			root.getChildren().add(dbMigrator);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add data manager layout to the root container anchor pane
	 */
	public void showDataManager() {
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.DATAMANAGER.getPath()));
		AnchorPane mongoDataManagerAncPane;
		try {
			mongoDataManagerAncPane = loader.load();
			MongoDataManager dataManager = (MongoDataManager) loader.getController();
			dataManager.setDatabase(MongoDBResource.INSTANCE.getDatabaseName());
			root.getChildren().clear();
			root.getChildren().add(mongoDataManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method for add query converter layout to the root container anchor pane
	 */
	public void showQueryConverter() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.QUERYCONVERTER.getPath()));
			AnchorPane queryConverter = loader.load();
			root.getChildren().clear();
			root.getChildren().add(queryConverter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add query generator layout to the root container anchor pane
	 */
	public void showQueryGenerator() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.QUERYGENERATOR.getPath()));
			AnchorPane queryGenerator = loader.load();
			root.getChildren().clear();
			root.getChildren().add(queryGenerator);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void showMongoDataManager() throws Exception {
		String databaseName = mongoDatabaseList.getSelectionModel().getSelectedItem();
		MongoDBResource.INSTANCE.setDB(databaseName);
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.DATAMANAGER.getPath()));
		AnchorPane mongoDataManagerAncPane;
		try {
			mongoDataManagerAncPane = loader.load();
			MongoDataManager dataManager = (MongoDataManager) loader.getController();
			dataManager.setDatabase(databaseName);
			root.getChildren().clear();
			root.getChildren().add(mongoDataManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Platform close action
	 * Set to close menu item action event
	 */
	@FXML
	public void closePlatform() {
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Do you want to terminate the application");
			alert.setContentText("Are you ok with this?");
			
			Optional<ButtonType> buttonAction = alert.showAndWait();
			if(buttonAction.get().equals(ButtonType.OK)) Platform.exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to open web page which has manual
	 * @param url
	 * @throws URISyntaxException
	 */
	public void openManualWebpage(String url) {
	    try {
	    	Desktop.getDesktop().browse(new URI(url));
	    } catch (IOException | URISyntaxException e) {
	        e.printStackTrace();
	    }
	}

//	public List<String> getDatabaseNames() throws Exception {
//		List<String> dbs = new ArrayList<String>();
//		String host = ConnectionParameters.SESSION.getMongoHostName();
//		int port = ConnectionParameters.SESSION.getMongoPort();
//		MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
//		MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
//		while (dbsCursor.hasNext()) {
//			dbs.add(dbsCursor.next());
//		}
//		return dbs;
//	}

}
