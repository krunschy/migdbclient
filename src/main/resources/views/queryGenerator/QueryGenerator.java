/**
 * 
 */
package org.migdb.migdbclient.views.queryGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.QueryDocumentDTO;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.MongoDBResource;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * @author KANI
 *
 */
public class QueryGenerator implements Initializable {

	@FXML
	private ComboBox<String> queryDatabaseListComboBox;
	@FXML
	private ComboBox<String> queryCollectionListComboBox;
	@FXML
	private ComboBox<String> queryFieldListComboBox;
	@FXML
	private ComboBox<String> queryOperatorsComboBox;
	@FXML
	private Button queryBuildButton;
	@FXML
	private Button addQueryParamButton;
	@FXML
	private TextField queryValuesTextField;
	@FXML
	private TextField queryLimitTextField;
	@FXML
	private TextField querySkipTextField;
	@FXML
	private ComboBox<String> querySortColumnComboBox;
	@FXML
	private ComboBox<String> querySortOrderComboBox;
	@FXML
	private TextArea outputQuery;
	@FXML
	private TableView<QueryDocumentDTO> queryParametersTableView;
	@FXML
	private TabPane queryGeneratorTabPane;

	@FXML
	private TableColumn<QueryDocumentDTO, String> fieldTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> operatorsTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> valuesTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, QueryDocumentDTO> removeTableColumn;
	/*
	 * @FXML private TableColumn<QueryDocumentDTO, String> conditionTableColumn;
	 */

	/**
	 * Text Search Tab
	 */
	@FXML
	private ComboBox<String> textSearchDatabaseComboBox;
	@FXML
	private ComboBox<String> textSearchCollectionComboBox;
	@FXML
	private ComboBox<String> textSearchFieldComboBox;
	@FXML
	private CheckBox textSearchOldVersionCheckBox;
	@FXML
	private Label textSearchTextIndexLabel;
	@FXML
	private TextField textSearchKeywordTextField;
	@FXML
	private Button textSearchCreateButton;
	@FXML
	private Button textSearchViewButton;
	@FXML
	private Button textSearchDropButton;
	/*********************************************/

	/*
	 * Aggregation Tab
	 */
	@FXML
	private Label aggregationHavingTypeLabel;
	
	@FXML
	private ComboBox<String> aggregationDatabaseComboBox;
	@FXML
	private ComboBox<String> aggregationColectionComboBox;
	@FXML
	private ComboBox<String> aggregationFieldsComboBox;
	@FXML
	private ComboBox<String> aggregationOperatorsComboBox;
	@FXML
	private ComboBox<String> aggregationGroupbyComboBox;
	@FXML
	private ComboBox<String> aggregationSortColumnComboBox;
	@FXML
	private ComboBox<String> aggregationSortOrderComboBox;
	@FXML
	private ComboBox<String> aggregationSumFieldComboBox;
	@FXML
	private ComboBox<String> aggregationHavingOperatorComboBox;
	
	@FXML
	private GridPane aggregationHavingGrid;

	private final ToggleGroup aggregationRadioGroup = new ToggleGroup();
	@FXML
	private RadioButton aggregationCountRadioButton;
	@FXML
	private RadioButton aggregationSumRadioButton;

	@FXML
	private TableView<QueryDocumentDTO> aggregationParametersTableView;
	@FXML
	private TableColumn<QueryDocumentDTO, String> aggregationFieldTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> aggregationOperatorsTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> aggregationValuesTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, QueryDocumentDTO> aggregationRemoveTableColumn;

	@FXML
	private TextField aggregationValueTextField;
	@FXML
	private TextField aggregationHavingTextField;

	@FXML
	private Button aggregationAddButton;
	@FXML
	private Button aggregationShowQueryButton;
	/*********************************************/

	MongoCollection<Document> collectionDocument;
	List<Document> selectedDocument;
	private MongoDatabase db;
	private String collectionFElementHolder;
	private int counter;

	private SqliteDAO dao;

