import java.sql.*;
import java.util.ArrayList;


////////////////////////////////////////////
/// Created by Nguyen Huu Ngoc - NGCTeam ///
////////////////////////////////////////////

public class MySQLAccess {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public boolean connect(String url, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public void updateData(String tableName, int id, String column, String value) {
		try {
			preparedStatement = connect
					.prepareStatement("UPDATE " + tableName + " SET " + column + "=" + "'" + value + "' WHERE id=?");
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertData(String tableName, String[] columns, String[] values) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("INSERT INTO ");
			builder.append(tableName);
			builder.append("(");
			builder.append(columns[0]);
			for (int i = 1; i < columns.length; i++) {
				builder.append(",");
				builder.append(columns[i]);
			}
			builder.append(") VALUE( ?");
			for (int i = 1; i < values.length; i++) {
				builder.append(",?");
			}
			builder.append(")");
			preparedStatement = connect.prepareStatement(builder.toString());
			for (int i = 0; i < values.length; i++) {
				preparedStatement.setString(i + 1, values[i]);
			}
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showDataTable(String tableName) {
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + tableName);
			writeResultSet(resultSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteByID(String tableName, int id) {
		try {
			preparedStatement = connect.prepareStatement("DELETE FROM " + tableName + " WHERE id=?");
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteAllRecord(String tableName) {
		try {
			preparedStatement = connect.prepareStatement("DELETE FROM " + tableName);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> selectData(String tableName, String column){
		ArrayList<String> data =new ArrayList<String>();
		try {
			resultSet = statement.executeQuery("SELECT "+ column +" FROM " + tableName);
			while (resultSet.next()){
				data.add(resultSet.getString(column));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public void showStructureOfTable(String tableName) {
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + tableName);
			writeMetaData(resultSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
			System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
		}
	}

	public ResultSet getAllData(String tableName){
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + tableName);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			String id = resultSet.getString("id");
			String name = resultSet.getString("name");
			String url = resultSet.getString("url");
			System.out.println("id: " + id);
			System.out.println("name: " + name);
			System.out.println("url: " + url);
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
			System.out.println("Cannot disconnect from MySQL");
		}
	}

}
