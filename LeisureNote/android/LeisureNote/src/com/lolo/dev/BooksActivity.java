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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.lolo.helper.HttpHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.helper.SyncHelper;
import com.lolo.tools.BooksAdapter;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.Sync;

public class BooksActivity extends Activity 
{
	
	/**
	 * Items
	 */

	
	private boolean     action;
	private boolean     isEdited    = false;
	private static int  selection = -1;
	private FrameLayout parentLay;
	private List<Map<String, Object>> list;
	private GridView booksView;
	
	private Button      buttonAdd;
	private PopupWindow winAdd;
	private Button      winButtonSave;
	private EditText    winEditName;
	private LocalSQLiteHelper
						sqliteHelper;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.books_layout);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.books_title);
	    
	    initComponents();
	    list = new ArrayList<Map<String, Object>>(); 
	    initList(list);
	    initPopWindow();
	    
	    initButtonListener();
	    initItemListener();
	}
	
	
	/**
	 * @author  LoLo
	 * @version 1.1
	 * @return
	 * @param
	 * @name initComponents()
	 */
	
	private void initComponents()
	{
		parentLay = (FrameLayout)findViewById(R.id.booksLayout);
		booksView = (GridView)findViewById(R.id.books_gridview);
		booksView.setBackgroundResource(R.drawable.notelist_background);
			
		buttonAdd = (Button)findViewById(R.id.button_newbook);
	}
	
	  /**
     * @Name:initButtonListener()
     * @Param:Button
     * @Param:Listener
     * @return:void
     * @Function:do add listeners
     */
	
	private void initButtonListener()
	{
		Button.OnClickListener onNewListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				setAction(FinalDefinition.ACTION_NEW);
				winAdd.showAtLocation(parentLay, Gravity.CENTER, 0, 0);
				
			}
			
		};
		
		this.addButtonListener(buttonAdd, onNewListener);
		
		
		/**
		 * =============================================================
		 */
		Button.OnClickListener onAddBookListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(action==FinalDefinition.ACTION_NEW)
				{
					doAddNewBook();
				}
				else if(action==FinalDefinition.ACTION_EDITE)
				{
					doUpdateBook(selection);
				}
				
			}
			
		};
		this.addButtonListener(winButtonSave, onAddBookListener);
	}
	
    /**
     * @Name:addButtonListener()
     * @Param:Button
     * @Param:Listener
     * @return:void
     * @Function:do add listeners
     */
    private void addButtonListener(Button button, OnClickListener listener)
    {
    	button.setOnClickListener(listener);
    }
	
	
	/**
	 * @author  LoLo
	 * @version 1.1
	 * @return
	 * @param
	 * @name getDate()
	 */
	private void getData(List<Map<String, Object>> list)
	{
		list.clear();
		doGetFromLocal(list);
	}

	/**
	 * @author  LoLo
	 * @version 1.1
	 * @return
	 * @param
	 * @name doGetFromLocal()
	 */
	private List<Map<String,Object>> doGetFromLocal(List<Map<String, Object>> list)
	{
		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
    	Cursor cursor = null;
    	try
    	{
		    String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		    String[] columns = null;
		    String selection = "iduser=?";
		   	String[] selectionArgs = {getUserID(sqliteHelper)+""};
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
	    			int index = cursor.getColumnIndex(Sync.BOOK_NAME);
//	    			Log.i("list",index+" title");
	    			String name = cursor.getString(index);
//	    			Log.i("list",index+" "+name);
	    			index = cursor.getColumnIndex(Sync.BOOK_ID);
//	    			Log.i("list",index+" text");
	    			int book_id  = cursor.getInt(index);
//	    			Log.i("list",index+" "+book_id);
	    			index = cursor.getColumnIndex(Sync.CTIME);
	    			long ctime = cursor.getLong(index);
//	    			Log.i("list",index+" "+ctime);
	    			SimpleDateFormat formater   = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");    
	    		    Date curDate      = new Date(ctime);//获取当前时间  
	    		    String createTime = formater.format(curDate).toString();
		    
	    			map.put(Sync.BOOK_NAME, name);
	    			map.put(Sync.BOOK_ID, book_id);
	    			map.put(Sync.BOOK_CTIME, createTime);
	    			map.put(Sync.BOOK_CTIME_L, ctime);
	    			
	    			
	    			String table_query = FinalDefinition.DATABASE_TABLE_NOTE;
	    		    String[] columns_query = {"idnote"};
	    		    String selection_query = "idbook=?";
	    		   	String[] selectionArgs_query = {book_id+""};
	    		   	String groupBy_query   = null;
	    		   	String having_query    = null;
	    		   	String orderBy_query   = null;	
	    			
	    	    	Cursor cursor_getCount = sqliteHelper.doQuery(table_query, columns_query, selection_query, selectionArgs_query, groupBy_query, having_query, orderBy_query);	    	
	    	    	if(cursor_getCount!=null )
	    	    	{
	    	    		int notes_count = cursor_getCount.getCount();
	    	    		map.put(Sync.BOOK_NOTES_NUM, notes_count);
	    	    	}
	    	    	cursor_getCount.close();
	    	    	
	    	    	
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
	private void initList(List<Map<String, Object>> list)
	{
		String[] from = {Sync.BOOK_NAME,Sync.BOOK_NOTES_NUM,Sync.CTIME};
		int[] to = {R.id.text_bookname,R.id.text_notes_num,R.id.text_booktime};
		
		getData(list);
		if(list==null)
		{
			list = new ArrayList<Map<String, Object>>();  	 
		}
		BooksAdapter adapter = new BooksAdapter(this,list,R.layout.book_item,
				from,
				to);
        booksView.setAdapter(adapter);
	}
	
	  /**
     * @name:initPopWindow
     * @Param:
     * @return:
     * @Function:
     */
    
    private void initPopWindow() 
    {  
        // 加载popupWindow的布局文件  
        View contentView = LayoutInflater.from(BooksActivity.this)  
                .inflate(R.layout.new_book_win, null);  

        // 声明一个弹出框  
        winAdd = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, false);  
        winAdd.setFocusable(true);
        winAdd.setTouchable(true);
        BitmapDrawable bitmap = new BitmapDrawable();
        winAdd.setBackgroundDrawable(bitmap);
//        winAdd.setBackgroundDrawable(getResources ().getDrawable(R.drawable.mic_back));//add back
        winAdd.setAnimationStyle(R.style.PopupAttachAnimation); 
        
        winButtonSave    = (Button)contentView.findViewById(R.id.button_book_save); 
    	winEditName      = (EditText)contentView.findViewById(R.id.edit_bookname);
    	
    	winAdd.setOnDismissListener
    	(
    			new OnDismissListener() 
		    	{		
					@Override
					public void onDismiss() 
					{
						// TODO Auto-generated method stub
						winEditName.setText("");
						isEdited = false;
					}
				}
    	);
    	
    	winEditName.addTextChangedListener
    	(
    			new TextWatcher() 
    			{
					
					@Override
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
							int arg3) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
						isEdited = true;
					}
				}
    	);

    }
    
    
    /**
     * @name:addNewBook
     * @Param:
     * @return:
     * @Function:
     */
    private void doAddNewBook()
    {
      	if(!isEdited)
    	{
    		return;
    	}
    	
      	if(HttpHelper.isConnect(this))
      	{
      		JSONObject book = addNewBookInLocal();
      		boolean isSuccess = addNewBookInServer(book);
      		if(!isSuccess)
      		{
      			doInsertInSync(Sync.ACTION_INSERT,book,null);
      		}
      	}
      	else if(!HttpHelper.isConnect(this))
      	{
      		JSONObject book = addNewBookInLocal();
      		doInsertInSync(Sync.ACTION_INSERT,book,null);
      	}
    	
    	
    	
    	this.doRefresh();
    	this.doReset();
    	
    }
    
    
    /**
     * @name:addNewBookInLocal
     * @Param:
     * @return:
     * @Function:
     */
    private JSONObject addNewBookInLocal()
    {
  	
    	//do add into local sqlite
    	sqliteHelper = null;
    	sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	
    	JSONObject book  = new JSONObject();
    	ContentValues cv = new ContentValues();
    	try
    	{
    		String name   = this.winEditName.getText().toString();
    		int    iduser = this.getUserID(sqliteHelper);
    		long   ctime  = System.currentTimeMillis();
     		
    		book.put(Sync.BOOK_NAME, name);
    		book.put(Sync.BOOK_USER, iduser);
    		book.put(Sync.BOOK_CTIME, ctime);
    		
    		cv.put(Sync.BOOK_NAME, name);
    		cv.put(Sync.BOOK_USER, iduser);
    		cv.put(Sync.BOOK_CTIME, ctime);
     		long result = sqliteHelper.doInsert(FinalDefinition.DATABASE_TABLE_BOOK, cv);
      		
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		sqliteHelper.doClose();
    	}
    	return book;
    		
    }
    
    /**
     * @name:addNewBookInServer()
     * @Param:
     * @return:
     * @Function:
     */
    private boolean addNewBookInServer(JSONObject book)
    {
    	JSONObject jsonResult;
    	String url           = HttpHelper.Base_Url+"servlet/BooksServlet";
    	JSONObject parameter = new JSONObject();
    	try 
    	{
    		parameter.put("book", book);
    		parameter.put("action",Sync.BOOK_ACTION_NEW);
		}
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try 
		{			
			HttpEntity entity = new StringEntity(URLEncoder.encode(parameter.toString(), "utf-8"));
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
     * @name:doInsertInSync()
     * @Param:
     * @return:
     * @Function:
     */ 
    private void doInsertInSync(int action,JSONObject book,String clause)
    {
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
	    	ContentValues cv = new ContentValues();
	    	cv.put(Sync.ACTION, action);
	    	cv.put(Sync.TABLE, Sync.TABLE_BOOK);
	    	cv.put(Sync.LOGTIME, System.currentTimeMillis());
     		
//	    	clause = "iduser=? and bookname=? and createtime=?";
//			book.put("clause", clause);
//			book.put(Sync.BOOK_NAME, map.get(Sync.BOOK_NAME).toString());
//			book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//			book.put(Sync.BOOK_USER,this.getUserID(sqliteHelper));
	    	String bookname = book.getString(Sync.BOOK_NAME);
	    	int iduser      = book.getInt(Sync.BOOK_USER);
	    	long ctime      = book.getLong(Sync.BOOK_CTIME);
	    	cv.put(Sync.CLAUSE, clause);
	    	cv.put(Sync.BOOK_NAME, bookname);
	    	cv.put(Sync.BOOK_USER, iduser);
	    	cv.put(Sync.BOOK_CTIME,ctime);
	    	cv.put(Sync.UTIME, ctime);
	    	
	    	Log.i("sync",cv.toString());
	 		
	 		long result = sync.doInsert(Sync.DATABASE_TABLE, cv);
	 		  		
    	}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		finally
		{
			sync.doClose();
		}
    }
    
    
    /**
     * @name:doGetUserID
     * @Param:
     * @return:
     * @Function:
     */
    private int getUserID(LocalSQLiteHelper sqliteHelper)
	{
		int user_id = 0;
//		LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
//    	sqliteHelper.doOpen();
//    	
		String username  = getIntent().getStringExtra("username");
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
//    	sqliteHelper.doClose();
    	return user_id;
	}
    

    /**
     * @name:doReset
     * @Param:
     * @return:
     * @Function:
     */
    private void initItemListener()
    {
    	this.booksView.setOnItemLongClickListener
		(
				new ListView.OnItemLongClickListener() 
				{

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) 
					{
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "book+ "+position, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(BooksActivity.this) 
			            .setTitle("提示") 
			            .setMessage("选择操作？") 
			            .setPositiveButton
			            (
			            	"编辑本子", new DialogInterface.OnClickListener()
			                { 
			        			            @Override 
			        			public void onClick(DialogInterface view, int arg1) 
			        			{ 
			        			// TODO Auto-generated method stub 
			        			   
			        			    view.dismiss();
			        			    doShowEditWindow(position);
			        		        	
			        			} 
			        		}
			            	
			            )
			            .setNeutralButton
			            (
		            		"删除本子", new DialogInterface.OnClickListener() 
			            	{
			        			
			        			@Override
			        			public void onClick(DialogInterface view, int arg1)
			        			{
			        				// TODO Auto-generated method stub
			        				doDelete(position);
			    		        	
			        			}
			        		}
			            )
			            .setNegativeButton
			            (
			            	"取消", new DialogInterface.OnClickListener() 
			            	{
			        			
			        			@Override
			        			public void onClick(DialogInterface view, int arg1)
			        			{
			        				// TODO Auto-generated method stub
			        				view.dismiss();
			    		        	
			        			}
			        		}
			            	
			            ).show();
						return false;
					}
				
				}
			);
    	
    	
    	this.booksView.setOnItemClickListener
    	(
    			new GridView.OnItemClickListener()
    			{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id) 
					{
						// TODO Auto-generated method stub
//						JSONObject book = new JSONObject();
						Map<String,Object> map = list.get(position);

//		    			map.put(Sync.BOOK_NAME, name);
//		    			map.put(Sync.BOOK_ID, book_id);
//		    			map.put(Sync.BOOK_CTIME, createTime);
//		    			map.put(Sync.BOOK_CTIME_L, ctime);
						int bookid = (Integer) map.get(Sync.BOOK_ID);
						Intent intent = getIntent();
						intent.putExtra("book_id", bookid);
						intent.setClass(BooksActivity.this, NotesActivity.class);
						startActivity(intent);
						//						int    bookid   = (Integer) map.get(Sync.BOOK_ID);
//						long   ctime    = (Long) map.get(Sync.BOOK_CTIME_L);
//						
//						try
//						{
//							book.put(Sync.BOOK_NAME, bookname);
//							book.put(Sync.BOOK_ID, bookid);
//							
//							Intent intent = getIntent();
//							intent.putExtra("book", book.toString());
//							intent.setClass(BooksActivity.this, NotesActivity.class);
//						}
//						catch(JSONException e)
//						{
//							e.printStackTrace();
//						}
					
						
					}
				}
    	);
    }
    
    
    
    /**
     * @name:doDelete
     * @Param:
     * @return:
     * @Function:
     */
    
    private void doDelete(int position)
    {
    	
    	if(HttpHelper.isConnect(this))
    	{
    		JSONObject book   = doDeleteInLocal(position);
    		boolean isSuccess = this.doDeleteInServer(book);
    		if(!isSuccess)
    		{
    			doDeleteDetectAndInsertSync(book,Sync.ACTION_DELETE);
    		}
    	}
    	else if(!HttpHelper.isConnect(this))
    	{
    		JSONObject book   = doDeleteInLocal(position);
    		//do insert in Sync
    		doDeleteDetectAndInsertSync(book,Sync.ACTION_DELETE);
    	}
    	
    	
    	
    	this.doRefresh();
    	this.doReset();
    }
    
    /**
     * @name:doDeleteInLocal
     * @Param:
     * @return:
     * @Function:
     */
    private JSONObject doDeleteInLocal(int position)
    {
    	Map<String,Object> map = list.get(position);
		
		sqliteHelper = null;
		sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
		String clause = "bookname=? and createtime=?";
		String[] args = {map.get(Sync.BOOK_NAME).toString(),map.get(Sync.BOOK_CTIME_L)+""};
		sqliteHelper.doDelete(FinalDefinition.DATABASE_TABLE_BOOK, clause, args);
		
		
		JSONObject book = new JSONObject();
		try
		{	
			clause = "iduser=? and createtime=?";
			book.put("clause", clause);
			book.put(Sync.BOOK_NAME, map.get(Sync.BOOK_NAME).toString());
			book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
			book.put(Sync.BOOK_USER,this.getUserID(sqliteHelper));
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		sqliteHelper.doClose();
	
		return book;
		
    }
    
    /**
     * @name:doDeleteInServer
     * @Param:
     * @return:
     * @Function:
     */
    private boolean doDeleteInServer(JSONObject book)
    {
    	JSONObject jsonResult;
    	String url           = HttpHelper.Base_Url+"servlet/BooksServlet";
    	JSONObject parameter = new JSONObject();
    	try 
    	{
    		parameter.put("book", book);
    		parameter.put("action",Sync.BOOK_ACTION_DELETE);
		}
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try 
		{			
			HttpEntity entity = new StringEntity(URLEncoder.encode(parameter.toString(), "utf-8"));
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
     * @name:doDetectAndInsertSync()
     * @Param:
     * @return:
     * @Function:
     */
    private void doDeleteDetectAndInsertSync(JSONObject book,int action)
    {
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
    		String table     = Sync.DATABASE_TABLE;
	    	String where     = "action=? and tabletype=? and createtime=?";
	    	String[] clause  = {Sync.ACTION_INSERT+"",Sync.TABLE_BOOK+"",book.getLong(Sync.BOOK_CTIME)+""};
	    	int affected_row = sync.doDelete(table, where,clause);
	    	//删除sync中尚未同步的向该待删除笔记本中增添和修改笔记的记录
	    	String where_note = "iduser=? and tabletype=? and bookname=?";
	    	String bookname = book.getString(Sync.BOOK_NAME);
	    	int iduser = book.getInt(Sync.BOOK_USER);
	    	int tabletype = Sync.TABLE_NOTE;
	    	String[] clause_note ={iduser+"",tabletype+"",bookname};
	    	sync.doDelete(table, where_note,clause_note);  	
	    	if(affected_row==0)
	    	{
	    		this.doInsertInSync(action,book,book.getString("clause"));
		    	Log.i("sync","执行插入");
	    	}
	    	if(affected_row!=0)
	    	{
	    		where = "iduser=? and tabletype=? and createtime=?";
	    		String[] clause_ ={iduser+"",Sync.TABLE_BOOK+"",book.getLong(Sync.BOOK_CTIME)+""};
	    		sync.doDelete(table, where,clause_);  
	    	}
	    	
	    	
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			sync.doClose();
		}
		
    }
    
    
    /**
     * @name:doShowEditWindow
     * @Param:
     * @return:
     * @Function:
     */
    private void doShowEditWindow(int position)
    {
    	setAction(FinalDefinition.ACTION_EDITE);
    	Map<String,Object> map = list.get(position);
    	selection = position;
    	this.winEditName.setText(map.get(Sync.BOOK_NAME).toString());
    	this.winAdd.showAtLocation(parentLay, Gravity.CENTER, 0, 0);
    }
    

    /**
     * @name:doEditeBook
     * @Param:
     * @return:
     * @Function:
     */
    private void doUpdateBook(int position)
    {
     	if(!isEdited)
    	{
    		return;
    	}
    	
     	if(HttpHelper.isConnect(this))
     	{
     		JSONObject book = doUpdateBookInLocal(position);
     		boolean isSuccess = doUpdateBookInServer(book);
     		if(!isSuccess)
     		{
     			doUpdateDetectAndInsertSync(Sync.ACTION_UPDATE,book);
     		}
     		
     	}
     	if(!HttpHelper.isConnect(this))
     	{
     		JSONObject book = doUpdateBookInLocal(position);
     		doUpdateDetectAndInsertSync(Sync.ACTION_UPDATE,book);
     	}
    	
    	
    	
    	this.doRefresh();
    	this.doReset();
    }
    
    /**
     * @name:doEditBookInLocal
     * @Param:
     * @return:
     * @Function:
     */
    private JSONObject doUpdateBookInLocal(int postion)
    {
    	Map<String,Object> map = list.get(postion);
    	sqliteHelper = null;
		sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	
    	ContentValues cv = new ContentValues();
    	JSONObject book = new JSONObject();
    	try
    	{

//			map.put(Sync.BOOK_NAME, name);
//			map.put(Sync.BOOK_ID, book_id);
//			map.put(Sync.BOOK_CTIME, createTime);
//			map.put(Sync.BOOK_CTIME_L, ctime);
    		
    		String from_name = map.get(Sync.BOOK_NAME).toString();
    		String to_name   = this.winEditName.getText().toString();
    		
    		int iduser = this.getUserID(sqliteHelper);
    		int idbook = (Integer) map.get(Sync.BOOK_ID);
    		
    		cv.put(Sync.BOOK_NAME, to_name);
    		
    		
     		/**
     		 * update
     		 * String table, 
     		 * ContentValues values, 
     		 * String whereClause,
     		 * String[] whereArgs
     		 */
     		String clause = "idnotebook=? and iduser=? and bookname=?";
     		String[] args = {idbook+"",iduser+"",from_name};
     		
     		int affectedRows = sqliteHelper.doUpdate(FinalDefinition.DATABASE_TABLE_BOOK, cv, clause, args);
     		
     		clause = "iduser=? and createtime=?";
  	
    		book.put("clause", clause);
			book.put(Sync.BOOK_NAME, to_name);
			book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
			book.put(Sync.BOOK_USER,iduser);
//			book.put("to_name", to_name);
			
			
      		
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		sqliteHelper.doClose();
    	}
    	return book;
    }
    
    
    /**
     * @name:doEditBookInServer
     * @Param:
     * @return:
     * @Function:
     */
    private boolean doUpdateBookInServer(JSONObject book)
    {
    	JSONObject jsonResult;
    	String url           = HttpHelper.Base_Url+"servlet/BooksServlet";
    	JSONObject parameter = new JSONObject();
    	try 
    	{
    		parameter.put("book", book);
    		parameter.put("action",Sync.BOOK_ACTION_UPDATE);
		}
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try 
		{			
			HttpEntity entity = new StringEntity(URLEncoder.encode(parameter.toString(), "utf-8"));
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
     * @name:doUpdateDetectAndInsertSync
     * @Param:
     * @return:
     * @Function:
     */
    private void doUpdateDetectAndInsertSync(int actionType,JSONObject book)
    {

// 		clause = "iduser=? and createtime=?";
//	
//		book.put("clause", clause);
//		book.put(Sync.BOOK_NAME, to_name);
//		book.put(Sync.BOOK_CTIME, map.get(Sync.BOOK_CTIME_L));
//		book.put(Sync.BOOK_USER,iduser);
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
    		ContentValues cv = new ContentValues();
    		String table     = Sync.DATABASE_TABLE;
	    	String where     = "tabletype=? and createtime=?";
	    	String[] args  = {Sync.TABLE_BOOK+"",book.getLong(Sync.BOOK_CTIME)+""};
	    	String to_name   = book.getString(Sync.BOOK_NAME);
	    	cv.put(Sync.BOOK_NAME, to_name);
	    	
	    	int affected_row = sync.doUpdate(table, cv, where, args);
	    	Log.i("sync","更新执行");
	    	if(affected_row==0)
	    	{
	    		this.doInsertInSync(actionType,book,book.getString("clause"));
	    		Log.i("sync","更新执行插入");
	    	}
	    	else
	    	{
	    		return;
	    	}
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			sync.doClose();
		}
    }
    
    
    /**
     * @name:doRefresh
     * @Param:
     * @return:
     * @Function:
     */
    private void doRefresh()
    {
    	this.getData(list);
    	BooksAdapter adapter = (BooksAdapter)this.booksView.getAdapter();
    	adapter.notifyDataSetChanged();
    	
    }
    
    
     /**
      * @name:doReset
      * @Param:
      * @return:
      * @Function:
      */
    private void doReset()
    {
    	this.isEdited = false;
    	this.winAdd.dismiss();
    }
    
    

    /**
     * @name:setAction
     * @Param:
     * @return:
     * @Function:
     */
    private void setAction(boolean dowhat)
    {
    	action = dowhat;
    }
}
