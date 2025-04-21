/**
 *
 */
package org.migdb.migdbclient.views.dbmigrate;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.migdb.migdbclient.controllers.MigrationProcess;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * @author KANI
 *
 */
public class DbMigratorController implements Initializable {

	@FXML
	private ComboBox<String> sqlDatabaseListComboBox;
	@FXML
	private Button migrateButton;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {

		MysqlDAO dao = new MysqlDAO();
		MigrationProcess migrationObj = new MigrationProcess();
		String host = ConnectionParameters.SESSION.getMysqlHostName();
		int port = ConnectionParameters.SESSION.getMysqlPort();
		String database = "";
		String username = ConnectionParameters.SESSION.getUserName();
		String password = ConnectionParameters.SESSION.getPassword();
		ArrayList<String> databases = dao.getDatabases(host, port, database, username, password);

		for (int i = 1; i < databases.size(); i++) {
			sqlDatabaseListComboBox.getItems().add(databases.get(i));
		}

		// migrateButton click action event
		migrateButton.setOnAction((event) -> {
			try {
				Session.INSTANCE.setActiveDB(sqlDatabaseListComboBox.getSelectionModel().getSelectedItem());
				migrationObj.initialize();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

}
