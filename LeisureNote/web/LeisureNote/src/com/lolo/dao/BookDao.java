package com.lolo.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.helper.DatabaseHelper;
import com.lolo.helper.Sync;

public class BookDao 
{
	public boolean doInsertBook(JSONObject book)
	{
		String sql_insert = "insert into leisurenote.notebook"+
				 "(iduser,bookname,createtime)"+
			     "values(?,?,?)";
	//
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
	
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql_insert);
			int iduser = book.getInt(Sync.BOOK_USER);
			String bookname = book.getString(Sync.BOOK_NAME);
			long ctime = book.getLong(Sync.BOOK_CTIME);
			
			statement.setInt(1, iduser);
			statement.setString(2, bookname);
			statement.setTimestamp(3, new Timestamp(ctime));
			
			
			int affectedRows = statement.executeUpdate();
			if(affectedRows>0)
			{
				System.out.println("插入本子成功");
				return true;
			}
				
			else 
			{
				System.out.println("插入本子失败");
				return false;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
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
	
	
	/**
	 * 
	 */
	
	public boolean doDeleteBook(JSONObject book)
	{
		String sql_delete = "delete from leisurenote.notebook where ";
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
//		String clause = "iduser=? and bookname=? and createtime=?";
//      book.put("clasuse", clause);
//		book.put(Sync.BOOK_NAME, map.get(Sync.BOOK_NAME).toString());
//		book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//		book.put(Sync.BOOK_USER,this.getUserID(sqliteHelper));
		try
		{
			String where    = book.getString("clause");
			int    iduser   = book.getInt(Sync.BOOK_USER);
			long   ctime    = book.getLong(Sync.BOOK_CTIME);
			String bookname = book.getString(Sync.BOOK_NAME);
			
			sql_delete = sql_delete + where;
			System.out.println(sql_delete);
			PreparedStatement statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, iduser);
			statement.setString(2, bookname);
			statement.setTimestamp(3, new Timestamp(ctime));
			
			int affectedRow = statement.executeUpdate();
			if(affectedRow>0)
			{
				System.out.println("删除本子成功");
				return true;
			}			
			else 
			{
				System.out.println("删除本子失败");
				return false;
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		return false;
	}
	
	/**
	 * 
	 */
	public boolean doUpdateBook(JSONObject book)
	{
		String sql_update = "UPDATE leisurenote.notebook " +
				"SET bookname=?, createtime=?"+
				"where ";
		
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try
		{
			
//     		clause = "iduser=? and createtime=? and bookname="+map.get(Sync.BOOK_NAME).toString();
//    		book.put("clause", clause);
//			book.put(Sync.BOOK_NAME, to_name);
//			book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//			book.put(Sync.BOOK_USER,iduser);
			String clause = book.getString("clause");
			sql_update = sql_update+clause;
			PreparedStatement statement = connection.prepareStatement(sql_update);
			int    iduser   = book.getInt(Sync.BOOK_USER);
			long   ctime    = book.getLong(Sync.BOOK_CTIME);
			String bookname = book.getString(Sync.BOOK_NAME);
			
			statement.setString(1, bookname);
			statement.setTimestamp(2, new Timestamp(ctime));
			statement.setInt(3, iduser);
			statement.setTimestamp(4, new Timestamp(ctime));
			
			System.out.print(statement.toString());
			int affectedRow = statement.executeUpdate();
			if(affectedRow>0)
			{
				System.out.println("更新本子成功");
				return true;
			}			
			else 
			{
				System.out.println("更新本子失败");
				return false;
			}
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
		return false;
	}
}
