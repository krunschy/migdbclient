package org.migdb.migdbclient.config.fromResources.widgets;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Confirmation {

	private String title;
	private String header;
	private String content;
	public Confirmation(String title, String header, String content) {
		super();
		this.title = title;
		this.header = header;
		this.content = content;
	}
	public Optional<ButtonType> showAndWait(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Are you ok with this?");

		Optional<ButtonType> result = alert.showAndWait();
		return result;
	}
}
