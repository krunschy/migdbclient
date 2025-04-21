/**
 * 
 */
package org.migdb.migdbclient.views.connectionmanager;

import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.threads.CheckInternetConnectivity;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * @author KANI
 *
 */
public class MainWindow implements Initializable {
	
	@FXML
	private Button getStartedButton;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {
		
		getStartedButton.addEventHandler(ActionEvent.ACTION, event -> getStarted() );
		
		/*CheckInternetConnectivity internetStatus = new CheckInternetConnectivity();

		//Check internet connectivity in separate tread
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000); // just emulates some loading time
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// just updates the list view items at the
					// Application Thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							while(true) {
								try {
									
								} catch (Exception e2) {
									// TODO: handle exception
								}
								System.out.println(internetStatus.testIneternet("google.com"));
								
							}
						}
					});
				}
			}
		}).start();*/

	}
	
	/**
	 * Method for action perform in get started button
	 */
	public void getStarted() {
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

}
