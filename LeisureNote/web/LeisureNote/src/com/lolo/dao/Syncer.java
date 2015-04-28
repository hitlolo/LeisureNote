package com.lolo.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.helper.DatabaseHelper;
import com.lolo.helper.Sync;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class Syncer 
{
	/**
	 * 
	 * @param logs
	 * @return
	 */
	static int  iduser  = 0;
	
	public boolean doSync(JSONObject logs)
	{
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		boolean isSuccess = false;
		long logtime = 0;
		
		try 
		{	
			long startTime = System.currentTimeMillis();
			JSONArray log_array = logs.getJSONArray("logs");
			if(log_array.length()==0)
			{
				System.out.println("无需同步");
			}
			for(int i = 0;i<log_array.length();i++)
			{
				JSONObject log = log_array.getJSONObject(i);
				if(iduser==0)
				{
					iduser = log.getInt(Sync.USER_ID);
				}
//				if(logtime<log.getLong(Sync.LOGTIME))
//				{
//					logtime = log.getLong(Sync.LOGTIME);
//				}
				System.out.println("同步记录："+i+" "+log);
				isSuccess = this.doSyncEachLog(log,connection);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("同步耗时："+(endTime-startTime)+"ms");
//			if(isSuccess)
//			{
//				doUpdateLogtime(logtime,iduser);
//			}
			
			
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		finally
		{
			dbhelper.closeConnection(connection);
			System.out.println("同步完成");
		}
		return isSuccess;
	}
	
	/**
	 * 
	 * @param log
	 * @param connection
	 * @return
	 */
	private boolean doSyncEachLog(JSONObject log,Connection connection)
	{
		long logtime;
		boolean isSuccess = false;
		try 
		{
			int table = log.getInt(Sync.TABLE);
			switch(table)
			{
				
				case Sync.TABLE_NOTE:
					logtime = log.getLong(Sync.LOGTIME);
					isSuccess = doSyncNoteLog(log, connection);
					if(isSuccess)
					{
						doUpdateLogtime(logtime,iduser);
					}
					break;
				case Sync.TABLE_BOOK:
					logtime = log.getLong(Sync.LOGTIME);
					isSuccess = doSyncBookLog(log, connection);
					if(isSuccess)
					{
						doUpdateLogtime(logtime,iduser);
					}
					break;						
			}
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
	/**
	 * 
	 * @param log
	 * @param connection
	 * @return
	 */
	private boolean doSyncNoteLog(JSONObject log,Connection connection)
	{
		boolean isSuccess = false;
		try 
		{
			int action = log.getInt(Sync.ACTION);
			switch(action)
			{
				case Sync.ACTION_INSERT:
					isSuccess = doInsertNoteSync(log,connection);
					break;
				case Sync.ACTION_UPDATE:
					isSuccess = doUpdateNoteSync(log,connection);
					break;
				case Sync.ACTION_DELETE:
					isSuccess = doDeleteNoteSync(log,connection);
					break;			
			}
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
	/**
	 * 
	 * @param log
	 * @param connection
	 * @return
	 */
	private boolean doSyncBookLog(JSONObject log,Connection connection)
	{
		System.out.print("books");
		boolean isSuccess = false;
		try 
		{
			int action = log.getInt(Sync.ACTION);//取得操作类型
			switch(action)
			{
				case Sync.ACTION_INSERT:
					isSuccess = doInsertBookSync(log,connection);//新增
					break;
				case Sync.ACTION_UPDATE:
					isSuccess = doUpdateBookSync(log,connection);//修改
					break;
				case Sync.ACTION_DELETE:
					isSuccess = doDeleteBookSync(log,connection);//删除
					break;			
			}
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
	
	/**
	 * 
	 * @param note
	 * @param connection
	 * @return
	 */
	private boolean doInsertNoteSync(JSONObject note,Connection connection)
	{
		String sql_insert = "insert into leisurenote.note"+
							"(iduser,idbook,title,text,image,sound,soundname,handwriting,createtime,updatetime)"+
							"values(?,?,?,?,?,?,?,?,?,?)";

		try
		{
			PreparedStatement statement = connection.prepareStatement(sql_insert);
			statement.setInt(1, note.getInt(Sync.USER_ID));
			statement.setInt(2, getBookId(connection,note));
			statement.setString(3,((note.getString(Sync.TITLE).equals(""))?"无标题笔记":note.getString(Sync.TITLE)));
			statement.setString(4,((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT)));
			if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(5, bis);
			}
			else if(note.getString(Sync.IMAGE).equals(""))
			{
				InputStream in = null;
				statement.setBlob(5, in);
			}
		
			
			if(!note.getString(Sync.AUDIO).equals(""))
			{
				String audio = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(audio);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(6, bis);
			}
			else if (note.getString(Sync.AUDIO).equals(""))
			{
				InputStream in = null;
				statement.setBlob(6, in);
			}
			statement.setString(7,((note.getString(Sync.AUDIO_NAME).equals(""))?null:note.getString(Sync.AUDIO_NAME)));
			
			if(!note.getString(Sync.HAND).equals(""))
			{
				String audio = note.getString(Sync.HAND);
				byte[] b = Base64.decode(audio);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(8, bis);
			}
			else if(note.getString(Sync.HAND).equals(""))
			{
				InputStream in = null;
				statement.setBlob(8, in);
			}
			
			statement.setTimestamp(9, new Timestamp(note.getLong(Sync.CTIME)));
			statement.setTimestamp(10, new Timestamp(note.getLong(Sync.UTIME)));
			
			
			int affectRows = statement.executeUpdate();
			if(affectRows>0)
			{
				return true;
			}
			else 
			{
				System.out.println("插入记录失败："+note);
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
		catch (Base64DecodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			
		}
		return false;
	
	}
	
	/**
	 * 
	 * @param note
	 * @param connection
	 * @return
	 */
	private boolean doUpdateNoteSync(JSONObject note,Connection connection)
	{
		String sql_update = "UPDATE leisurenote.note " +
							"SET idbook=?,title=?,text=?,image=?,sound=?,soundname=?,handwriting=?,createtime=?,updatetime=? " +
							"WHERE iduser=? and createtime=?";
		try
		{
			
			PreparedStatement statement = connection.prepareStatement(sql_update);
			statement.setInt(1, getBookId(connection,note));
			statement.setString(2,((note.getString(Sync.TITLE).equals(""))?null:note.getString(Sync.TITLE)));
			statement.setString(3,((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT)));
			if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(4, bis);
			}
			else if(note.getString(Sync.IMAGE).equals(""))
			{
				InputStream in = null;
				statement.setBlob(4, in);
			}
		
			
			if(!note.getString(Sync.AUDIO).equals(""))
			{
				String audio = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(audio);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(5, bis);
			}
			else if (note.getString(Sync.AUDIO).equals(""))
			{
				InputStream in = null;
				statement.setBlob(5, in);
			}
			statement.setString(6,((note.getString(Sync.AUDIO_NAME).equals(""))?null:note.getString(Sync.AUDIO_NAME)));
			
			if(!note.getString(Sync.HAND).equals(""))
			{
				String audio = note.getString(Sync.HAND);
				byte[] b = Base64.decode(audio);
				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				statement.setBlob(7, bis);
			}
			else if(note.getString(Sync.HAND).equals(""))
			{
				InputStream in = null;
				statement.setBlob(7, in);
			}
			
			statement.setTimestamp(8, new Timestamp(note.getLong(Sync.CTIME)));
			statement.setTimestamp(9, new Timestamp(note.getLong(Sync.UTIME)));
			
			statement.setInt(10,note.getInt("iduser"));
//			statement.setTimestamp(10, new Timestamp(note.getLong(Sync.CTIME)));
			
			String temp = note.getString(Sync.CLAUSE);
			JSONObject tem_json = new JSONObject(temp);
				
			statement.setTimestamp(11, new Timestamp(tem_json.getLong("ctime")));
//			System.out.println(statement.toString());
			int affectRows = statement.executeUpdate();
//			System.out.println("update+ "+affectRows);
//			System.out.println(note.toString());
			if(affectRows>0)
			{
				return true;
			}
			else 
			{
				System.out.println("更新记录失败："+note);
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
		catch (Base64DecodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			
		}
		
		return false;
	}
	
	
	/**
	 * 
	 * @param log
	 * @param connection
	 * @return
	 */
	private boolean doDeleteNoteSync(JSONObject log,Connection connection)
	{
		String sql_delete = "delete from leisurenote.note "+
				"where ";
		//
		
		
		try
		{
		String clause = log.getString(Sync.CLAUSE);
		JSONObject json_clause = new JSONObject(clause);
		String where = json_clause.getString("where");
		long time = json_clause.getLong("clause");
		sql_delete = sql_delete + where+"=?";
		System.out.println(sql_delete);
		PreparedStatement statement = connection.prepareStatement(sql_delete);
		statement.setTimestamp(1, new Timestamp(time));
		int affectRows = statement.executeUpdate();
		if(affectRows>0)
		{
			return true;
		}
		else 
		{
			System.out.println("删除记录失败："+log);
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
		
		}
		return false;
	}
	
	/**
	 * 
	 * @param note
	 * @param connection
	 * @return
	 */
	private boolean doInsertBookSync(JSONObject log,Connection connection)
	{
		String sql_insert = "insert into leisurenote.notebook"+
				 "(iduser,bookname,createtime)"+
			     "values(?,?,?)";

		try
		{
			PreparedStatement statement = connection.prepareStatement(sql_insert);
			int iduser = log.getInt(Sync.BOOK_USER);
			String bookname = log.getString(Sync.BOOK_NAME);
			long ctime = log.getLong(Sync.BOOK_CTIME);
			
			statement.setInt(1, iduser);
			statement.setString(2, bookname);
			statement.setTimestamp(3, new Timestamp(ctime));
			
			
			
			int affectedRows = statement.executeUpdate();
			if(affectedRows>0)
				return true;
			else 
			{
				System.out.println("插入笔记本记录失败："+log);
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
		
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param note
	 * @param connection
	 * @return
	 */
	private boolean doUpdateBookSync(JSONObject book,Connection connection)
	{
// 		clause = "iduser=? and createtime=?"; 	  	
//		book.put("clause", clause);
//		book.put(Sync.BOOK_NAME, to_name);
//		book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//		book.put(Sync.BOOK_USER,iduser);
		String sql_update = "UPDATE leisurenote.notebook " +
				"SET bookname=?, createtime=?"+
				"where ";
//		clause = "iduser=? and createtime=?"; 	  
		try
		{
			
			String clause = book.getString(Sync.CLAUSE);
			sql_update = sql_update+clause;
			PreparedStatement statement = connection.prepareStatement(sql_update);
			int    iduser   = book.getInt(Sync.BOOK_USER);
			long   ctime    = book.getLong(Sync.BOOK_CTIME);
			String bookname = book.getString(Sync.BOOK_NAME);
			
			statement.setString(1, bookname);
			statement.setTimestamp(2, new Timestamp(ctime));
			statement.setInt(3, iduser);
			statement.setTimestamp(4, new Timestamp(ctime));
			
			System.out.print("sync:"+statement.toString());
			int affectedRow = statement.executeUpdate();
			if(affectedRow==0)
			{
				System.out.println("更新笔记本记录失败："+book);
				return false;
			}
			else return true;
			
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
			
		}
		return false;
	}
	
	/**
	 * 
	 * @param note
	 * @param connection
	 * @return
	 */
	private boolean doDeleteBookSync(JSONObject log,Connection connection)
	{
		String sql_delete = "delete from leisurenote.notebook "+
				"where ";
		//
		
		try
		{

//			clause = "iduser=? and createtime=?";
//			book.put("clause", clause);
//			book.put(Sync.BOOK_NAME, map.get(Sync.BOOK_NAME).toString());
//			book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//			book.put(Sync.BOOK_USER,this.getUserID(sqliteHelper));
			String clause = log.getString(Sync.CLAUSE);
	    	int iduser      = log.getInt(Sync.BOOK_USER);
	    	long ctime      = log.getLong(Sync.BOOK_CTIME);
			sql_delete = sql_delete + clause;
			System.out.println(sql_delete);
			PreparedStatement statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, iduser);
			statement.setTimestamp(2, new Timestamp(ctime));
			int affectRows = statement.executeUpdate();
			if(affectRows==0)
			{
				System.out.println("删除笔记本记录失败："+log);
				return false;
			}
			else return true;
			
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
		
		}
		return false;
	}
	
	/**
	 * 
	 * @param connection
	 * @param note
	 * @return
	 */
	public int getBookId(Connection connection,JSONObject note)
	{
		String sql_getBookId = "select idnotebook from leisurenote.notebook where iduser=binary(?) and bookname=binary(?)";
		
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_getBookId);
			statement.setInt(1, note.getInt("iduser"));
			statement.setString(2, note.getString("bookname"));
			ResultSet set = statement.executeQuery();
			if(set.next())
			{
				return set.getInt("idnotebook"); 
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;		
	}
	
	/**
	 * 
	 * @param logtime
	 * @param userid
	 */
	private void doUpdateLogtime(long logtime,int userid)
	{
		String sql = "UPDATE `leisurenote`.`synclog` SET `logtime`=? WHERE `iduser`=?";
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, logtime);
			statement.setInt(2, userid);
			statement.executeUpdate();
			System.out.println("更新同步时间");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbhelper.closeConnection(connection);
		}
	}
	
	
}
