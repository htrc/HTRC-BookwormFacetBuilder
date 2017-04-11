package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BookwormFacet {
	private String facetName; // name of the facet
	private String tableName; // the table in the Bookworm MySQL db that holds data about this facet
	private String dbcodeFieldName; // the field in the above table that contains "dbcode" values
	private String valueFieldName; // the field in the above table that contains facet values
	private String facetValueOrder; // the order in which facet values are listed in the output
	private int maxValues; // the maximum no. of facet values required in the output
	
	public BookwormFacet(String facetName, String tableName, String dbcodeFieldName, String valueFieldName,
			             String facetValueOrder, int maxValues) {
		this.facetName = facetName;
		this.tableName = tableName;
		this.dbcodeFieldName = dbcodeFieldName;
		this.valueFieldName = valueFieldName;
		this.facetValueOrder = facetValueOrder;
		this.maxValues = maxValues;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	public String getFacetName() {
		return this.facetName;
	}
	
	// create the MySQL query used to obtain maxValues no. of facet values and ids in the desired order
	private String createQuery() {
		String query = "select " + dbcodeFieldName + ", " + valueFieldName + " from " + tableName;
		if (facetValueOrder.equals("alphabetical")) {
			query = query + " order by " + valueFieldName;
		}
		if (maxValues >= 0) {
			query = query + " limit " + maxValues;
		}
		return query;
	}
	
	// build the ui_components-style JSON object for this facet
	public JsonObject facetMetadata(HTRCBookwormDBClient htrcBookwormDBClient) throws SQLException {
		ResultSet rs = htrcBookwormDBClient.executeQuery(createQuery(), tableName);
		if (rs == null) {
			System.err.println(facetName + " not included in result.");
			return null;
		}
		
		JsonObject description = new JsonObject();
		JsonArray sortOrder = new JsonArray();
		while (rs.next()) {
			JsonObject valueAsJson = facetValueToJSON(rs);
			String valueId = valueAsJson.get("dbcode").getAsString();
			description.add(valueId, valueAsJson);
			sortOrder.add(valueId);
		}
		htrcBookwormDBClient.close();
		
		JsonObject result = new JsonObject();
		result.add("categorical", createCategorical(description, sortOrder));
		result.addProperty("dbfield", tableName);
		result.addProperty("name", facetName);
		result.addProperty("type", "categorical");
		return result;
	}
	
	private JsonObject facetValueToJSON(ResultSet rs) throws SQLException {
		JsonObject result = new JsonObject();
		result.addProperty("dbcode", rs.getString(dbcodeFieldName));
		String value = rs.getString(valueFieldName);
		result.addProperty("name", value);
		result.addProperty("shortname", value);
		return result;
	}
	
	private JsonObject createCategorical(JsonObject description, JsonArray sortOrder) {
		JsonObject result = new JsonObject();
		result.add("descriptions", description);
		result.add("sort_order", sortOrder);
		return result;
	}
}
