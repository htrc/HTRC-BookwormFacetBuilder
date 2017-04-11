package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

interface HTRCBookwormDBSettings {
	public String getHTRCBookwormDBServer();
	public String getHTRCBookwormDBName();
	public String getHTRCBookwormDBUser();
	public String getHTRCBookwormDBPsswd();
}

public class HTRCBookwormDBClient {
	private String htrcBookwormDBEndpoint, htrcBookwormDBUser, htrcBookwormDBPsswd;
	
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public HTRCBookwormDBClient(HTRCBookwormDBSettings settings) {
		htrcBookwormDBEndpoint = settings.getHTRCBookwormDBServer() + "/" + settings.getHTRCBookwormDBName();
		htrcBookwormDBUser = settings.getHTRCBookwormDBUser();
		htrcBookwormDBPsswd = settings.getHTRCBookwormDBPsswd();
	}
	
	public ResultSet executeQuery(String query, String tableName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + htrcBookwormDBEndpoint, htrcBookwormDBUser,
					                              htrcBookwormDBPsswd);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
		} catch (Exception e) {
			System.err.println("Exception while processing table " + tableName + ": " + e);
			resultSet = null;
		}
		
		return resultSet;
	}
	
	private void printResultSet(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int numCols = rsmd.getColumnCount();
		
		// print column names
		for (int i = 1; i <= numCols; i++) {
			System.out.print(rsmd.getColumnName(i));
			if (i < numCols) {
				System.out.print(", ");
			}
		}
		System.out.println();
		
		// print rows in the result
		while (resultSet.next()) {
			for (int i = 1; i <= numCols; i++) {
				System.out.print(resultSet.getString(i));
				if (i < numCols) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
	
	public void close() {
		try {	
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			System.err.println("HTRCBookwormDBClient: exception during close: " + e);
		}
	}

}
