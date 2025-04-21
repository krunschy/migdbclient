package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class DocumentManager implements Initializable {
	@FXML
	private AnchorPane documentAncPane;
	@FXML
	private Label collectionNameLabel;
	@FXML
	private Label objectIdLabel;
	@FXML
	private TreeView<String> documentTreeView;
	@FXML
	private Button backButton;
	public MongoCollection<Document> collection;
	private static String collectionName;
	private String documentId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setDocument(MongoCollection<Document> collection, String name, String id) {
		DocumentManager.collectionName = name;
		this.collection = collection;
		this.documentId = id;
		System.out.println(collectionName + " " + documentId);
		showDocument();
	}

	@FXML
	public void showDocument() {
		collectionNameLabel.setText(collectionName);
		objectIdLabel.setText(documentId);

		MongoCursor<Document> cursor = collection.find(new Document("_id", documentId)).iterator();
		Document document;
		if (cursor.hasNext()) {
			document = cursor.tryNext();
		} else {
			Document filter;
			try {
				filter = new Document("_id", new ObjectId(documentId));
			} catch (NoSuchElementException e) {
				filter = new Document("_id", documentId);
			} catch (IllegalArgumentException e) {
				filter = new Document("_id", documentId);
			}
			cursor = collection.find(filter).iterator();
			document = cursor.next();
		}

		Set<Entry<String, Object>> entrySet = document.entrySet();
		TreeItem<String> root = new TreeItem<String>("_id : " + document.get("_id").toString());
		root.setExpanded(true);
		resolveDocument(document, root);
		documentTreeView.setRoot(root);
		documentTreeView.setEditable(true);

		documentTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new RenameMenuTreeCell(documentTreeView);
			}
		});
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

	private void resolveDocument(Document document, TreeItem<String> parent) {
		Set<Entry<String, Object>> entrySet = document.entrySet();

		for (Entry<String, Object> entry : entrySet) {

			if (entry.getValue() instanceof Document) {
				TreeItem<String> newParent = new TreeItem<String>(entry.getKey());
				resolveDocument((Document) entry.getValue(), newParent);
				newParent.setExpanded(true);
				parent.getChildren().add(newParent);

			} else if (entry.getValue() instanceof ArrayList) {
				ArrayList<Object> arrayList = (ArrayList<Object>) entry.getValue();
//				System.out.println(arrayList);
				int count = arrayList.size();
				TreeItem<String> newParent = new TreeItem<String>(entry.getKey()+" ["+count+"]");
				resolveArrayList((ArrayList<Object>) entry.getValue(), newParent);
				newParent.setExpanded(true);
				parent.getChildren().add(newParent);
			} else if (entry.getValue() instanceof ObjectId) {
				System.out.println("object id found");
			} else {
				parent.getChildren().add(new TreeItem<String>(entry.getKey() + " : " + entry.getValue()));
			}

		}
	}

	private void resolveArrayList(ArrayList<Object> list, TreeItem<String> parent) {
		int c=0;
		for (Object object : list) {
			
			if (object instanceof Document) {
				TreeItem<String> newParent = new TreeItem<String>(c+"");
				resolveDocument((Document) object, newParent);
				newParent.setExpanded(false);
				parent.getChildren().add(newParent);
			} else if (object instanceof ArrayList) {
				resolveArrayList((ArrayList<Object>) object, parent);
			} else if (object instanceof ObjectId) {
				parent.getChildren().add(new TreeItem<String>("_id : " + object.toString()));
			} else {
				parent.getChildren().add(new TreeItem<String>(object.toString()));
			}
			c++;
		}
	}

	private static class RenameMenuTreeCell extends TextFieldTreeCell<String> {
		private ContextMenu menu = new ContextMenu();
		private TreeView<String> tree = null;

		public RenameMenuTreeCell(TreeView<String> tree) {
			super(new DefaultStringConverter());
			this.tree = tree;

			MenuItem renameItem = new MenuItem("Edit");
			menu.getItems().add(renameItem);
			renameItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					startEdit();
				}
			});
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (!isEditing()) {
				setContextMenu(menu);
			}
		}

		@Override
		public void commitEdit(String newValue) {

			// if(getItem().contains(":")) {
			if (this.tree.getSelectionModel().getSelectedItem().isLeaf()) {
				String key = getItem().substring(0, getItem().indexOf(":") - 1);
				String value = newValue.substring(newValue.indexOf(":") + 1, newValue.length());
				String keyValue = key + " : " + newValue;
				String rootValue = this.tree.getRoot().getValue();
				String id = rootValue.substring(rootValue.indexOf(":") + 2, rootValue.length());
				super.commitEdit(keyValue);
				String path = getPath(this.tree, this.tree.getSelectionModel().getSelectedItem().getParent(),"");
				// update the real database
//				 System.out.println("key ="+key);
//				 System.out.println("value ="+value);
//				 System.out.println("keyValue ="+keyValue);
//				 System.out.println("id ="+id);
				MongoDatabase db = MongoDBResource.INSTANCE.getDatabase();
				MongoCollection<Document> collection = db.getCollection(collectionName);
				System.out.println(path);
				if (path.equals("/")) {

					System.out.println(collection.updateOne(new Document("_id", id), new Document("$set", new Document(key, value))));
				} else {
//					collection.updateOne(new Document("_id", id), new Document("$set", new Document(path+"."+key, value)));
				}

			} else {
				System.out.println("You can not edit this field!!!");
			}
		}

		private String getPath(TreeView<String> treeView, TreeItem<String> item, String path) {
//			String path = "";
			if (item.getParent()==null) {
				path = "/" + path;
			} else {
				if (path.equals("")) {
					path = item.getValue();
				} else {
					path =  getFirstWord(item.getValue())+"."+path;
				}
				getPath(treeView, item.getParent(),path);
			}
			return path;
		}
		private String getFirstWord(String text) {
		    if (text.indexOf(' ') > -1) {
		      return text.substring(0, text.indexOf(' ')); 
		    } else {
		      return text;
		    }
		  }

	}

}
