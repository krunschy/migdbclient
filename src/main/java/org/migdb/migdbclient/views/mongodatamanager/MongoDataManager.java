package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.bson.Document;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.NotificationConfig;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;
import org.migdb.migdbclient.utils.MigDBNotifier;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MongoDataManager implements Initializable {
	@FXML
	private Button btnAddCollection;
	@FXML
	private ListView<String> collectionList;
	@FXML
	private ListView<String> databaseList;
	@FXML
	private AnchorPane rootAncPane;
	@FXML
	private Label databaseNameLabel;

	private MongoDatabase db;
	// private String databaseName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/*
		 * try { db = MongoConnManager.INSTANCE.connectToDatabase("test"); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } try { displayCollections(); } catch (Exception
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * try { ObservableList<String> list =
		 * FXCollections.observableArrayList(getDatabaseNames());
		 * databaseList.setItems(list);
		 * collectionList.setItems(getCollectionNames());
		 * 
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } System.out.println(databaseName);
		 */

		/*
		 * try { // get list of collections MongoConnManager connManager = new
		 * MongoConnManager(); DB db = connManager.connect(); databaseResource
		 * databaseResource = new databaseResource();
		 * databaseResource.setCollections(db);
		 * System.out.println(databaseResource.getCollections());
		 * 
		 * collectionList.getItems().addAll(databaseResource.getCollections());
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	@FXML
	public void display() throws Exception {
		System.out.println("button clicked");

		MongoCollection<Document> collection = db.getCollection("zips");

		List<Document> foundDocument = collection.find().into(new ArrayList<Document>());

		foundDocument.stream().forEach(e -> {
			System.out.println(e.toJson());
		});

		// System.out.println(databaseName);

	}

	@FXML
	public void testHandler() {
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane collectionManager;
		try {
			collectionManager = loader.load();
			root.getChildren().clear();
			root.getChildren().add(collectionManager);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	public void showCollectionManger() {
		System.out.println(collectionList.getSelectionModel().getSelectedItem());
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane mongoCollectionManagerAncPane;
		try {
			mongoCollectionManagerAncPane = loader.load();
			CollectionManager collectionManager = (CollectionManager) loader.getController();
			String st = collectionList.getSelectionModel().getSelectedItem();
			collectionManager.setCollection(st.substring(0, st.indexOf(' ')));
			root.getChildren().clear();
			root.getChildren().add(mongoCollectionManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	public void showNewCollection() {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Add New Collection");

		VBox dialogVbox = new VBox(20);
		dialogVbox.setPadding(new Insets(20, 0, 20, 20));
		dialogVbox.setStyle("-fx-background-color : white");
		dialogVbox.setAlignment(Pos.CENTER);

		Label label = new Label("Collection Name ");
		TextField txtCollectionName = new TextField();

		HBox detailHbox = new HBox(30);

		detailHbox.getChildren().addAll(label, txtCollectionName);
		HBox btnHbox = new HBox(30);
		btnHbox.setAlignment(Pos.CENTER);
		Button btnAdd = new Button("Create");
		btnAdd.setPrefWidth(100);
		btnAdd.setOnAction((ActionEvent e) -> {
			String collectionname = txtCollectionName.getText();
			System.out.println(collectionname);
			if (!collectionname.equals("")) {
				addNewCollection(collectionname);
				dialog.close();
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

	@FXML
	public void deleteCollection() {
		String name = collectionList.getSelectionModel().getSelectedItem().toString().split(" ", 2)[0];
		MongoDatabase db = MongoDBResource.INSTANCE.getDatabase();

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Delete Collection");
		alert.setContentText("Command = db." + name + ".drop() \n Are you sure?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// ... user chose OK
			MongoCollection<Document> collection = db.getCollection(name);
			collection.drop();
			String title = "Attention";
			String message = "Successfully Deleted!";
			String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
			int showTime = 6;

			MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
			notification.createDefinedNotification();
			System.out.println(name);
			refreshDataManager();
			// System.out.println(MongoResult.toString());
		} else {
			// ... user chose CANCEL or closed the dialog
		}

	}

	private void addNewCollection(String name) {
		MongoDatabase db = MongoDBResource.INSTANCE.getDatabase();
		try {
			db.createCollection(name);
			refreshDataManager();
			String title = "Attention";
			String message = "Successfully created!";
			String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
			int showTime = 6;

			MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
			notification.createDefinedNotification();
		} catch (MongoCommandException ex) {
			ex.printStackTrace();
			String title = "Attention";
			String message = "Collection Already Exsist";
			String notificationType = NotificationConfig.SHOWERROR.getInfo();
			int showTime = 6;

			MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
			notification.createDefinedNotification();
		}
	}

	private ObservableList<String> getCollectionsOf(String dbName) throws Exception {
		ObservableList<String> list = FXCollections.observableArrayList();
		databaseNameLabel.setText(dbName);
		MongoDatabase db = MongoConnManager.INSTANCE.connectToDatabase(dbName);
		MongoCursor<String> cursor = db.listCollectionNames().iterator();
		while (cursor.hasNext()) {
			String st = cursor.next();
			if (!st.equals("system.indexes")) {
				MongoCollection<Document> collection = db.getCollection(st);
				int count = (int) collection.countDocuments();
				list.add(st + "   (" + count + " Documents)");
			}
		}
		System.out.println(list);
		return list;

	}

	public void setDatabase(String databaseName) throws Exception {
		try {
			collectionList.setItems(getCollectionsOf(MongoDBResource.INSTANCE.getDatabaseName()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(databaseName);

	}

	public void refreshDataManager() {
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

}
