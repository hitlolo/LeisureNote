package com.lolo.dev;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lolo.helper.HttpHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.helper.SyncHelper;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.NotesAdapter;
import com.lolo.tools.Sync;

public class NotesActivity extends ListActivity 
{

	private ProgressDialog progressLog;
	private List<Map<String, Object>> list;
	private Button         buttonNew;
	private Spinner        spinnerBook;
	
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.notelist);	
    
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.notelist_title);
        
        initProgressDialog();
        initButton();
        initSpinner();
        list = new ArrayList<Map<String, Object>>();  	 
        initList(list);
        
        setLongClick();
       
        
        Message msg = new Message();
		msg.what    = FinalDefinition.MESSAGE_JOBDONE;
		myHandler.sendMessage(msg);
		
		  
	}
	
	/**
     * @name:
     * @Param:null
     * @return:void
     * @Function:init
     */ 
	private void initList(List<Map<String, Object>> list)
	{
		String[] from = {Sync.TITLE,Sync.TEXT,Sync.CTIME,Sync.IMAGE,Sync.AUDIO,Sync.HAND};
		int[] to = {R.id.list_title,R.id.list_content,R.id.list_time,R.id.list_image_item,R.id.list_sound_item,R.id.list_hand_item};
		
		
		Intent intent = this.getIntent();
		int bookid   = intent.getIntExtra("book_id", 0);
		doGetData(list,bookid);
		if(list==null)
		{
			list = new ArrayList<Map<String, Object>>();  	 
		}
		NotesAdapter adapter = new NotesAdapter(this,list,R.layout.list_item,
				from,
				to);
        setListAdapter(adapter);
	}
	
	
	/**
     * @name:initProgressDialog()
     * @Param:null
     * @return:void
     * @Function:init
     */ 
    private void initProgressDialog()
    {
    	progressLog = new ProgressDialog(this);
    	progressLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressLog.setMessage("努力ing...");
    	progressLog.setIndeterminate(false);
		progressLog.show();
    }
    
    
    /**
     * @name:initSpinner()
     * @Param:null
     * @return:void
     * @Function:init
     */ 
    private void initSpinner()
    {
//    	spinnerBook = (Spinner)findViewById(R.id.spinner_chooseBook);
    	//需要添加进度条，提示等待   	
    	//1.从服务器数据库中取出笔记本名称
//    	JSONObject resultJSON      = getBooksFromServer(user);
    	JSONObject resultJSON      = doGetBooks(getIntent().getStringExtra("username"));
    	String[]   resultStrArray  = null;
    	int selection = 0;
    	String book_selected = this.getSelectedBookName();
    	
    	try 
    	{
			JSONArray  result = resultJSON.getJSONArray("books");
			resultStrArray = new String[result.length()+1];
			resultStrArray[0] ="所有笔记";
			for(int i = 1;i < result.length()+1;i++)
			{
				resultStrArray[i] = result.get(i-1).toString();
				if(book_selected!=null )
				{
					if(book_selected.equals(resultStrArray[i]))
					{
						selection = i;
					}
				}
			}
		} 
    	catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//已经取得string数据，开始为spinner设置适配器
   	
    	//取得spinner实例
    	spinnerBook = (Spinner)findViewById(R.id.spinner_notes_books);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.drawable.spinner_font,resultStrArray);
    	adapter.setDropDownViewResource(R.drawable.spinner_list);
    	Log.i("view",adapter.toString());
    	spinnerBook.setAdapter(adapter);
    	spinnerBook.setSelection(selection);
    	spinnerBook.setOnItemSelectedListener
    	(
    			new Spinner.OnItemSelectedListener()
    			{
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
					{
						// TODO Auto-generated method stub
				
						int selected_bookid = doGetSelectedBookId(parent.getAdapter().getItem(position).toString());
						doGetData(list,selected_bookid);
				    	NotesAdapter adapter = (NotesAdapter)getListAdapter();
				    	adapter.notifyDataSetChanged();
						parent.setVisibility(View.VISIBLE);						
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) 
					{
						// TODO Auto-generated method stub					
					}  				
    			}
    	);
    	
    }
    
    /**
     * @name:doGetBooks()
     * @Param:String username
     * @return:JSONObject
     * @Function:do get books from local or server
     */  
    private JSONObject doGetBooks(String user)
    {
    	JSONObject jsonObject = getBooksFromLocal(user);
    	return jsonObject;
    }
    
    /**
     * @name:getBooksFromLocal()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private JSONObject getBooksFromLocal(String username)
    {
    	LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	
    	int user_id = 0;
    	{
	    	String table     = FinalDefinition.DATABASE_TABLE_USER;
	    	String[] columns = {"id"};
	    	String selection = "username=?";
	    	String[] selectionArgs = {username};
	    	String groupBy   = null;
	    	String having    = null;
	    	String orderBy   = null;
	    	
	    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	    	
	    	
	    	if(cursor!=null && cursor.getCount()!=0)
	    	{
	    		if(cursor.moveToFirst())
	    		{
	    			int index = cursor.getColumnIndex("id");
	    			
	    			user_id    = cursor.getInt(index);
		    		Log.i("add",user_id+"  userid");
	    		}
	    		
	    	}
	    	cursor.close();
    	}
    	
    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
    	String[] columns   = {"bookname"};
    	String selection = "iduser=?" ;
    	String[] selectionArgs = {""+user_id};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = null;
    	
    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    
    	
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		JSONArray jsonArray = new JSONArray();
    		if(cursor.moveToFirst())
    		{
    			do
    			{
    				int index   = cursor.getColumnIndex("bookname");
    				String name = cursor.getString(index);	
    				Log.i("add",name);
    				jsonArray.put(name);
    			}
    			while(cursor.moveToNext());
    		}
    		JSONObject jsonObject = new JSONObject();
    		try {
				jsonObject.put("books", jsonArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cursor.close();
			sqliteHelper.doClose();
    		return jsonObject;
    	}
    	else
    	{
    		cursor.close();
    		sqliteHelper.doClose();
    		return null;
    	}
    	
    }
    
    /**
     * 
     */
    private void initButton()
    {
    	buttonNew = (Button)findViewById(R.id.button_new);
    	
    	buttonNew.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Intent intent = getIntent();
						intent.setClass(NotesActivity.this, AddNoteActivity.class);
						intent.putExtra(FinalDefinition.NOTE_MODEL, FinalDefinition.NOTE_NEW);
						startActivity(intent);
						NotesActivity.this.finish();
						
					}
    				
    			}
    	);
    }
    /**
     * @name:
     * @Param:null
     * @return:void
     * @Function:init
     */ 
	private void doGetData(List<Map<String, Object>> list,int bookid)
	{
//		List<Map<String, Object>> list = null;
//		list = this.doGetFromLocal();
		list.clear();
		doGetFromLocal(list,bookid);
//		if(list==null||list.isEmpty())
//		{
//			if(HttpHelper.isConnect(this))
//			{
//				list = this.doGetFromServer(list);
//			}
//		}	   	
	}
		
	/**
     * @name:
     * @Param:null
     * @return:void
     * @Function:init
     */ 
	private List<Map<String,Object>> doGetFromLocal(List<Map<String, Object>> list,int bookid)
	{
		
		
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
    	Cursor cursor = null;
    	
//    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  	 
        
//    	timeList = new HashMap<String,String>();
//    	String time = "";
    	try
    	{


//    		book.put(Sync.BOOK_NAME, bookname);
//			book.put(Sync.BOOK_ID, bookid);
//			book.put(Sync.BOOK_CTIME, ctime);
    		
    		String selection = null;
    		String[] selectionArgs =null;
    		if(bookid!=0)
    		{
    			selection = "idbook=?";   			
    			selectionArgs = new String[]{bookid+""};	    		
    		}    		
    		else if(bookid==0)
    		{
    			selection  = null;
    			selectionArgs = null;
    		}
		    String table     = FinalDefinition.DATABASE_TABLE_NOTE;
		    String[] columns = null;
//		    String selection = null;
//		   	String[] selectionArgs = null;
		   	String groupBy   = null;
		   	String having    = null;
		   	String orderBy   = null;	
	    
	    	cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);	    	
	    	if(cursor!=null && cursor.getCount()!=0)
	    	{
	    		Log.i("sync",cursor.getCount()+"");
	    		cursor.moveToFirst();
	    		do
	    		{
	    			Map<String, Object> map = new HashMap<String, Object>();
	    			int index = cursor.getColumnIndex(Sync.TITLE);
//	    			Log.i("list",index+" title");
	    			String title = cursor.getString(index);
//	    			Log.i("list",index+" "+title);
	    			index = cursor.getColumnIndex(Sync.TEXT);
//	    			Log.i("list",index+" text");
	    			String text  = cursor.getString(index);
//	    			Log.i("list",index+" "+text);
	    			index = cursor.getColumnIndex(Sync.CTIME);
	    			long ctime = cursor.getLong(index);
//	    			Log.i("list",index+" "+ctime);
	    			SimpleDateFormat formater   = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");    
	    		    Date curDate      = new Date(ctime);//获取当前时间  
	    		    String createTime = formater.format(curDate).toString();
	    		    
	    		    
//	    		    SimpleDateFormat formater1   = new SimpleDateFormat("yyyy年MM月dd日");    
//	    		    String createTime1 = formater1.format(curDate).toString();
//	    		    if(list.size()%2==0)
//	    		    {
////	    		    	timeList.put(list.size()+"", createTime1);
////	    		    	timeList.
////	    		    	time = createTime1;
//	    		    	map.put("", "");
//	    		    	list.add(map);
//	    		    	continue;
//	    		    }
	    		    
	    		    index = cursor.getColumnIndex(Sync.IMAGE);
	    		    byte[] image_b = cursor.getBlob(index);
	    		    if(image_b!=null)
	    		    {
//	    		    	map.put(Sync.IMAGE, new Boolean(image));
	    		    	map.put(Sync.IMAGE, image_b);
	    		    	    		    	
	    		    }
	    		    else
	    		    {
	    		    	map.put(Sync.IMAGE, null);
	    		    }
	    		    
	    		    index = cursor.getColumnIndex(Sync.AUDIO);
	    		    byte[] audio_b = cursor.getBlob(index);
	    		    if(audio_b!=null)
	    		    {
//		    			map.put(Sync.AUDIO, new Boolean(audio));
	    		    	map.put(Sync.AUDIO,audio_b);
	    		    	index = cursor.getColumnIndex(Sync.AUDIO_NAME);
	    		    	String audio_n = cursor.getString(index);
	    		    	map.put(Sync.AUDIO_NAME, audio_n);
	    		    }
	    		    else
	    		    {
	    		    	map.put(Sync.AUDIO, null);
	    		    	map.put(Sync.AUDIO_NAME, null);
	    		    }
	    		    
	    		    index = cursor.getColumnIndex(Sync.HAND);
	    		    byte[] hand_b = cursor.getBlob(index);
	    		    if(hand_b!=null)
	    		    {				
//		    			map.put(Sync.HAND, new Boolean(hand));
	    		    	map.put(Sync.HAND, hand_b);
	    		    }
	    		    else
	    		    {
	    		    	map.put(Sync.HAND, null);
	    		    }
	    		    
	    			map.put(Sync.TITLE, title);
	    			map.put(Sync.TEXT, text);
	    			map.put(Sync.CTIME, createTime);
	    			map.put(Sync.CTIME_L, ctime);
	    	
	    			list.add(map);
	    		}
	    		while(cursor.moveToNext());
	    	}
    	}
    	finally
		{
    		sqliteHelper.doClose();
		}
    	return list;
	}
	/**
     * @name:
     * @Param:null
     * @return:void
     * @Function:init
     */ 
	private List<Map<String, Object>> doGetFromServer(List<Map<String, Object>> list)
	{
		String     url        = HttpHelper.Base_Url+"servlet/GetNotesServlet";
		JSONObject result     = null;
		JSONObject parameters = new JSONObject();
		
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
		
		try 
		{	
			String clause = "where iduser='"+getUserID(getIntent().getStringExtra("username"))+"'";
			parameters.put(Sync.COLUMNS, "*");
			parameters.put(Sync.CLAUSE,clause );
		}
		catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		//send request
		try 
		{
			HttpEntity entity = new StringEntity(URLEncoder.encode(parameters.toString(),"utf-8"));
			result =  HttpHelper.getResponseByHttpPostWithJSON(url, entity);
			
			if(result!=null)
			{
				try 
				{
					JSONArray json_a = result.getJSONArray("notes");
					for (int i = 0; i < json_a.length(); i++) 
					{
						JSONObject note = json_a.getJSONObject(i);
						Log.i("new add",note.toString());
						Map<String, Object> map = new HashMap<String, Object>();
		    			String title = note.getString(Sync.TITLE);
		    	
		    			String text  = note.getString(Sync.TEXT);
//		    			Log.i("list"," "+text);
		    			long ctime = note.getLong(Sync.CTIME);
//		    			Log.i("list",index+" "+ctime);
		    			SimpleDateFormat formater   = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");    
		    		    Date curDate      = new Date(ctime);//获取当前时间  
		    		    String createTime = formater.format(curDate).toString();
		    		   
		    		    String image_base64 = note.getString(Sync.IMAGE);
		    		    if(!image_base64.equals(""))
		    		    {
		    		    	byte[] image_b = Base64.decode(image_base64,Base64.DEFAULT);
			    		    if(image_b!=null)
			    		    {
			    		    	map.put(Sync.IMAGE, image_b);	    		    	    		    	
			    		    }
			    		    
		    		    }
		    		    else
		    		    {
		    		    	map.put(Sync.IMAGE, null);
		    		    }
		    		   
		    		    String audio_base64 = note.getString(Sync.AUDIO);
		    		    Log.i("new add", audio_base64);
		    		    if(!audio_base64.equals(""))
		    		    {
		    		    		byte[] audio_b = Base64.decode(audio_base64,Base64.DEFAULT);
				    		    if(audio_b!=null)
				    		    {
				    		    	map.put(Sync.AUDIO,audio_b);
				    		    	String audio_n = note.getString(Sync.AUDIO_NAME);
				    		    	map.put(Sync.AUDIO_NAME, audio_n);
				    		    }
		    		    }	   
		    		    else
		    		    {
		    		    	map.put(Sync.AUDIO, null);
		    		    	map.put(Sync.AUDIO_NAME, null);
		    		    }
		    		   
		    		    String hand_base64 = note.getString(Sync.HAND);
		    		    if(!hand_base64.equals(""))
		    		    {
		    		    	byte[] hand_b = Base64.decode(hand_base64,Base64.DEFAULT);
			    		    if(hand_b!=null)
			    		    {
			    		    	map.put(Sync.HAND, hand_b);
			    		    }
		    		    }    
		    		    else
		    		    {
		    		    	map.put(Sync.HAND, null);
		    		    }
		    		    
		    			map.put(Sync.TITLE, title);
		    			map.put(Sync.TEXT, text);
		    			map.put(Sync.CTIME, createTime);
		    			map.put(Sync.CTIME_L, ctime);
		    			list.add(map);
					}
					
				} 
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return list;
			}
			else
				return null;
		
		
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		return null;
		
	}
	
	
	private Handler myHandler = new Handler()
	{
		 @Override
	     public void handleMessage(Message msg) 
		 {
	        		     		
	                switch (msg.what) 
	                {
	                	case FinalDefinition.MESSAGE_PROGRESS:

	                		
	                		break;
	                	case FinalDefinition.MESSAGE_JOBDONE:
	                		progressLog.dismiss();
	                		break;
	                    default:
	                        super.handleMessage(msg);
	                }
		 }
	};
	
	private int getUserID(String username)
	{
		int user_id = 0;
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	
    	
    	String table     = FinalDefinition.DATABASE_TABLE_USER;
    	String[] columns = {"id"};
    	String selection = "username=?";
    	String[] selectionArgs = {username};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = null;
    	
    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		if(cursor.moveToFirst())
    		{
    			int index = cursor.getColumnIndex("id");
    			
    			user_id  = cursor.getInt(index);
    		}
    		
    	}
    	cursor.close();
    	sqliteHelper.doClose();
    	return user_id;
	}
	
	/**
	 * =====================================================================
	 * 								ITEM 事件
	 * 								单击进入编辑
	 * 								长按进入删除
	 * =====================================================================
	 */
	
	/**
     * @name:
     * @Param:null
     * @return:void
     * @Function:init
     */ 
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Toast.makeText(this, "list+ "+position, Toast.LENGTH_SHORT).show();
		Map<String,Object> map = list.get(position);
		long date = (Long) map.get(Sync.CTIME_L);
