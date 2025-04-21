/**
 * 
 */
package org.migdb.migdbclient.controllers;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ManyToManyValidator;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kani
 *
 */
public class CardinalityMap {

	public static MysqlDAO dao;
	public static SqliteDAO sqliteDao;
	public static MySQLParser mysqlParser;
	static ArrayList<ManyToManyValidator> verifiedObjects = new ArrayList<ManyToManyValidator>();

	static String host = ConnectionParameters.SESSION.getMysqlHostName();
	static int port = ConnectionParameters.SESSION.getMysqlPort();
	static String database = Session.INSTANCE.getActiveDB();
	static String username = ConnectionParameters.SESSION.getUserName();
	static String password = ConnectionParameters.SESSION.getPassword();

	/**
	 * Cardinality identify main function Insert identified relationship object
	 * into sqlite database
	 */
	public void cardinalityAnalyze() {

		try {

			dao = new MysqlDAO();
			sqliteDao = new SqliteDAO();
			sqliteDao.createRelationshipTypes();
			ArrayList<ReferenceDTO> referencedList = dao.getReferencedList(host, port, database, username, password);
			String cardinality = null;

			File dataFile = new File(FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(dataFile);
			doc.getDocumentElement().normalize();
			NodeList tableNodeList = doc.getElementsByTagName("table_data");

			// Loop for tables in xml file
			for (int i = 0; i < tableNodeList.getLength(); i++) {

				// Get table node one by one from table node list
				Node tableNode = tableNodeList.item(i);
				if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
					Element tableElement = (Element) tableNode;

					// Loop for referenced dto object
					// It has tables which has relationships
					for (ReferenceDTO dtoReferenceList : referencedList) {
						if (tableElement.getAttribute("name").equals(dtoReferenceList.getTableName())) {

							// Get rows list for a paticular table
							NodeList rowNodeList = tableNode.getChildNodes();

							/*
							 * System.out.println("--------------- Function 1 "
							 * +tableElement.getAttribute("name")+
							 * " ---------------");
							 */
							ArrayList<String> funct2 = getReferencedByData(dtoReferenceList.getReferencedTableName(),
									dtoReferenceList.getReferencedColumnName());
							ArrayList<String> valueHolder = new ArrayList<String>();
							// Loop value returns from func2
							for (int m = 0; m < funct2.size(); m++) {
								/* System.out.println(funct2.get(m)); */

								/* System.out.println(); */
								// Loop rows belongs to particular table
								for (int j = 0; j < rowNodeList.getLength(); j++) {

									// Take row node from row node list
									Node rowNode = rowNodeList.item(j);
									if ("row".equals(rowNode.getNodeName())) {

										// Get fields belongs to particular row
										// node
										NodeList fieldNodeList = rowNode.getChildNodes();

										// Loop fields belongs to a particular
										// row
										for (int k = 0; k < fieldNodeList.getLength(); k++) {

											// Take field node from field node
											// list
											Node fieldNode = fieldNodeList.item(k);
											if ("field".equals(fieldNode.getNodeName())) {
												Element fieldElement = (Element) fieldNode;

												// Compare if the referenced dto
												// has table name outputed from
												// xml
												if (fieldElement.getAttribute("name")
														.equals(dtoReferenceList.getColumnName())) {

													// data comparison funct2
													// return value
													if (funct2.get(m).equals(
															String.valueOf(fieldNode.getTextContent().trim()))) {
														valueHolder
																.add(String.valueOf(fieldNode.getTextContent().trim()));
														/*
														 * System.out.println(
														 * fieldElement.
														 * getAttribute("name")
														 * + " - " +
														 * String.valueOf(
														 * fieldNode.
														 * getTextContent().trim
														 * ()));
														 */
													}
												}
											}
										}
									}
								}
								if (valueHolder.size() >= 2) {
									cardinality = "more";
									break;
								} else {
									cardinality = "1:1";
								}
								valueHolder.clear();
							}
							if (cardinality.equals("more")) {
								int l = 0;

								// Compare and increment table appear count
								ArrayList<ReferenceDTO> filteredObjects = new ArrayList<ReferenceDTO>();
								for (ReferenceDTO dtoReferenceListComparison : referencedList) {
									if (dtoReferenceList.getTableName()
											.equals(dtoReferenceListComparison.getTableName())) {
										l++;
										filteredObjects.add(dtoReferenceListComparison);
									}
								}

								if (l >= 2) {

									for (ReferenceDTO dtoMostFrequent : filteredObjects) {
										// System.out.println(dtoMostFrequent.getCOLUMN_NAME());
										checkBidirectionalVal(filteredObjects, dtoReferenceList);
									}

									boolean isManyToMany = false;
									for (ManyToManyValidator verifiedObj : verifiedObjects) {
										if ((verifiedObj.getTableName().equals(dtoReferenceList.getTableName()))
												&& (verifiedObj.getColumns().contains(dtoReferenceList.getColumnName()))
												&& (verifiedObj.getTables()
														.contains(dtoReferenceList.getReferencedTableName()))) {
											if (verifiedObj.isValid()) {
												sqliteDao.insertRelationshipTypes(dtoReferenceList.getTableName(),
														dtoReferenceList.getColumnName(),
														dtoReferenceList.getReferencedTableName(),
														dtoReferenceList.getReferencedColumnName(), "ManyToMany");
											} else {
												sqliteDao.insertRelationshipTypes(dtoReferenceList.getTableName(),
														dtoReferenceList.getColumnName(),
														dtoReferenceList.getReferencedTableName(),
														dtoReferenceList.getReferencedColumnName(), "OneToMany");
											}
										}
										// System.out.println(verifiedObj.getTableName()+"
										// ---> "+verifiedObj.getColumns()+"
										// "+verifiedObj.isValid());
									}

								} else {
									sqliteDao.insertRelationshipTypes(dtoReferenceList.getTableName(),
											dtoReferenceList.getColumnName(), dtoReferenceList.getReferencedTableName(),
											dtoReferenceList.getReferencedColumnName(), "OneToMany");
								}

							} else {
								sqliteDao.insertRelationshipTypes(dtoReferenceList.getTableName(),
										dtoReferenceList.getColumnName(), dtoReferenceList.getReferencedTableName(),
										dtoReferenceList.getReferencedColumnName(), "OneToOne");
							}
						}
					}
				}

			}

			mysqlParser = new MySQLParser();
			mysqlParser.sqlParser();
			sqliteDao.dropRelationshipTypes();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get referenced by data for identify to 1:N and M:N
	 * 
	 * @param referencedTable
	 * @param referencedColumn
	 * @return
	 */
	public ArrayList<String> getReferencedByData(String referencedTable, String referencedColumn) {
		ArrayList<String> referencingVal = null;
		try {
			File dataFile = new File(FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(dataFile);
			doc.getDocumentElement().normalize();
			NodeList tableNodeList = doc.getElementsByTagName("table_data");

			// Loop for tables in xml file
			for (int i = 0; i < tableNodeList.getLength(); i++) {
				// Get table node one by one from table node list
				Node tableNode = tableNodeList.item(i);
				if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
					Element tableElement = (Element) tableNode;
					if (tableElement.getAttribute("name").equals(referencedTable)) {

						// Get rows list for a paticular table
						NodeList rowNodeList = tableNode.getChildNodes();

						/*
						 * System.out.println("--------------- Function 2 "
						 * +referencedTable+" ---------------");
						 */
						referencingVal = new ArrayList<String>();
						// Loop rows belongs to particular table
						for (int j = 0; j < rowNodeList.getLength(); j++) {

							// Take row node from row node list
							Node rowNode = rowNodeList.item(j);
							if ("row".equals(rowNode.getNodeName())) {

								// Get fields belongs to particular row node
								NodeList fieldNodeList = rowNode.getChildNodes();

								// Loop fields belongs to a particular row
								for (int k = 0; k < fieldNodeList.getLength(); k++) {

									// Take field node from field node list
									Node fieldNode = fieldNodeList.item(k);
									if ("field".equals(fieldNode.getNodeName())) {
										Element fieldElement = (Element) fieldNode;
										if (fieldElement.getAttribute("name").equals(referencedColumn)) {
											referencingVal.add(String.valueOf(fieldNode.getTextContent().trim()));
											/*
											 * System.out.println(fieldElement.
											 * getAttribute("name") + " - " +
											 * String.valueOf(fieldNode.
											 * getTextContent().trim()));
											 */
										}
									}
								}
							}
						}
						/* System.out.println(); */
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return referencingVal;

	}

	/**
	 * Check bi-directional ways to values for identify many to many
	 * relationships
	 * 
	 * @param filteredObjects
	 * @param referenceObj
	 */
	public void checkBidirectionalVal(ArrayList<ReferenceDTO> filteredObjects, ReferenceDTO referenceObj) {
		try {
			dao = new MysqlDAO();
			ArrayList<String> frequentValues = dao.getMostFrequentValues(host, port, database, username, password,
					referenceObj.getTableName(), referenceObj.getColumnName());
			for (ReferenceDTO filteredObj : filteredObjects) {
				if (!(filteredObj.getColumnName().equals(referenceObj.getColumnName()))) {
					int distinctCount = dao.getDistinctObjCount(host, port, database, username, password,
							referenceObj.getTableName(), referenceObj.getColumnName(), filteredObj.getColumnName(),
							frequentValues.get(0));
					if (distinctCount > 1) {
						if (verifiedObjects.isEmpty()) {
							ManyToManyValidator obj = new ManyToManyValidator();
							obj.setTableName(referenceObj.getTableName());
							ArrayList<String> cols = new ArrayList<String>();
							cols.add(referenceObj.getColumnName());
							cols.add(filteredObj.getColumnName());
							obj.setColumns(cols);
							ArrayList<String> tables = new ArrayList<String>();
							tables.add(referenceObj.getReferencedTableName());
							tables.add(filteredObj.getReferencedTableName());
							obj.setTables(tables);
							verifiedObjects.add(obj);
						} else {
							boolean existing = false;
							for (int i = 0; i < verifiedObjects.size(); i++) {
								ManyToManyValidator existingObj = verifiedObjects.get(i);
								if ((existingObj.getColumns().contains(referenceObj.getColumnName()))
										&& (existingObj.getColumns().contains(filteredObj.getColumnName()))) {
									existingObj.setValid(true);
									existing = true;
								}
							}
							if (!existing) {
								ManyToManyValidator obj = new ManyToManyValidator();
								obj.setTableName(referenceObj.getTableName());
								ArrayList<String> cols = new ArrayList<String>();
								cols.add(referenceObj.getColumnName());
								cols.add(filteredObj.getColumnName());
								obj.setColumns(cols);
								ArrayList<String> tables = new ArrayList<String>();
								tables.add(referenceObj.getReferencedTableName());
								tables.add(filteredObj.getReferencedTableName());
								obj.setTables(tables);
								verifiedObjects.add(obj);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
