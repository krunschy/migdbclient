package org.migdb.migdbclient.tablegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class TableBean {
	private Map<String, String> var = new HashMap<>();

     public TableBean() {
		// TODO Auto-generated constructor stub
	}

    public Set<String> getProperties() {
        return this.var.keySet();
    }

    public void setCellData(String property, String value) {
        var.put(property, value);
    }

    public ObservableValue<String> getCellValue(String property) {
       return new SimpleStringProperty(var.get(property));
    }
}
