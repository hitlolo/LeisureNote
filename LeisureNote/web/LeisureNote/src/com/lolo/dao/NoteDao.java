package com.lolo.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
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
import com.lolo.helper.TypeTransformer;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class NoteDao 
{
	
	public boolean InsertNote(JSONObject note)
	{
		String sql_insert = "insert into leisurenote.note"+
					 "(iduser,idbook,title,text,image,sound,soundname,handwriting,createtime,updatetime)"+
				     "values(?,?,?,?,?,?,?,?,?,?)";
		//
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
		try
		{
			long startTime = System.currentTimeMillis();
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
//				statement.setBytes(5, b);
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
			
			
			int affectedRows = statement.executeUpdate();
		
			long endTime = System.currentTimeMillis();
			System.out.println("插入笔记耗费时间："+(endTime-startTime)+"ms");
			
			if(affectedRows>0)
			{
				System.out.println("插入笔记成功");
				return true;
			}
				
			else 
			{
				System.out.println("插入笔记失败");
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
		catch (Base64DecodingException  e) 
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
	
	public int getBookId(Connection connection,JSONObject note)
	{
		String sql_getBookId = "select idnotebook from leisurenote.notebook where iduser=binary(?) and bookname=binary(?)";
		
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql_getBookId);
			statement.setInt(1, note.getInt(Sync.USER_ID));
			statement.setString(2, note.getString(Sync.BOOKNAME));
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
	 * @param columns
	 * @param clause
	 * @return
	 */
	
	public JSONArray getNotes(String columns,String clause)
	{
		JSONArray result_array = new JSONArray();
		
		String sql = "select"+" "+columns+" "+
					 "from leisurenote.note "+
				     clause;
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
		try 
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet set = statement.executeQuery();
			if(set!=null&&set.next())
			{
				set.first();
				do
				{
					JSONObject note = new JSONObject();
					int userid = set.getInt(Sync.COLUMN_ID);
					int bookid = set.getInt(Sync.COLUMN_IDBOOK);
					String title = set.getString(Sync.COLUMN_TITLE);
					if(title==null)
					{
						title = "";
					}
					String text  = set.getString(Sync.COLUMN_TEXT);
					if(text==null)
					{
						text = "";
					}
					
					String image ="";
					Blob blob_i  = set.getBlob(Sync.COLUMN_IMAGE);
					if(blob_i!=null)
					{
//						byte[] b_i = set.getBytes(Sync.COLUMN_IMAGE);
						byte[] b_i = blob_i.getBytes(1, (int)blob_i.length());
						if(b_i!=null)
						{
							image = Base64.encode(b_i,b_i.length);
						}
						else
						{
							image = "";
						}
						
					}
//					
					
					String sound;
					
					Blob blob_s  = set.getBlob(Sync.COLUMN_SOUND);
					if(blob_s!=null)
					{
						byte[] b_s =  blob_s.getBytes(1, (int) blob_s.length());
						sound = Base64.encode(b_s,b_s.length);
					}
					else
					{
						sound = "";
					}
					
					
					String sound_n = set.getString(Sync.COLUMN_SOUND_N);
					if(sound_n==null)
					{
						sound="";
					}
					String hand;
					
					Blob blob_h  = set.getBlob(Sync.COLUMN_HAND);
					if(blob_h!=null)
					{
						byte[] b_h =  TypeTransformer.blobToBytes(blob_h);
						hand = Base64.encode(b_h,b_h.length);
					}
					else
					{
						hand = "";
					}
				
					
					Timestamp c_time = set.getTimestamp(Sync.COLUMN_CTIME);
					Long ctime = c_time.getTime();
					
					Timestamp u_time = set.getTimestamp(Sync.COLUMN_UTIME);
					Long utime = u_time.getTime();
					
					try 
					{
						note.put(Sync.COLUMN_ID, userid);
						note.put(Sync.COLUMN_IDBOOK, bookid);
						note.put(Sync.COLUMN_TITLE, title);
						note.put(Sync.COLUMN_TEXT, text);
						note.put(Sync.COLUMN_IMAGE, image);
						note.put(Sync.COLUMN_SOUND, sound);
						note.put(Sync.COLUMN_SOUND_N, sound_n);
						note.put(Sync.COLUMN_HAND, hand);
						note.put(Sync.COLUMN_CTIME, ctime);
						note.put(Sync.COLUMN_UTIME, utime);
						
						System.out.println(note);
						result_array.put(note);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
								
				}
				while(set.next());
			}
			
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally
		{
			dbhelper.closeConnection(connection);
		}
		return result_array;
	}
	
	
	public boolean deleteNote(JSONObject note)
	{
		String sql_delete = "delete from leisurenote.note where ";
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		
		try
		{
			long startTime = System.currentTimeMillis();
		
			String where  = note.getString("where");
			int    iduser = note.getInt("iduser");
			long    ctime = note.getLong("ctime");
			sql_delete = sql_delete + where;
			System.out.println(sql_delete);
			PreparedStatement statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, iduser);
			statement.setTimestamp(2, new Timestamp(ctime));
			
			int affectedRow = statement.executeUpdate();
			long endTime = System.currentTimeMillis();
			System.out.println("插入笔记耗费时间："+(endTime-startTime)+"ms");
			if(affectedRow>0)
			{
				System.out.println("删除笔记成功");
				return true;
			}
				
			else 
			{
				System.out.println("删除笔记失败");
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
	
	
	public boolean doUpdateNote(JSONObject json_note)
	{
	
		String sql_update = "UPDATE leisurenote.note " +
				"SET idbook=?,title=?,text=?,image=?,sound=?,soundname=?,handwriting=?,createtime=?,updatetime=? " +
				"WHERE iduser=? and createtime=?";
		
		
		DatabaseHelper dbhelper = new DatabaseHelper();		
		Connection connection   = dbhelper.openConnection();
		try
		{
			long startTime = System.currentTimeMillis();
			
			JSONObject note = json_note.getJSONObject("note");
			System.out.println(note.toString());
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
			
			statement.setInt(10,json_note.getInt("iduser"));
			//statement.setTimestamp(10, new Timestamp(note.getLong(Sync.CTIME)));
			
	//		String temp = note.getString(Sync.CLAUSE);
	//		JSONObject tem_json = new JSONObject(temp);
				
			statement.setTimestamp(11, new Timestamp(json_note.getLong("ctime")));
			//System.out.println(statement.toString());
			int affectRows = statement.executeUpdate();
			//System.out.println("update+ "+affectRows);
			//System.out.println(note.toString());
			long endTime = System.currentTimeMillis();
			System.out.println("插入笔记耗费时间："+(endTime-startTime)+"ms");
			if(affectRows>0)
			{
				System.out.println("更新笔记成功");
				return true;
			}
				
			else 
			{
				System.out.println("更新笔记失败");
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
			dbhelper.closeConnection(connection);
		}
		return false;
	}
}
