/**
 * 
 */
package org.migdb.migdbclient.config.fromResources.threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author KANI
 *
 */
public class CheckInternetConnectivity extends Thread {

	public void run() {
		while(true){
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						if(!testIneternet("google.com")){
							if(!ConnectivityIsShowInstance.INSTANCE.isShow()){
								ConnectivityIsShowInstance.INSTANCE.setShow(true);
								final Stage dialog = new Stage();
								AnchorPane popup = new AnchorPane();
								popup.setLayoutY(300.0);
								dialog.initModality(Modality.APPLICATION_MODAL);
								dialog.initStyle(StageStyle.TRANSPARENT);
								FXMLLoader loader = new FXMLLoader();
								loader.setLocation(MainApp.class.getResource(FxmlPath.INTERNETCONNECTIVITY.getPath()));
								popup = loader.load();
								Scene scene = new Scene(popup, Color.TRANSPARENT);
								dialog.setScene(scene);
								dialog.centerOnScreen();
								dialog.show();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean testIneternet(String site) {
	    Socket sock = new Socket();
	    InetSocketAddress addr = new InetSocketAddress(site,22);
	    try {
	        sock.connect(addr,3000);
	        return true;
	    } catch (IOException e) {
	        return false;
	    } finally {
	        try {sock.close();}
	        catch (IOException e) {}
	    }
	}
}