	private ObservableList<QueryDocumentDTO> queryParams = FXCollections.observableArrayList();
	private final ObservableList<String> conditionList = FXCollections.observableArrayList("AND", "OR");

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		queryParametersTableView.setEditable(true);
		aggregationSumFieldComboBox.setVisible(false);
		aggregationHavingGrid.setVisible(false);
		
		// aggregationRadioGroup's radio button click event
		aggregationRadioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle old_toggle, Toggle new_toggle) {
		            if (aggregationRadioGroup.getSelectedToggle() != null) {
		            	RadioButton aggregationType = (RadioButton) aggregationRadioGroup.getSelectedToggle();
		            	aggregationHavingTypeLabel.setText(aggregationType.getText());
		            	aggregationHavingGrid.setVisible(true);
		            	if(aggregationType.getText().toLowerCase().toString().equals("count")){
		            		aggregationSumFieldComboBox.setVisible(false);
		            	} else {
		            		aggregationSumFieldComboBox.setVisible(true);
		            	}
		            }
		        }
		});
		

		// Set database name list to the databaseListComboBox
		ArrayList<String> mongoDatabases = (ArrayList<String>) getMongoDatabases();
		for (int i = 0; i < mongoDatabases.size(); i++) {
			queryDatabaseListComboBox.getItems().add(mongoDatabases.get(i));
			textSearchDatabaseComboBox.getItems().add(mongoDatabases.get(i));
			aggregationDatabaseComboBox.getItems().add(mongoDatabases.get(i));
		}
		queryDatabaseListComboBox.getSelectionModel().select(0);
		textSearchDatabaseComboBox.getSelectionModel().select(0);
		aggregationDatabaseComboBox.getSelectionModel().select(0);

		// Set collection list according to a selected database when ui loaded
		queryCollectionListComboBox.getItems()
				.addAll(getCollectionOf(queryDatabaseListComboBox.getSelectionModel().getSelectedItem()));
		queryCollectionListComboBox.getSelectionModel().select(0);

		// Set values to variable when initializing a UI
		MongoDBResource.INSTANCE.setDB(queryDatabaseListComboBox.getSelectionModel().getSelectedItem().toString());
		db = MongoDBResource.INSTANCE.getDatabase();
		collectionDocument = db
				.getCollection(queryCollectionListComboBox.getSelectionModel().getSelectedItem().toString());
		selectedDocument = collectionDocument.find().into(new ArrayList<Document>());

		textSearchCollectionComboBox.getItems()
				.addAll(getCollectionOf(textSearchDatabaseComboBox.getSelectionModel().getSelectedItem()));
		textSearchCollectionComboBox.getSelectionModel().select(0);

		aggregationColectionComboBox.getItems()
				.addAll(getCollectionOf(aggregationDatabaseComboBox.getSelectionModel().getSelectedItem()));
		aggregationColectionComboBox.getSelectionModel().select(0);

		/**
		 * databaseListComboBox changed event When changing
		 * databaseListComboBox, according to that one set list of collections
		 * to the collectionListComboBox
		 */
		queryDatabaseListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				counter = 0;
				collectionFElementHolder = getCollectionOf(
						queryDatabaseListComboBox.getSelectionModel().getSelectedItem()).get(0).toString();
				MongoDBResource.INSTANCE
						.setDB(queryDatabaseListComboBox.getSelectionModel().getSelectedItem().toString());
				db = MongoDBResource.INSTANCE.getDatabase();
				collectionDocument = db
						.getCollection(queryCollectionListComboBox.getSelectionModel().getSelectedItem().toString());
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
			}
		});

		// Text search database combo box change event
		textSearchDatabaseComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				counter = 0;
				collectionFElementHolder = getCollectionOf(
						textSearchDatabaseComboBox.getSelectionModel().getSelectedItem()).get(0).toString();
				MongoDBResource.INSTANCE
						.setDB(textSearchDatabaseComboBox.getSelectionModel().getSelectedItem().toString());
				db = MongoDBResource.INSTANCE.getDatabase();
				collectionDocument = db
						.getCollection(textSearchCollectionComboBox.getSelectionModel().getSelectedItem().toString());
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
			}
		});

		// Aggregate database combo box change event
		aggregationDatabaseComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				counter = 0;
				collectionFElementHolder = getCollectionOf(
						aggregationDatabaseComboBox.getSelectionModel().getSelectedItem()).get(0).toString();
				MongoDBResource.INSTANCE
						.setDB(aggregationDatabaseComboBox.getSelectionModel().getSelectedItem().toString());
				db = MongoDBResource.INSTANCE.getDatabase();
				collectionDocument = db
						.getCollection(aggregationColectionComboBox.getSelectionModel().getSelectedItem().toString());
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
			}
		});

		// databaseListComboBox click action event
		queryDatabaseListComboBox.setOnAction((event) -> {
			queryCollectionListComboBox.getItems().clear();
			queryCollectionListComboBox.getItems()
					.addAll(getCollectionOf(queryDatabaseListComboBox.getSelectionModel().getSelectedItem()));
			queryCollectionListComboBox.getSelectionModel().select(0);
		});

		// textSearchDatabaseComboBox click action event
		textSearchDatabaseComboBox.setOnAction((event) -> {
			textSearchCollectionComboBox.getItems().clear();
			textSearchCollectionComboBox.getItems()
					.addAll(getCollectionOf(textSearchDatabaseComboBox.getSelectionModel().getSelectedItem()));
			textSearchCollectionComboBox.getSelectionModel().select(0);
		});

		// aggregationDatabaseComboBox click action event
		aggregationDatabaseComboBox.setOnAction((event) -> {
			aggregationColectionComboBox.getItems().clear();
			aggregationColectionComboBox.getItems()
					.addAll(getCollectionOf(aggregationDatabaseComboBox.getSelectionModel().getSelectedItem()));
			aggregationColectionComboBox.getSelectionModel().select(0);
		});

		// Fill queryFieldListComboBox and querySortColumnComboBox Combo boxe's
		// when document combo box change
		if (!selectedDocument.isEmpty()) {
			queryFieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			queryFieldListComboBox.getSelectionModel().select(0);
			textSearchFieldComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			textSearchFieldComboBox.getSelectionModel().select(0);
			aggregationFieldsComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			aggregationFieldsComboBox.getSelectionModel().select(0);
			aggregationGroupbyComboBox.getItems().add("null");
			aggregationGroupbyComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			aggregationGroupbyComboBox.getSelectionModel().select(0);
			aggregationSumFieldComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			aggregationSumFieldComboBox.getSelectionModel().select(0);
			querySortColumnComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			aggregationSortColumnComboBox.getItems().addAll(getCommonColumns(selectedDocument));
		}

		// Fill querySortOrderComboBox Combo box when ui loaded
		querySortOrderComboBox.getItems().addAll("Ascending", "Descending");
		querySortOrderComboBox.getSelectionModel().select(0);

		// Fill aggregationSortOrderComboBox Combo box when ui loaded
		aggregationSortOrderComboBox.getItems().addAll("Ascending", "Descending");
		aggregationSortOrderComboBox.getSelectionModel().select(0);

		/**
		 * collectionListComboBox's value changing event action
		 */
		queryCollectionListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				collectionDocument = (counter <= 0) ? db.getCollection(collectionFElementHolder)
						: db.getCollection((String) comboBoxVal.getValue());
				counter++;
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
				queryFieldListComboBox.getItems().clear();
				querySortColumnComboBox.getItems().clear();
				if (!selectedDocument.isEmpty()) {
					queryFieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					queryFieldListComboBox.getSelectionModel().select(0);
					querySortColumnComboBox.getItems().addAll(getCommonColumns(selectedDocument));
				}

			}
		});

		/**
		 * textSearchCollectionComboBox's value changing event action
		 */
		textSearchCollectionComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				collectionDocument = (counter <= 0) ? db.getCollection(collectionFElementHolder)
						: db.getCollection((String) comboBoxVal.getValue());
				counter++;
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
				textSearchFieldComboBox.getItems().clear();
				if (!selectedDocument.isEmpty()) {
					textSearchFieldComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					textSearchFieldComboBox.getSelectionModel().select(0);
					textSearchTextIndexLabel
							.setText(textSearchFieldComboBox.getSelectionModel().getSelectedItem().toString());

				}

			}
		});

		/**
		 * aggregationColectionComboBox's value changing event action
		 */
		aggregationColectionComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				collectionDocument = (counter <= 0) ? db.getCollection(collectionFElementHolder)
						: db.getCollection((String) comboBoxVal.getValue());
				counter++;
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
				aggregationFieldsComboBox.getItems().clear();
				aggregationGroupbyComboBox.getItems().clear();
				aggregationSumFieldComboBox.getItems().clear();
				if (!selectedDocument.isEmpty()) {
					aggregationFieldsComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					aggregationFieldsComboBox.getSelectionModel().select(0);
					aggregationGroupbyComboBox.getItems().add("null");
					aggregationGroupbyComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					aggregationGroupbyComboBox.getSelectionModel().select(0);
					aggregationSumFieldComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					aggregationSumFieldComboBox.getSelectionModel().select(0);
					aggregationSortColumnComboBox.getItems()
							.addAll(aggregationFieldsComboBox.getSelectionModel().getSelectedItem().toString());

				}

			}
		});

		textSearchFieldComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				textSearchTextIndexLabel.setText(String.valueOf(comboBoxVal.getValue()));
			}
		});

		// Set text search index to label when page loading
		textSearchTextIndexLabel.setText(textSearchFieldComboBox.getSelectionModel().getSelectedItem().toString());

		// Set operators to the operatorsComboBox
		dao = new SqliteDAO();
		queryOperatorsComboBox.getItems().addAll(dao.getQueryOperators());

		// Set operators to the aggregationOperatorsComboBox
		dao = new SqliteDAO();
		aggregationOperatorsComboBox.getItems().addAll(dao.getQueryOperators());
		aggregationHavingOperatorComboBox.getItems().addAll(dao.getQueryOperators());

		// addQueryParamButton button click event
		addQueryParamButton.addEventHandler(ActionEvent.ACTION, event -> addQueryParam());
		
		// aggregationAddButton button click event
				aggregationAddButton.addEventHandler(ActionEvent.ACTION, event -> addAggregationQueryParam());

		// queryBuildButton button click event
		queryBuildButton.addEventHandler(ActionEvent.ACTION, event -> queryBuild());

		// Generate text index button click event
		textSearchCreateButton.addEventHandler(ActionEvent.ACTION, event -> textSearchBuild(event));
		
		// aggregationShowQueryButton button click event
		aggregationShowQueryButton.addEventHandler(ActionEvent.ACTION, event -> aggregationBuild());

		// View text index button click event
		textSearchViewButton.addEventHandler(ActionEvent.ACTION, event -> textSearchBuild(event));

		// Drop text index button click event
		textSearchDropButton.addEventHandler(ActionEvent.ACTION, event -> textSearchBuild(event));

		// Initialize queryParameters table view
		fieldTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Field"));
		operatorsTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Operators"));
		valuesTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Values"));
		removeTableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		removeTableColumn.setCellFactory(param -> new TableCell<QueryDocumentDTO, QueryDocumentDTO>() {
			private final Button deleteButton = new Button("Remove");

			@Override
			protected void updateItem(QueryDocumentDTO dtoItem, boolean empty) {
				super.updateItem(dtoItem, empty);

				if (dtoItem == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> queryParams.remove(dtoItem));
			}
		});

		// Initialize aggregationTableView table view
		aggregationFieldTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Field"));
		aggregationOperatorsTableColumn
				.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Operators"));
		aggregationValuesTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Values"));
		aggregationRemoveTableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		aggregationRemoveTableColumn.setCellFactory(param -> new TableCell<QueryDocumentDTO, QueryDocumentDTO>() {
			private final Button deleteButton = new Button("Remove");

			@Override
			protected void updateItem(QueryDocumentDTO dtoItem, boolean empty) {
				super.updateItem(dtoItem, empty);

				if (dtoItem == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> queryParams.remove(dtoItem));
			}
		});

		/*
		 * conditionTableColumn.setCellValueFactory(new
		 * PropertyValueFactory<QueryDocumentDTO, String>("condition"));
		 * conditionTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
		 * conditionList)); conditionTableColumn.setOnEditCommit(new
		 * EventHandler<CellEditEvent<QueryDocumentDTO, String>>() {
		 * 
		 * @Override public void handle(CellEditEvent<QueryDocumentDTO, String>
		 * t) { ((QueryDocumentDTO)
		 * t.getTableView().getItems().get(t.getTablePosition().getRow()))
		 * .setCondition(t.getNewValue()); }; });
		 */

		// force the queryLimitTextField Text field to be numeric only
		queryLimitTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					queryLimitTextField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		// force the querySkipTextField Text field to be numeric only
		querySkipTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					querySkipTextField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		/* ****************** Aggregation tab ****************** */

		// Set toggle group to radio button
		aggregationCountRadioButton.setToggleGroup(aggregationRadioGroup);
		aggregationSumRadioButton.setToggleGroup(aggregationRadioGroup);

	}

	/**
	 * Method for building query
	 */
	public void queryBuild() {
		try {
			String dbName = queryDatabaseListComboBox.getSelectionModel().getSelectedItem();
			String collection = queryCollectionListComboBox.getSelectionModel().getSelectedItem();
			ObservableList<QueryDocumentDTO> param = (ObservableList<QueryDocumentDTO>) queryParametersTableView
					.getItems();

			int limit = (!queryLimitTextField.getText().isEmpty()) ? Integer.parseInt(queryLimitTextField.getText())
					: 0;
			int skip = (!querySkipTextField.getText().isEmpty()) ? Integer.parseInt(querySkipTextField.getText()) : 0;
			String sortField = (querySortColumnComboBox.getSelectionModel().getSelectedItem() != null)
					? querySortColumnComboBox.getSelectionModel().getSelectedItem().toString() : "";
			int sortOrder = (querySortOrderComboBox.getSelectionModel().getSelectedItem()).equals("Ascending") ? 1 : -1;

			outputQuery.clear();
			outputQuery.setEditable(false);

			MongoDatabase db = MongoConnManager.INSTANCE.connectToDatabase(dbName);
			BasicDBObject javaQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();

			if (param.size() >= 1) {
				for (QueryDocumentDTO dto : param) {
					dao = new SqliteDAO();
					String operator = dao.getQueryOperatorsKeyword(dto.getOperators().toString());

					if (dao.getQueryOperatorsKeyword(dto.getOperators().toString()).equals("")) {
						obj.add(new BasicDBObject(dto.getField(), dto.getValues()));
					} else {
						obj.add(new BasicDBObject(dto.getField(), new BasicDBObject(operator, dto.getValues())));
					}

				}

				javaQuery.put("$and", obj);
			}

			FindIterable<Document> iterable = null;
			BasicDBObject sortObj = new BasicDBObject(sortField, sortOrder);

			if (limit == 0 && skip == 0 && sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty()\n");
			} else if (limit != 0 && skip == 0 && sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).limit(limit);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText(
						"db." + collection + ".find(" + javaQuery.toString() + ").pretty().limit(" + limit + ")\n");
			} else if (limit == 0 && skip != 0 && sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).skip(skip);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText(
						"db." + collection + ".find(" + javaQuery.toString() + ").pretty().skip(" + skip + ")\n");
			} else if (limit == 0 && skip == 0 && !sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).sort(sortObj);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty().sort({'"
						+ sortField + "':" + sortOrder + "})\n");
			} else if (limit != 0 && skip != 0 && sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).limit(limit)
						.skip(skip);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty().limit("
						+ limit + ").skip(" + skip + ")\n");
			} else if (limit != 0 && skip == 0 && !sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).limit(limit)
						.sort(sortObj);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty().limit("
						+ limit + ").sort({'" + sortField + "':" + sortOrder + "})\n");
			} else if (limit == 0 && skip != 0 && !sortField.isEmpty()) {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).skip(skip)
						.sort(sortObj);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty().skip(" + skip
						+ ").sort({'" + sortField + "':" + sortOrder + "})\n");
			} else {
				iterable = (FindIterable<Document>) db.getCollection(collection).find(javaQuery).limit(limit).skip(skip)
						.sort(sortObj);
				outputQuery.appendText(
						"-------------------------------------------------------------------------------------- MongoDB Command -------------------------------------------------------------------------------------\n\n");
				outputQuery.appendText("db." + collection + ".find(" + javaQuery.toString() + ").pretty().limit("
						+ limit + ").skip(" + skip + ").sort({'" + sortField + "':" + sortOrder + "})\n");
			}

			outputQuery.appendText(
					"--------------------------------------------------------------------------------------             Result             -------------------------------------------------------------------------------------\n\n");
			iterable.forEach(new Block<Document>() {
				@Override
				public void apply(final Document document) {
					outputQuery.appendText(document.toJson() + "\n");
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for building text search queries
	 */
	private void textSearchBuild(ActionEvent event) {
		try {

			Button btn = (Button) event.getSource();

			String dbName = textSearchDatabaseComboBox.getSelectionModel().getSelectedItem();
			String collection = textSearchCollectionComboBox.getSelectionModel().getSelectedItem();
			String[] keywordTextArray = (textSearchKeywordTextField.getText().toString()).split(",");
			StringJoiner keywordText = new StringJoiner(" ");
			String textIndex = textSearchTextIndexLabel.getText();
			boolean isChecked = (textSearchOldVersionCheckBox.isSelected()) ? true : false;
			outputQuery.clear();

			for (String a : keywordTextArray) {
				keywordText.add(a.trim());
			}

			if (btn.getId().equals("textSearchCreateButton")) {
				if (!isChecked) {
					outputQuery.appendText("Using below query you can create a text index on " + textIndex
							+ " field : \n\t\t db." + collection + ".ensureIndex({" + textIndex + ":'text'})\n\n");
					outputQuery.appendText("Using below query you can search for all the documents that have word '"
							+ keywordText + "' in their text according to created text index : \n\t\t db." + collection
							+ ".find({$text:{$search:'" + keywordText + "'}}).pretty()");
				} else {
					outputQuery.appendText(
							"Initially Text Search was an experimental feature but starting from version 2.6, the configuration is enabled by default. But if you are using previous version of MongoDB, \n you have to enable text search with following code: \n\t\t"
									+ "db.a	dminCommand({setParameter:true,textSearchEnabled:true}) \n\n");
					outputQuery.appendText("Using below query you can create a text index on " + textIndex
							+ " field : \n\t\t db." + collection + ".ensureIndex({" + textIndex + ":'text'})\n\n");
					outputQuery.appendText("Using below query you can search for all the documents that have word '"
							+ keywordText + "' in their text according to created text index : \n\t\t db." + collection
							+ ".runCommand({$text:{$search:'" + keywordText + "'}})");
				}
			} else if (btn.getId().equals("textSearchViewButton")) {
				outputQuery.appendText("db." + collection + ".getIndexes()");
			} else {
				outputQuery.appendText("db." + collection + ".dropIndex('" + textIndex + "_text')");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Method for building aggregation queries
	 */
	private void aggregationBuild() {
		try {
			String dbName = aggregationDatabaseComboBox.getSelectionModel().getSelectedItem();
			String collection = aggregationColectionComboBox.getSelectionModel().getSelectedItem();
			ObservableList<QueryDocumentDTO> parameters = (ObservableList<QueryDocumentDTO>) aggregationParametersTableView
					.getItems();
			RadioButton aggregationType = (RadioButton) aggregationRadioGroup.getSelectedToggle();
			String aggregationOption = (aggregationType.getText().toLowerCase().toString().equals("count")) ? "1" : '"'+"$"+aggregationSumFieldComboBox.getSelectionModel().getSelectedItem()+'"';
			
			outputQuery.clear();
			outputQuery.appendText("db."+collection+".aggregate([");
			outputQuery.appendText("\n\t");
			if(parameters.size() > 0) {
				for (QueryDocumentDTO dto : parameters) {
					dao = new SqliteDAO();
					String operator = dao.getQueryOperatorsKeyword(dto.getOperators().toString());

					if (operator.equals("")) {
						outputQuery.appendText("{$match: {"+dto.getField()+": "+dto.getValues()+"}},");
					} else {
						outputQuery.appendText("{$match: {"+dto.getField()+":{"+operator+":"+dto.getValues()+"}}},");
					}
				}
				outputQuery.appendText("\n\t");
			}
			outputQuery.appendText("{ $group:{");
			outputQuery.appendText("\n\t\t");
			if(aggregationGroupbyComboBox.getSelectionModel().getSelectedItem().equals("null")){
				outputQuery.appendText("_id:null,");
			} else {
				outputQuery.appendText("_id:'"+aggregationGroupbyComboBox.getSelectionModel().getSelectedItem().toString()+"',");
			}
			outputQuery.appendText("\n\t\t");
			outputQuery.appendText(aggregationType.getText()+":{$sum:"+aggregationOption+"}");
			outputQuery.appendText("\n\t  }");
			outputQuery.appendText("\n\t");
			outputQuery.appendText("}");
			if((aggregationSortColumnComboBox.getSelectionModel().getSelectedItem()) != null){
				outputQuery.appendText(",");
				outputQuery.appendText("\n\t");
				int sortOrder = (aggregationSortOrderComboBox.getSelectionModel().getSelectedItem().toLowerCase().toString().equals("ascending")) ? 1 : -1 ;
				outputQuery.appendText("{$sort: {"+aggregationSortColumnComboBox.getSelectionModel().getSelectedItem().toString()+":"+sortOrder+"}}");
			}
			if(!aggregationHavingTextField.getText().isEmpty()){
				String havingOperator = dao.getQueryOperatorsKeyword(aggregationHavingOperatorComboBox.getSelectionModel().getSelectedItem());
				outputQuery.appendText(",");
				outputQuery.appendText("\n\t");
				if (havingOperator.equals("")) {
					outputQuery.appendText("{$match:{"+aggregationType.getText()+":"+aggregationHavingTextField.getText()+"}}");
				} else {
					outputQuery.appendText("{$match:{"+aggregationType.getText()+":{"+havingOperator+":"+aggregationHavingTextField.getText()+"}}}");
				}
			}
			outputQuery.appendText("\n])");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get list of all the mongo databases
	 * 
	 * @return
	 */
	private List<String> getMongoDatabases() {
		List<String> databases = new ArrayList<String>();
		try {
			String host = ConnectionParameters.SESSION.getMongoHostName();
			int port = ConnectionParameters.SESSION.getMongoPort();
			MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
			MongoCursor<String> dbCursor = client.listDatabaseNames().iterator();
			while (dbCursor.hasNext()) {
				databases.add(dbCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return databases;
	}

	/**
	 * Get list of collections related to the database
	 * 
	 * @param database
	 * @return
	 */
	private ObservableList<String> getCollectionOf(String database) {
		ObservableList<String> collectionsOf = FXCollections.observableArrayList();
		try {
			MongoDatabase db = MongoConnManager.INSTANCE.connectToDatabase(database);
			MongoCursor<String> cursor = db.listCollectionNames().iterator();
			while (cursor.hasNext()) {
				collectionsOf.add(cursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectionsOf;
	}

	/**
	 * Get common columns related to the particular document
	 * 
	 * @param doc
	 * @return
	 */
	public Set<String> getCommonColumns(List<Document> doc) {
		JSONObject commonColumns = new JSONObject();
		for (Document document : doc) {
			JSONObject object = new JSONObject(document);

			for (Object key : object.keySet()) {
				document.containsKey(key);
				if (commonColumns.containsKey(key)) {
					int cnt = Integer.parseInt(commonColumns.get(key).toString());
					cnt++;
					commonColumns.put(key, cnt);
				} else {
					commonColumns.put(key, "1");
				}
			}

		}

		Set<String> set = null;
		Iterator<?> keys = commonColumns.keySet().iterator();
		int[] arr = new int[commonColumns.keySet().size()];
		int cnt = 0;
		while (keys.hasNext()) {
			arr[cnt++] = Integer.parseInt(commonColumns.get(keys.next()).toString());
		}
		Document commonDoc = new Document();
		int commonCount = getMostPopularElement(arr);
		Iterator<?> keyset = commonColumns.keySet().iterator();
		while (keyset.hasNext()) {
			Object key = keyset.next();
			String st = commonColumns.get(key).toString();
			int value = Integer.parseInt(st);
			if (value == commonCount) {
				commonDoc.append((String) key, value);
			}

		}

		return commonDoc.keySet();
	}

	private static int getMostPopularElement(int[] a) {
		int maxElementIndex = getArrayMaximumElementIndex(a);
		int[] b = new int[a[maxElementIndex] + 1];
		for (int i = 0; i < a.length; i++) {
			++b[a[i]];
		}
		return getArrayMaximumElementIndex(b);
	}

	private static int getArrayMaximumElementIndex(int[] a) {
		int maxElementIndex = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] >= a[maxElementIndex]) {
				maxElementIndex = i;
			}
		}
		return maxElementIndex;
	}

	/**
	 * Add query fields and operators into table
	 */
	private void addQueryParam() {
		try {
			QueryDocumentDTO dto = new QueryDocumentDTO();
			dto.setField(queryFieldListComboBox.getSelectionModel().getSelectedItem());
			dto.setOperators(queryOperatorsComboBox.getSelectionModel().getSelectedItem());
			dto.setValues(queryValuesTextField.getText());
			queryParams.add(dto);
			queryParametersTableView.setItems(queryParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addAggregationQueryParam() {
		try {
			QueryDocumentDTO dto = new QueryDocumentDTO();
			dto.setField(aggregationFieldsComboBox.getSelectionModel().getSelectedItem());
			dto.setOperators(aggregationOperatorsComboBox.getSelectionModel().getSelectedItem());
			dto.setValues(aggregationValueTextField.getText());
			queryParams.add(dto);
			aggregationParametersTableView.setItems(queryParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inner class for create custom combo box in table cell
	 * 
	 * @author KANI
	 *
	 */
	/*
	 * class ComboBoxCell extends TableCell<QueryDocumentDTO, String> {
	 * 
	 * private ComboBox<String> comboBox; private final ObservableList<String>
	 * typData = FXCollections.observableArrayList("AND", "OR");
	 * 
	 * public ComboBoxCell() { }
	 * 
	 * @Override public void startEdit() { super.startEdit();
	 * 
	 * if (comboBox == null) { createComboBox(); }
	 * 
	 * setGraphic(comboBox); setContentDisplay(ContentDisplay.GRAPHIC_ONLY); }
	 * 
	 * @Override public void cancelEdit() { super.cancelEdit();
	 * 
	 * setText(String.valueOf(getItem()));
	 * setContentDisplay(ContentDisplay.TEXT_ONLY); }
	 * 
	 * public void updateItem(String item, boolean empty) {
	 * super.updateItem(item, empty);
	 * 
	 * if (empty) { setText(null); setGraphic(null); } else { if (isEditing()) {
	 * if (comboBox != null) { comboBox.setValue(getString()); }
	 * setGraphic(comboBox); setContentDisplay(ContentDisplay.GRAPHIC_ONLY); }
	 * else { setText(getString()); setContentDisplay(ContentDisplay.TEXT_ONLY);
	 * } } }
	 * 
	 * private void createComboBox() { // ClassesController.getLevelChoice() is
	 * the observable list of // String comboBox = new ComboBox<>(typData);
	 * comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
	 * comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
	 * 
	 * @Override public void handle(KeyEvent t) { if (t.getCode() ==
	 * KeyCode.ENTER) {
	 * commitEdit(comboBox.getSelectionModel().getSelectedItem()); } else if
	 * (t.getCode() == KeyCode.ESCAPE) { cancelEdit(); } } }); }
	 * 
	 * private String getString() { return getItem() == null ? "" :
	 * getItem().toString(); } }
	 */

}
