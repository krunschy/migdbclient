package org.migdb.migdbclient.tablegen;

import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


public class CustomCellFactory<S,T> implements Callback<TableColumn.CellDataFeatures<S,T>, ObservableValue<String>> {

    private final String property;

    private Class<?> columnClass;
    private String previousProperty;

    public CustomCellFactory(@NamedArg("property") String property) {
        this.property = property;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<S, T> param) {
        return getCellDataReflectively(param.getValue());
    }

    public final String getProperty() { return this.property; }

	private ObservableValue<String> getCellDataReflectively(S rowData) {
        if (getProperty() == null || getProperty().isEmpty() || rowData == null) return null;


        try {
            // we attempt to cache the property reference here, as otherwise
            // performance suffers when working in large data models.
            if (columnClass == null || previousProperty == null ||
                    ! columnClass.equals(rowData.getClass()) ||
                    ! previousProperty.equals(getProperty())) {

                // create a new PropertyReference
                this.columnClass = rowData.getClass();
                this.previousProperty = getProperty();
            }

            return ((TableBean)rowData).getCellValue(getProperty());

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return null;
    }

}