//		String date = (String) map.get(Sync.CTIME);
//		Log.i("new add","get date "+date);
//		SimpleDateFormat formater   = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");    
//	    try 
//	    {
//			Date d = formater.parse(date);
			Intent intent = getIntent();
//			intent.putExtra("note", d.getTime());
			intent.putExtra("note", date);
			intent.setClass(this, AddNoteActivity.class);
			intent.putExtra(FinalDefinition.NOTE_MODEL, FinalDefinition.NOTE_UPDATE);
			startActivity(intent);
//		} 
//	    catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	private void setLongClick()
	{
		/**
		  * 设置长按事件
		  */
		getListView().setOnItemLongClickListener
		(
			new ListView.OnItemLongClickListener() 
			{

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) 
				{
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "list+ "+position, Toast.LENGTH_SHORT).show();
					new AlertDialog.Builder(NotesActivity.this) 
		            .setTitle("提示") 
		            .setMessage("确定删除笔记？") 
		            .setPositiveButton
		            (
		            	"确定", new DialogInterface.OnClickListener()
		                { 
		        			            @Override 
		        			public void onClick(DialogInterface arg0, int arg1) 
		        			{ 
		        			// TODO Auto-generated method stub 
		        			    progressLog.show();
		        			    
		        			    doDelete(position);
		        		        	
		        			} 
		        		}
		            	
		            )
		            .setNegativeButton
		            (
		            	"取消", new DialogInterface.OnClickListener() 
		            	{
		        			
		        			@Override
		        			public void onClick(DialogInterface arg0, int arg1)
		        			{
		        				// TODO Auto-generated method stub
		        				arg0.dismiss();
		    		        	
		        			}
		        		}
		            	
		            ).show();
					return false;
				}
			
			}
		);
	}
	/**
	 * 
	 */
	private void doDelete(int position)
	{
		doDeleteInLocal(position);
		if(HttpHelper.isConnect(getApplicationContext()))
		{
			boolean isSuccess = doDeleteInServer(position);
			if(!isSuccess)
			{
				doDetectAndInsertSync(position);
			}
		}
		else
		{
			doDetectAndInsertSync(position);
		}
//		
		
		
		
		list.remove(position);
		NotesAdapter adapter = (NotesAdapter)getListAdapter();
    	adapter.notifyDataSetChanged();
    	
    	Message msg = new Message();
 		msg.what    = FinalDefinition.MESSAGE_JOBDONE;
 		myHandler.sendMessage(msg);
	}
	
	/**
	 * 
	 */
	
	private void doDeleteInLocal(int position)
	{
			
		Map<String,Object> map = list.get(position);
		long date = (Long) map.get(Sync.CTIME_L);
		
		LocalSQLiteHelper sqlite = new LocalSQLiteHelper(this);
		sqlite.doOpen();
		String clause = "createtime="+date;
		sqlite.doDelete(FinalDefinition.DATABASE_TABLE_NOTE, clause);
		sqlite.doClose();
		sqlite = null;
	}
	
	/**
	 * 
	 */
	private boolean doDeleteInServer(int position)
	{
		String url           = HttpHelper.Base_Url+"servlet/DeleteNoteServlet";
    	JSONObject parameter = new JSONObject();
    	JSONObject jsonResult= null;
    	try 
    	{
    		Map<String,Object> map = list.get(position);
    		long createtime = (Long) map.get(Sync.CTIME_L);
    		String username = getIntent().getStringExtra("username");
    		int iduser = this.getUserID(username);
    		parameter.put("where", "iduser=? and createtime=?");
    		parameter.put("iduser", iduser);
    		parameter.put("ctime", createtime);
			Log.i("string", parameter.toString());
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
	 * public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)

	 * Since: API Level 1
	 * Query the given table, returning a Cursor over the result set.
	 * Parameters

	 * table	The table name to compile the query against.
	 * columns	A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * selection	A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * selectionArgs	You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * groupBy	A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * having	A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * orderBy	How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 */
	
	/**
	 * 
	 */
	private void doDetectAndInsertSync(int position)
	{
		
		Map<String,Object> map = list.get(position);
		long date = (Long) map.get(Sync.CTIME_L);
	
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
    		String table     = Sync.DATABASE_TABLE;
	    	String where     = "createtime=?";
	    	String[] clause  = {date+""};
	    	int affected_row = sync.doDelete(table, where,clause);
	    	Log.i("sync",affected_row+" 删除！时间为:"+date);
	    	if(affected_row==0)
	    	{
	    		this.doInsertInSync(sync,position);
	    		Log.i("sync","执行插入");
	    	}
	    	else
	    	{
	    		return;
	    	}
    	}
    	
		finally
		{
			sync.doClose();
		}
		
	}
	
	/**
	 * 
	 * @param position
	 */
	private void doInsertInSync(SyncHelper sync,int position)
	{
		
		Map<String,Object> map = list.get(position);
		long date = (Long) map.get(Sync.CTIME_L);
		
		String username = getIntent().getStringExtra("username");
		int userid = this.getUserID(username);
		int action = Sync.ACTION_DELETE;
		int table  = Sync.TABLE_NOTE;
		JSONObject clause = new JSONObject();
		
    	try
    	{
    		
    		clause.put("where", "createtime");
    		clause.put("clause", date);
    		
	    	ContentValues cv = new ContentValues();
	    	cv.put(Sync.USER_ID, userid);
	    	cv.put(Sync.ACTION, action);
	    	cv.put(Sync.TABLE, table);
	    	cv.put(Sync.CLAUSE, clause.toString());
	    	cv.put(Sync.LOGTIME, System.currentTimeMillis());
	    	cv.put(Sync.CTIME, date);
	     	
	 		long result = sync.doInsert(Sync.DATABASE_TABLE, cv);  
	 		Log.i("sync", result+" 插入结果");
    	}
    	catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
		
		}
	}
	
	/**
	 * 
	 */
	private String getSelectedBookName()
	{
		
		Intent intent = this.getIntent();
		int    bookid   = intent.getIntExtra("book_id", 0);
		if(bookid==0)
		{
			return null;
		}
		
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
		
		String bookname = null;		
		String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		String selection = "idnotebook=?" ;
		String[] selectionArgs = {""+bookid};
		String[] columns = {"bookname"};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = null;
    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);    	
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		if(cursor.moveToFirst())
    		{
    			
    		
    			int index   = cursor.getColumnIndex("bookname");
    			bookname = cursor.getString(index);	
    			Log.i("add",bookname);
    		}
			
				
    	}
    	
    	cursor.close();
    	sqliteHelper.doClose();  
    	sqliteHelper = null;
    	return bookname;
    
	}
	
	/**
	 * 
	 */
	
	private int doGetSelectedBookId(String bookname)
	{
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
		
		int bookid = 0;		
		String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		String selection = "bookname=?" ;
		String[] selectionArgs = {""+bookname};
		String[] columns = {"idnotebook"};
    	String groupBy   = null;
    	String having    = null;
    	String orderBy   = null;
    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);    	
    	if(cursor!=null && cursor.getCount()!=0)
    	{
    		if(cursor.moveToFirst())
    		{
    			
    		
    			int index   = cursor.getColumnIndex("idnotebook");
    			bookid = cursor.getInt(index);	
    			
    		}
				
    	}
    	
    	cursor.close();
    	sqliteHelper.doClose();  
    	return bookid;
	}
	/**
	 * 
	 */
	
	private void dealloc()
	{
		list.clear();
		list = null;
	}
	
	/**
	 * 
	 */
	protected void onDestroy()
	{
//		dealloc();
		System.gc();
		super.onDestroy();
	}
	
	
	/**
	 * 
	 */
	public void onBackPressed()
	{
		dealloc();
		System.gc();
		super.onBackPressed();
		this.finish();
	}
	
	
    /**
     * @name:onResume()
     * @Param:
     * @return:
     * @Function:release resources
     */
    protected void onResume()
    {
    	
    	Intent intent = this.getIntent();
		int    bookid   = intent.getIntExtra("book_id", 0);
    	this.doGetData(list,bookid);
    	NotesAdapter adapter = (NotesAdapter)getListAdapter();
    	adapter.notifyDataSetChanged();
    	super.onResume();
    }
}
