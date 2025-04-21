package org.migdb.migdbclient.views.modificationevaluator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.DataTypes;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.TreeviewSize;
import org.migdb.migdbclient.controllers.mapping.manytomany.ManyToMany;
import org.migdb.migdbclient.controllers.mapping.onetomany.OneToManyMapper;
import org.migdb.migdbclient.controllers.mapping.onetoone.OneToOneMap;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.modificationevaluator.ForeignKeyReference;
import org.migdb.migdbclient.models.modificationevaluator.TableReference;
import org.migdb.migdbclient.config.fromResources.CenterLayout;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Malki
 *
 */

public class ModificationEvaluator {

	@FXML
	private AnchorPane modificationEvalAnchor;
	@FXML
	private ScrollPane modificationEvalScroll;
	@FXML Button nextButton;

	JSONArray tableList = new JSONArray();
	ArrayList<TableReference> fkArr = new ArrayList<TableReference>();
	ArrayList<String> removedTbls = new ArrayList<String>();
	LinkedHashMap<String, ArrayList<String>> removedCols = new LinkedHashMap<String, ArrayList<String>>();
	JSONArray deletedItems = new JSONArray();

	JSONParser parser = new JSONParser();
	Object obj;
	JSONObject jsonObject;
	File file = new File(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath());
	
	ChangeStructure structure = ChangeStructure.getInstance();

