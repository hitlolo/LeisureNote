package com.lolo.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lolo.helper.HttpHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.helper.Player;
import com.lolo.helper.RecorderHelper;
import com.lolo.helper.SyncHelper;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.MyBitmap;
import com.lolo.tools.MyMap;
import com.lolo.tools.Sync;
import com.lolo.tools.TypeTransformer;

public class AddNoteActivity extends Activity 
{
	//初始化状态
	private int               MODEL    = FinalDefinition.NOTE_NEW;
	private boolean           isEdited = false;
	
    private ProgressDialog    progressLog;
    
	private RecorderHelper    mic;
	private File              micFile;
//  private MyImageVector     vector;
	private MyMap             imageMap,
							  handMap;
	private boolean           timeFlag;
	
	private EditText          editTitle;
	private EditText          editContent;
	private String            title;
	private String            content;
	private int               action = FinalDefinition.INTENT_CODE_NONE;
	private static String     tempImageName;
	
	private HandWrite         handView;
	/**
	 * main view
	 * -------------------------------------------------------------------------------------------    
	 */
	private int               userID = 0;
	private Spinner           spinnerBook;
	private LocalSQLiteHelper sqliteHelper;
	
	private FrameLayout       parentLay;
	/**
	 * Buttons
	 */
	private Button            buttonMic,
	                          buttonPlay,
	                          buttonAttach,
	                          buttonHand;
	
	private Button            buttonStart,
	                          buttonStop,
	                          buttonDel;
	
	private Button            buttonPlayer_go,
	                          buttonPlayer_stop,
	                          buttonReMic;
	private TextView          textStarted,
	                          textEnded;
	private SeekBar           seekBar_player;
	private Player            player;
	
	private Button            buttonGallery,
						      buttonCamera,
						      buttonDraw;
	                        
	
	private PopupWindow		  winRecord,
							  winPlay,
							  winAttach;
	
	private TextView          timingText;
	

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.note);
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.note_title);
	     
		timeFlag = false;
		mic      = new RecorderHelper(this);
