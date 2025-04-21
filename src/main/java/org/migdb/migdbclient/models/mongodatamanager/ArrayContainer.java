package org.migdb.migdbclient.models.mongodatamanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.TextField;

public class ArrayContainer implements JsonGeneratable{

	TextField key;
	ArrayList<TextField> array = new ArrayList<>();
	
	
	
	public ArrayContainer(TextField key, TextField value) {
		super();
		this.key = key;
		this.array.add(value);
	}
	
	public void addValue(TextField field){
		this.array.add(field);
	}



	@Override
	public String generateJson() {
		String result = "\""+key.getText()+"\" : [\"";
		int i =1;
		String st = array.get(0).getText();
		List<String> items = Arrays.asList(st.split(","));
		for(String item : items){
			
			// validate comma
			result += item;
			if (i++ != array.size()) {
				result += "\" , \"";
			}
		}
		result += "\"]";
		return result;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
