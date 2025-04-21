package org.migdb.migdbclient.models.mongodatamanager;

import javafx.scene.control.TextField;

public class KeyValueContainer implements JsonGeneratable{

	private TextField key;
	private TextField value;
		
	public KeyValueContainer(TextField key, TextField value) {
		super();
		this.key = key;
		this.value = value;
	}


	@Override
	public String generateJson() {
		//null validation
		String result = "\""+key.getText()+"\" : \""+value.getText()+"\"";
		return result;
	}


	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