//		vector   = new MyImageVector();
		imageMap = new MyMap();
		handMap  = new MyMap();
		
	   
	    initProgressDialog();
	    initComponents();    
	    initHandView();	    
	    initPopWindow();
	    initButtonListeners();
	    
	    init();
	    	
	    initTextWatcher();
	    
	    Message msg = new Message();
		msg.what    = FinalDefinition.MESSAGE_INIT_DONE;
		myHandler.sendMessage(msg);
	    
	}
	
	
	 /**
     * @name:init()
     * @Param:null
     * @return:void
     * @Function:init
     */  
	private void init()
	{

		Intent intent = getIntent();
		MODEL = intent.getIntExtra(FinalDefinition.NOTE_MODEL, FinalDefinition.NOTE_NEW);
		Long note_ctime = intent.getLongExtra("note",0);
		
		Log.i("new add",MODEL+" "+note_ctime);
		//init spinner
		initSpinner(note_ctime);	
		if(MODEL==FinalDefinition.NOTE_UPDATE)
		{
			initNote(note_ctime);
		}
	}
	
	/**
     * @name:initNote()
     * @Param:null
     * @return:void
     * @Function:init
     */  
	private void initNote(long note_ctime)
	{
		sqliteHelper = new LocalSQLiteHelper(this);
		sqliteHelper.doOpen();
    	Cursor cursor = null;
    	
    	try
    	{
		    String table     = FinalDefinition.DATABASE_TABLE_NOTE;
		    String[] columns = null;
		    String selection = "createtime=?";
		   	String[] selectionArgs = {note_ctime+""};
		   	String groupBy   = null;
		   	String having    = null;
		   	String orderBy   = null;	
	    
	    	cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);	    	
	    	if(cursor!=null && cursor.getCount()!=0)
	    	{
	    		Log.i("new add",cursor.getCount()+"");
	    		cursor.moveToFirst();
	    		do
	    		{
	    			int index = cursor.getColumnIndex(Sync.TITLE);
//	    			Log.i("list",index+" title");
	    			String title = cursor.getString(index);
	    			this.editTitle.setText(title);
//	    			Log.i("list",index+" "+title);
	    			index = cursor.getColumnIndex(Sync.TEXT);
//	    			Log.i("list",index+" text");
	    			String text  = cursor.getString(index);
	    			this.editContent.setText(text);
//	    			Log.i("list",index+" "+text);
//	    			index = cursor.getColumnIndex(Sync.CTIME);
//	    			long ctime = cursor.getLong(index);
////	    			Log.i("list",index+" "+ctime);
//	    			SimpleDateFormat formater   = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");    
//	    		    Date curDate      = new Date(ctime);//获取当前时间  
//	    		    String createTime = formater.format(curDate).toString();
	    		    
	    		    
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
	    		    	SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
	    		    	imageMap = (MyMap) TypeTransformer.BytesToObject(image_b);
	    		    	
	    	    		Set<String> s = imageMap.keySet();
	    	    		Object[] sa = s.toArray();
	    	    		
	    	    		for (int i = 0; i < sa.length; i++) 
	    	    		{
	    	    			Pattern p = Pattern.compile((String)sa[i]);  
	    		    	    Matcher match = p.matcher(sbuilder);  
	    		    	        if (match.find()) 
	    		    	        {  
//	    		    	            span = new ForegroundColorSpan(Color.RED);//需要重复！
	    		    	        	ImageSpan imSpan = new ImageSpan(imageMap.get(sa[i]).getBitmap(),ImageSpan.ALIGN_BASELINE);
	    		    	          //span = new ImageSpan(drawable,ImageSpan.XX);//设置现在图片
	    		    	        	sbuilder.setSpan(imSpan, match.start(), match.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	    		    	        }  
	    				}
	    	    		
	    	    		editContent.setText(sbuilder);  
	    		    	    		    	
	    		    }
	    		    else
	    		    {
	    		    	
	    		    }
	    		    
	    		    index = cursor.getColumnIndex(Sync.AUDIO);
	    		    byte[] audio_b = cursor.getBlob(index);
	    		    if(audio_b!=null)
	    		    {
//		    			map.put(Sync.AUDIO, new Boolean(audio));
//	    		    	map.put(Sync.AUDIO,audio_b);
	    		    	
	    		    	index = cursor.getColumnIndex(Sync.AUDIO_NAME);
	    		    	String audio_n = cursor.getString(index);
//	    		    	map.put(Sync.AUDIO_NAME, audio_n);
	    		    	this.micFile = TypeTransformer.BytesToFile(audio_b, audio_n);
	    		    	
	    		    	Log.i("new add", micFile.exists()+" ");
	    		    	mic.setFile(micFile);
	    		    	this.buttonPlay.setEnabled(true);
	    		    	this.buttonStart.setEnabled(true);
	    		    	this.buttonStop.setEnabled(false);
	    		    	this.buttonDel.setEnabled(true);
	    		    }	    		    
	    		    index = cursor.getColumnIndex(Sync.HAND);
	    		    byte[] hand_b = cursor.getBlob(index);
	    		    if(hand_b!=null)
	    		    {				
//		    			map.put(Sync.HAND, new Boolean(hand));
//	    		    	map.put(Sync.HAND, hand_b);
	    		    	SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
	    		    	handMap = (MyMap) TypeTransformer.BytesToObject(hand_b);
	    	    		Set<String> s = handMap.keySet();
	    	    		Object[] sa = s.toArray();
	    	    		
	    	    		for (int i = 0; i < sa.length; i++) 
	    	    		{
	    	    			Pattern p = Pattern.compile((String)sa[i]);  
	    		    	    Matcher match = p.matcher(sbuilder);  
	    		    	        if (match.find()) 
	    		    	        {  
//	    		    	            span = new ForegroundColorSpan(Color.RED);//需要重复！
	    		    	        	ImageSpan imSpan = new ImageSpan(handMap.get(sa[i]).getBitmap(),ImageSpan.ALIGN_BASELINE);
	    		    	          //span = new ImageSpan(drawable,ImageSpan.XX);//设置现在图片
	    		    	        	sbuilder.setSpan(imSpan, match.start(), match.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	    		    	        }  
	    				}
	    	    		
	    	    		editContent.setText(sbuilder);  
	    		    }
	    		}
	    		while(cursor.moveToNext());
	    	}
    	}
    	finally
		{
    		cursor.close();
    		sqliteHelper.doClose();
		}
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
     * @name:initComponents()
     * @Param:null
     * @return:void
     * @Function:init
     */  
    private void initComponents()
    {
    	
    	parentLay        = (FrameLayout)findViewById(R.id.frameMainAdd);
	
    	buttonMic        = (Button)findViewById(R.id.button_mic); 
    	buttonPlay       = (Button)findViewById(R.id.button_play);
    	buttonAttach     = (Button)findViewById(R.id.button_append);
//    	buttonCamera     = (Button)findViewById(R.id.button_camera);
    	
    	buttonHand       = (Button)findViewById(R.id.button_hand);
    	
    	editTitle        = (EditText)findViewById(R.id.add_noteTitle);
    	editContent      = (EditText)findViewById(R.id.add_noteContent);
       	
    }
    
    /**
     * @Name:initTextWatcher()
     * @Param:
     * @Param:
     * @return:void
     * @Function:do init
     */
    private void initTextWatcher()
    {
    	editTitle.addTextChangedListener
    	(
    			new TextWatcher()
    			{

					@Override
					public void afterTextChanged(Editable text) 
					{
						// TODO Auto-generated method stub
						isEdited = true;
						Log.i("new add", isEdited+" 是否修改");
					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) 
					{
						// TODO Auto-generated method stub
						
						
					}

					@Override
					public void onTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) 
					{
						// TODO Auto-generated method stub
						
					}}
    	);
    	
    	editContent.addTextChangedListener
    	(
    			new TextWatcher()
    			{

					@Override
					public void afterTextChanged(Editable text) 
					{
						// TODO Auto-generated method stub
						isEdited = true;
						Log.i("new add", isEdited+" 是否修改");
						
						SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
	    	    		if(!imageMap.isEmpty())
	    	    		{
	    	    			Set<String> s = imageMap.keySet();
		    	    		Object[] sa = s.toArray();
		    	    		
		    	    		for (int i = 0; i < sa.length; i++) 
		    	    		{
		    	    			Pattern p = Pattern.compile((String)sa[i]);  
		    		    	    Matcher match = p.matcher(sbuilder);  
		    		    	        if (!match.find()) 
		    		    	        {  
		    		    	          imageMap.remove((String)sa[i]);
		    		    	        }  
		    				}
	    	    		}
	    	    		
	    	    		if(!handMap.isEmpty())
	    	    		{
	    	    			Set<String> s = handMap.keySet();
		    	    		Object[] sa = s.toArray();
		    	    		
		    	    		for (int i = 0; i < sa.length; i++) 
		    	    		{
		    	    			Pattern p = Pattern.compile((String)sa[i]);  
		    		    	    Matcher match = p.matcher(sbuilder);  
		    		    	        if (!match.find()) 
		    		    	        {  
		    		    	        	handMap.remove((String)sa[i]);
		    		    	        }  
		    				}
	    	    		}
						
					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) 
					{
						// TODO Auto-generated method stub
						
						
					}

					@Override
					public void onTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) 
					{
						// TODO Auto-generated method stub
						
					}}
    	);
    }
    /**
     * @Name:initHandView()
     * @Param:
     * @Param:
     * @return:void
     * @Function:do init
     */
    
    private void initHandView()
    {
    	this.handView = new HandWrite(this,handMap,editContent);
    	
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
     * @Name:initButtonListeners()
     * @Params:null
     * @returns:void
     * @Function:do Listeners initialize
     */
    private void initButtonListeners()
    {
    	OnClickListener onMicListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub

                if(!winRecord.isShowing())
    			{
                	winRecord.showAsDropDown(buttonMic);	
				}
				else if(winRecord.isShowing())
				{
					winRecord.dismiss();
				}			
			}
		};
		
		
		OnClickListener onPlayListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub

                if(!winPlay.isShowing())
    			{
                	player.LoadAndPrepare(micFile);
                	winPlay.showAsDropDown(buttonPlay);	
				}
				else if(winPlay.isShowing())
				{
					winPlay.dismiss();
				}
				
			}
		};
		
		
		OnClickListener onAttachListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
                if(!winAttach.isShowing())
    			{
                	winAttach.showAtLocation(parentLay, Gravity.CENTER, 0, 300);
                	setAction(FinalDefinition.INTENT_CODE_NONE);
                	
				}
				else if(winAttach.isShowing())
				{
					winAttach.dismiss();
				}
				
			}
		};
		
		OnClickListener onHandListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(!handView.isShown())
				{
					LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,960,Gravity.CENTER);
					parentLay.addView(handView, lp);
					parentLay.bringChildToFront(handView);
					handView.start();
					buttonHand.setBackgroundDrawable(getResources().getDrawable(R.drawable.hand_checked));
				}
				else if(handView.isShown())
				{
					handView.stop();
					parentLay.removeView(handView);
					buttonHand.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_hand_selector));
				}
				
			}
		};
		
		this.addButtonListener(buttonMic, onMicListener);
		this.addButtonListener(buttonPlay, onPlayListener);
		this.addButtonListener(buttonAttach, onAttachListener);
		this.addButtonListener(buttonHand, onHandListener);
    }
	
	
	 /**
     * @name:initSpinner()
     * @Param:null
     * @return:void
     * @Function:init
     */
    private void initSpinner(long ctime)
    {
    	String bookSelected = "";
    	int selection = 0;
    	if(this.MODEL==FinalDefinition.NOTE_UPDATE && ctime!=0)
    	{
    		bookSelected = doGetSelectedBook(ctime);
    		Log.i("new add", "book selected= "+bookSelected);
    	}
    	//需要添加进度条，提示等待   	
    	//1.从服务器数据库中取出笔记本名称
//    	JSONObject resultJSON      = getBooksFromServer(user);
    	JSONObject resultJSON      = doGetBooks(getIntent().getStringExtra("username"));
    	String[]   resultStrArray  = null;
    	Log.i("string",resultJSON.toString());
    	
    	try 
    	{
			JSONArray  result = resultJSON.getJSONArray("books");
			resultStrArray = new String[result.length()];
			Log.i("string", "JSONARRAY length"+" "+result.length());
			for(int i = 0;i < result.length();i++)
			{
				resultStrArray[i] = result.get(i).toString();
				if(MODEL==FinalDefinition.NOTE_UPDATE && !bookSelected.equals(""))
				{
					if(resultStrArray[i].equals(bookSelected))
					{
						selection = i;
					}
				}
				Log.i("string","数组"+resultStrArray[i]);
			}
		} 
    	catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//已经取得string数据，开始为spinner设置适配器
   	
    	//取得spinner实例
    	final int book_selected = selection;
    	spinnerBook = (Spinner)findViewById(R.id.spinner_chooseBook);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.drawable.spinner_font,resultStrArray);
    	adapter.setDropDownViewResource(R.drawable.spinner_list);
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
						if(position != book_selected && MODEL==FinalDefinition.NOTE_UPDATE)
						{
							isEdited = true;
						}
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
     * @name:getBooksFromServer()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private JSONObject getBooksFromServer(String user)
    {
    	String url           = HttpHelper.Base_Url+"servlet/GetNotebookServlet";
    	JSONObject parameter = new JSONObject();
    	
    	try 
    	{
			parameter.put("username", user);
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
	    	return HttpHelper.getBookResponseByHttpPostWithJSON(url, entity);		
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return null;
    	
    }
    
    
    /**
     * @name:getBooksFromLocal()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private JSONObject getBooksFromLocal(String username)
    {
    	sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	
    	
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
	    			
		    		userID    = cursor.getInt(index);
		    		Log.i("add",userID+"  userid");
	    		}
	    		
	    	}
	    	cursor.close();
    	}
    	
    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
    	String[] columns   = {"bookname"};
    	String selection = "iduser=?" ;
    	String[] selectionArgs = {""+userID};
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
			Log.i("add","本地BOOKS");
			
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
     * @name:doGetBooks()
     * @Param:String username
     * @return:JSONObject
     * @Function:do get books from local or server
     */  
    private JSONObject doGetBooks(String user)
    {
    	JSONObject jsonObject = getBooksFromLocal(user);
    	if(jsonObject==null)
    	{
    		if(HttpHelper.isConnect(AddNoteActivity.this))
    		{
    			jsonObject = getBooksFromServer(user);
    		}
    		else if(!HttpHelper.isConnect(AddNoteActivity.this))
    		{
    			jsonObject = null;
    		}
    	}
    	return jsonObject;
    }
    
    
    
    /**
     * @name:doGetSelectedBook()
     * @Param:long ctime
     * @return:String
     * @Function:do get books from local or server
     */
    
    private String doGetSelectedBook(long ctime)
    {
    	int bookId = 0;
    	String bookname = "";
    	sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	   	
    	{
	    	String table     = FinalDefinition.DATABASE_TABLE_NOTE;
	    	String[] columns = {"idbook"};
	    	String selection = "createtime=?";
	    	String[] selectionArgs = {ctime+""};
	    	String groupBy   = null;
	    	String having    = null;
	    	String orderBy   = null;
	    	
	    	Cursor cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	    	Log.i("new add",ctime+" ");
	    	Log.i("new add", "cursor get book selected"+cursor.getCount());
	    	if(cursor!=null && cursor.getCount()!=0)
	    	{
	    		if(cursor.moveToFirst())
	    		{
	    			int index = cursor.getColumnIndex("idbook");
	    			
	    			bookId    = cursor.getInt(index);
		    		Log.i("add",bookId+"  bookId");
	    		}
	    		
	    	}
	    	cursor.close();
    	}
    	
    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
    	String[] columns   = {"bookname"};
    	String selection = "idnotebook=?" ;
    	String[] selectionArgs = {""+bookId};
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
    	return bookname;
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
        View contentView = LayoutInflater.from(getApplicationContext())  
                .inflate(R.layout.note_recording, null);  

        // 声明一个弹出框  
        winRecord = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, false);  
        winRecord.setFocusable(true);
        winRecord.setTouchable(true);
        winRecord.setBackgroundDrawable(getResources ().getDrawable(R.drawable.mic_back));//add back
        winRecord.setAnimationStyle(R.style.PopupRecordAnimation); 
        
    	timingText       = (TextView)contentView.findViewById(R.id.text_recordingTime); 
    	buttonStart      = (Button)contentView.findViewById(R.id.button_startRecording);
    	buttonStop       = (Button)contentView.findViewById(R.id.button_stopRecording);
    	buttonDel        = (Button)contentView.findViewById(R.id.button_deleteRecording);
    	
    	
    	
    	//        winRecording.setFocusable(true);  
        /* 
         * popupWindow.showAsDropDown（View view）弹出对话框，位置在紧挨着view组件 
         * showAsDropDown(View anchor, int xoff, int yoff)弹出对话框，位置在紧挨着view组件，x y 代表着偏移量 
         * showAtLocation(View parent, int gravity, int x, int y)弹出对话框 
         * parent 父布局 gravity 依靠父布局的位置如Gravity.CENTER  x y 坐标值 
         */   
        
//        void	 setOnDismissListener(PopupWindow.OnDismissListener onDismissListener)
//        Sets the listener to be called when the window is dismissed.
        
        PopupWindow.OnDismissListener dmListener = new PopupWindow.OnDismissListener() 
        {
			
			@Override
			public void onDismiss() 
			{
				// TODO Auto-generated method stub
			
				if(mic.getIsRecording())
				{
					abortionRecord();
					Toast.makeText(AddNoteActivity.this, "录音终止。", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(AddNoteActivity.this, "Dismiss", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		this.winRecord.setOnDismissListener(dmListener);
		
		
		
		/**
		 * winPlay
		 */
		
		 View playView = LayoutInflater.from(getApplicationContext())  
         .inflate(R.layout.playmic, null);  
		
		 // 声明一个弹出框  
		 winPlay = new PopupWindow(playView,365,248, false);  
		 winPlay.setFocusable(true);
		 winPlay.setTouchable(true);
		// BitmapDrawable bitmap = new BitmapDrawable();//add back
		 winPlay.setBackgroundDrawable(getResources ().getDrawable(R.drawable.play_background));//add back
		 winPlay.setAnimationStyle(R.style.PopupPlayAnimation); 
		 
		
		 seekBar_player    = (SeekBar)playView.findViewById(R.id.player_seekbar);
		 buttonPlayer_go   = (Button)playView.findViewById(R.id.button_player_start);
         buttonPlayer_stop = (Button)playView.findViewById(R.id.button_player_stop);
         buttonReMic       = (Button)playView.findViewById(R.id.button_reRecord);
         textStarted       = (TextView)playView.findViewById(R.id.text_started);
         textEnded         = (TextView)playView.findViewById(R.id.text_ended);
         player     = new Player(this,seekBar_player,myHandler);
         
         
         PopupWindow.OnDismissListener dm_PlayListener = new PopupWindow.OnDismissListener() 
         {
 			
 			@Override
 			public void onDismiss() 
 			{
 				// TODO Auto-generated method stub
 			
 				if(player.getIsPlaying())
 				{
 					player.stop();
 					Toast.makeText(AddNoteActivity.this, "播放终止。", Toast.LENGTH_SHORT).show();
 				}
 				else
 				{
 					Toast.makeText(AddNoteActivity.this, "Dismiss", Toast.LENGTH_SHORT).show();
 				}
 			}
 		};
 		
 		this.winPlay.setOnDismissListener(dm_PlayListener);
		 
 		/**
		  * winAttach
		 */ 		 
 		
 		
		 View attachView = LayoutInflater.from(getApplicationContext())  
         .inflate(R.layout.attach, null);  
		 winAttach = new PopupWindow(attachView,LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, false);
		 winAttach.setFocusable(true);
		 winAttach.setTouchable(true);
		 BitmapDrawable bitmap = new BitmapDrawable();
		 winAttach.setBackgroundDrawable(bitmap);
		 winAttach.setAnimationStyle(R.style.PopupAttachAnimation);
		 
		 buttonGallery = (Button)attachView.findViewById(R.id.button_gallery);
		 buttonCamera  = (Button)attachView.findViewById(R.id.button_camera);
		 buttonDraw    = (Button)attachView.findViewById(R.id.button_draw);
		 winAttach.setIgnoreCheekPress();
		 winAttach.setClippingEnabled(true);
		 PopupWindow.OnDismissListener dm_AttachListener = new PopupWindow.OnDismissListener() 
         {
 			
 			@Override
 			public void onDismiss() 
 			{
 				// TODO Auto-generated method stub	
 				switch(getAction())
 				{
 				case FinalDefinition.INTENT_CODE_GALLERY:
 					doGotoGallery();
 					break;
 				case FinalDefinition.INTENT_CODE_CAMERA:
 					doGotoCamera();
 					break;
 				case FinalDefinition.INTENT_CODE_DRAW:
 					doGotoDraw();
 					break;
 				default:
 					break;
 				}
 				
 			}
 			private int getAction()
 			{
 				return action;
 			}
 		};
 		
 		this.winAttach.setOnDismissListener(dm_AttachListener);

 		/**
 		 * 
 		 */
 		initPopButtonListeners();
    }  
    
    /**
     * @name:timing()
     * @Param:
     * @return:
     * @Function:timing for recorder
     */
    
    private void initPopButtonListeners()
    {
    	OnClickListener onStartListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				// TODO Auto-generated method stub
				micFile = mic.getAudioFile();
				if(micFile!=null)
				{
					new AlertDialog.Builder(AddNoteActivity.this) 
		            .setTitle("提示") 
		            .setMessage("已存在录音，是否替换？") 
		            .setPositiveButton
		            (
		            	"确定", new DialogInterface.OnClickListener()
			            { 
							            @Override 
							public void onClick(DialogInterface arg0, int arg1) 
							{ 
							// TODO Auto-generated method 
							       startRecord();
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
				}
				else
				{
					 startRecord();   
				}
				
			}
		};
		
		
		OnClickListener onStopListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				stopRecord();
			}
    	};
    	
    	OnClickListener onDelListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				new AlertDialog.Builder(AddNoteActivity.this) 
	            .setTitle("提示") 
	            .setMessage("确认移除录音？") 
	            .setPositiveButton
	            (
	            	"确定", new DialogInterface.OnClickListener()
		            { 
						            @Override 
						public void onClick(DialogInterface arg0, int arg1) 
						{ 
						// TODO Auto-generated method 
						       delRecord();
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
				
			}
    	};
    	
    	
    	
    	OnClickListener onPlayListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				if(micFile==null)
				{
					micFile = mic.getAudioFile();
				}
				if(micFile==null||!micFile.exists())
				{
					return;
				}
				else
				{
					player.play();
				}
			}
    	};
    	
    	OnClickListener onPlayStopListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				player.pause();
			}
    	};
    	
    	OnClickListener onRemicListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				player.doStopOnly();
				
				new AlertDialog.Builder(AddNoteActivity.this) 
	            .setTitle("提示") 
	            .setMessage("对录音感到不爽？") 
	            .setPositiveButton
	            (
	            	"重录", new DialogInterface.OnClickListener()
		            { 
						            @Override 
						public void onClick(DialogInterface arg0, int arg1) 
						{ 
						// TODO Auto-generated method 
						       delRecord();
						       winPlay.dismiss();
							   winRecord.showAsDropDown(buttonMic);					       
						} 
					}
	            	
	            )
	            .setNeutralButton
	            (
            		"删除", new DialogInterface.OnClickListener() 
	            	{
						
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
							// TODO Auto-generated method stub
							delRecord();
							winPlay.dismiss();	
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
			}
    	};
    	
    	//gallery	
    	OnClickListener onGalleryListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				setAction(FinalDefinition.INTENT_CODE_GALLERY);
				doScaleDismissAnimation(buttonGallery,winAttach);
			}
    	};
    	//camera
    	OnClickListener onCameraListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				setAction(FinalDefinition.INTENT_CODE_CAMERA);
				doScaleDismissAnimation(buttonCamera,winAttach);
			}
    	};
    	
    	//draw
    	OnClickListener onDrawListener = new Button.OnClickListener()
    	{
			@Override
			public void onClick(View button) 
			{
				setAction(FinalDefinition.INTENT_CODE_DRAW);
				doScaleDismissAnimation(buttonDraw,winAttach);
			}
    	};
		
    	//MicPhone
		this.addButtonListener(buttonStart, onStartListener);
		this.addButtonListener(buttonStop, onStopListener);
		this.addButtonListener(buttonDel, onDelListener);
		
		//MicPhone play
		this.addButtonListener(buttonPlayer_go, onPlayListener);
		this.addButtonListener(buttonPlayer_stop, onPlayStopListener);
		this.addButtonListener(buttonReMic, onRemicListener);
		
		//image attach
		this.addButtonListener(buttonGallery, onGalleryListener);
		this.addButtonListener(buttonCamera, onCameraListener);
		this.addButtonListener(buttonDraw, onDrawListener);
		
			
		this.buttonStop.setEnabled(false);
		this.buttonDel.setEnabled(false);
		this.buttonPlay.setEnabled(false);
    }
	
    
    /**
     * @name:timing()
     * @Param:
     * @return:
     * @Function:timing for recorder
     */
    private void timing()
    {
    	timeFlag = true;
    	new Thread()
    	{
    		public void run()
    		{	
    			while(timeFlag)
    			{
    				try 
    				{
    					Message msg = new Message();
    					msg.what = FinalDefinition.MESSAGE_TIMING;
    					myHandler.sendMessage(msg);
    					Thread.sleep(1000);
    				} 
    				catch (InterruptedException e) 
    				{
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    					Thread.currentThread().interrupt();
    				}
    			}
    		}
    	}.start();
    }
    
    /**
     * @name:stopTiming()
     * @Param:
     * @return:
     * @Function:stop timing for recorder
     */
    private void stopTiming()
    {
    	Message msg = new Message();
		msg.what = FinalDefinition.MESSAGE_STOP_TIMING;
		myHandler.sendMessage(msg);
    }
    
    static int minute     = 0;
	static int second     = 0;  
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        		     		
                switch (msg.what) {
                        case FinalDefinition.MESSAGE_TIMING:
                            
                        	second += 1;
                        	if( second == 60 )
                        	{
                        		minute += 1;
                        		second = 0;
                        	}
                        	String text = String.format("%02d:%02d",minute,second);
                        	timingText.setText(text);
                            break;
                        case FinalDefinition.MESSAGE_STOP_TIMING:
                        	timingText.setText("");
                        	timeFlag = false;
                        	second = 0;
                        	minute = 0;
                        	break;
                        case FinalDefinition.MESSAGE_PROGRESS:
                        	textStarted.setText(player.getStartedSecond());
                        	textEnded.setText(player.getEndSecond());
                        	break;
                        case FinalDefinition.MESSAGE_DISMISS:
                        	progressLog.dismiss();
                        	break;
                        case FinalDefinition.MESSAGE_LOCAL:
                        	progressLog.setMessage("本地ing...");	
                        	break;
                        case FinalDefinition.MESSAGE_CLOUD:
                        	progressLog.setMessage("云端ing...");	
                        	break;
                        case FinalDefinition.MESSAGE_SYNC:
                        	progressLog.setMessage("Syncing...");	
                        	break;
                        case FinalDefinition.MESSAGE_JOBDONE:
                        	progressLog.dismiss();
                        	doRelease();
                        	AddNoteActivity.super.onBackPressed();
                        	AddNoteActivity.this.finish();
                        	break;
                        case FinalDefinition.MESSAGE_INIT_DONE:
                        	progressLog.dismiss();
                        	
                        	break;
                        default:
                            super.handleMessage(msg);
            }
        }
    };
    
    public Handler getHandler()
    {
    	return myHandler;
    }
    
    /**
     * @name:startRecord()
     * @Param:
     * @return:
     * @Function:start
     */
    
    private void startRecord()
    {
        mic.deleteAudio(null);
        mic.startRecording();
        timing(); 
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        buttonDel.setEnabled(false);
        
        isEdited = true;
    }
    
    /**
     * @name:stopRecord()
     * @Param:
     * @return:
     * @Function:start
     */
    
    private void stopRecord()
    {
    	mic.stopRecording();
        stopTiming(); 
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false); 
        this.micFile = mic.getAudioFile();
        if(micFile!=null && micFile.exists())
        {
        	buttonDel.setEnabled(true);
        	buttonPlay.setEnabled(true);
        }
    }
    
    /**
     * @name:delRecord()
     * @Param:
     * @return:
     * @Function:delete
     */
    
    private void delRecord()
    {
    	if(micFile==null||!micFile.exists())
    	{
    		return;
    	}
    	
    	if(micFile!=null && micFile.exists())
        {
        	mic.deleteAudio(null);
        	this.micFile = null;
        	
        	this.buttonDel.setEnabled(false);
        	this.buttonPlay.setEnabled(false);
        }
       
    	isEdited = true;
    }
    
    /**
     * @name:abortionRecord()
     * @Param:
     * @return:
     * @Function:start
     */
    
    private void abortionRecord()
    {
    	mic.stopRecording();
        stopTiming(); 
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false); 
        this.micFile = mic.getAudioFile();
        delRecord();
    }
    
    /**
     * @name:doScaleAnimation()
     * @Param:View
     * @return:
     * @Function:animation
     */
    
    private void doScaleDismissAnimation(View view,final PopupWindow win)
    {
    	AnimationSet animationSet = new AnimationSet(true);
    	ScaleAnimation myAnimation_Scale;
		myAnimation_Scale =new ScaleAnimation(1.0f, 4.4f, 1.0f, 4.4f,
	             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		myAnimation_Scale.setDuration(400);
		animationSet.addAnimation(myAnimation_Scale);
		
		AlphaAnimation myAnimation_Alpha;
		myAnimation_Alpha = new AlphaAnimation(1.0f,0.0f);
		myAnimation_Alpha.setDuration(200);
		animationSet.addAnimation(myAnimation_Alpha);
		animationSet.setFillEnabled(false);
		
		/*********************************************************/
	
		
		animationSet.setAnimationListener
    	(
    			new AnimationListener() 
	    		{
		    		@Override
		    		public void onAnimationStart(Animation animation) {
		    			
		    		}
		    		
		    		@Override
		    		public void onAnimationRepeat(Animation animation) {
		    		
		    		}
		    		
		    		@Override
		    		public void onAnimationEnd(Animation animation) {
		    			win.dismiss();    			
		    		}
	    		}
    	);
		
		view.setAnimation(animationSet);
		view.startAnimation(animationSet);		
    }
    
    /**
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     *                             开始图片处理
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     */
    
    /**
     * @name:setAction()
     * @Param:null
     * @return:void
     * @Function:do what?
     */
    
    private void setAction(int action_code)
    {
    	this.action = action_code;
    }
    
    
    /**
     * @name:doGotoGallery()
     * @Param:null
     * @return:void
     * @Function:do Pick Local image in gallery
     */
    private void doGotoGallery()
    {
    	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(intent, FinalDefinition.INTENT_CODE_GALLERY);  
    }
    
    /**
     * @name:doGotoCamera()
     * @Param:null
     * @return:void
     * @Function:do Pick Local image from camera
     */
    private void doGotoCamera()
    {
    	File picPath = new File(FinalDefinition.BASE_IMAGE_PATH);
    	//没有目录先创建目录   (确保文件夹存在）
	    if(!picPath.exists())
	    {  
	    	picPath.mkdir();   	
        }	      
		String createTime = System.currentTimeMillis()+""; 	 
		tempImageName = "";
		tempImageName = createTime + ".jpg";
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   	
    	File file = new File(picPath,tempImageName);
		Uri uri     = Uri.fromFile(file);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, FinalDefinition.INTENT_CODE_CAMERA);
		
		picPath = null;
    }
    
    /**
     * @name:doGotoDraw()
     * @Param:null
     * @return:void
     * @Function:do Draw a picture
     */
    private void doGotoDraw()
    {
		Intent intent = new Intent(AddNoteActivity.this,DrawActivity.class);
		this.startActivityForResult(intent, FinalDefinition.INTENT_CODE_DRAW);
    }
    
    
    /**
     * @name:doAttachGalleryImage()
     * @Param:String
     * @return:void
     * @Function:do attach to text
     */
    private void doAttachGalleryImage(String filePath)
    {	
    	//do scale original bitmap
        File image= new File(filePath);
        String createTime = image.lastModified()+"";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = 10;//缩小为1/10
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        
        //存放临时文件的文件夹
		File picPath = new File(FinalDefinition.BASE_IMAGE_PATH);
	    if(!picPath.exists())
	    {  
	    	picPath.mkdir();//没有目录先创建目录  	        	    	
        }
	    //do create temporary file
	    tempImageName = "";
	    tempImageName = createTime + ".jpg";
        FileOutputStream fos = null;
		try 
		{
			fos = new FileOutputStream(FinalDefinition.BASE_IMAGE_PATH + tempImageName);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
            try 
            {
				fos.close();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//do attach
		SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
//		int start = sbuilder.length();
//		int end   = start + tempImageName.length();
//		sbuilder.append(tempImageName);
		int start = editContent.getSelectionEnd();
		int end   = start + tempImageName.length();
		sbuilder.insert(start, tempImageName);
		ImageSpan imSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);
		sbuilder.setSpan(imSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		imageMap.put(tempImageName, new MyBitmap(bitmap));
	
		editContent.setText(sbuilder);
		editContent.setSelection(end);
		
//		isEdited = true;
		options = null;
		fos = null;
		System.gc();
    }
    
    
    /**
     * @name:doAttachCameraImage()
     * @Param:String
     * @return:void
     * @Function:do attach to text
     */
    private void doAttachCameraImage()
    {
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(FinalDefinition.BASE_IMAGE_PATH + tempImageName, options);
        options.inSampleSize = 10;//缩小为1/10
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(FinalDefinition.BASE_IMAGE_PATH + tempImageName, options);
    	
        //压缩图片
        FileOutputStream fos = null;
		try 
		{
			fos = new FileOutputStream(FinalDefinition.BASE_IMAGE_PATH + tempImageName);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
            try 
            {
				fos.close();				
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//do attach
		SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
//		int start = sbuilder.length();
//		int end   = start + tempImageName.length();
//		sbuilder.append(tempImageName);
		int start = editContent.getSelectionEnd();
		int end   = start + tempImageName.length();
		sbuilder.insert(start, tempImageName);
		ImageSpan imSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);
		sbuilder.setSpan(imSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		imageMap.put(tempImageName, new MyBitmap(bitmap));
	
		editContent.setText(sbuilder);
		editContent.setSelection(end);
		
//		isEdited = true;
		options = null;
		fos = null;
		System.gc();
		
		
    }
    
    
    /**
     * @name:doAttachDrawImage();
     * @Param:String
     * @return:void
     * @Function:do attach to text
     */
    private void doAttachDrawImage(Intent imageReturnedIntent)
    {
    	tempImageName = imageReturnedIntent.getStringExtra("filename");
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(FinalDefinition.BASE_IMAGE_PATH + tempImageName, options);
        options.inSampleSize = 2;//缩小为1/2
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(FinalDefinition.BASE_IMAGE_PATH + tempImageName, options);
    	
    	//do attach
		SpannableStringBuilder sbuilder = new SpannableStringBuilder(editContent.getText());
		int start = editContent.getSelectionEnd();
		int end   = start + tempImageName.length();
		sbuilder.insert(start, tempImageName);
		ImageSpan imSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);
		sbuilder.setSpan(imSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		imageMap.put(tempImageName, new MyBitmap(bitmap));
	
		editContent.setText(sbuilder);
		editContent.setSelection(end);
		
		options = null;
//		isEdited = true;
		System.gc();
    	
    }
    
    
    
    
    /**
     * @name:isEdited()
     * @Param:
     * @return:boolean
     * @Function:whether save or not
     */
    
    private boolean isEdited()
    {
//    	Log.i("new add",(this.micFile==null)+"");
//    	Log.i("new add",(editTitle.getText().toString().equals(""))+"");
//    	Log.i("new add",(editContent.getText().toString().equals(""))+"");
//    	Log.i("new add",(this.micFile==null && editTitle.getText().equals("") && editContent.getText().equals(""))+"");
    	
    	if(this.MODEL==FinalDefinition.NOTE_NEW)
    	{
    		if(this.micFile==null && !isEdited)
        	{
        		return false;
        	}
        	else
        		return true;
    	}
    	else if(this.MODEL==FinalDefinition.NOTE_UPDATE)
    	{
    		if(isEdited)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	else
    		return false;
    	
    }
    
    
    
    
    /**
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     *                          Activity方法重载
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     */
    

    /**
     * @name:onActivityResult()
     * @Param:
     * @return:void
     * @Function:do picture file saving when the activity is returned
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) 
    { 
        
        if (resultCode != RESULT_OK) 
        {        
            return;
        }       
        switch(requestCode)
        {
	        case FinalDefinition.INTENT_CODE_GALLERY:
               
	        	Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};	
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();	
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);	              
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                
                doAttachGalleryImage(filePath);
	         	            
	            break;
	        case FinalDefinition.INTENT_CODE_CAMERA:
	        	progressLog.show();
	        	doAttachCameraImage();
	        	Message msg = new Message();
	        	msg.what = FinalDefinition.MESSAGE_DISMISS;
	        	myHandler.sendMessage(msg);
	        	msg = null;
	        	break;
	        case FinalDefinition.INTENT_CODE_DRAW:
	        	doAttachDrawImage(imageReturnedIntent);
	        	break;
	    }
     }
    
    
    /**
     * @name:onDestory()
     * @Param:
     * @return:
     * @Function:release resources
     */
    
    protected void onDestory()
    {
    	//释放掉播放器占用的资源
    	
    	Log.i("destory", "release");
    	doRelease();
    	
    	super.onDestroy();
    	
    }
    
    private void doRelease()
    {
    	player.doStopRelease();
    	imageMap.clear();
    	handMap.clear();
    	handView = null;
    	
    	progressLog = null;
    	    
    	mic = null;
    	micFile = null;
    	imageMap= null;
    	handMap= null;
   
    	editTitle = null;
    	editContent = null;
    	title = null;
    	content = null;
   
    	tempImageName= null;
    		
    	handView= null;
  
    	spinnerBook= null;
    	sqliteHelper= null;
    	parentLay= null;
    		/**
    		 * Buttons
    		 */
    	buttonMic= null;
    	buttonPlay= null;
    	buttonAttach= null;
    	buttonHand= null;
    		
    	buttonStart= null;
    	buttonStop= null;
    	buttonDel= null;
    		
    	buttonPlayer_go= null;
    	buttonPlayer_stop= null;
    	buttonReMic= null;
    	textStarted= null;
    	textEnded= null;
    	seekBar_player= null;
    	player= null;
    		
    	buttonGallery= null;
    	buttonCamera= null;
    	buttonDraw= null;
    		                        
    		
    	winRecord= null;
    	winPlay= null;
    	winAttach= null;
    		
    	timingText= null;
    	System.gc();
    }
    
    /**
     * @name:onBackPressed()
     * @Param:
     * @return:
     * @Function:release resources
     */
    public void onBackPressed()
    {
    	if(handView.isShown())
    	{
    	  	handView.stop();
    		parentLay.removeView(handView);
    		buttonHand.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_hand_selector));
    	}
    	else if(isEdited())
    	{
    		switch(MODEL)
    		{
    		case FinalDefinition.NOTE_NEW:
    			doInsert();
    			break;
    		case FinalDefinition.NOTE_UPDATE:
    			doUpdate();
    			break;
    		} 		
    	}
    	else
    	{
    		doRelease();
    		this.finish();
    		super.onBackPressed();
    	}
    }

    
    
    /**
     * ==================================================================
     * 							DO SAVE AND EXIT
     * ==================================================================
     */
    
    /**
     * @name:doGetData()
     * @Param:
     * @return:
     * @Function:release resources
     */
    private JSONObject doGetData()
    {
    	this.title      = editTitle.getText().toString();
    	this.content    = editContent.getText().toString();
    	this.micFile    = this.mic.getAudioFile();
    	Log.i("sync",(micFile==null)+" 录音是否被删");
       	    	
    	JSONObject jsonNote = new JSONObject();

    	try 
    	{	
    		jsonNote.put(Sync.USER_ID, userID);
    		jsonNote.put(Sync.BOOKNAME,this.spinnerBook.getAdapter().getItem(spinnerBook.getSelectedItemPosition()).toString());
    		jsonNote.put(Sync.TITLE, title);       
    		jsonNote.put(Sync.TEXT, content);
    		
    		if(micFile!=null && micFile.exists())
        	{

    			{
    				String b = Base64.encodeToString(TypeTransformer.FileToBytes(micFile), Base64.DEFAULT);
    				jsonNote.put(Sync.AUDIO, b);
    				jsonNote.put(Sync.AUDIO_NAME, micFile.getName());
    			}	      		
        	}
    		if(micFile==null || !micFile.exists())
        	{
    			jsonNote.put(Sync.AUDIO, "");
				jsonNote.put(Sync.AUDIO_NAME, "");
        	}
    		
    		if(imageMap!=null && !imageMap.isEmpty())
    		{
    	    	{
    			    String b = Base64.encodeToString(TypeTransformer.ObjectToBytes(imageMap), Base64.DEFAULT);
    				jsonNote.put(Sync.IMAGE, b);
    	    	}		
    		}
    		if(imageMap==null || imageMap.isEmpty())
        	{	
    			jsonNote.put(Sync.IMAGE, "");
        	}
    		
    		if(handMap!=null && !handMap.isEmpty())
    		{
    	    	{
    	    		String b = Base64.encodeToString(TypeTransformer.ObjectToBytes(handMap), Base64.DEFAULT);
    	    		jsonNote.put(Sync.HAND, b);
    	    	}
    		}
    		
      		if(handMap==null || handMap.isEmpty())
        	{
    			jsonNote.put(Sync.HAND, "");
        	}
    		
      		if(MODEL==FinalDefinition.NOTE_NEW)
      		{
      			jsonNote.put(Sync.CTIME, System.currentTimeMillis());
      		}
      		else if(MODEL==FinalDefinition.NOTE_UPDATE)
      		{
      			Intent intent = getIntent();
        		Long note_ctime = intent.getLongExtra("note",0);
        		jsonNote.put(Sync.CTIME, note_ctime);
      		}
    		
    		jsonNote.put(Sync.UTIME, System.currentTimeMillis());
    		
    		Log.i("view",jsonNote.toString());
    	}
    	catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return jsonNote;  	
    }
   
    /**
     * @name:doInsertNoteIntoLocal()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private void doInsertNoteIntoLocal(JSONObject note)
    {
    	Message msg = new Message();
    	msg.what    = FinalDefinition.MESSAGE_LOCAL;
    	myHandler.sendMessage(msg);
    	int bookId = 0;
    	sqliteHelper.doOpen();
    	Cursor cursor = null;
    	{
    		try
    		{
		    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		    	String[] columns = {"idnotebook"};
		    	String selection = "bookname=?";
		    	String[] selectionArgs = {note.getString(Sync.BOOKNAME)};
		    	String groupBy   = null;
		    	String having    = null;
		    	String orderBy   = null;	
		    
		    	cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);	    	
		    	if(cursor!=null && cursor.getCount()!=0)
		    	{
		    		if(cursor.moveToFirst())
		    		{
		    			int index = cursor.getColumnIndex("idnotebook");		    			
		    			bookId    = cursor.getInt(index);
		    		}		    		
		    	}
		    	
    		}
    		catch (JSONException e)
    		{// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
    		finally
    		{
    			cursor.close();
    		}
    	}
    	
    	ContentValues cv = new ContentValues();
    	try
    	{
    		String title = ((note.getString(Sync.TITLE).equals(""))?"无标题笔记":note.getString(Sync.TITLE));
    		String content = ((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT));
    		
    		cv.put(FinalDefinition.DATABASE_NOTE_IDU, note.getString(Sync.USER_ID));
    		cv.put(FinalDefinition.DATABASE_NOTE_IDB, bookId);
    		cv.put(FinalDefinition.DATABASE_NOTE_TITLE, title);
    		cv.put(FinalDefinition.DATABASE_NOTE_CONTENT, content);
    		if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_IMAGE, b);
			}
    		
     		if(!note.getString(Sync.AUDIO).equals(""))
			{
				String image = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_SOUND, b);
				cv.put(FinalDefinition.DATABASE_NOTE_SONAME, note.getString(Sync.AUDIO_NAME));
			}
     		if(!note.getString(Sync.HAND).equals(""))
			{
				String image = note.getString(Sync.HAND);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_HAND, b);
			}
     		cv.put(FinalDefinition.DATABASE_NOTE_CTIME, note.getLong(Sync.CTIME));
     		cv.put(FinalDefinition.DATABASE_NOTE_UTIME, note.getLong(Sync.UTIME));
     		
     		long result = sqliteHelper.doInsert(FinalDefinition.DATABASE_TABLE_NOTE, cv);
      		
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		sqliteHelper.doClose();
    	}
    }
    
    
    /**
     * @name:doInsertNoteIntoServer()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private boolean doInsertNoteIntoServer(JSONObject note)
    {
    	
    	Message msg = new Message();
    	msg.what    = FinalDefinition.MESSAGE_CLOUD;
    	myHandler.sendMessage(msg);
    	JSONObject jsonResult;
    	String url           = HttpHelper.Base_Url+"servlet/InsertNoteServlet";
    	JSONObject parameter = new JSONObject();
    	
    	try 
    	{
			parameter.put("note", note);
			Log.i("string", parameter.toString());
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
     * @name:doInsertSyncData()
     * @Param:int(ACTION),int(TABLE)
     * @return:void
     * @Function:add the note to sync
     */
    private void doInsertSyncData(int action,int table,String whereArgs,JSONObject note)
    {
    	
    	Message msg = new Message();
    	msg.what    = FinalDefinition.MESSAGE_SYNC;
    	myHandler.sendMessage(msg);
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
	    	ContentValues cv = new ContentValues();
	    	cv.put(Sync.ACTION, action);
	    	cv.put(Sync.TABLE, table);
	    	cv.put(Sync.LOGTIME, System.currentTimeMillis());
	    	
	    	String title = ((note.getString(Sync.TITLE).equals(""))?"无标题笔记":note.getString(Sync.TITLE));
			String content = ((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT));
			
			cv.put(Sync.USER_ID, note.getString(Sync.USER_ID));
			cv.put(Sync.BOOKNAME, note.getString(Sync.BOOKNAME));
			cv.put(Sync.TITLE, title);
			cv.put(Sync.TEXT, content);
			cv.put(Sync.CLAUSE, whereArgs);
			if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
				cv.put(Sync.IMAGE, b);
			}
			
	 		if(!note.getString(Sync.AUDIO).equals(""))
			{
				String image = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
				cv.put(Sync.AUDIO, b);
				cv.put(Sync.AUDIO_NAME, note.getString(Sync.AUDIO_NAME));
			}
	 		if(!note.getString(Sync.HAND).equals(""))
			{
				String image = note.getString(Sync.HAND);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
				cv.put(Sync.HAND, b);
			}
	 		cv.put(Sync.CTIME, note.getLong(Sync.CTIME));
	 		cv.put(Sync.UTIME, note.getLong(Sync.UTIME));
	 		
	 		long result = sync.doInsert(Sync.DATABASE_TABLE, cv);
	 		Log.i("sync",result+"是新插入的ID");
	 		Log.i("sync","插入新同步记录，时间为："+note.getLong("createtime"));  		
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
     * @name:doAdd()
     * @Param:null
     * @return:void
     * @Function:add the note
     */
    private void insert()
    {
    	new Thread()
    	{
    		public void run()
    		{
    			Looper.prepare();
    			
    	    	JSONObject note = doGetData();
    	    	if(HttpHelper.isConnect(AddNoteActivity.this))
    	    	{
    	    		doInsertNoteIntoLocal(note);
    	    		boolean isSuccess = doInsertNoteIntoServer(note);
    	    		if(!isSuccess)
    	    		{
    	    			doInsertSyncData(Sync.ACTION_INSERT,Sync.TABLE_NOTE,null,note);
    	    		}
    	    	}
    	    	else if(!HttpHelper.isConnect(AddNoteActivity.this))
    	    	{
    	    		doInsertNoteIntoLocal(note);
    	    		doInsertSyncData(Sync.ACTION_INSERT,Sync.TABLE_NOTE,null,note);
    	    	}
    	    	note = null;
    	    	System.gc();
    	    	
    	    	Message msg = new Message();
    	    	msg.what    = FinalDefinition.MESSAGE_JOBDONE;
    	    	myHandler.sendMessage(msg);
    		}
    	}.start();
    
    }
    
    
    /**
     * @name:doInsert()
     * @Param:null
     * @return:void
     * @Function:do final insert the note
     */
    private void doInsert()
    {
    	new AlertDialog.Builder(this) 
        .setTitle("提示") 
        .setMessage("笔记尚未保存，是否退出前保存？") 
        .setPositiveButton
        (
        	"确定", new DialogInterface.OnClickListener()
            { 
    			            @Override 
    			public void onClick(DialogInterface arg0, int arg1) 
    			{ 
    			// TODO Auto-generated method stub 
    			    progressLog.show();
    			    insert();
    		        	
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
		        	AddNoteActivity.super.onBackPressed();
    			}
    		}
        	
        ).show();
    }
    
    
    /**
     * @name:doUpdate()
     * @Param:null
     * @return:void
     * @Function:do final update the note
     */
    private void doUpdate()
    {
    	new AlertDialog.Builder(this) 
        .setTitle("提示") 
        .setMessage("笔记尚未保存，是否退出前保存？") 
        .setPositiveButton
        (
        	"确定", new DialogInterface.OnClickListener()
            { 
    			            @Override 
    			public void onClick(DialogInterface arg0, int arg1) 
    			{ 
    			// TODO Auto-generated method stub 
    			    progressLog.show();
    			    update();
    		        	
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
		        	AddNoteActivity.super.onBackPressed();
    			}
    		}
        	
        ).show();
    }
    
    /**
     * @name:update()
     * @Param:null
     * @return:void
     * @Function:do update the note
     */
    
    private void update()
    {
    	new Thread()
    	{
    		public void run()
    		{
    			Looper.prepare();
    			
    	    	JSONObject note = doGetData();
    	    	JSONObject whereArgs = new JSONObject();
	    		Intent intent = getIntent();
	    		Long note_ctime = intent.getLongExtra("note",0);
	    		try 
	    		{
					whereArgs.put("where", "where iduser=? and createtime=?");
					whereArgs.put("iduser", userID);
    	    		whereArgs.put("ctime", note_ctime);
	    		} 
	    		catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    	if(HttpHelper.isConnect(AddNoteActivity.this))
    	    	{
    	    		doUpdateNoteIntoLocal(note);
    	    		boolean isSuccess = doUpdateNoteIntoServer(note);
    	    		if(!isSuccess)
    	    		{	
    	    			boolean isUpdateInSync = doDetectUpdateInSync(Sync.ACTION_UPDATE,Sync.TABLE_NOTE,whereArgs.toString(),note);
    	    			
    	    			if( !isUpdateInSync )
    	    			{
    	    				doInsertSyncData(Sync.ACTION_UPDATE,Sync.TABLE_NOTE,whereArgs.toString(),note);
    	    			}
    	    			
    	    		}
    	    	}
    	    	else if(!HttpHelper.isConnect(AddNoteActivity.this))
    	    	{
    	    		doUpdateNoteIntoLocal(note);
    	    		
    	    		boolean isUpdateInSync = doDetectUpdateInSync(Sync.ACTION_UPDATE,Sync.TABLE_NOTE,whereArgs.toString(),note);
	    			
	    			if( !isUpdateInSync )
	    			{
	    				doInsertSyncData(Sync.ACTION_UPDATE,Sync.TABLE_NOTE,whereArgs.toString(),note);
	    			}
    	    	}
    	    	note = null;
    	    	System.gc();
    	    	Message msg = new Message();
    	    	msg.what    = FinalDefinition.MESSAGE_JOBDONE;
    	    	myHandler.sendMessage(msg);
    		}
    	}.start();
    }
    
    /**
     * ===================================================================
     * 								DO UPDATE
     * ===================================================================
     */
    
    /**
     * @name:doUpdateNoteIntoLocal()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
     
    private void doUpdateNoteIntoLocal(JSONObject note)
    {
    	Message msg = new Message();
    	msg.what    = FinalDefinition.MESSAGE_LOCAL;
    	myHandler.sendMessage(msg);
    	int bookId = 0;
    	sqliteHelper.doOpen();
    	Cursor cursor = null;
    	{
    		try
    		{
		    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		    	String[] columns = {"idnotebook"};
		    	String selection = "bookname=?";
		    	String[] selectionArgs = {note.getString(Sync.BOOKNAME)};
		    	String groupBy   = null;
		    	String having    = null;
		    	String orderBy   = null;	
		    
		    	cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);	    	
		    	if(cursor!=null && cursor.getCount()!=0)
		    	{
		    		if(cursor.moveToFirst())
		    		{
		    			int index = cursor.getColumnIndex("idnotebook");		    			
		    			bookId    = cursor.getInt(index);
		    		}		    		
		    	}
		    	
    		}
    		catch (JSONException e)
    		{// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
    		finally
    		{
    			cursor.close();
    		}
    	}
    	
    	ContentValues cv = new ContentValues();
    	try
    	{
    		
    		String title = ((note.getString(Sync.TITLE).equals(""))?"无标题笔记":note.getString(Sync.TITLE));
    		String content = ((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT));
    		
    		cv.put(FinalDefinition.DATABASE_NOTE_IDU, note.getString(Sync.USER_ID));
    		cv.put(FinalDefinition.DATABASE_NOTE_IDB, bookId);
    		cv.put(FinalDefinition.DATABASE_NOTE_TITLE, title);
    		cv.put(FinalDefinition.DATABASE_NOTE_CONTENT, content);
    		if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_IMAGE, b);
			}else
			{
				byte[] b = null;
				cv.put(FinalDefinition.DATABASE_NOTE_IMAGE, b);
			}
    		
     		if(!note.getString(Sync.AUDIO).equals(""))
			{
				String image = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_SOUND, b);
				cv.put(FinalDefinition.DATABASE_NOTE_SONAME, note.getString(Sync.AUDIO_NAME));
			}
     		else
			{
				byte[] b = null;
				cv.put(FinalDefinition.DATABASE_NOTE_SOUND, b);
				cv.put(FinalDefinition.DATABASE_NOTE_SONAME,"");
			}
     		if(!note.getString(Sync.HAND).equals(""))
			{
				String image = note.getString(Sync.HAND);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_HAND, b);
			}
     		else
			{
				byte[] b = null;
				cv.put(FinalDefinition.DATABASE_NOTE_HAND, b);
			}
     		
     		Intent intent = getIntent();
    		Long note_ctime = intent.getLongExtra("note",0);
     		cv.put(FinalDefinition.DATABASE_NOTE_CTIME, note_ctime);
     		cv.put(FinalDefinition.DATABASE_NOTE_UTIME, note.getLong(Sync.UTIME));
     		
     		/**
     		 * update
     		 * String table, 
     		 * ContentValues values, 
     		 * String whereClause,
     		 * String[] whereArgs
     		 */
     		String where = "createtime=? and iduser=?";
     		String[] args = {note_ctime+"",userID+""};
     		
     		int affectedRows = sqliteHelper.doUpdate(FinalDefinition.DATABASE_TABLE_NOTE, cv, where, args);
     		Log.i("new add","update:"+affectedRows);
      		
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		sqliteHelper.doClose();
    	}
    }
    
    /**
     * @name:doUpdateNoteIntoServer()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */    
    private boolean doUpdateNoteIntoServer(JSONObject note)
    {
    	Message msg = new Message();
    	msg.what    = FinalDefinition.MESSAGE_CLOUD;
    	myHandler.sendMessage(msg);
    	JSONObject jsonResult;
    	String url           = HttpHelper.Base_Url+"servlet/UpdateNoteServlet";
    	JSONObject parameter = new JSONObject();
    	try 
    	{
    		Intent intent = getIntent();
    		Long note_ctime = intent.getLongExtra("note",0);
			parameter.put("note", note);
			parameter.put("where", "where iduser=? and createtime=?");
			parameter.put("iduser", userID);
			parameter.put("ctime", note_ctime);
			Log.i("string", parameter.toString());
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
     * @name:doDetectUpdateInSync()
     * @Param:
     * @return:boolean
     * @Function:do pre Detect In Sync
     */ 
    
    private boolean doDetectUpdateInSync(int action,int table,String whereArgs,JSONObject note)
    {
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
    		ContentValues cv = new ContentValues(); 
	    	cv.put(Sync.ACTION, action);
	    	cv.put(Sync.TABLE, table);
	    	cv.put(Sync.LOGTIME, System.currentTimeMillis());
	    	
	    	String title = ((note.getString(Sync.TITLE).equals(""))?"无标题笔记":note.getString(Sync.TITLE));
			String content = ((note.getString(Sync.TEXT).equals(""))?null:note.getString(Sync.TEXT));
			
			cv.put(Sync.USER_ID, note.getString(Sync.USER_ID));
			cv.put(Sync.BOOKNAME, note.getString(Sync.BOOKNAME));
			cv.put(Sync.TITLE, title);
			cv.put(Sync.TEXT, content);
			cv.put(Sync.CLAUSE, whereArgs);
			if(!note.getString(Sync.IMAGE).equals(""))
			{
				String image = note.getString(Sync.IMAGE);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.IMAGE, b);
			}
			else
			{
				byte[] b = null;
				cv.put(Sync.IMAGE, b);
			}
    		
			
	 		if(!note.getString(Sync.AUDIO).equals(""))
			{
				String image = note.getString(Sync.AUDIO);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.AUDIO, b);
				cv.put(Sync.AUDIO_NAME, note.getString(Sync.AUDIO_NAME));
			}
	 		else
			{
				byte[] b = null;
				cv.put(Sync.AUDIO, b);
				cv.put(Sync.AUDIO_NAME,"");
			}
	 		if(!note.getString(Sync.HAND).equals(""))
			{
				String image = note.getString(Sync.HAND);
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.HAND, b);
			}
	 		else
			{
				byte[] b = null;
				cv.put(Sync.HAND, b);
			}
     		
	 		
	 		
	 		Intent intent = getIntent();
    		Long note_ctime = intent.getLongExtra("note",0);
     		cv.put(FinalDefinition.DATABASE_NOTE_CTIME, note_ctime);
	 		cv.put(Sync.UTIME, note.getLong(Sync.UTIME));
	 		
	 		/**
     		 * update
     		 * String table, 
     		 * ContentValues values, 
     		 * String whereClause,
     		 * String[] whereArgs
     		 */
     		String where = "createtime=? and iduser=? and action=?";
     		String[] args = {note_ctime+"",userID+"",action+""};
     		int affectedRows = sync.doUpdate(Sync.DATABASE_TABLE, cv, where, args);
     		if(affectedRows>0)
     		{
     			return true;
     		}
     		else
     			return false;
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			sync.doClose();
		}
		return false;
    }
}
