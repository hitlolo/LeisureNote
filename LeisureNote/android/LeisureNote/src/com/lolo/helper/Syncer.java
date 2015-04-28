package com.lolo.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.lolo.tools.FinalDefinition;
import com.lolo.tools.Sync;

public class Syncer 
{
	private Context myContext;
	long    latestTime = 0;

	private Handler myHandler;
	public Syncer(Context context,Handler handler)
	{
		myHandler = handler;
		myContext = context;
	
	}
	
	public void doSync()
	{
		JSONObject logs;
		Long logtime = getSynclogTime();
		boolean syncStatus = false;
		if(logtime!=-1)
		{
			logs = dogetSynclogs(logtime);
			try {
				if(logs.length()==1 && (logs.getJSONArray("logs").length()==0) && logtime!=-1)
				{
					Message msg = new Message();
					msg.what    = FinalDefinition.MESSAGE_NO_SYNC;
					myHandler.sendMessage(msg);
					return;
				}
				else
				{
					syncStatus = doUpdateToServer(logs);
				}
			} 
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(syncStatus)
		{
			
			doDeleteLogsOutOfTime(latestTime);
			Message msg = new Message();
			msg.what    = FinalDefinition.MESSAGE_SUC_SYNC;
			myHandler.sendMessage(msg);
		}
		else if(!syncStatus)
		{
			Message msg = new Message();
			msg.what    = FinalDefinition.MESSAGE_FAIL_SYNC;
			myHandler.sendMessage(msg);
		}
		
	}
	
	
	private long getSynclogTime()
	{	
		long logtime = -1;
		try 
		{
			JSONObject jsonResult;
			int userID = getUserid();
			jsonResult = doGetLogtimeFromServer(userID);
			if(jsonResult!=null)
			{
				logtime = jsonResult.getLong("logtime");
			}
			
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logtime;
	}
	
	private int getUserid()
	{
		int userid = 0;
		SharedPreferences settings = myContext.getSharedPreferences(FinalDefinition.PREFS_NAME, 0);	
    	String username = settings.getString("username", "");
        String password = settings.getString("password", "");
		
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(myContext);
    	sqliteHelper.doOpen();
    	String table     = FinalDefinition.DATABASE_TABLE_USER;
    	String[] columns   = {"id"};
    	String selection = "username=? and password=?" ;
    	
    	String[] selectionArgs = {username,password};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = null;
    	
    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    		
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		if(cursor.moveToFirst())
    		{
    			int index = cursor.getColumnIndex("id");    			
    			userid    = cursor.getInt(index);
    		}
    	}
    	
    	Log.i("sync","userid="+userid+"");
    	cursor.close();
    	sqliteHelper.doClose();
    	return userid;
	}
	
	private JSONObject doGetLogtimeFromServer(int userid)
	{
		JSONObject jsonResult = null;
    	String url           = HttpHelper.Base_Url+"servlet/GetLogtimeServlet";
    	JSONObject parameter = new JSONObject();
    	
    	try 
    	{
			parameter.put("userid", userid);
		}
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try 
		{			
			HttpEntity entity = new StringEntity(parameter.toString());
			jsonResult = HttpHelper.getBookResponseByHttpPostWithJSON(url, entity);	
			return jsonResult;
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return jsonResult;  	
	}
	
	
	private JSONObject dogetSynclogs(Long logtime)
	{
		Log.i("sync",logtime+" logtime");
		SyncHelper syncHelper = new SyncHelper(myContext);
		syncHelper.doOpen();
    	String table       = Sync.DATABASE_TABLE;
    	String[] columns   = null;//return all columns
    	String selection = "logtime>?" ;
    	
    	String[] selectionArgs = {logtime+""};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = "logtime";
    	
    	Cursor cursor = syncHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	JSONArray jsonArray = new JSONArray(); 
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		Log.i("sync",cursor.getCount()+" cursor.count");
    		cursor.moveToFirst();
    		do
    		{
    			jsonArray.put(getALog(cursor));
    		}while(cursor.moveToNext());
    		
    	}
    	cursor.close();
    	syncHelper.doClose();
    	JSONObject jsonLogs = new JSONObject();
    	try 
    	{
			jsonLogs.put("logs", jsonArray);
			
		}
    	catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i("sync",jsonLogs.toString());
    	return jsonLogs;
	}
	
	private JSONObject getALog(Cursor cursor)
	{
		int  userid    = cursor.getInt(0);
		int  action    = cursor.getInt(1);
		long logtime   = cursor.getLong(2);
		int  table     = cursor.getInt(3);
		String wclause = cursor.getString(4);
		String bookname= cursor.getString(5);
		String title   = cursor.getString(6);
		String text    = cursor.getString(7);
		String image   = null;
		byte[] imagebyte = cursor.getBlob(8);
		if(imagebyte!=null)
		{
			image = Base64.encodeToString(imagebyte, Base64.DEFAULT);
		}
		
		String sound   = null;
		byte[] soundbyte = cursor.getBlob(9);
		if(soundbyte!=null)
		{
			sound = Base64.encodeToString(soundbyte, Base64.DEFAULT);
		}
		String soundN  = cursor.getString(10);
		
		String hand   = null;
		byte[] handbyte = cursor.getBlob(11);
		if(handbyte!=null)
		{
			hand = Base64.encodeToString(handbyte, Base64.DEFAULT);
		}
		
		long ctime = cursor.getLong(12);
		long utime = cursor.getLong(13);
		
		JSONObject log = new JSONObject();
		try 
		{
			log.put(Sync.USER_ID, userid);
			log.put(Sync.ACTION, action);
			log.put(Sync.LOGTIME, logtime);
			log.put(Sync.BOOKNAME, bookname);
			log.put(Sync.TABLE, table);
			log.put(Sync.CLAUSE, wclause);
			log.put(Sync.TITLE, title==null?"":title);
			log.put(Sync.TEXT, text==null?"":text);
			log.put(Sync.IMAGE, image==null?"":image);
			log.put(Sync.AUDIO, sound==null?"":sound);
			log.put(Sync.AUDIO_NAME, soundN==null?"":soundN);
			log.put(Sync.HAND, hand==null?"":hand);
			log.put(Sync.CTIME, ctime);
			log.put(Sync.UTIME, utime);
			
			if(this.latestTime<logtime)
			{
				this.latestTime = logtime;
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("sync",log.toString());
		return log;
	}
	
	private boolean doUpdateToServer(JSONObject logs)
	{
		JSONObject jsonResult;
		String url           = HttpHelper.Base_Url+"servlet/SyncServlet";
		Log.i("sync",url);
//    	JSONObject parameter = new JSONObject();
    	
    	try 
		{			
			HttpEntity entity = new StringEntity(URLEncoder.encode(logs.toString(), "utf-8"));
			jsonResult = HttpHelper.getBookResponseByHttpPostWithJSON(url, entity);	
			if(jsonResult!=null)
			{
				if(jsonResult.getString("status").equals("success"))
				{
					return true;
				}
				else return false;
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param logtime
	 */
	
	private void doDeleteLogsOutOfTime(long logtime)
	{
		

		SyncHelper sync = new SyncHelper(myContext);
		sync.doOpen();
		String clause = "logtime<=?";
		String args[] = {logtime+""};
		Log.i("sync","logtime<="+logtime);
		int affectedRows = sync.doDelete(Sync.DATABASE_TABLE, clause,args);
		Log.i("sync", "同步后删除数据！条数为："+affectedRows);
		sync.doClose();
		sync = null;
	}
}
