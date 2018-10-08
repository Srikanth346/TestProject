package com.acis.downstreemfeed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.acis.downstreemfeed.Config.Enum_Config;

public class DB2Connection {

	private static Config config = null;

	/**
	 * Function Name : releaseTransaction Description : This function is used to
	 * create connection to DB2 Database
	 **/
	public static Connection getConnection() {
		Connection connection = null;
		config = new Config();
		String DB2HostURL = config.getConfigPropValue(Enum_Config.DB2HostURL);
		String userName = config.getConfigPropValue(Enum_Config.UserName);
		String passwd = config.getConfigPropValue(Enum_Config.PassWord);
		try {
			Class.forName("COM.ibm.db2os390.sqlj.jdbc.DB2SQLJDriver");
			connection = DriverManager.getConnection(DB2HostURL, userName, passwd);
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("Unable to Connect to DataBase");
		}
		return connection;
	}

	/**
	 * Function Name : releaseTransaction Description : This function is used to
	 * release transaction from release history to fake batch
	 **/

	public void getLatestTransaction() {

		Connection connectionObject = null;
		try {
			connectionObject = DB2Connection.getConnection();
			String QrySetSchema = "set schema ACISAT7";
			String sqlQry = "select *from transaction where trans_id=14357";
			// set schema
			PreparedStatement preStatement = connectionObject.prepareStatement(QrySetSchema);
			Statement statement = connectionObject.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlQry);
			while(resultSet.next()){
				System.out.println("Customer Name " + resultSet.getString(4));
			}
			// Close Prepared Statement & Connection Object
			preStatement.close();
			statement.close();
			connectionObject.close();
			//System.out.println("Transaction " + tranId + " released sucessfully from Batch");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connectionObject != null) {
				try {
					connectionObject.close();
				} catch (SQLException sqlexp) {
					sqlexp.printStackTrace();
				}

			}
		}
	}
	
	public static void main(String[] args){
		new DB2Connection().getLatestTransaction();
	}
}
