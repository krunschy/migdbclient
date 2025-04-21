package org.migdb.migdbclient.models.mongodatamanager;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EmbededObjectContainer extends ObjectContainer {

	private TextField key;
	private VBox container;
	private CheckBox check;
	
	public EmbededObjectContainer() {
		this.key = new TextField();
		this.container = new VBox();
		check = new CheckBox();
	}
	
	@Override
	public String generateJson() {
		return "\"" + key.getText() + "\" :" + super.generateJson(); 
	}
	
	public VBox getView() {
		Label label = new Label("Object : ");
		HBox box = new HBox();
		box.getChildren().add(label);
		box.getChildren().add(check);
		box.getChildren().add(key);
		container.getChildren().add(box);
		return this.container;
	}

	@Override
	public boolean isSelected() {
		return this.check.isSelected();
	}
	
	public VBox getContainer() {
		if(check.isSelected()) {
			return container;
		}
		return null;
	}
	
	public String getKeyValue() {
		return this.key.getText();
	}
}
