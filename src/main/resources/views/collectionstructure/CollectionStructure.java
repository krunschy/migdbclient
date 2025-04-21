package org.migdb.migdbclient.views.collectionstructure;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.migdb.migdbclient.controllers.UpdateDataSet;
import org.migdb.migdbclient.controllers.mapping.changemapping.ChangeEmbedding;
import org.migdb.migdbclient.controllers.mapping.changemapping.ChangeReferencing;
import org.migdb.migdbclient.controllers.mapping.writer.MongoWriter;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.config.fromResources.ChangeStructure;
import org.migdb.migdbclient.utils.ToggleSwitch;
import org.migdb.migdbclient.views.connectionmanager.ConnectionManagerController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class CollectionStructure implements Initializable {

    @FXML
    private WebView webview;
    @FXML
    public WebEngine engine;
    @FXML
    private Button proceed;

    @FXML
    private AnchorPane relationshipAnchorPane;

    private JSONObject jsonObject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = webview.getEngine();
        loadStructure();
        proceed.addEventHandler(ActionEvent.ACTION, event -> procesContinuous());
        loadToggleButtonDynamically();

    }


    public void procesContinuous() {
    	try {

    		JSONArray changestructure = ChangeStructure.getInstance().linkDataArray;
    		for (int i = 0; i < changestructure.size(); i++) {
    			JSONObject relation = (JSONObject) changestructure.get(i);
    			if(relation.get("originalvalue").toString().equals("REFERENCING")&& relation.get("toText").toString().equals("EMBEDDING")){
    				ChangeReferencing changeReferencing = new ChangeReferencing();
    				changeReferencing.change(relation.get("from").toString(), relation.get("to").toString());
    			}
    			else if(relation.get("originalvalue").toString().equals("EMBEDDING")&& relation.get("toText").toString().equals("REFERENCING")){
    				//ChangeEmbedding changeEmbedding = new ChangeEmbedding();
    				//changeEmbedding.changeEmbeddingToReferencing(relation.get("from")
    				//.toString(), relation.get("to").toString());
    			}
    			System.out.println(changestructure.get(i));
    		}

    		MongoWriter mongoWriter = new MongoWriter();
    		mongoWriter.write();
    		UpdateDataSet dataSet = new UpdateDataSet();
    		dataSet.updateDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void loadCollectionChangeList(){
      //  jsonObject = ServiceAccessor.getCollectionStructureJSON();
      //  System.out.println("success");
    }

    public void loadToggleButtonDynamically(){
    	ChangeStructure structure = ChangeStructure.getInstance();
    	int relationshipCount = structure.linkDataArray.size();
    	int layoutXReferenceType = 0;
    	int layoutXToggleButton = 20;
    	for(int i =0 ; i < relationshipCount; i++){

    		JSONObject relationship = (JSONObject) structure.linkDataArray.get(i);
     		boolean referenceType = (relationship.get("toText").equals("EMBEDDING")) ? true : false ;
    		Label referenceTypeLabel = new Label(relationship.get("to")+" to "+relationship.get("from"));
    		String buttonId = relationship.get("from").toString()+"#"+relationship.get("to").toString();
    		String originRelationship = relationship.get("toText").toString();
    		ToggleSwitch toggleButton = new ToggleSwitch(referenceType,buttonId, "Embed", "Reference",engine);
    		relationship.put("originalvalue", originRelationship);
    		relationship.put("buttonID",buttonId);
    		referenceTypeLabel.setLayoutY(layoutXReferenceType);
    		referenceTypeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
    		toggleButton.setLayoutY(layoutXToggleButton);
    		layoutXReferenceType+=60;
    		layoutXToggleButton+=60;
    		relationshipAnchorPane.getChildren().addAll(referenceTypeLabel,toggleButton);

    	}

    }

    public void loadStructure(){

        String url = MainApp.class.getResource("/org/migdb/migdbclient/resources/webcontent/collectionRelationship.html").toExternalForm();;
        System.out.println(url);
        java.net.CookieManager manager = new java.net.CookieManager();
        java.net.CookieHandler.setDefault(manager);
        manager.getCookieStore().removeAll();
        java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
        engine.load(url);
        engine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                        if(newValue == Worker.State.SUCCEEDED){
                            engine.executeScript("function init() {\n" +
                                    "    if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this\n" +
                                    "    var $ = go.GraphObject.make;  // for conciseness in defining templates\n" +
                                    "\n" +
                                    "    myDiagram =\n" +
                                    "      $(go.Diagram, \"myDiagramDiv\",  // must name or refer to the DIV HTML element\n" +
                                    "        {\n" +
                                    "          initialContentAlignment: go.Spot.Center,\n" +
                                    "          allowDelete: false,\n" +
                                    "          allowCopy: false,\n" +
                                    "          layout: $(go.ForceDirectedLayout),\n" +
                                    "          \"undoManager.isEnabled\": true\n" +
                                    "        });\n" +
                                    "\n" +
                                    "    // define several shared Brushes\n" +
                                    "    var bluegrad = $(go.Brush, \"Linear\", { 0: \"rgb(150, 150, 250)\", 0.5: \"rgb(86, 86, 186)\", 1: \"rgb(86, 86, 186)\" });\n" +
                                    "    var greengrad = $(go.Brush, \"Linear\", { 0: \"rgb(158, 209, 159)\", 1: \"rgb(67, 101, 56)\" });\n" +
                                    "    var redgrad = $(go.Brush, \"Linear\", { 0: \"rgb(206, 106, 100)\", 1: \"rgb(180, 56, 50)\" });\n" +
                                    "    var yellowgrad = $(go.Brush, \"Linear\", { 0: \"rgb(254, 221, 50)\", 1: \"rgb(254, 182, 50)\" });\n" +
                                    "    var lightgrad = $(go.Brush, \"Linear\", { 1: \"#E6E6FA\", 0: \"#FFFAF0\" });\n" +
                                    "\n" +
                                    "    // the template for each attribute in a node's array of item data\n" +
                                    "    var itemTempl =\n" +
                                    "      $(go.Panel, \"Horizontal\",\n" +
                                    "        $(go.Shape,\n" +
                                    "          { desiredSize: new go.Size(10, 10) },\n" +
                                    "          new go.Binding(\"figure\", \"figure\"),\n" +
                                    "          new go.Binding(\"fill\", \"color\")),\n" +
                                    "        $(go.TextBlock,\n" +
                                    "          { stroke: \"#333333\",\n" +
                                    "            font: \"bold 14px sans-serif\" },\n" +
                                    "          new go.Binding(\"text\", \"name\"))\n" +
                                    "      );\n" +
                                    "\n" +
                                    "    // define the Node template, representing an entity\n" +
                                    "    myDiagram.nodeTemplate =\n" +
                                    "      $(go.Node, \"Auto\",  // the whole node panel\n" +
                                    "        { selectionAdorned: true,\n" +
                                    "          resizable: true,\n" +
                                    "          layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,\n" +
                                    "          fromSpot: go.Spot.AllSides,\n" +
                                    "          toSpot: go.Spot.AllSides,\n" +
                                    "          isShadowed: true,\n" +
                                    "          shadowColor: \"#C5C1AA\" },\n" +
                                    "        new go.Binding(\"location\", \"location\").makeTwoWay(),\n" +
                                    "        // define the node's outer shape, which will surround the Table\n" +
                                    "        $(go.Shape, \"Rectangle\",\n" +
                                    "          { fill: lightgrad, stroke: \"#756875\", strokeWidth: 3 }),\n" +
                                    "        $(go.Panel, \"Table\",\n" +
                                    "          { margin: 8, stretch: go.GraphObject.Fill },\n" +
                                    "          $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),\n" +
                                    "          // the table header\n" +
                                    "          $(go.TextBlock,\n" +
                                    "            {\n" +
                                    "              row: 0, alignment: go.Spot.Center,\n" +
                                    "              margin: new go.Margin(0, 14, 0, 2),  // leave room for Button\n" +
                                    "              font: \"bold 16px sans-serif\"\n" +
                                    "            },\n" +
                                    "            new go.Binding(\"text\", \"key\")),\n" +
                                    "          // the collapse/expand button\n" +
                                    "          $(\"PanelExpanderButton\", \"LIST\",  // the name of the element whose visibility this button toggles\n" +
                                    "            { row: 0, alignment: go.Spot.TopRight }),\n" +
                                    "          // the list of Panels, each showing an attribute\n" +
                                    "          $(go.Panel, \"Vertical\",\n" +
                                    "            {\n" +
                                    "              name: \"LIST\",\n" +
                                    "              row: 1,\n" +
                                    "              padding: 3,\n" +
                                    "              alignment: go.Spot.TopLeft,\n" +
                                    "              defaultAlignment: go.Spot.Left,\n" +
                                    "              stretch: go.GraphObject.Horizontal,\n" +
                                    "              itemTemplate: itemTempl\n" +
                                    "            },\n" +
                                    "            new go.Binding(\"itemArray\", \"items\"))\n" +
                                    "        )  // end Table Panel\n" +
                                    "      );  // end Node\n" +
                                    "\n" +
                                    "    // define the Link template, representing a relationship\n" +
                                    "    myDiagram.linkTemplate =\n" +
                                    "      $(go.Link,  // the whole link panel\n" +
                                    "        {\n" +
                                    "          selectionAdorned: true,\n" +
                                    "          layerName: \"Foreground\",\n" +
                                    "          reshapable: true,\n" +
                                    "          routing: go.Link.AvoidsNodes,\n" +
                                    "          corner: 5,\n" +
                                    "          curve: go.Link.JumpOver\n" +
                                    "        },\n" +
                                    "        $(go.Shape,  // the link shape\n" +
                                    "          { stroke: \"#303B45\", strokeWidth: 2.5 }),\n" +
                                    "        $(go.TextBlock,  // the \"from\" label\n" +
                                    "          {\n" +
                                    "            textAlign: \"center\",\n" +
                                    "            font: \"bold 14px sans-serif\",\n" +
                                    "            stroke: \"#1967B3\",\n" +
                                    "            segmentIndex: 0,\n" +
                                    "            segmentOffset: new go.Point(NaN, NaN),\n" +
                                    "            segmentOrientation: go.Link.OrientUpright\n" +
                                    "          },\n" +
                                    "          new go.Binding(\"text\", \"text\")),\n" +
                                    "        $(go.TextBlock,  // the \"to\" label\n" +
                                    "          {\n" +
                                    "            textAlign: \"center\",\n" +
                                    "            font: \"bold 14px sans-serif\",\n" +
                                    "            stroke: \"#1967B3\",\n" +
                                    "            segmentIndex: -1,\n" +
                                    "            segmentOffset: new go.Point(NaN, NaN),\n" +
                                    "            segmentOrientation: go.Link.OrientUpright\n" +
                                    "          },\n" +
                                    "          new go.Binding(\"text\", \"toText\"))\n" +
                                    "      );\n" +
                                    "              nodeDataArray = "+ChangeStructure.getInstance().nodeDataArray.toString()+";\n" +
                                    "              linkDataArray = "+ChangeStructure.getInstance().linkDataArray.toString()+";\n" +
                                    "              myDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);\n" +
                                    "  } \n" +
                                    " init();");
                        }
                    }
                }
        );
    }


}