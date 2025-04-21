package org.migdb.migdbclient.models.mongodatamanager;

import java.util.ArrayList;

public class ObjectContainer implements JsonGeneratable {

	private ArrayList<JsonGeneratable> objectContainer = new ArrayList<>();
	
	public ArrayList<JsonGeneratable> getObjectContainer() {
		return objectContainer;
	}
	
	public void setObjectContainer(ArrayList<JsonGeneratable> objectContainer) {
		this.objectContainer = objectContainer;
	}
	
	@Override
	public String generateJson() {
		String result = "{";
		int i =1;
		for (JsonGeneratable item : objectContainer) {
			result += item.generateJson();
			if (i++ != objectContainer.size()) {
				result += " , ";
			}
			// add comma
		}
		return result + "}";
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
