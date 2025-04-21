package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bson.Document;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.mongodatamanager.ArrayContainer;
import org.migdb.migdbclient.models.mongodatamanager.EmbededObjectContainer;
import org.migdb.migdbclient.models.mongodatamanager.JsonGeneratable;
import org.migdb.migdbclient.models.mongodatamanager.KeyValueContainer;
import org.migdb.migdbclient.models.mongodatamanager.ObjectContainer;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NewDocument implements Initializable{
	@FXML
	private Button addKeyValue;
	@FXML
	private Button addArray;
	@FXML
	private Button addObject;
	@FXML
	private Button backButton;
	
	@FXML private VBox container;
	
	private ObjectContainer objectContainer;
	private String collectionName;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		objectContainer = new ObjectContainer();
	}
	
	@FXML
	public void addKeyValue(){
		VBox currentContainer = this.container;
		EmbededObjectContainer eo = null;
		for(JsonGeneratable s : objectContainer.getObjectContainer()) {
			if(s.isSelected()) {
				eo = ((EmbededObjectContainer) s);
				currentContainer = eo.getContainer();
			}
		}
		
		Label label = new Label("Key Value :  ");
		TextField key = new TextField();
		key.setPromptText("Key");
		
		TextField value = new TextField();
		value.setPromptText("Value");
		KeyValueContainer childContainer = new KeyValueContainer(key, value);
		if(eo != null) {
			eo.getObjectContainer().add(childContainer);
			label.setText(eo.getKeyValue());
		}else {
			objectContainer.getObjectContainer().add(childContainer);
		}
		
		
		HBox box = new HBox();
		box.getChildren().add(label);
		box.getChildren().add(key);
		box.getChildren().add(value);
		
		currentContainer.getChildren().add(box);
	}
	
	@FXML
	public void addArray(){
		VBox currentContainer = this.container;
		EmbededObjectContainer eo = null;
		for(JsonGeneratable s : objectContainer.getObjectContainer()) {
			if(s.isSelected()) {
				eo = ((EmbededObjectContainer) s);
				currentContainer = eo.getContainer();
			}
		}
		Label label = new Label("Array List  :  ");
		TextField key = new TextField();
		key.setPromptText("Key");
		TextField value = new TextField();
		value.setPromptText("Value");
		ArrayContainer arrayContainer = new ArrayContainer(key, value);
		
		if(eo != null) {
			eo.getObjectContainer().add(arrayContainer);
			label.setText(eo.getKeyValue());
		}else {
			objectContainer.getObjectContainer().add(arrayContainer);
		}
		
		HBox box = new HBox();
		box.getChildren().add(label);
		box.getChildren().add(key);
		box.getChildren().add(value);
		currentContainer.getChildren().add(box);
	}
	
	@FXML
	public void addEmbeddedObject() {
		EmbededObjectContainer embededObject = new EmbededObjectContainer();
		objectContainer.getObjectContainer().add(embededObject);
		
		container.getChildren().add(embededObject.getView());
	}
	
	@FXML
	public void getJson() {
		System.out.println(objectContainer.generateJson());
		MongoDatabase db = MongoDBResource.INSTANCE.getDatabase();
		MongoCollection<Document> collection = db.getCollection(collectionName);
		collection.insertOne(Document.parse(objectContainer.generateJson()));
		showCollectionManger();
	}
	
	public void setNewDocument(String name){
		this.collectionName = name;
	}
	
	@FXML
	public void showCollectionManger() {
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane mongoCollectionManagerAncPane;
		try {
			mongoCollectionManagerAncPane = loader.load();
			CollectionManager collectionManager = (CollectionManager) loader.getController();
			collectionManager.setCollection(collectionName);
			root.getChildren().clear();
			root.getChildren().add(mongoCollectionManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
