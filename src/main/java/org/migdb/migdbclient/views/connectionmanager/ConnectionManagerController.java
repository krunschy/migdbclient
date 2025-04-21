package org.migdb.migdbclient.views.connectionmanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.ImagePath;
import org.migdb.migdbclient.config.NotificationConfig;
import org.migdb.migdbclient.controllers.MigrationProcess;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ConnectorDTO;
import org.migdb.migdbclient.config.fromResources.Session;
import org.migdb.migdbclient.config.fromResources.widgets.Confirmation;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.LayoutInstance;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;
import org.migdb.migdbclient.utils.MigDBNotifier;
import org.migdb.migdbclient.views.mongodatamanager.MongoDataManager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConnectionManagerController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private AnchorPane connectionAnchorpane = new AnchorPane();
	@FXML
	private Label addConnectionLabel;

	private TreeView<String> mysqlTree;
	private TreeView<String> mongoTree;
	private ProgressIndicator loadingIndicator = new ProgressIndicator();

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Add connection label click event
		addConnectionLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				createNewConnectionPopup();
			}
		});

		connectionInfoSet();
		
		loadingIndicator.setPrefSize(40.0, 40.0);

	}

	/**
	 * Create new connection manager pop up window method
	 */
	public void createNewConnectionPopup() {

		try {
			final Stage dialog = new Stage();
			AnchorPane popup = new AnchorPane();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.setTitle("Create a new connection");
			dialog.setResizable(false);
			dialog.centerOnScreen();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.NEWDBCONNECTION.getPath()));
			popup = loader.load();
			Scene scene = new Scene(popup);
			dialog.setScene(scene);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void connectionInfoSet() {
		String connName, uName, mysqlHost, mongoHost, mysqlpassword, schema = null;
		int mysqlPort, mongoPort = 0;
		Double x = 10.0;
		Double y = 10.0;

		SqliteDAO dao = new SqliteDAO();
		ArrayList<ConnectorDTO> connection = dao.getConnectionInfo();

		if (!connection.isEmpty()) {
			for (ConnectorDTO dto : connection) {
				connName = dto.getConnectionName();
				uName = dto.getUserName();
				mysqlHost = dto.getMysqlHostName();
				mongoHost = dto.getMongoHostName();
				mysqlPort = dto.getMysqlPort();
				mongoPort = dto.getMongoPort();
				mysqlpassword = dto.getPassword();
				schema = dto.getSchemaName();

				connectionAnchorpane.getChildren().add(displayConnectionInfo(x, y, connName, uName, mysqlHost,
						mongoHost, mysqlPort, mongoPort, mysqlpassword, schema));

				if (x > 800) {
					x = 10.0;
					y = y + 120.0;
				} else {
					x = x + 120;
				}
			}
		} else {
			Label noConn = new Label("No connections available");
			noConn.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
			connectionAnchorpane.getChildren().add(noConn);
			// connectionAnchorpane.setStyle("-fx-background-image:url(/org/migdb/migdbclient/resources/images/connection_add_hover.png)");
		}
	}

	public VBox displayConnectionInfo(Double x, Double y, String connName, String uName, String mysqlHost,
			String mongoHost, int mysqlPort, int mongoPort, String password, String schema) {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(8);
		vbox.setLayoutX(x);
		vbox.setLayoutY(y);
		vbox.setStyle("-fx-background-color: #237f4e;");

		Text connectionName = new Text(connName);
		connectionName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		vbox.getChildren().add(connectionName);

		Label userName = new Label(uName, new Glyph("FontAwesome", FontAwesome.Glyph.USER));
		userName.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
		Label mysqlHostPort = new Label(mysqlHost + ":" + mysqlPort, new Glyph("FontAwesome", FontAwesome.Glyph.RSS));
		mysqlHostPort.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
		Label mongoHostPort = new Label(mongoHost + ":" + mongoPort, new Glyph("FontAwesome", FontAwesome.Glyph.RSS));
		mongoHostPort.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

		vbox.getChildren().add(userName);
		vbox.getChildren().add(mysqlHostPort);
		vbox.getChildren().add(mongoHostPort);

		// Mouse clicked event
		vbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				try {
					if (mouseevent.getButton().equals(MouseButton.PRIMARY)) {
						ConnectionParameters.SESSION.setConnectionName(connName);
						ConnectionParameters.SESSION.setUserName(uName);
						ConnectionParameters.SESSION.setMysqlHostName(mysqlHost);
						ConnectionParameters.SESSION.setMongoHostName(mongoHost);
						ConnectionParameters.SESSION.setMysqlPort(mysqlPort);
						ConnectionParameters.SESSION.setMongoPort(mongoPort);
						ConnectionParameters.SESSION.setPassword(password);
						ConnectionParameters.SESSION.setSchemaName(schema);

						SetupNewDBConnectionController cntrl = new SetupNewDBConnectionController();
						if (cntrl.testMySQLConnection(mysqlHost, mysqlPort, schema, uName, password)) {
							// Load main stage after instance make active
							// connection
							FXMLLoader loader = new FXMLLoader();
							loader.setLocation(MainApp.class.getResource(FxmlPath.MAINWINDOW.getPath()));
							AnchorPane mainWindowAnchorPane = loader.load();
							rootLayoutAnchorpane = CenterLayout.INSTANCE.getRootContainer();
							rootLayoutAnchorpane.getChildren().clear();
							rootLayoutAnchorpane.getChildren().add(mainWindowAnchorPane);
							setSideBarDatabases();
						} else {
							String title = "Attention";
							String message = "It seems to be error. Please check your connection \n info again!";
							String notificationType = NotificationConfig.SHOWERROR.getInfo();
							int showTime = 6;

							MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
							notification.createDefinedNotification();
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Mouse entered event
		// Add hover effect
		vbox.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				vbox.setStyle("-fx-background-color: #65A583;");
			}
		});

		// Mouse exit event
		// Remove hover effect
		vbox.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				vbox.setStyle("-fx-background-color: #237f4e;");
			}
		});

		return vbox;
	}

	public void setSideBarDatabases() {

		try {

			MysqlDAO dao = new MysqlDAO();
			MigrationProcess migrationObj = new MigrationProcess();

			String host = ConnectionParameters.SESSION.getMysqlHostName();
			int port = ConnectionParameters.SESSION.getMysqlPort();
			String database = "";
			String username = ConnectionParameters.SESSION.getUserName();
			String password = ConnectionParameters.SESSION.getPassword();

			final Node dbIcon = new ImageView(new Image(getClass().getResourceAsStream(ImagePath.DBICON.getPath())));
			final Node dbIcon2 = new ImageView(new Image(getClass().getResourceAsStream(ImagePath.DBICON.getPath())));
			TreeItem<String> mysqlItem = new TreeItem<String>("MySQL Databases", dbIcon);
			TreeItem<String> mongoItem = new TreeItem<String>("Mongo Databases", dbIcon2);
			mysqlItem.setExpanded(true);
			mongoItem.setExpanded(true);

			mysqlTree = new TreeView<String>(mysqlItem);
			mongoTree = new TreeView<String>(mongoItem);
			mysqlTree.getStyleClass().add("sidebar tree-cell:selected");

			// MYSQL tree item context menu
			ContextMenu mysqlContext = new ContextMenu();
			MenuItem migrate = new MenuItem("Migrate");
			migrate.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					try {
						Session.INSTANCE.setActiveDB(mysqlTree.getSelectionModel().getSelectedItem().getValue());
						migrationObj.initialize();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
			mysqlContext.getItems().add(migrate);

			// Mongo tree item context menu
			ContextMenu mongoContext = new ContextMenu();
			MenuItem select = new MenuItem("Select");
			select.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					try {
						MongoDBResource.INSTANCE.setDB(mongoTree.getSelectionModel().getSelectedItem().getValue());
						showMongoDataManager();
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			});
			MenuItem drop = new MenuItem("Drop");
			drop.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					try {
						String dbName = mongoTree.getSelectionModel().getSelectedItem().getValue();
						Confirmation confirmation = new Confirmation("Confirmation Dialog", "Drop database " + dbName,
								"Command : Drop " + dbName);
						Optional<ButtonType> result = confirmation.showAndWait();
						if (result.get() == ButtonType.OK) {
							System.out.println("Ok");
							MongoDBResource.INSTANCE.setDB(dbName);
							MongoDatabase database = MongoDBResource.INSTANCE.getDatabase();
							database.drop();
							setSideBarDatabases();
						} else {
							System.out.println("cancel");
						}
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			});
			mongoContext.getItems().addAll(select, drop);
			//loadingIndicator.setVisible(true);
			mysqlTree.setContextMenu(mysqlContext);
			mongoTree.setContextMenu(mongoContext);
			VBox sidebarVbox = new VBox();
			sidebarVbox.setStyle("-fx-pref-height: 613;");
			sidebarVbox.getChildren().addAll(mysqlTree, mongoTree);

			VBox sidebar;
			sidebar = LayoutInstance.INSTANCE.getSidebar();
			sidebar.getChildren().clear();
			sidebar.setAlignment(Pos.CENTER);
			sidebar.getChildren().add(loadingIndicator);

			// loads the items at another thread, asynchronously
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(2000); // just emulates some loading time
						ArrayList<String> databases = dao.getDatabases(host, port, database, username, password);
						for (int i = 1; i < databases.size(); i++) {
							TreeItem<String> item = new TreeItem<String>(databases.get(i));
							mysqlItem.getChildren().add(item);
						}
						ArrayList<String> mongoDatabses = (ArrayList<String>) getDatabaseNames();
						for (int k = 0; k < mongoDatabses.size(); k++) {
							TreeItem<String> mongoDB = new TreeItem<String>(mongoDatabses.get(k));
							mongoItem.getChildren().add(mongoDB);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// just updates the list view items at the
						// Application Thread
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								sidebar.getChildren().clear();
								sidebar.getChildren().add(sidebarVbox);
							}
						});
					}
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<String> getDatabaseNames() throws Exception {
		List<String> dbs = new ArrayList<String>();
		String host = ConnectionParameters.SESSION.getMongoHostName();
		int port = ConnectionParameters.SESSION.getMongoPort();
		MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
		MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
		while (dbsCursor.hasNext()) {
			dbs.add(dbsCursor.next());
		}
		return dbs;
	}

	@FXML
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
			e.printStackTrace();
		}

	}

}
