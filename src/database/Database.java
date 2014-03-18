package database;

import java.sql.*;

public class Database {
	private static Connection conn = null;
	private static String url = "jdbc:mysql://mysql.stud.ntnu.no/";
	private static String dbName = "test";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String userName = "simenha_admin";
	private static String password = "gruppe44";
	
	public Database() {
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ResultSet readQuery(String sql) {
		Statement s;
		try {
			s = conn.createStatement();
			ResultSet rs;
			rs = s.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void updateQuery(String sql) {
		Statement s;
		try {
			s = conn.createStatement();
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
}

