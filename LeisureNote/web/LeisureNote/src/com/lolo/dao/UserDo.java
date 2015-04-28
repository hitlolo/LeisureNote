package com.lolo.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;

import com.lolo.entity.User;
import com.lolo.helper.DatabaseHelper;

public class UserDo implements UserDao
{

	public User Login(String username, String password) 
	{
		// TODO Auto-generated method stub
		String sql_query = "select id,username,password from usertable where username=?and password=?";
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet result = statement.executeQuery();
			
			if(result.next())
			{
				User user = new User();
				user.setId(result.getInt(1));
				user.setUsername(username);
				user.setPassword(password);
				return user;
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		
		return null;
	}

	public boolean Register(User user) 
	{
		// TODO Auto-generated method stub
		String sql_insert = 
				"insert into leisurenote.userTable" +
				"(username,password,name,gender,remark) " +
				"values(?,?,?,?,?)";
		//创建账户后，给账户新增一个默认笔记本和默认笔记
		String sql_notebook = 
				"insert into leisurenote.notebook" +
				"(bookname,iduser,createtime)"+
				"values(?,?,?)";
		String sql_selectid = "select id from leisurenote.usertable where username=binary(?)";
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
		try 
		{
			
			
			PreparedStatement statement = connection.prepareStatement(sql_insert);
			
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getGender());
			statement.setString(5, user.getRemark());
			
			int affectedRows = statement.executeUpdate();
			System.out.println(affectedRows);
			boolean isSuccess;
			if( affectedRows >0 )
			{
				
				//如果创建账户成功，则再新增一个默认笔记本
				//1.取得用户ID 用作笔记本表的外键
				PreparedStatement statementId = connection.prepareStatement(sql_selectid);
				statementId.setString(1, user.getUsername());
				ResultSet result = statementId.executeQuery();
				
				if(result.next())
				{
					int id = result.getInt("id");
					//2.插入默认笔记本
					PreparedStatement statementBook = connection.prepareStatement(sql_notebook);
					statementBook.setString(1, user.getName()+"的默认笔记本");
					statementBook.setInt(2, id);
					statementBook.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					statementBook.execute();
				}
				isSuccess = true;
			}
			else
			{
				isSuccess = false;
			}
		
			System.out.println(isSuccess);
			return isSuccess;
			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		
		return false;
	}
	
	public User RegisterToUser(User user) 
	{
		// TODO Auto-generated method stub
		String sql_insert = 
				"insert into leisurenote.userTable" +
				"(username,password,name,gender,remark) " +
				"values(?,?,?,?,?)";
		//创建账户后，给账户新增一个默认笔记本和默认笔记
		String sql_notebook = 
				"insert into leisurenote.notebook" +
				"(bookname,iduser,createtime)"+
				"values(?,?,?)";
		String sql_selectid = "select id from leisurenote.usertable where username=binary(?)";
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		long startTime = System.currentTimeMillis();
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_insert);
			
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getGender());
			statement.setString(5, user.getRemark());
			
			int affectedRows = statement.executeUpdate();
			System.out.println(affectedRows);
			boolean isSuccess;
			if( affectedRows >0 )
			{
				
				//如果创建账户成功，则再新增一个默认笔记本
				//1.取得用户ID 用作笔记本表的外键
				PreparedStatement statementId = connection.prepareStatement(sql_selectid);
				statementId.setString(1, user.getUsername());
				ResultSet result = statementId.executeQuery();
				
				if(result.next())
				{
					int id = result.getInt("id");
					//2.插入默认笔记本
					PreparedStatement statementBook = connection.prepareStatement(sql_notebook);
					statementBook.setString(1, user.getName()+"的默认笔记本");
					statementBook.setInt(2, id);
					statementBook.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					statementBook.execute();
					
					
					//初始化同步时间
					String sql_sync = "insert into `leisurenote`.`synclog` " +
									  "(iduser,logtime)"+
									  "values(?,?)";
					statementBook.clearParameters();
					statementBook.close();
					statementBook = connection.prepareStatement(sql_sync);
					statementBook.setInt(1, id);
					statementBook.setInt(2, 0);
					statementBook.execute();
					statementBook.close();
					
				}
				isSuccess = true;
			}
			else
			{
				isSuccess = false;
			}
			