	@FXML
	private void initialize() {

		try {
			obj = parser.parse(new FileReader(file));
			jsonObject = (JSONObject) obj;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		generateTreeView();
		
		nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				AnchorPane root;
				root = CenterLayout.INSTANCE.getRootContainer();
				root.getChildren().clear();
				
				OneToOneMap ooObj = new OneToOneMap();
				ooObj.oneToOneMapper();
				
				OneToManyMapper omObj = new OneToManyMapper();
				omObj.mapOneToMany();
				
				ManyToMany many = new ManyToMany();
				many.identifyM2M();
				
				/*MongoWriter mongoWriter = new MongoWriter();
	    		mongoWriter.write();*/
				
				 try {
			    		root = CenterLayout.INSTANCE.getRootContainer();
			    		FXMLLoader loader = new FXMLLoader();
			    		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONSTRUCTURE.getPath()));
			    		AnchorPane collectionStructure = loader.load();
			    		root.getChildren().clear();
			    		root.getChildren().add(collectionStructure);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		});

	}
	

	/**
	 * CheckBoxTreeItem uncheck event
	 * @return
	 */
	EventHandler getCheckBoxSelectionHandler() {
		return new EventHandler() {
			@Override
			public void handle(Event e) {
				ArrayList<String> removedColArr = new ArrayList<String>();
				CheckBoxTreeItem<String> item = (CheckBoxTreeItem<String>) e.getSource();
				CheckBoxTreeItem<String> parent = (CheckBoxTreeItem<String>) item.getParent();
				if ((!parent.isSelected()) && (!removedTbls.contains(parent.getValue()))) {
					removedTbls.add(parent.getValue());
				} else if (parent.isSelected()) {
					if (removedTbls.contains(parent.getValue())) {
						removedTbls.remove(parent.getValue());
					}
					if ((!item.isSelected()) && (item.isLeaf())) {
						if (removedCols.isEmpty()) {
							removedColArr.add(item.getValue());
							removedCols.put(item.getParent().getValue(), removedColArr);
						} else {
							if (removedCols.keySet().contains(parent.getValue())) {
								removedCols.get(parent.getValue()).add(item.getValue());
							} else {
								removedColArr.add(item.getValue());
								removedCols.put(item.getParent().getValue(), removedColArr);
							}
						}
					} else if ((item.isSelected()) && (item.isLeaf())) {
						for (String colKey : removedCols.keySet()) {
							if (colKey.equals(item.getParent().getValue())) {
								removedCols.get(colKey).remove(item.getValue());
							}
						}
					}
				}
			}
		};
	}

	/**
	 * Method to dynamically generate table structure tree view
	 */
	private void generateTreeView() {

		try {
			
			List<String> numberTypes = DataTypes.NUMBERTYPES.getTypes();
			
			List<String> dateTypes = DataTypes.DATETYPES.getTypes();

			tableList = (JSONArray) jsonObject.get("tables");

			Iterator<JSONObject> iterator = tableList.iterator();
			double x = TreeviewSize.XSTART.getSize();
			double y = TreeviewSize.YSTART.getSize();

			while (iterator.hasNext()) {
				JSONObject tbl = (JSONObject) iterator.next();
				String name = (String) tbl.get("name");

				CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>(name);
				root.setSelected(true);
				root.setExpanded(true);

				JSONArray columnList = (JSONArray) tbl.get("columns");
				Iterator<JSONObject> cols = columnList.iterator();
				JSONArray refColumnList = (JSONArray) tbl.get("referencedBy");

				if (!refColumnList.isEmpty()) {
					Iterator<JSONObject> refCols = refColumnList.iterator();

					while (refCols.hasNext()) {
						JSONObject ref = (JSONObject) refCols.next();
						String referencedCol = (String) ref.get("referencedCol");
						String referencingTbl = (String) ref.get("referencingTab");
						String referencingCol = (String) ref.get("referencingCol");
						String relationship = (String) ref.get("relationshipType");

						TableReference reference = new TableReference(name, referencedCol, referencingTbl,
								referencingCol, relationship);
						fkArr.add(reference);
					}
				}

				String primaryKey = (String) tbl.get("primaryKey");
				JSONArray refFromList = (JSONArray) tbl.get("referencingFrom");
				
				List<String> fkColList = new ArrayList<String>();
				List<ForeignKeyReference> fkList = new ArrayList<ForeignKeyReference>();
				
				if (!refFromList.isEmpty()) {
					Iterator<JSONObject> refFromCols = refFromList.iterator();
					
					while (refFromCols.hasNext()) {
						JSONObject ref = (JSONObject) refFromCols.next();
						String referencingCol = (String) ref.get("referencingCol");
						String referencedCol = (String) ref.get("referencedCol");
						String referencedTbl = (String) ref.get("referencedTab");
						String relationship = (String) ref.get("relationshipType");
						
						ForeignKeyReference fkRef = new ForeignKeyReference(name, referencingCol, referencedTbl, referencedCol, relationship);
						fkList.add(fkRef);
						fkColList.add(referencingCol);
					}
				}
				
				while (cols.hasNext()) {
					JSONObject col = cols.next();
					String colName = (String) col.get("colName");
					String dataType = (String) col.get("dataType");
					CheckBoxTreeItem<String> column = new CheckBoxTreeItem<String>(colName);
					
					if(colName.equals(primaryKey) && fkColList.contains(colName)) {
						Glyph bothKeys = new Glyph("FontAwesome", FontAwesome.Glyph.KEY).color(Color.GOLD).useGradientEffect();
						for(ForeignKeyReference fkRef : fkList) {
							if(fkRef.getReferencingCol().equals(colName)) {
								String tooltip = "References "+fkRef.getReferencedCol()+" of "+fkRef.getReferencedTab()+" : "+fkRef.getRelationshipType();
								Tooltip t = new Tooltip(tooltip);
						        Tooltip.install(bothKeys,t);
							}
						}
						bothKeys.setId("fk"+name+"-"+colName);
						column.setGraphic(bothKeys);

					} else if(colName.equals(primaryKey)) {
						column.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.KEY).color(Color.GOLD));
					}
					else if(fkColList.contains(colName)) {
						Glyph fk = new Glyph("FontAwesome", FontAwesome.Glyph.KEY).color(Color.SILVER);	  
						for(ForeignKeyReference fkRef : fkList) {
							if(fkRef.getReferencingCol().equals(colName)) {
								String tooltip = "References "+fkRef.getReferencedCol()+" of "+fkRef.getReferencedTab()+" : "+fkRef.getRelationshipType();
								Tooltip t = new Tooltip(tooltip);
						        Tooltip.install(fk, t);
							}
						}
						fk.setId("fk"+name+"-"+colName);
						column.setGraphic(fk);						
					} else if(containsCaseInsensitive(dataType,numberTypes)){
						column.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.CALCULATOR));
					} else if(containsCaseInsensitive(dataType,dateTypes)) {
						column.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.CALENDAR_ALT));
					} else {
						column.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.FILE_TEXT_ALT));
					}
					
					column.setSelected(true);
					root.getChildren().add(column);
					column.addEventHandler(column.checkBoxSelectionChangedEvent(), getCheckBoxSelectionHandler());
				}
				
				CheckTreeView<String> treeView = new CheckTreeView<String>(root);
				treeView.setEditable(true);
				treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

				treeView.setMaxHeight(TreeviewSize.TREEVIEWHEIGHT.getSize());
				treeView.setMaxWidth(TreeviewSize.TREEVIEWIDTH.getSize());
				treeView.setLayoutX(x);
				treeView.setLayoutY(y);
				
				treeView.setOnMouseClicked(new EventHandler<MouseEvent>()
				{
				    @Override
				    public void handle(MouseEvent mouseEvent)
				    { 	
				    	showFkDetails(mouseEvent,treeView,fkList);
				    }
				});

				modificationEvalAnchor.getChildren().add(treeView);

				double nextX = x + (TreeviewSize.XSPACE.getSize()) + 2 * (TreeviewSize.TREEVIEWIDTH.getSize());
				if (modificationEvalAnchor.getPrefWidth() <= nextX) {
					x = TreeviewSize.XSTART.getSize();
					y = y + TreeviewSize.YSTART.getSize() + (TreeviewSize.YSPACE.getSize())
							+ (TreeviewSize.TREEVIEWHEIGHT.getSize());
				} else {
					x = x + (TreeviewSize.XSPACE.getSize()) + (TreeviewSize.TREEVIEWIDTH.getSize());
				}

				double nextY = y + (TreeviewSize.YSPACE.getSize()) + (TreeviewSize.TREEVIEWHEIGHT.getSize());
				if (modificationEvalAnchor.getPrefHeight() <= nextY) {
					modificationEvalAnchor.setPrefHeight(
							y + (TreeviewSize.YSPACE.getSize()) + (TreeviewSize.TREEVIEWHEIGHT.getSize()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawLine() {
		ArrayList<Line> lines = new ArrayList<>();
		for (TableReference ref : fkArr) {
			String referencedTab = ref.getReferencedTab();
			String referencingTab = ref.getReferencingTab();
			Line line = new Line();

			for (Node tbl : modificationEvalAnchor.getChildren()) {
				TreeView<String> tree = (TreeView) tbl;
				String tableName = tree.getRoot().getValue().toString();
				if (tableName.equals(referencedTab)) {
					line.setStartX(tree.getLayoutX() + tree.getMaxWidth());
					line.setStartY(tree.getLayoutY() + (tree.getMaxHeight() / 2));
				}
				if (tableName.equals(referencingTab)) {
					line.setEndX(tree.getLayoutX());
					line.setEndY(tree.getLayoutY());
				}
			}

			line.setStrokeWidth(2);
			line.setStroke(Color.BLACK);

			lines.add(line);
		}

		for (Line line : lines) {
			modificationEvalAnchor.getChildren().add(line);
		}
	}
	
	/**
	 * Method to show foreign key relationship details
	 * @param mouseEvent
	 * @param treeView
	 * @param fkList
	 */
	private void showFkDetails(MouseEvent mouseEvent, CheckTreeView<String> treeView, List<ForeignKeyReference> fkList) {
		
		if(mouseEvent.getClickCount() == 2 && (treeView.getSelectionModel().getSelectedItem().getGraphic().getId() != null)) {
			TreeItem<String> root = treeView.getRoot();
			TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();

			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.setTitle("FK Relationship Details");
             
			VBox dialogVbox = new VBox(20);
			dialogVbox.setPadding(new Insets(20, 0, 20, 20));
			dialogVbox.setStyle("-fx-background-color : white");
			dialogVbox.setAlignment(Pos.CENTER);
             
			HBox detailHbox = new HBox(30);
			ComboBox<String> cmb = new ComboBox<String>();
			cmb.getItems().addAll("One To One", "One To Many", "Many To Many");
			int index = -1;

			for(ForeignKeyReference fkRef : fkList) {
				if(fkRef.getReferencingTab().equals(root.getValue()) && fkRef.getReferencingCol().equals(item.getValue())) {
					VBox fieldsVbox = new VBox(20);
					fieldsVbox.getChildren().addAll(new Label("Referencing Table"),new Label("Referncing Column"),
								new Label("Referenced Table"),new Label("Refernced Column"),new Label("Relationship Type"));
						
					VBox valuesVbox = new VBox(20);

					if(fkRef.getRelationshipType().equals("OneToOne")) {
						cmb.setValue("One To One");
					} else if(fkRef.getRelationshipType().equals("OneToMany")) {
						cmb.setValue("One To Many");
					} else {
						cmb.setValue("Many To Many");
					}
					
					valuesVbox.getChildren().addAll( new Label(fkRef.getReferencingTab()),new Label(fkRef.getReferencingCol()),
								new Label(fkRef.getReferencedTab()), new Label(fkRef.getReferencedCol()), cmb);
						
					detailHbox.getChildren().addAll(fieldsVbox,valuesVbox);
					index = fkList.indexOf(fkRef);
				}
			}
			
			int fkIndex = new Integer(index);

            Button btn = new Button("Update");
            btn.setPrefWidth(100);
            btn.setOnAction((ActionEvent e)
                    -> {
                        String newValue = cmb.getValue();
                        changeRelationship(fkList.get(fkIndex),newValue);
                        dialog.close();
                    }
            );
             
            dialogVbox.getChildren().addAll(detailHbox,btn);
             
            Scene dialogScene = new Scene(dialogVbox, 300, 250);
            dialog.setScene(dialogScene);
            dialog.show();
		}
	}
	
	/**
	 * Method to change relationship type
	 * @param fkRef
	 * @param newValue
	 */
	private void changeRelationship(ForeignKeyReference fkRef, String newValue) {

		for (int i = 0; i < tableList.size(); i++) {
			JSONObject table = (JSONObject) tableList.get(i);
			String name = (String) table.get("name");
			if(fkRef.getReferencingTab().equals(name)) {
				JSONArray refFromList = (JSONArray) table.get("referencingFrom");
				for(int k = 0; k < refFromList.size(); k++) {
					JSONObject refFrom = (JSONObject) refFromList.get(k);
					if(refFrom.get("referencingCol").equals(fkRef.getReferencingCol())) {
						refFrom.put("relationshipType", newValue.replace(" ", ""));
					}
				}
			} else if(fkRef.getReferencedTab().equals(name)) {
				JSONArray refByList = (JSONArray) table.get("referencedBy");
				for(int k = 0; k < refByList.size(); k++) {
					JSONObject refBy = (JSONObject) refByList.get(k); 
					if( refBy.get("referencingCol").equals(fkRef.getReferencingCol()) && refBy.get("referencingTab").equals(fkRef.getReferencingTab())
							&& refBy.get("referencedCol").equals(fkRef.getReferencedCol()) ) {
						refBy.put("relationshipType", newValue.replace(" ", ""));
					}
				}
			}
		}

		JSONObject json = new JSONObject();
		String updatedson = json.toJSONString(jsonObject);
		try {
			FileUtils.writeStringToFile(file, updatedson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		generateTreeView();
	}

	/**
	 * Method to evaluate user selection/removal of tables/columns
	 */
	@FXML
	private void evaluate() {
		try {
			File jsonFile = new File(FilePath.DOCUMENT.getPath() + FilePath.DELETEDITEMFILE.getPath());
			boolean exist = jsonFile.exists();
			int errorCount = 0;
			String message = "";
			List<Integer> removedTblIndex = new ArrayList<Integer>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedColIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedRefByIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedRefFromIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();

			// loop through the json table objects
			for (int i = 0; i < tableList.size(); i++) {
				JSONObject table = (JSONObject) tableList.get(i);
				String name = (String) table.get("name");
				JSONArray referenceByList = (JSONArray) table.get("referencedBy");
				JSONArray referenceFromList = (JSONArray) table.get("referencingFrom");
				String primaryKey = (String) table.get("primaryKey");
				JSONArray cols = (JSONArray) table.get("columns");
				int colCount = cols.size();
				JSONArray dataList = (JSONArray) table.get("data");

				if (!removedCols.isEmpty()) {
				
					// loop though the removed java column o0bjects
					for (String colKey : removedCols.keySet()) {
						if (colKey.equals(name)) {
							int removedColCount = removedCols.get(colKey).size();
							// if all columns are deleted, add to tables to be removed
							if (removedColCount == colCount) {
								removedTbls.add(name);
							} else {
								ArrayList<Integer> colIndex = new ArrayList();
								ArrayList<Integer> refByIndex = new ArrayList();
								ArrayList<Integer> refFromIndex = new ArrayList();
								JSONArray removedColList = new JSONArray();
								JSONArray removedRefByList = new JSONArray();
								JSONArray removedRefFromList = new JSONArray();
								// loop through the json object columns array
								for (int k = 0; k < colCount; k++) {
									JSONObject column = (JSONObject) cols.get(k);
									String colName = (String) column.get("colName");
									// check current column is to be removed
									if (removedCols.get(colKey).contains(colName)) {
										// remove column if the current table object does not have references
										if (referenceByList.isEmpty()) {
											colIndex.add(k);
											removedColList.add(cols.get(k));
										} else {
											List<String> colReferencingTbls = new ArrayList<String>();
											// loop through the reference objects
											for (int l = 0; l < referenceByList.size(); l++) {
												JSONObject ref = (JSONObject) referenceByList.get(l);
												String refCol = (String) ref.get("referencedCol");
												// check whether current column is referenced
												if (refCol.equals(colName)) {
													String refByTbl = (String) ref.get("referencingTab");
													// if referencing table is not removed, check for referencing column removal
													if (!removedTbls.contains(refByTbl)) {
			
														String refByCol = (String) ref.get("referencingCol");
														if (removedCols.keySet().contains(refByTbl)) {
															for (String c : removedCols.keySet()) {
																if (c.equals(refByTbl)) {
																	// if referencing column in not removed, add constraint error
																	if (!removedCols.get(c).contains(refByCol)) {
																		colReferencingTbls.add(refByTbl);
																	} else {
																		// add ref object
																		removedRefByList.add(referenceByList.get(l));
																		if(!refByIndex.contains(l)) {
																			refByIndex.add(l);
																		} 
																	}	
																}
															}
														} else {
															colReferencingTbls.add(refByTbl);
														}

														if (colReferencingTbls.isEmpty()) {
															if (!colIndex.contains(k)) {
																colIndex.add(k);
																removedColList.add(cols.get(k));
															}
														} else {
															// add ref object
															removedRefByList.add(referenceByList.get(l));
															if(!refByIndex.contains(l)) {
																refByIndex.add(l);
															}
														}
													} 
												} else {
													if (!colIndex.contains(k)) {
														colIndex.add(k);
														removedColList.add(cols.get(k));
													}
												}
											}
											if (!colReferencingTbls.isEmpty()) {
												message = setMessage(message, colName, "column", colReferencingTbls);
												errorCount++;
											}
										}
										
										if(primaryKey.equals(colName)) {
											putJsonField(name,primaryKey,"primaryKey");
										}
									}
								}
								if (!colIndex.isEmpty()) {
									
									for (int x = 0; x < referenceFromList.size(); x++) {
										JSONObject ref = (JSONObject) referenceFromList.get(x);
										String refCol = (String) ref.get("referencingCol");

										for (int y = 0; y < colIndex.size(); y++) {
											JSONObject col = (JSONObject) cols.get(colIndex.get(y));
											String colName = (String) col.get("colName");

											if (refCol.equals(colName)) {
												removedRefFromList.add(ref);
												if(!refFromIndex.contains(x)) {
													refFromIndex.add(x);
												}
											}
										}
									}
									removedColIndex.put(i, colIndex);

									if(!refByIndex.isEmpty()) {
										removedRefByIndex.put(i, refByIndex);
									}
									if(!refFromIndex.isEmpty()) {
										removedRefFromIndex.put(i, refFromIndex);
									}
									
									//remove data associated with removed columns
									JSONArray removedDataList = new JSONArray();
									for(int k=0; k<dataList.size(); k++){
										JSONObject data = (JSONObject) dataList.get(k);
										for(int l=0; l<removedColList.size(); l++) {
											JSONObject removedCol = (JSONObject) removedColList.get(l);
											String colName = (String) removedCol.get("colName");
											JSONObject removedData = new JSONObject();
											removedData.put("index", k);
											removedData.put(colName, data.get(colName));
											removedDataList.add(removedData);
										}
									}

									putJsonArray(name,removedColList,"columns");
									putJsonArray(name,removedRefByList,"referencedBy");
									putJsonArray(name,removedRefFromList,"referencingFrom");
									putJsonArray(name,removedDataList,"data");

								}
							}
						}
					}

		
				}

				if (removedTbls.contains(name)) {
					if (referenceByList.isEmpty()) {
						removedTblIndex.add(i);
						deletedItems.add(table);
					} else {
						List<String> referencingTbls = new ArrayList<String>();
						for (int k = 0; k < referenceByList.size(); k++) {
							JSONObject ref = (JSONObject) referenceByList.get(k);
							String refTbl = (String) ref.get("referencingTab");
							if (!removedTbls.contains(refTbl)) {
								String refCol = (String) ref.get("referencedCol");
								if (removedCols.keySet().contains(refTbl)) {
									for (String c : removedCols.keySet()) {
										if (c.equals(refTbl)) {
											if (!removedCols.get(c).contains(refCol)) {
												referencingTbls.add(refTbl);
											} 	
										}
									}
								} else {
									referencingTbls.add(refTbl);
								}
							}
						}
						
						if(referencingTbls.size()>0){
							message = setMessage(message, name, "table", referencingTbls);
							errorCount++;
						} else {
							removedTblIndex.add(i);
							deletedItems.add(table);
						}
						
					}
				}
				
				
				if ((!referenceByList.isEmpty()) && (!removedTbls.contains(name)) && (!removedCols.containsKey(i)) ) {
					ArrayList<Integer> refByIndex = new ArrayList();
					for (int k = 0; k < colCount; k++) {
						JSONObject column = (JSONObject) cols.get(k);
						String colName = (String) column.get("colName");
						JSONArray removedRefByList = new JSONArray();
						for (int l = 0; l < referenceByList.size(); l++) {
							JSONObject ref = (JSONObject) referenceByList.get(l);
							String refCol = (String) ref.get("referencedCol");
							if (refCol.equals(colName)) {
								String refByTbl = (String) ref.get("referencingTab");
								// if referencing table is not removed, check for referencing column removal
								if (!removedTbls.contains(refByTbl)) {
									String refByCol = (String) ref.get("referencingCol");
									if (removedCols.keySet().contains(refByTbl)) {
										for (String c : removedCols.keySet()) {
											if (c.equals(refByTbl)) {
												if (removedCols.get(c).contains(refByCol)) {
													// add ref object
													removedRefByList.add(referenceByList.get(l));
													if(!removedRefByIndex.containsKey(i)) {
														refByIndex.add(l);
													} else {
														if(!removedRefByIndex.get(i).contains(l)) {
															removedRefByIndex.get(i).add(l);
														}
													}
												}
											}
										}
									}
								} else {
									removedRefByList.add(referenceByList.get(l));
									if(!removedRefByIndex.containsKey(i)) {
										refByIndex.add(l);
									} else {
										if(!removedRefByIndex.get(i).contains(l)) {
											removedRefByIndex.get(i).add(l);
										}
									}
								}
							}
						}
						if (!removedRefByList.isEmpty()) {
							if(!refByIndex.isEmpty()) {
								removedRefByIndex.put(i, refByIndex);
							}

							putJsonArray(name,removedRefByList,"referencedBy");
						}
					}
				} 
				
			}

			JSONObject json = new JSONObject();
			JSONObject delObject;
			
			
			if (exist) {
				delObject = (JSONObject) parser.parse(new FileReader(jsonFile));
				JSONArray deletedList = (JSONArray) delObject.get("deletedItems");
				for (int i = 0; i < deletedItems.size(); i++) {
					deletedList.add(deletedItems.get(i));
				}
			} else {
				delObject = new JSONObject();
				delObject.put("deletedItems", deletedItems);
			}

			if (errorCount == 0) {
				String resultingJson = json.toJSONString(delObject);
				FileUtils.writeStringToFile(jsonFile, resultingJson);

				for (int colKeyIndex : removedColIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(colKeyIndex);
					JSONArray data = (JSONArray) table.get("data");
					JSONArray cols = (JSONArray) table.get("columns");
					Long colCount = (Long) table.get("colCount");
					String primaryKey = (String) table.get("primaryKey");
					boolean isFirstCol = true;
					for (int colIndex : removedColIndex.get(colKeyIndex)) {					
						JSONObject column = (JSONObject) cols.get(colIndex);
						String colName = (String) column.get("colName");
						if (primaryKey.equals(colName)) {
							table.remove("primaryKey");
						}
						if(isFirstCol) {
							cols.remove(colIndex);
							isFirstCol = false;
						} else {
							cols.remove(--colIndex);
						}
						colCount--;
						
						for(int k =0; k<data.size(); k++) {
							JSONObject dataObj = (JSONObject) data.get(k);
							dataObj.remove(colName);
						}
					}
					table.remove("colCount");
					table.put("colCount", colCount);
				}
				
				for (int refKeyIndex : removedRefByIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(refKeyIndex);
					JSONArray refBy = (JSONArray) table.get("referencedBy");
					boolean isFirstRef = true;
					for (int refIndex : removedRefByIndex.get(refKeyIndex)) {
						if(isFirstRef) {
							refBy.remove(refIndex);
							isFirstRef = false;
						} else {
							refBy.remove(--refIndex);
						}
					}
				}
				
				for (int refKeyIndex : removedRefFromIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(refKeyIndex);
					JSONArray refBy = (JSONArray) table.get("referencingFrom");
					boolean isFirstRef = true;
					for (int refIndex : removedRefFromIndex.get(refKeyIndex)) {
						if(isFirstRef) {
							refBy.remove(refIndex);
							isFirstRef = false;
						} else {
							refBy.remove(--refIndex);
						}
					}
				}
				
				boolean isFirst = true;
				for (int tableIndex : removedTblIndex) {
					if (isFirst) {
						tableList.remove(tableIndex);
						structure.nodeDataArray.remove(tableIndex);
						isFirst = false;
					} else {
						int index = --tableIndex;
						tableList.remove(index);
						structure.nodeDataArray.remove(index);
					}
				}

				String updatedson = json.toJSONString(jsonObject);
				FileUtils.writeStringToFile(file, updatedson);
				
				modificationEvalAnchor.getChildren().clear();
				generateTreeView();
				removedTbls.clear();
				removedCols.clear();
				deletedItems.clear();

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Occured");
				alert.setHeaderText(null);
				alert.setContentText(message);
				alert.showAndWait();
			}

		} catch (IOException | ParseException e1) {
			e1.printStackTrace();
		}
	}

	
	/**
	 * Method to generate user error message
	 * @param message
	 * @param itemName
	 * @param itemType
	 * @param tblArray
	 * @return
	 */
	private String setMessage(String message, String itemName, String itemType, List<String> tblArray) {
		message += "Cannot remove " + itemType + " " + itemName + " due to foreign key reference(s) on " + "table(s) ";
		boolean isFirst = true;
		for (String tbl : tblArray) {
			if (isFirst) {
				message += tbl;
				isFirst = false;
			} else {
				message += ", " + tbl;
			}
		}
		return message += "\n";
	}
	
	
	/**
	 * Method to add json key value pair into objects of deleted json file
	 * @param name
	 * @param value
	 * @param keyName
	 */
	private void putJsonField(String name, String value, String keyName) {

		if(deletedItems.isEmpty()) {
			JSONObject newObject = new JSONObject();
			newObject.put("name", name);
			newObject.put(keyName, value);
			deletedItems.add(newObject);
		} else {
			for(int i =0; i < deletedItems.size(); i++) {
				JSONObject obj = (JSONObject) deletedItems.get(i);
				if(obj.get("name").equals(name)) {
					obj.put(keyName, value);
				} else {
					JSONObject newObject = new JSONObject();
					newObject.put("name", name);
					newObject.put(keyName, value);
					deletedItems.add(newObject);
				}
			}
		}
	}
	
	/**
	 * Method to add json objects into json arrays of deleted json file
	 * @param name
	 * @param object
	 * @param keyName
	 */
	private void putJsonArray(String name, JSONArray object, String keyName) {

		int delItemsSize = deletedItems.size();
		if(deletedItems.isEmpty()) {
			JSONObject newObject = new JSONObject();
			newObject.put("name", name);
			newObject.put(keyName, object);
			deletedItems.add(newObject);
		} else {
			boolean isFound = false;
			for(int i =0; i < delItemsSize; i++) {
				JSONObject obj = (JSONObject) deletedItems.get(i);
				String tableName = (String) obj.get("name");
				if(tableName.equals(name)) {
					isFound = true;
					JSONArray objArr = (JSONArray) obj.get(keyName);
					if((objArr == null) || (objArr.isEmpty())) {
						obj.put(keyName, object);
					} else {
						for(int k=0; k<object.size(); k++) {
							objArr.add(object.get(k));
						}
					}
				} 
			}
			if(!isFound) {
				JSONObject newObject = new JSONObject();
				newObject.put("name", name);
				newObject.put(keyName, object);
				deletedItems.add(newObject);
			}
		}
	}
	
	
	/**
	 * Method for case insensitive arrayList.contains
	 * @param str
	 * @param list
	 * @return
	 */
	public boolean containsCaseInsensitive(String str, List<String> list){
	     for (String string : list){
	        if (string.equalsIgnoreCase(str)){
	            return true;
	         }
	     }
	    return false;
	  }

}
