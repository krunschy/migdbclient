package org.migdb.migdbclient.views.connectionmanager;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.ConnectionManager;
import org.migdb.migdbclient.config.NotificationConfig;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.controllers.dbconnector.MySQLDbConnManager;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ConnectorDTO;
import org.migdb.migdbclient.utils.MigDBNotifier;
import org.migdb.migdbclient.views.root.RootLayoutController;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetupNewDBConnectionController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private AnchorPane mysqlLayoutAnchorpane;
	@FXML
	private AnchorPane mongoLayoutAnchorpane;
	@FXML
	private TextField connectionNameTextField;
	@FXML
	private TextField mysqlHostTextField;
	@FXML
	private TextField mysqlPortTextField;
	@FXML
	private TextField mysqlUsernameTextField;
	@FXML
	private PasswordField mysqlPasswordTextField;
	@FXML
	private TextField mongoHostTexField;
	@FXML
	private TextField mongoPortTextField;
	@FXML
	private TextField mongoScematextField;
	@FXML
	private Label mysqlLabel;
	@FXML
	private Label mongoLabel;
	@FXML
	private Label mysqlBackLabel;
	@FXML
	private Label mongoBackLabel;
	@FXML
	private Button submitButton;
	@FXML
	private Button testMySQLConnectionButton;
	@FXML
	private Button testMongoConnectionButton;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Set visible false when page loaded
		mysqlLayoutAnchorpane.setVisible(false);
		mongoLayoutAnchorpane.setVisible(false);

		// MySQL label click event
		mysqlLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mysqlPopup();
			}
		});

		// Mongo label click event
		mongoLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mongoPopup();
			}
		});

		// Mysql back label click event
		mysqlBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});

		// Mongo back label click event
		mongoBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});

		submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				insertConnection();
			}
		});

		testMySQLConnectionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				String host = mysqlHostTextField.getText(), database = "", username = mysqlUsernameTextField.getText(),
						password = mysqlPasswordTextField.getText();
				int port = Integer.parseInt(mysqlPortTextField.getText());
				if(testMySQLConnection(host, port, database, username, password)) {
					String title = "MySQL Connection Status";
					String message = "A successful MySQL connection was made with" + "\n"
									+ " the parameters defined for this connection!";
					String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
					int showTime = 6;
					
					MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
					notification.createDefinedNotification();
				} else {
					String title = "MySQL Connection Status";
					String message = "Can't connect to MySQL server with defined \n parameters!";
					String notificationType = NotificationConfig.SHOWERROR.getInfo();
					int showTime = 6;
					
					MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
					notification.createDefinedNotification();
				}
			}
		});
		
		testMongoConnectionButton.setOnAction((event) -> {
			testMongoConnection();
		});
	}

	/**
	 * MySQL layout popup method
	 */
	public void mysqlPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mysqlLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mongo layout popup method
	 */
	public void mongoPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mongoLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Back to root layout anchor pane from mysql or mongo layout
	 * 
	 * @param e
	 */
	public void back(Event e) {
		if ((((Node) e.getSource()).getId()).equals(ConnectionManager.MYSQLBACKLABELID.getConnManager())) {
			mysqlLayoutAnchorpane.setVisible(false);
			rootLayoutAnchorpane.setVisible(true);
		} else {
			mongoLayoutAnchorpane.setVisible(false);
			mysqlLayoutAnchorpane.setVisible(true);
		}
		
	}

	/**
	 * Connector data transfer model
	 * 
	 * @return
	 */
	public ConnectorDTO connectionSave() {
		ConnectorDTO dto = new ConnectorDTO();
		dto.setConnectionName(connectionNameTextField.getText());
		dto.setMysqlHostName(mysqlHostTextField.getText());
		dto.setMongoHostName(mongoHostTexField.getText());
		dto.setMysqlPort(Integer.parseInt(mysqlPortTextField.getText()));
		dto.setMongoPort(Integer.parseInt(mongoPortTextField.getText()));
		dto.setUserName(mysqlUsernameTextField.getText());
		dto.setPassword(mysqlPasswordTextField.getText());
		dto.setSchemaName(mongoScematextField.getText());
		return dto;
	}

	/**
	 * Connection information save method
	 */
	public void insertConnection() {
		try {
			SqliteDAO dao = new SqliteDAO();
			boolean result = dao.insertConnection(connectionSave());
			if (result == true) {
				Stage stage = (Stage) submitButton.getScene().getWindow();
				stage.close();
				
				String title = "Attention";
				String message = "Successfully created!";
				String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
				int showTime = 6;
				
				// Load connection manager fxml after successfully insert connection info
				RootLayoutController rootCtrl = new RootLayoutController();
				rootCtrl.showConnectionManager();
				
				MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
				notification.createDefinedNotification();
			} else {
				String title = "Attention";
				String message = "It seems to be error. Please check again!";
				String notificationType = NotificationConfig.SHOWERROR.getInfo();
				int showTime = 6;
				
				MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
				notification.createDefinedNotification();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test if MySQL connection established or not
	 */
	public boolean testMySQLConnection(String host, int port, 
			String database, String username, String password) {
		boolean result = false;
		MySQLDbConnManager dao = new MySQLDbConnManager();
		Connection dbConn = null;
		try {
			dbConn = dao.getConnection(host, port, 
					database, username, password);
			result = (dbConn != null) ? true:false;
		} catch (Exception e) {
			result = false;
		} finally {
			if(dbConn != null) dao.closeConnection(dbConn);
		}
		return result;
	}
	
	/**
	 * Test if Mongo connection established or not
	 */
	public void testMongoConnection() {
		try {
			String host = mongoHostTexField.getText();
			int port = Integer.parseInt(mongoPortTextField.getText());
			if(!(MongoConnManager.INSTANCE.connect(host, port)).equals("")) {
				String title = "Mongo Connection Status";
				String message = "A successful Mongo connection was made with" + "\n"
						+ " the parameters defined for this connection!";
				String notificationType = NotificationConfig.SHOWSUCCESS.getInfo();
				int showTime = 6;
				
				MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
				notification.createDefinedNotification();
			}
		} catch (Exception e) {
			String title = "MySQL Connection Status";
			String message = "Can't connect to Mongo server with defined \n parameters!";
			String notificationType = NotificationConfig.SHOWERROR.getInfo();
			int showTime = 6;
			
			MigDBNotifier notification = new MigDBNotifier(title, message, notificationType, showTime);
			notification.createDefinedNotification();
		}
	}

}