			if(isSuccess)
			{
				String query_all = "select * from leisurenote.usertable where username=?";
				PreparedStatement statementAll = connection.prepareStatement(query_all);
				statementAll.setString(1, user.getUsername());
				
				ResultSet result = statementAll.executeQuery();
				
				
				if(result.next())
				{
					User userAllInfo = new User();
					userAllInfo.setId(result.getInt("id"));
					userAllInfo.setPassword(result.getString("password"));
					userAllInfo.setUsername(result.getString("username"));
					userAllInfo.setName(result.getString("name"));
					userAllInfo.setGender(result.getString("gender"));
					userAllInfo.setHeadimg_url(result.getString("headurl"));
					userAllInfo.setRemark(result.getString("remark"));
					
					long endTime = System.currentTimeMillis();
					System.out.println("注册耗费时间："+(endTime-startTime)+"ms");
					
					return userAllInfo;
				}
				else
					return null;
				
			}
			
			else
			{
				return null;
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		return null;
	}
	
	
	
	
	public boolean isOccuipiedUsername(String str)
	{
		String sql_query = "select id from leisurenote.usertable where username=binary(?)"; 
		
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try 
		{
			long startTime = System.currentTimeMillis();
			PreparedStatement statement = connection.prepareStatement(sql_query);
			
			statement.setString(1, str);	
			ResultSet result = statement.executeQuery();
			
			long endTime = System.currentTimeMillis();
			System.out.println("冲突检测耗费时间："+(endTime-startTime)+"ms");
			if(result.next())
			{	
				System.out.println(statement.toString());
				System.out.println(result.toString());
				System.out.println("occupied");
				return true;
			}
			else
			{
				System.out.println("free");
				return false;
			}
			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		
		return false;
	}
	
	
	public boolean isOccuipiedNickname(String str)
	{
		String sql_query = "select id from leisurenote.usertable where name=binary(?)"; 
		
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_query);
			
			statement.setString(1, str);	
			ResultSet result = statement.executeQuery();
			
			if(result.next())
			{
				System.out.println(statement.toString());
				System.out.println(result.toString());
				System.out.println("occupied");
				return true;
			}
			else
			{
				return false;
			}
			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		
		return false;
	}

	public JSONArray getUsersBooks(String username) 
	{
		// TODO Auto-generated method stub
		String sql_query  = "select bookname from leisurenote.notebook " +
				            "where iduser=" +
				            "(select id from leisurenote.usertable where username=binary(?))";
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();

		JSONArray resultJSON = null;
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_query);
			
			statement.setString(1, username);	
			ResultSet result = statement.executeQuery();
			resultJSON = new JSONArray();
			int i = 0;
			while(result.next())
			{			
				try 
				{
					//取得的笔记本名都为第一列
					resultJSON.put(i,result.getString(1));
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i+=1;
			}
			System.out.println(resultJSON);
			
			return resultJSON;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		return null;
	}
	
	
	public Long getLogtime(int userid)
	{
//		String sql_query  = "select logtime from leisurenote.synclog " +
//							"where iduser=" +
//							"(select id from leisurenote.usertable where username=binary(?))";
		String sql_query  = "select logtime from leisurenote.synclog " +
							"where iduser=" +
							"?";
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_query);
			
			statement.setInt(1, userid);	
			ResultSet result = statement.executeQuery();
			if(result.next())
			{			
				return result.getLong("logtime");
			}
		}
		catch (SQLException e)
		{
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		}
		finally
		{
		dbhelper.closeConnection(connection);
		}
		return null;
	}

}
