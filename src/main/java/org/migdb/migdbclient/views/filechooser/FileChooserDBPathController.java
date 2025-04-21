/**
 * 
 */
package org.migdb.migdbclient.views.filechooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.dao.SqliteDAO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author HP
 *
 */
public class FileChooserDBPathController implements Initializable {
	
	@FXML
	private TextField browseDbPathTextField;
	@FXML
	private Button browseDbPathButton;
	@FXML
	private Button browseDbPathOkButton;
	@FXML
	private Button browseDbPathCancelButton;
	@FXML
	private CheckBox browseDbPathRememberCheckbox;
	
	MainApp applicationInstance = new MainApp();
	
	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {
		
		browseDbPathOkButton.setOnAction(event -> dbPathBrowseOkButtonAction());
		
		browseDbPathCancelButton.setOnAction(event -> closeStage());
		
		browseDbPathButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser fileChooser = new DirectoryChooser();
				fileChooser.setTitle("Mongo database path chooser");
				//Show open file dialog
	              File selectedDirectory = fileChooser.showDialog(null);
	              browseDbPathTextField.clear();
	              String directoryPath = (selectedDirectory.isDirectory() && selectedDirectory != null) ? selectedDirectory.getAbsolutePath() : "" ;
	              browseDbPathTextField.setText(directoryPath);
			}
		});
		
	}
	
	private void closeStage() {
		Stage stage = (Stage) browseDbPathCancelButton.getScene().getWindow();
		stage.close();
	}
	
	private void dbPathBrowseOkButtonAction() {
		SqliteDAO dao = new SqliteDAO();
		String dbPath = browseDbPathTextField.getText();
		int isDefault = (browseDbPathRememberCheckbox.isSelected())? 1 : 0 ;
		String dbType = "MongoDbPath";
		if(isDefault != 0){
			dao.insertMongoDbPath(dbPath, isDefault, dbType);
		}
		closeStage();
		applicationInstance.showMainStage();
	}

}
