package com.lolo.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
 * Author:Lolo
 * Date:2012/3/14
 * Class Name:DatabaseHelper
 * Class Description:to helping the database connection.
 */

public class DatabaseHelper 
{
	
	public Connection openConnection()
	{
		
		//get properties
		Properties properties = new Properties();
		
		String database_Driver   = null;
		String database_url      = null;
		String database_username = null;
		String database_password = null;
		
		try
		{
			properties.load(this.getClass().getClassLoader().getResourceAsStream("DatabaseConfig.properties"));
			
			database_Driver      = properties.getProperty("driver");
			database_url         = properties.getProperty("url");
			database_username    = properties.getProperty("username");
			database_password    = properties.getProperty("password");
			
			//Register Driver
			Class.forName(database_Driver);
			return DriverManager.getConnection(database_url, database_username, database_password);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return null;
	}
	
	
	public void closeConnection(Connection con)
	{
		
		try{
			
			//WARNING: The web application [/LeisureNote] registered the JDBC driver [com.mysql.jdbc.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
			//Modify Needed;
			con.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

}
