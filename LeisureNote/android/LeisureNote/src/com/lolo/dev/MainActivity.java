package com.lolo.dev;

import org.json.JSONException;

import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.helper.Syncer;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.MyImageButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

/*
 * Author:Lolo
 * Date:2012/3/20
 * File Description:to implement the Main Menu function
 * Included Component:
 * 1.4 self definition ImageButtons
 */

public class MainActivity extends Activity 
{
	private FrameLayout   buttonAddLayout       = null,
	                      buttonUpdateLayout    = null,
						  buttonAllLayout       = null,
						  buttonNotebookLayout  = null;
	private MyImageButton buttonAdd       = null,
						  buttonUpdate    = null,
						  buttonAll       = null,
						  buttonNotebook  = null;
	
	private ProgressDialog progressLog    = null;
	
	
	private Dialog  sucDialog;
	private Dialog  faiDialog;
	private Dialog  noSyncDialog;
	private Syncer  syncer;
	
	private int     noteNum;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);  
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
        
        syncer = new Syncer(MainActivity.this,myHandler);
        noteNum = getNotesCount();
        getComponents();
        setButtonListeners();
        initProgressDialog();
        initDialog();
    }
    
    private void getComponents()
    {
    	buttonAdd            = new MyImageButton(this,R.drawable.button_add_selector,"新增笔记");
    	buttonAddLayout      = (FrameLayout)findViewById(R.id.Button_Add);  	
    	buttonAddLayout.addView(buttonAdd);
    	
    	buttonUpdate         = new MyImageButton(this,R.drawable.button_update_selector,"同步备份");
    	buttonUpdateLayout   = (FrameLayout)findViewById(R.id.Button_Update);
    	buttonUpdateLayout.addView(buttonUpdate);
    	
    	buttonAll            = new MyImageButton(this,R.drawable.button_all_selector,"全部笔记"+"("+noteNum+")");
    	buttonAllLayout      = (FrameLayout)findViewById(R.id.Button_All);
    	buttonAllLayout.addView(buttonAll);
    	
    	buttonNotebook       = new MyImageButton(this,R.drawable.button_notebook_selector,"笔记本");
    	buttonNotebookLayout = (FrameLayout)findViewById(R.id.Button_Notebook);
    	buttonNotebookLayout.addView(buttonNotebook);
    	
    }
    
    
    private void initDialog()
    {
    	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    	{
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		};
		
		sucDialog = new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("同步完成！")
		.setPositiveButton("确定",listener)
		.create();
		
		faiDialog = new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("同步失败！网络或服务器故障！")
		.setPositiveButton("确定",listener)
		.create();
		
		noSyncDialog = new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("数据库已更新至最新，暂时无需同步！")
		.setPositiveButton("确定",listener)
		.create();
    }
    private void initProgressDialog()
    {
    	progressLog = new ProgressDialog(this);
    	progressLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressLog.setMessage("云里雾里ing...");
    	progressLog.setIndeterminate(false);
    	
    }
    
    private void setButtonListeners()
    {
    	//add
    	buttonAdd.setOnClickListener
    	(
    			new ImageButton.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						
						Intent intent = getIntent();
						intent.setClass(MainActivity.this, AddNoteActivity.class);
						intent.putExtra(FinalDefinition.NOTE_MODEL, FinalDefinition.NOTE_NEW);
						startActivity(intent);
					}
    				
    			}
    	);
    	
    	//update
    	buttonUpdate.setOnClickListener
    	(
    			new ImageButton.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						progressLog.show();
						doSync();
					}
    				
    			}
    	);
    	//all
    	buttonAll.setOnClickListener
    	(
    			new ImageButton.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Intent intent = getIntent();
						intent.setClass(MainActivity.this, NotesActivity.class);
						startActivity(intent);						
					}
    				
    			}
    	);
    	//note
    	buttonNotebook.setOnClickListener
    	(
    			new ImageButton.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Intent intent = getIntent();
						intent.setClass(MainActivity.this,BooksActivity.class);
						startActivity(intent);						
						
					}
    				
    			}
    	);
    }    
    
    
    private int getNotesCount()
    {
    	LocalSQLiteHelper sqliteHelper = new LocalSQLiteHelper(this);
    	sqliteHelper.doOpen();
    	Cursor cursor = null;
    	int count = 0;
    	try
		{
	    	String table     = FinalDefinition.DATABASE_TABLE_NOTE;
	    	String[] columns = {"idnote"};
	    	String selection = null;
	    	String[] selectionArgs = null;
	    	String groupBy   = null;
	    	String having    = null;
	    	String orderBy   = null;	
	    
	    	cursor = sqliteHelper.doQuery(table, columns, selection, selectionArgs, groupBy, having, orderBy);	    	
	    	if(cursor!=null && cursor.getCount()!=0)
	    	{
	    		count = cursor.getCount();		
	    	}
	    	
		}
		finally
		{
			cursor.close();
			sqliteHelper.doClose();
		}
		return count;
    }
    
    /**
     * 
     */
    private void doSync()
    {
		new Thread()
		{
			public void run()
			{
				Looper.prepare();
				
				syncer.doSync();
				Message msg = new Message();
				msg.what = FinalDefinition.MESSAGE_JOBDONE;
				myHandler.sendMessage(msg);
			}
		}.start();
    }
    
    /**
     * 
     */
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        		     		
                switch (msg.what) {
                        case FinalDefinition.MESSAGE_JOBDONE:
                        	progressLog.dismiss();
                            break;
                        case FinalDefinition.MESSAGE_NO_SYNC:
                        	noSyncDialog.show();
                            break;
                        case FinalDefinition.MESSAGE_SUC_SYNC:
                        	sucDialog.show();
                            break;
                        case FinalDefinition.MESSAGE_FAIL_SYNC:
                        	faiDialog.show();
                            break;
                        default:
                            super.handleMessage(msg);
            }
        }
    };
    
    /**
     * 
     */
    protected void onResume()
    {
    	super.onResume();
    	noteNum = getNotesCount();
    	buttonAll.setText("全部笔记"+"("+noteNum+")");
    }
}
