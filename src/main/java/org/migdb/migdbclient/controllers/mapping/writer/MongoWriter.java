package org.migdb.migdbclient.controllers.mapping.writer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.NotificationConfig;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.utils.MigDBNotifier;
import org.migdb.migdbclient.views.connectionmanager.ConnectionManagerController;
import org.migdb.migdbclient.views.mongodatamanager.MongoDataManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MongoWriter {

	private JSONObject mappedJson;

	public MongoWriter() {
		super();
		JSONParser parser = new JSONParser();
		try {
			Object object = parser
					.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			mappedJson = (JSONObject) object;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write() {
		getNewName();
	}

	public void getNewName() {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Enter a Name to the Migrated schema");

		VBox dialogVbox = new VBox(20);
		dialogVbox.setPadding(new Insets(20, 0, 20, 20));
		dialogVbox.setStyle("-fx-background-color : white");
		dialogVbox.setAlignment(Pos.CENTER);

		Label label = new Label("Database Name ");
		TextField txtCollectionName = new TextField();

		HBox detailHbox = new HBox(30);

		detailHbox.getChildren().addAll(label, txtCollectionName);
		HBox btnHbox = new HBox(30);
		btnHbox.setAlignment(Pos.CENTER);
		Button btnAdd = new Button("Create");
		btnAdd.setPrefWidth(100);
		btnAdd.setOnAction((ActionEvent e) -> {
			String databaseName = txtCollectionName.getText();
			if (!databaseName.equals("")) {
				try {
					if (validateDatabaseName(databaseName)) {
						writeToDB(databaseName);
						dialog.close();
					}
					else{
						String title = "Attention";
						String message = "The Name Already Exsists.Please Enter a new name to create database";
						String notificationType = NotificationConfig.SHOWERROR.getInfo();
						int showTime = 6;

						MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
						notification.createDefinedNotification();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				String title = "Attention";
				String message = "Please Enter a name to create collection";
				String notificationType = NotificationConfig.SHOWERROR.getInfo();
				int showTime = 6;

				MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
				notification.createDefinedNotification();
			}

		});
		Button btnCancel = new Button("Cancel");
		btnCancel.setPrefWidth(100);
		btnCancel.setOnAction((ActionEvent e) -> {
			dialog.close();
		});
		btnHbox.getChildren().addAll(btnCancel, btnAdd);
		dialogVbox.getChildren().addAll(detailHbox, btnHbox);

		Scene dialogScene = new Scene(dialogVbox, 350, 150);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	public boolean validateDatabaseName(String name) throws Exception {
		boolean result = true;
		String host = ConnectionParameters.SESSION.getMongoHostName();
		int port = ConnectionParameters.SESSION.getMongoPort();
		MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
		MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
		while (dbsCursor.hasNext()) {
			if (dbsCursor.next().equals(name)) {
				result = false;
			}
		}
		return result;
	}

	private void writeToDB(String name) {
		try {
			String mongoHost = ConnectionParameters.SESSION.getMongoHostName();
			int mongoPort = ConnectionParameters.SESSION.getMongoPort();
			MongoConnManager.INSTANCE.connect(mongoHost, mongoPort);
			
			System.out.println(mappedJson);
			JSONArray collections = (JSONArray) mappedJson.get("collections");
			System.out.println(collections);

			MongoDBResource.INSTANCE.setDB(name);
			MongoDatabase database = MongoDBResource.INSTANCE.getDatabase();
			for (int i = 0; i < collections.size(); i++) {
				JSONObject collection = (JSONObject) collections.get(i);
				JSONArray documents = (JSONArray) collection.get("data");
				for (int j = 0; j < documents.size(); j++) {

					Document document = Document.parse(documents.get(j).toString());
					database.getCollection(collection.get("collectionName").toString()).insertOne(document);
				}
				System.out.println(collections.get(i));
			}
			String title = "Success";
			String message = "Database migration successfully completed";
			String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
			int showTime = 6;
			
			//Load side bar after migrated database creation successfully
			ConnectionManagerController connCtrl = new ConnectionManagerController();
			connCtrl.setSideBarDatabases();

			MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
			notification.createDefinedNotification();
			
			showMongoDataManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void showMongoDataManager() throws Exception {
		
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
		}

	}
}
