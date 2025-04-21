package org.migdb.migdbclient.views.queryconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.migdb.migdbclient.config.DataTypes;
import org.migdb.migdbclient.models.queryconverter.AggregateLookup;
import org.migdb.migdbclient.models.queryconverter.CreateCollection;
import org.migdb.migdbclient.models.queryconverter.CreateDocumentCollection;
import org.migdb.migdbclient.models.queryconverter.DropCollection;
import org.migdb.migdbclient.models.queryconverter.FindDocument;
import org.migdb.migdbclient.models.queryconverter.InsertDocument;
import org.migdb.migdbclient.models.queryconverter.MatchCondition;
import org.migdb.migdbclient.models.queryconverter.RemoveDocument;
import org.migdb.migdbclient.models.queryconverter.UpdateCollection;
import org.migdb.migdbclient.models.queryconverter.UpdateDocument;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author Malki
 *
 */
public class QueryConverterController {

	@FXML
	private AnchorPane rootLayoutAnchorPane;
	@FXML
	private TextArea sqlQueryTxt;
	@FXML
	private TextArea mongoQueryTxt;

	List<String> numberTypes = DataTypes.NUMBERTYPES.getTypes();

	List<String> dateTypes = DataTypes.DATETYPES.getTypes();

	@FXML
	private void convert() {

		String sqlQuery = sqlQueryTxt.getText().replace("\n", " ").replace("\t", " ");
		System.out.println(sqlQuery);

		try {
			Statement statement = CCJSqlParserUtil.parse(sqlQuery);

			String mongoQuery = "";

			if (statement instanceof Insert) {
				Insert insertStatement = (Insert) statement;
				mongoQuery = convertInsertRecord(insertStatement);
			} else if (statement instanceof CreateTable) {
				CreateTable createStatement = (CreateTable) statement;
				mongoQuery = convertCreateTable(createStatement);
			} else if (statement instanceof Alter) {
				Alter alterStatement = (Alter) statement;
				mongoQuery = convertAlterTable(alterStatement);
			} else if (statement instanceof CreateIndex) {
				CreateIndex indexStatement = (CreateIndex) statement;
				mongoQuery = convertCreateIndex(indexStatement, sqlQuery);
			} else if (statement instanceof Drop) {
				Drop dropStatement = (Drop) statement;
				mongoQuery = convertDropTable(dropStatement);
			} else if (statement instanceof Update) {
				Update updateStatement = (Update) statement;
				mongoQuery = convertUpdateRecord(updateStatement);
			} else if (statement instanceof Delete) {
				Delete deleteStatement = (Delete) statement;
				mongoQuery = convertDeleteRecord(deleteStatement);
			} else if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				mongoQuery = convertSelectRecord(selectStatement);
			}

			mongoQueryTxt.setText(mongoQuery);

		} catch (JSQLParserException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Query cannot be processed. Please check it for syntax errors.");
			alert.showAndWait();
		}
	}

	/**
	 * @param createStatement
	 * @return
	 */
	private String convertCreateTable(CreateTable createStatement) {

		Table table = createStatement.getTable();
		List<ColumnDefinition> colDef = createStatement.getColumnDefinitions();
		List<Index> indexes = createStatement.getIndexes();

		CreateCollection obj1 = new CreateCollection(table.getName());

		CreateDocumentCollection obj2 = new CreateDocumentCollection(table.getName());

		for (int i = 0; i < colDef.size(); i++) {
			ColumnDefinition def = colDef.get(i);
			String dataType = def.getColDataType().getDataType();
			if (containsCaseInsensitive(dataType, numberTypes)) {
				if (dataType.equalsIgnoreCase("int")) {
					obj2.addPair(def.getColumnName(), "int");
				} else if (dataType.equalsIgnoreCase("bigint")) {
					obj2.addPair(def.getColumnName(), "bigint");
				} else {
					obj2.addPair(def.getColumnName(), "numeric");
				}
			} else if (containsCaseInsensitive(dataType, dateTypes)) {
				obj2.addPair(def.getColumnName(), "date");
			} else {
				obj2.addPair(def.getColumnName(), "string");
			}
		}

		for (int i = 0; i < indexes.size(); i++) {
			Index index = indexes.get(i);
			String keyType = index.getType();
			if (keyType.equalsIgnoreCase("foreign key")) {
				ForeignKeyIndex fk = (ForeignKeyIndex) index;
				obj2.addReferencePair(fk.getTable().getName(), fk.getColumnsNames().get(0));
			}
		}

		String mongoQuery = obj1.toString() + "\n\nOR\n\n" + obj2.toString();

		return mongoQuery;
	}

	/**
	 * @param alterStatement
	 * @return
	 */
	private String convertAlterTable(Alter alterStatement) {

		String operation = alterStatement.getOperation();
		Table table = alterStatement.getTable();

		UpdateCollection obj = new UpdateCollection(table.getName());

		if (operation.equalsIgnoreCase("add")) {
			String dataType = alterStatement.getDataType().toString();
			if (containsCaseInsensitive(dataType, numberTypes)) {
				if (dataType.equalsIgnoreCase("int")) {
					obj.addColumn(alterStatement.getColumnName(), "int");
				} else if (dataType.equalsIgnoreCase("bigint")) {
					obj.addColumn(alterStatement.getColumnName(), "bigint");
				} else {
					obj.addColumn(alterStatement.getColumnName(), "numeric");
				}
			} else if (containsCaseInsensitive(dataType, dateTypes)) {
				obj.addColumn(alterStatement.getColumnName(), "date");
			} else {
				obj.addColumn(alterStatement.getColumnName(), "string");
			}
		} else if (operation.equalsIgnoreCase("drop")) {
			obj.dropColumn(alterStatement.getColumnName());
		}

		String mongoQuery = obj.toString();

		return mongoQuery;
	}

	/**
	 * @param indexStatement
	 * @return
	 */
	private String convertCreateIndex(CreateIndex indexStatement, String sqlQuery) {

		String[] indexFields = sqlQuery.substring(sqlQuery.indexOf("(") + 1, sqlQuery.indexOf(")")).split(",");

		Table table = indexStatement.getTable();
		Index index = indexStatement.getIndex();
		String indexName = index.getName();
		List<String> columns = index.getColumnsNames();

		org.migdb.migdbclient.models.queryconverter.CreateIndex obj = new org.migdb.migdbclient.models.queryconverter.CreateIndex(
				table.getName(), indexName);

		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			int order = 1;
			for (String col : indexFields) {
				if (col.contains(column)) {
					if (col.toLowerCase().endsWith("desc")) {
						order = -1;
					}
				}
			}
			obj.addIndex(column, order);
		}

		String mongoQuery = obj.toString();
		return mongoQuery;
	}

	/**
	 * @param dropStatement
	 * @return
	 */
	private String convertDropTable(Drop dropStatement) {

		Table table = dropStatement.getName();

		DropCollection obj = new DropCollection(table.getName());

		String mongoQuery = obj.toString();
		return mongoQuery;
	}

	/**
	 * Converts a MySQL insert statement into MongoDB insert statement
	 * 
	 * @param insertStatement
	 * @return
	 */
	private String convertInsertRecord(Insert insertStatement) {
		Table table = insertStatement.getTable();
		List<Column> columnList = insertStatement.getColumns();
		String values = insertStatement.getItemsList().toString().replace("(", "").replace(")", "").replace(" ", "");

		InsertDocument obj = new InsertDocument(table.getName());

		StringTokenizer st = new StringTokenizer(values, ",");
		int count = 0;
		while (st.hasMoreElements()) {
			Object value = st.nextElement();

			if (columnList == null) {
				obj.addPair(count + 1, value);
			} else {
				obj.addPair(columnList.get(count).getColumnName(), value);
			}
			count++;
		}

		String mongoQuery = obj.toString();
		return mongoQuery;
	}

	/**
	 * @param updateStatement
	 * @return
	 */
	private String convertUpdateRecord(Update updateStatement) {

		Table table = updateStatement.getTables().get(0);
		List<Column> columnList = updateStatement.getColumns();
		List<Expression> expressionList = updateStatement.getExpressions();
		Expression whereExpression = updateStatement.getWhere();

		UpdateDocument obj = new UpdateDocument(table.getName());

		for (int i = 0; i < columnList.size(); i++) {
			Column column = columnList.get(i);
			Expression expression = expressionList.get(i);

			if (expression instanceof Addition) {
				Addition op = (Addition) expression;
				obj.addUpdatePair(op.getLeftExpression().toString(), op.getLeftExpression(), "inc");
			} else if (expression instanceof Subtraction) {
				Subtraction op = (Subtraction) expression;
				obj.addUpdatePair(op.getLeftExpression().toString(), op.getRightExpression(), "sub");
			} else if (expression instanceof Multiplication) {
				Multiplication op = (Multiplication) expression;
				obj.addUpdatePair(op.getLeftExpression().toString(), op.getRightExpression(), "mul");
			} else if (expression instanceof Division) {
				Division op = (Division) expression;
				obj.addUpdatePair(op.getLeftExpression().toString(), op.getRightExpression(), "div");
			} else if (expression instanceof LongValue) {
				LongValue exp = (LongValue) expression;
				obj.addUpdatePair(column.getColumnName(), exp.getValue(), "long");
			} else if (expression instanceof DateValue || expression instanceof TimestampValue) {
				DateValue exp = (DateValue) expression;
				obj.addUpdatePair(column.getColumnName(), exp.getValue(), "date");
			} else {
				obj.addUpdatePair(column.getColumnName(), expression, "other");
			}
		}

		MatchCondition conditionPair = convertWhere(whereExpression);
		obj.setMatchPairs(conditionPair);

		String mongoQuery = obj.toString();

		return mongoQuery;
	}

	/**
	 * @param deleteStatement
	 * @return
	 */
	private String convertDeleteRecord(Delete deleteStatement) {

		Table table = deleteStatement.getTable();
		Expression whereExpression = deleteStatement.getWhere();

		RemoveDocument obj = new RemoveDocument(table.getName());
		MatchCondition conditionPair = convertWhere(whereExpression);
		obj.setMatchPairs(conditionPair);

		String mongoQuery = obj.toString();
		return mongoQuery;
	}

	private String convertSelectRecord(Select selectStatement) {

		String mongoQuery = "";

		SelectBody select = selectStatement.getSelectBody();

		if (select instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) select;

			List<Join> joins = plainSelect.getJoins();

			if (joins != null) {

				return convertJoin(joins, plainSelect);

			} else {
				List<SelectItem> selectItems = plainSelect.getSelectItems();
				Expression whereExpression = plainSelect.getWhere();
				Limit limit = plainSelect.getLimit();
				List<OrderByElement> orderByList = plainSelect.getOrderByElements();

				FindDocument obj = new FindDocument(plainSelect.getFromItem().toString());

				MatchCondition conditions = convertWhere(whereExpression);

				for (int i = 0; i < selectItems.size(); i++) {
					SelectItem item = selectItems.get(i);
					if (item instanceof SelectExpressionItem) {
						SelectExpressionItem expressionItem = (SelectExpressionItem) item;
						Expression expression = expressionItem.getExpression();
						if (expression instanceof Function) {
							Function func = (Function) expression;
							if (func.getName().equalsIgnoreCase("count")) {
								if (func.getParameters() != null) {
									List<Expression> exList = func.getParameters().getExpressions();
									for (int k = 0; k < exList.size(); k++) {
										HashMap<String, Object> pair = new HashMap<String, Object>();
										pair.put("$exists", "true");
										conditions.getMatchPair().put(exList.get(k).toString(), pair);
									}
								}
								obj.setCount(true);
							}
						} else if (expression instanceof Column) {
							Column col = (Column) expression;
							obj.addProjection(col.getColumnName());
						}
					}

				}

				if (orderByList != null) {
					for (int i = 0; i < orderByList.size(); i++) {
						OrderByElement orderBy = orderByList.get(i);
						if (orderBy.isAsc()) {
							obj.addSort(orderBy.getExpression().toString(), "asc");
						} else if (orderBy.isAscDescPresent()) {
							obj.addSort(orderBy.getExpression().toString(), "desc");
						}
					}
				}

				if (limit != null) {
					obj.setLimit((int) limit.getRowCount());
				}

				obj.setMatchPairs(conditions);

				mongoQuery = obj.toString();
			}
		}
		return mongoQuery;
	}

	/**
	 * Method to convert join queries
	 * 
	 * @param joins
	 * @param plainSelect
	 * @return
	 */
	private String convertJoin(List<Join> joins, PlainSelect plainSelect) {

		Expression whereExpression = plainSelect.getWhere();

		AggregateLookup obj = new AggregateLookup();

		List<SelectItem> selectItems = plainSelect.getSelectItems();

		for (int k = 0; k < joins.size(); k++) {
			Join join = joins.get(k);
			Table leftTable = (Table) plainSelect.getFromItem();
			Table rightTable = (Table) join.getRightItem();

			if (join.isLeft() || (join.isRight() && k == 0)) {

				if (join.isRight()) {
					rightTable = (Table) plainSelect.getFromItem();
					leftTable = (Table) join.getRightItem();
				}

				obj.setCollectionName(leftTable.getName());
				if (leftTable.getAlias() != null) {
					obj.addAlias(leftTable.getName(), leftTable.getAlias().getName());
				}
				if (rightTable.getAlias() != null) {
					obj.addAlias(rightTable.getName(), rightTable.getAlias().getName());
				}

				Expression onExpression = join.getOnExpression();
				Column leftCol = null;
				Column rightCol = null;

				if (onExpression instanceof EqualsTo) {
					String localField = "";
					String foreignField = "";
					EqualsTo eq = (EqualsTo) onExpression;
					Expression leftExp = eq.getLeftExpression();
					if (join.isRight()) {
						leftExp = eq.getRightExpression();
					}

					if (leftExp instanceof Column) {
						leftCol = (Column) leftExp;

						if (leftTable.getAlias() == null && leftCol.getTable().getName().equals(leftTable.getName())) {
							localField = leftCol.getColumnName();
						} else if (leftTable.getAlias() != null
								&& leftTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							localField = leftCol.getColumnName();
						} else if (rightTable.getAlias() == null
								&& leftCol.getTable().getName().equals(rightTable.getName())) {
							foreignField = leftCol.getColumnName();
						} else if (rightTable.getAlias() != null
								&& rightTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							foreignField = leftCol.getColumnName();
						}
					}
					Expression rightExp = eq.getRightExpression();
					if (join.isRight()) {
						rightExp = eq.getLeftExpression();
					}
					if (rightExp instanceof Column) {
						rightCol = (Column) rightExp;
						if (rightTable.getAlias() == null
								&& rightCol.getTable().getName().equals(rightTable.getName())) {
							foreignField = rightCol.getColumnName();
						} else if (rightTable.getAlias() != null
								&& rightTable.getAlias().getName().equals(rightCol.getTable().getName())) {
							foreignField = rightCol.getColumnName();
						} else if (leftTable.getAlias() == null
								&& leftCol.getTable().getName().equals(leftTable.getName())) {
							localField = rightCol.getColumnName();
						} else if (leftTable.getAlias() != null
								&& leftTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							localField = rightCol.getColumnName();
						}
					}
					obj.addLookup(rightTable.getName(), localField, foreignField, rightTable.getName());
				}

				for (int i = 0; i < selectItems.size(); i++) {
					SelectItem item = selectItems.get(i);
					if (item instanceof SelectExpressionItem) {
						SelectExpressionItem expressionItem = (SelectExpressionItem) item;
						Expression expression = expressionItem.getExpression();
						if (expression instanceof Column) {
							Column column = (Column) expression;
							if (column.getTable().getName().equals(rightTable.getName())) {
								obj.addProject(expression.toString(), rightTable.getName());
							} else if (column.getTable().getName().equals(leftTable.getName())
									|| (leftTable.getAlias() != null
											&& leftTable.getAlias().getName().equals(column.getTable().getName()))) {
								obj.addProject(expression.toString(), leftTable.getName());
							} else if (rightTable.getAlias() != null
									&& rightTable.getAlias().getName().equals(column.getTable().getName())) {
								obj.addProject(expression.toString(), rightTable.getName());
							}
						}
					}
				}

			}
		}

		MatchCondition conditions = convertWhere(whereExpression);
		obj.setMatchPairs(conditions);

		return obj.toString();

	}

	/**
	 * @param str
	 * @param list
	 * @return
	 */
	public boolean containsCaseInsensitive(String str, List<String> list) {
		for (String string : list) {
			if (string.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to where expressions in a SQL query
	 * 
	 * @param whereExpression
	 * @return
	 */
	public MatchCondition convertWhere(Expression whereExpression) {

		MatchCondition conditions = new MatchCondition();

		if (whereExpression instanceof AndExpression) {

			AndExpression whereExp = (AndExpression) whereExpression;
			Expression leftExp = whereExp.getLeftExpression();
			Expression rightExp = whereExp.getRightExpression();

			MatchCondition innerPairLeft = convertWhere(leftExp);
			conditions.getMatchPair().putAll(innerPairLeft.getMatchPair());
			MatchCondition innerPairRight = convertWhere(rightExp);
			conditions.getMatchPair().putAll(innerPairRight.getMatchPair());
		}

		else if (whereExpression instanceof OrExpression) {

			OrExpression whereExp = (OrExpression) whereExpression;
			Expression leftExp = whereExp.getLeftExpression();
			Expression rightExp = whereExp.getRightExpression();

			MatchCondition innerPair = new MatchCondition();

			innerPair.setMatchPairs(conditions.getMatchPair());
			conditions.addOrCondition(convertWhere(leftExp).getMatchPair());

			if (rightExp instanceof AndExpression) {
				AndExpression andExp = (AndExpression) rightExp;
				Expression leftAndExp = andExp.getLeftExpression();
				Expression rightAndExp = andExp.getRightExpression();
				MatchCondition left = convertWhere(leftAndExp);
				conditions.addOrCondition(left.getMatchPair());

				MatchCondition right = convertWhere(rightAndExp);
				conditions.getMatchPair().putAll(right.getMatchPair());
			} else {
				innerPair.setMatchPairs(conditions.getMatchPair());
				conditions.addOrCondition(convertWhere(rightExp).getMatchPair());

			}
		}

		else if (whereExpression instanceof NotEqualsTo) {
			NotEqualsTo whereExp = (NotEqualsTo) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof EqualsTo) {
			EqualsTo whereExp = (EqualsTo) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof GreaterThan) {
			GreaterThan whereExp = (GreaterThan) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof MinorThan) {
			MinorThan whereExp = (MinorThan) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof GreaterThanEquals) {
			GreaterThanEquals whereExp = (GreaterThanEquals) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof MinorThanEquals) {
			MinorThanEquals whereExp = (MinorThanEquals) whereExpression;
			conditions.addCondition(whereExp.getStringExpression(), whereExp.getLeftExpression().toString(),
					whereExp.getRightExpression());
		}

		else if (whereExpression instanceof InExpression) {
			InExpression whereExp = (InExpression) whereExpression;
			ItemsList items = whereExp.getRightItemsList();
			String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
			conditions.addCondition("IN", whereExp.getLeftExpression().toString(), values);

		}

		else if (whereExpression instanceof LikeExpression) {
			LikeExpression exp = (LikeExpression) whereExpression;
			String rightExpression = exp.getRightExpression().toString().replace("'", "");
			conditions.addCondition(exp.getStringExpression(), exp.getLeftExpression().toString(), rightExpression);
		}

		return conditions;
	}

}
