package com.lolo.old_verision;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.dev.R;
import com.lolo.dev.R.id;
import com.lolo.dev.R.layout;
import com.lolo.helper.HttpHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.helper.RecorderHelper;
import com.lolo.helper.SyncHelper;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.ImageAdapter;
import com.lolo.tools.MyVector;
import com.lolo.tools.NoteBean;
import com.lolo.tools.Sync;
import com.lolo.tools.TypeTransformer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/*
 * Author:Lolo
 * Date:2012/3/23
 * File Description:Add new note
 * Included Component:
 * 1.spnner for notebook
 * 2.editView for title
 * 3.editview for note
 * 4.button for image
 * 5.button for sound
 * 6.button for handwriting
 * 7.button for save
 * 
 */
public class AddActivity extends Activity 
{
	/*
	 * main view
	 * -------------------------------------------------------------------------------------------    
	 */
	private Spinner      spinnerBook;
	
	private EditText     editTitle,
	                     editContent;
	
	private Button       buttonSound,
	                     buttonPic,
	                     buttonHand,
	                     buttonAdd;
	
	private String       noteTitle,
						 noteContent;
	private File         noteAudio,
						 noteImage;
	private MyVector     noteHandVector;
	
	/*
	 * 录音view
	 * -------------------------------------------------------------------------------------------    
	 */
	private Button       buttonPlay,
					     buttonRecorder;
	
	private TextView     textRecordeTitle,
	                     textRecordeTime,
	                     textRecordeFormat,
	                     textRecordeTiming;
	
	private LinearLayout   deleteRecLayout;
	private RecorderHelper recorder = null;
	
	private final String   BUTTON_STATE_RECORDE = "按下录音",
	                       BUTTON_STATE_COMPLETE= "完成";
	
	private TimingThread   timingThread;
	private boolean        timingFlag = false;
	private boolean        flag       = true;
	private final int      REFRESH = 1;
	private int            timing  = 0;
	
	/*
	 * pic View
	 * --------------------------------------------------------------------------------------------    
	 */
	private TextView     textPicTitle,
	                     textPicTime,
	                     textPicFormat;
	private ImageView    imagePicView,
						 imagePreView;
	
	private FrameLayout  mainLayout,
	                     recorderLayout,
	                     timingLayout,
	                     picLayout;
	
	private Button       buttonPicCamera,
				         buttonPicLocal,
				         buttonPicDraw;
	/*
	 * Hand Write View
	 * --------------------------------------------------------------------------------------------    
	 */
	private FrameLayout  handInfoLayout;
	private Button       buttonHandWrite;
	private GridView     handGrid;
	private ImageAdapter handAdapter;
	private MyVector     adapterVector;
	/*
	 * Common
	 * --------------------------------------------------------------------------------------------    
	 */
	private String         user;
	private static String  baseImagePath  = "/mnt/sdcard/LeisureNote/image/";
	private static String  temImageName   = "";
	
	private LocalSQLiteHelper sqliteHelper;
	
	int userID = 0;
	/*
	 * Methods
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
       
        //取得当前用户
        initStringAtt();
        initSpinner();
        initComponents();      
        initViews();
        setButtonListeners();
        
        noteAudio    = null;
        noteImage    = null;
		recorder     = getRecorder(this);
    	handAdapter  = new ImageAdapter(this,40,40);
    	timingThread = new TimingThread();
    	timingThread.start();
    	
	
    }
    
    public void onConfigurationChanged(Configuration config) 
    { 

        super.onConfigurationChanged(config); 

    } 
    
    /*
     * @name:onStop()
     * @Param:null
     * @return:void
     * @Function:overwrite onStop()
     */     
    public void onStop()
    {
    	if( getViewVisibility(timingLayout) )//正在录音时点击了HOME
		 {
			 recorder.stopRecording();
			 recorder.deleteAudio(recorder.getAudioFile().getName());
			 if( timingThread.isAlive() )
			 {
				 timingThread.interrupt();
				 timingFlag = false;
				 flag       = false;
				 setViewVisibility(timingLayout);
			 }
			 this.onBackPressed();
			 super.onStop();
		 }
    	else
    	{
    		super.onStop();
    	}
    	
    }
    /*
     * @name:initStringAtt()
     * @Param:null
     * @return:void
     * @Function:get the username infomation to download the notebooks
     */    
    //获取当前intent
    //从中获取之前Activity传过来的参数
    private void initStringAtt()
    {
    	user      = getIntent().getStringExtra("username");
    	Log.i("string", "getString"+ user);
    }
	
    /*
     * @name:initComponents()
     * @Param:null
     * @return:void
     * @Function:init
     */  
    private void initComponents()
    {
    	//main
    	editTitle        = (EditText)findViewById(R.id.add_noteTitle);
    	editContent      = (EditText)findViewById(R.id.add_noteContent);
    	
    	buttonPic        = (Button)findViewById(R.id.button_pic);
    	buttonSound      = (Button)findViewById(R.id.button_sound);
    	buttonHand       = (Button)findViewById(R.id.button_handwriting);
    	buttonAdd        = (Button)findViewById(R.id.button_addNote);
    	
    	//recorder 
    	textRecordeTitle = (TextView)findViewById(R.id.textRecordTitle);
    	textRecordeTime  = (TextView)findViewById(R.id.textRecordTime);
    	textRecordeFormat= (TextView)findViewById(R.id.textRecordFormat);
    	
    	buttonPlay       = (Button)findViewById(R.id.buttonRecorderPlay);
    	buttonRecorder   = (Button)findViewById(R.id.buttonRecorder);
    	
    	timingLayout     = (FrameLayout)findViewById(R.id.back_recording);
    	textRecordeTiming= (TextView)findViewById(R.id.text_recordingTime);
    	deleteRecLayout  = (LinearLayout)findViewById(R.id.layout_RecordeDelete);
    	
    	//picture
    	picLayout        = (FrameLayout)findViewById(R.id.pictureFrame);
    	
    	textPicTitle     = (TextView)findViewById(R.id.textPicTitle);
    	textPicTime      = (TextView)findViewById(R.id.textPicTime);
    	textPicFormat    = (TextView)findViewById(R.id.textPicFormat);
    	
    	imagePicView     = (ImageView)findViewById(R.id.imageResource);
    	imagePreView     = (ImageView)findViewById(R.id.preViewImage);
    	
    	buttonPicCamera  = (Button)findViewById(R.id.buttonPicCamera);
    	buttonPicLocal   = (Button)findViewById(R.id.buttonPicLocal);
    	buttonPicDraw    = (Button)findViewById(R.id.buttonPicDraw);
    	
    	//hand write
    	handInfoLayout   = (FrameLayout)findViewById(R.id.writeFrame);
    	
    	buttonHandWrite  = (Button)findViewById(R.id.buttonHandWrite);
    	
    	handGrid         = (GridView)findViewById(R.id.handwriteGrid);
    	
    	
    }
    /*
     * @name:initSpinner()
     * @Param:null
     * @return:void
     * @Function:init
     */
    private void initSpinner()
    {

    	//需要添加进度条，提示等待   	
    	//1.从服务器数据库中取出笔记本名称
//    	JSONObject resultJSON      = getBooksFromServer(user);
    	JSONObject resultJSON      = doGetBooks(user);
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
    	spinnerBook = (Spinner)findViewById(R.id.spinner_chooseBook);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,resultStrArray);
    	spinnerBook.setAdapter(adapter);
    	spinnerBook.setOnItemSelectedListener
    	(
    			new Spinner.OnItemSelectedListener()
    			{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
					{
						// TODO Auto-generated method stub
						arg0.setVisibility(View.VISIBLE);						
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) 
					{
						// TODO Auto-generated method stub					
					}  				
    			}
    	);
    	
    }
    /*
     * @name:initViews()
     * @Param:null
     * @return:void
     * @Function:init
     */
    private void initViews()
    {
    	mainLayout      = (FrameLayout)this.findViewById(R.id.frameMainAdd);
    	recorderLayout  = (FrameLayout)this.findViewById(R.id.recorederFrame);
//    	recorderLayout = (LinearLayout)this.getLayoutInflater().inflate(R.layout.recorder, null).findViewById(R.id.recorderLayout);
//    	recorderFrame = new RecorderView(this);
//    	recorderLayout.setBackgroundResource(android.R.drawable.btn_default);
//    	recorderFrame.addView(recorderLayout);
//    	LayoutParams param = new LayoutParams(400, 250, Gravity.CENTER);
//    	Log.i("view", recorderLayout.getWidth()+"宽");
//    	recorderLayout.setFocusable(false);
//    	recorderLayout.setClickable(false);
//    	recorderLayout.bringToFront();
//    	recorderLayout.setVisibility(View.INVISIBLE);
//    	mainLayout.addView(recorderFrame,param);
    	
//    	Log.i("view", recorderLayout.isClickable()+" "+recorderLayout.isFocusable());
    }
    
    
    /*
     * @name:getRecorder()
     * @Param:Context
     * @return:RecorderHelper
     * @Function:recorderView will call this to get helper
     * @Modle:singleton
     */
    private RecorderHelper getRecorder(Context context)
    {
    	if( recorder == null)
    	{
    		return new RecorderHelper(context);
    	}
    	else return recorder;
    }
	
    /*
     * @name:getEdittext()
     * @Param:null
     * @return:void
     * @Function:get TEXT Title and content
     */
    private void getEdittext(){}
	
    
    /*
     * @name:setButtonListeners()
     * @Param:null
     * @return:void
     * @Function:set Button's Listeners
     */
    private void setButtonListeners()
    {
    	//弹出录音View
    	this.buttonSound.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if( getViewVisibility(recorderLayout) )
						{					
							resetButtonClickable(recorderLayout);
							setViewVisibility(recorderLayout);
						}
						else if( !getViewVisibility(recorderLayout) )
						{
							setRecorderLayoutTitles();
							resetButtonClickable(recorderLayout);
							resetViewsVisibility(recorderLayout);
						}						
					}   				
    			}
    	);
    	
    	//弹出图片VIEW
    	this.buttonPic.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if( getViewVisibility(picLayout))
						{
							resetButtonClickable(picLayout);
							setViewVisibility(picLayout);
						}
						else if (!getViewVisibility(picLayout))
						{
							resetButtonClickable(picLayout);
							resetViewsVisibility(picLayout);
						}						
					} 				
    			}
    	);
    	
    	//弹出手写VIEW
    	this.buttonHand.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub	
						if( getViewVisibility(handInfoLayout))
						{
							resetButtonClickable(handInfoLayout);
							setViewVisibility(handInfoLayout);
						}
						else if (!getViewVisibility(handInfoLayout))
						{
							resetButtonClickable(handInfoLayout);
							resetViewsVisibility(handInfoLayout);
						}	
					}
    				
    			}
    	);
    	//添加进数据库
    	this.buttonAdd.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						doAdd();
					}
    				
    			}
    	);
    	
    	
    	/*
    	 * recorder's buttons
    	 */
    	buttonRecorder.setOnClickListener
		(
				new Button.OnClickListener()
				{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Log.i("view", "true");

						if( recordeButtonState().equals(BUTTON_STATE_RECORDE))
						{
							resetButtonClickable(buttonRecorder);//禁用其他按钮，防止误操作
							setViewVisibility(timingLayout);
						
							recorder.startRecording();
							setRecordeButtonState(BUTTON_STATE_COMPLETE);
							timing = 0;
							timingFlag = true;
						
						}
						else if( recordeButtonState().equals(BUTTON_STATE_COMPLETE))
						{
							resetButtonClickable(buttonRecorder);//禁用其他按钮，防止误操作
							setViewVisibility(timingLayout);
							recorder.stopRecording();
							setRecorderLayoutTitles();
							setRecordeButtonState(BUTTON_STATE_RECORDE);
							timingFlag = false;
//							timingThread = null;
						}
						
					}
					
				}
		); 
    	
    	/*
    	 * recorder's buttonPlay
    	 */  	
    	buttonPlay.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						File audioTem = recorder.getAudioFile();
						if( audioTem != null )
						{
							playAudio(audioTem);						
						}
						else if ( audioTem == null )
						{
							Toast.makeText(AddActivity.this, "暂无录音笔记", Toast.LENGTH_SHORT);
						}							
					}   				
    			}
    	);
    	   	
    	//设置删除录音笔记的监听器
    	deleteRecLayout.setOnLongClickListener
    	(
    			new View.OnLongClickListener()
    			{

					@Override
					public boolean onLongClick(View arg0) {
						// TODO Auto-generated method stub
						File temFile = recorder.getAudioFile();
						if( temFile!=null )
						{
				            new AlertDialog.Builder(AddActivity.this) 
				            .setTitle("提示") 
				            .setMessage("确认移除该录音笔记？") 
				            .setPositiveButton
				            (
				            	"确定", new DialogInterface.OnClickListener()
					            { 
									            @Override 
									public void onClick(DialogInterface arg0, int arg1) 
									{ 
									// TODO Auto-generated method stub 
						                    recorder.deleteAudio(recorder.getAudioFile().getName());
						                    setRecorderLayoutTitles();
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

						return false;
					}
    				
    			}
    	);
    	//end
    	
    	/*
    	 * Pic View buttons
    	 * --------------------------------------------------------------------------------------------    
    	 * --------------------------------------------------------------------------------------------    
    	 */
    	this.buttonPicCamera.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if ( imagePicView.getDrawable()!=null )
						{
						 
			                	 new AlertDialog.Builder(AddActivity.this) 
						            .setTitle("提示") 
						            .setMessage("确认替换该图片笔记？") 
						            .setPositiveButton
						            (
						            	"确定", new DialogInterface.OnClickListener()
							            { 
											            @Override 
											public void onClick(DialogInterface arg0, int arg1) 
											{ 
											// TODO Auto-generated method stub 
											        File imageFile = new File(baseImagePath+temImageName);
								                    if( imageFile.exists() )
								                    {
								                    	imageFile.delete();
								                    	imageFile = null;
								                    	
								                    }
								                
											        imagePicView.setImageDrawable(null);
								                    setImageLayoutTitles(null);
								                    doCameraCapture();
								                    
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
							doCameraCapture();
						}
						
							
		            
						
					}
    				
    			}
    	);
    	
    	this.buttonPicLocal.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if( imagePicView.getDrawable()!=null )
						{
							 new AlertDialog.Builder(AddActivity.this) 
					            .setTitle("提示") 
					            .setMessage("确认重选以替换该图片笔记？") 
					            .setPositiveButton
					            (
					            	"确定", new DialogInterface.OnClickListener()
						            { 
										            @Override 
										public void onClick(DialogInterface arg0, int arg1) 
										{ 
										// TODO Auto-generated method stub 
										        File imageFile = new File(baseImagePath+temImageName);
							                    if( imageFile.exists() )
							                    {
							                    	imageFile.delete();
							                    	imageFile = null;
							                    	
							                    }
							                   
										        imagePicView.setImageDrawable(null);
							                    setImageLayoutTitles(null);
							                    doPickLocalImage();
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
							doPickLocalImage();
							
						}
						
					}
    				
    			}
    	);
    	
    	this.buttonPicDraw.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if( imagePicView.getDrawable()!=null )
						{
							 new AlertDialog.Builder(AddActivity.this) 
					            .setTitle("提示") 
					            .setMessage("确认重绘以替换该图片笔记？") 
					            .setPositiveButton
					            (
					            	"确定", new DialogInterface.OnClickListener()
						            { 
										            @Override 
										public void onClick(DialogInterface arg0, int arg1) 
										{ 
										// TODO Auto-generated method stub 
										        File imageFile = new File(baseImagePath+temImageName);
							                    if( imageFile.exists() )
							                    {
							                    	imageFile.delete();
							                    	imageFile = null;
							                    	
							                    }
							                   
										        imagePicView.setImageDrawable(null);
							                    setImageLayoutTitles(null);
							                    doDraw();
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
							doDraw();
							
						}
						
					}
    				
    			}
    	);
    	this.imagePicView.setOnClickListener
    	(
    	
    			new View.OnClickListener() 
    			{
					
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						
						if( imagePicView.getDrawable()!=null )
						{
							if(!getViewVisibility(imagePreView))
							{								
								imagePreView.setImageDrawable(imagePicView.getDrawable());
								doScaleBigAnimation(imagePreView);
								setViewVisibility(imagePreView);
							}
						}

					}
				}
    	);
    	
    	this.imagePicView.setOnLongClickListener
    	(
    	
    			new View.OnLongClickListener() 
    			{
					@Override
					public boolean onLongClick(View arg0) 
					{
						// TODO Auto-generated method stub
						if( imagePicView.getDrawable()!=null )
						{
							 new AlertDialog.Builder(AddActivity.this) 
					            .setTitle("提示") 
					            .setMessage("确认移除该图片笔记？") 
					            .setPositiveButton
					            (
					            	"确定", new DialogInterface.OnClickListener()
						            { 
										            @Override 
										public void onClick(DialogInterface arg0, int arg1) 
										{ 
										// TODO Auto-generated method stub 
										        File imageFile = new File(baseImagePath+temImageName);
							                    if( imageFile.exists() )
							                    {
							                    	imageFile.delete();
							                    	imageFile = null;
							                    }
										        imagePicView.setImageDrawable(null);
							                    setImageLayoutTitles(null);
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
						return false;
					}
				}
    	);
    	
    	this.imagePreView.setOnClickListener
    	(
    	
    			new View.OnClickListener() 
    			{
					
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						
					
						if( getViewVisibility(imagePreView) )
							{

								doScaleSmallAnimation(imagePreView);
								setViewVisibility(imagePreView);
								
							}
						
					}
				}
    	);
    	
 
    	this.buttonHandWrite.setOnClickListener
    	(
    	
    			new View.OnClickListener() 
    			{
					
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub						
						doHandWrite();
					}
				}
    	);
    	
    	
    }
    
    /*
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
    
    
    /*
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
    
    /*
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
    		if(HttpHelper.isConnect(AddActivity.this))
    		{
    			jsonObject = getBooksFromServer(user);
    		}
    		else if(!HttpHelper.isConnect(AddActivity.this))
    		{
    			jsonObject = null;
    		}
    	}
    	return jsonObject;
    }
    
    /*
     * @name:onKeyDown()
     * @Param:String username
     * @return:JSONObject
     * @Function:overwrite onKeyDown
     */
    //重写返回键方法 
    public boolean onKeyDown(int keyCode,KeyEvent event) 
    {

    	 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
    	 {
    	//这里重写返回键
    		 dismissView(keyCode,event);
    		 return false;
    	 }
    	 return false;

    }
    /*
     * @name:getViewVisibility()
     * @Param:View
     * @return:boolean
     * @Function:get view's visibility
     */
    private boolean getViewVisibility(View view)
    {
    	if( view.getVisibility()==View.VISIBLE )
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    /*
     * @name:setViewVisibility()
     * @Param:View
     * @return:void
     * @Function:set view's visibility
     */
    //反转View的可见性
    private void setViewVisibility(View view)
    {
    	if ( getViewVisibility(view) )
    	{
    		view.setVisibility(View.INVISIBLE);
    	}
    	else if ( !getViewVisibility(view) )
    	{
    		view.setVisibility(View.VISIBLE);
    	}
    }
	
    /*
     * @name:recordeButtonState()
     * @Param:null
     * @return:String
     * @Function:return current recorderButton's title
     */
    private String recordeButtonState()
    {
    	return buttonRecorder.getText().toString();
    }
    /*
     * @name:setRecordeButtonState()
     * @Param:String
     * @return:void
     * @Function:set current recorderButton's title
     */
    private void setRecordeButtonState(String state)
    {
    	buttonRecorder.setText(state);
    }
    /*
     * @name:setRecorderLayoutTitles()
     * @Param:void
     * @return:void
     * @Function:set current recorderLayout's title to show the audio's infomation
     */
    private void setRecorderLayoutTitles()
    {
    	File audioTem = recorder.getAudioFile();
    	String defaultStr = "暂无录音";
    	if( audioTem == null )
    	{
    		textRecordeTitle.setText(defaultStr);
            textRecordeTime.setText(defaultStr);
            textRecordeFormat.setText(defaultStr);
    		return;
    	}
    	else
    	{
    		String fileName   = audioTem.getName();
//    		String createTime = recorder.getCreatTime();
    		String fileFormat = "amr";
    		
    		textRecordeTitle.setText(fileName);
//            textRecordeTime.setText(createTime);
            textRecordeFormat.setText(fileFormat);
    	}
    }
    
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                        case REFRESH:
                        	Log.i("view", timing+"");
                        	int tem     = 0;
                        	if( timing == 60 )
                        	{
                        		tem += 1;
                        		timing = 0;
                        	}
                        	String text = String.format("%02d:%02d",tem,timing);
                        	textRecordeTiming.setText(text);
                            break;
                        default:
                            super.handleMessage(msg);
            }
        }
    };
    
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
    //inner thread class
    private class TimingThread extends Thread
    {

    	public void run()
		{	
			
    		while(flag)
    		{
    			while(timingFlag)
    			{
    				
    				try 
    				{
    					timing += 1;					
    					Message msg = new Message();
    					msg.what = REFRESH;
    					handler.sendMessage(msg);
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
			
		
		}
	}
    /*
     * @name:playAudio()
     * @Param:File
     * @return:void
     * @Function:play the audio
     */
    private void playAudio(File file)
    {
    	Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "audio");
		startActivity(intent);	
    }
    
    /*
     * @name:dismissView()
     * @Param:int keyCode,KeyEvent event
     * @return:void
     * @Function:dismiss views according to the key event
     */
    private void dismissView(int keyCode,KeyEvent event)
    {
		 if( getViewVisibility(recorderLayout) && !getViewVisibility(timingLayout) )
		 {
			 resetButtonClickable(recorderLayout);
			 recorderLayout.setVisibility(View.INVISIBLE);
		 }
		 else if ( getViewVisibility(recorderLayout) && getViewVisibility(timingLayout) )
		 {
			 //do nothing
		 }
		 else if( getViewVisibility(picLayout) && !getViewVisibility(imagePreView))
		 {
			 resetButtonClickable(picLayout);
			 picLayout.setVisibility(View.INVISIBLE);
		 }
		 else if ( getViewVisibility(handInfoLayout) )
		 {
			 resetButtonClickable(handInfoLayout);
			 handInfoLayout.setVisibility(View.INVISIBLE);
		 }
		 else if( getViewVisibility(timingLayout) )//正在录音时点击了返回
		 {
			 recorder.stopRecording();
			 recorder.deleteAudio(recorder.getAudioFile().getName());
			 setViewVisibility(timingLayout);
		 }
		 else if ( getViewVisibility(imagePreView) )//正在预览图片时点击了返回
		 {
			 doScaleSmallAnimation(imagePreView);
			 setViewVisibility(imagePreView);
		 }
		 else
		 {
			 super.onKeyDown(keyCode, event);
			 this.finish();
		 }
    }
    
    /*
     * @name:resetViewsVisibility()
     * @Param:View
     * @return:void
     * @Function:dismiss views according to the view
     */
    private void resetViewsVisibility(View view)
    {
    	if( view == recorderLayout )
    	{
    		recorderLayout.setVisibility(View.VISIBLE);
    		picLayout.setVisibility(View.INVISIBLE);
    		handInfoLayout.setVisibility(View.INVISIBLE);
    	}
    	else if( view == picLayout )
    	{
    		picLayout.setVisibility(View.VISIBLE);
    		recorderLayout.setVisibility(View.INVISIBLE);
    		handInfoLayout.setVisibility(View.INVISIBLE);
    	}
    	else if ( view == handInfoLayout )
    	{
    		handInfoLayout.setVisibility(View.VISIBLE);
    		picLayout.setVisibility(View.INVISIBLE);
    		recorderLayout.setVisibility(View.INVISIBLE);
    		
    	}
    }
    /*
     * @name:resetButtonClickable()
     * @Param:View
     * @return:void
     * @Function:set button is Clickable
     */
    private void resetButtonClickable(View view)
    {
    	if( view == recorderLayout && !getViewVisibility(recorderLayout))
    	{
    		this.buttonSound.setClickable(true);
    		this.buttonAdd.setClickable(false);
    		this.buttonHand.setClickable(false);
    		this.buttonPic.setClickable(false);
    		
    		this.editContent.setEnabled(false);
    		return;
    		
    	}
    	else if( view == recorderLayout && getViewVisibility(recorderLayout))
    	{
    		this.buttonSound.setClickable(true);
    		this.buttonAdd.setClickable(true);
    		this.buttonHand.setClickable(true);
    		this.buttonPic.setClickable(true);
    		
    		this.editContent.setEnabled(true);
    		return;
    	}
    	else if( view == picLayout && !getViewVisibility(picLayout))
    	{
    		this.buttonPic.setClickable(true);
    		this.buttonSound.setClickable(false);
    		this.buttonAdd.setClickable(false);
    		this.buttonHand.setClickable(false);
    		
    		this.editContent.setEnabled(false);
    	
    		return;
    	}
    	else if( view == picLayout && getViewVisibility(picLayout))
    	{
    		this.buttonSound.setClickable(true);
    		this.buttonAdd.setClickable(true);
    		this.buttonHand.setClickable(true);
    		this.buttonPic.setClickable(true);
    		
    		this.editContent.setEnabled(true);
    	
    		return;
    	}
    	else if( view == buttonRecorder && buttonSound.isClickable() )
    	{
    		this.buttonSound.setClickable(false);
    	}
    	else if( view == buttonRecorder && !buttonSound.isClickable() )
    	{
    		this.buttonSound.setClickable(true);
    	}
    	
    	else if( view == handInfoLayout && !getViewVisibility(handInfoLayout))
    	{
    		this.buttonHand.setClickable(true);
    		this.buttonPic.setClickable(false);
    		this.buttonSound.setClickable(false);
    		this.buttonAdd.setClickable(false);
    	   		
    		this.editContent.setEnabled(false);
    	}
     	else if( view == handInfoLayout && getViewVisibility(handInfoLayout))
    	{
    		this.buttonSound.setClickable(true);
    		this.buttonAdd.setClickable(true);
    		this.buttonHand.setClickable(true);
    		this.buttonPic.setClickable(true);
    		
    		this.editContent.setEnabled(true);
    	
    		return;
    	}
    }
    
    /*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
    private final int IMAGE_PICK_LOCAL     = 0;
    private final int IMAGE_CAPTURE_CAMERA = 1;
    private final int IMAGE_DRAW           = 2;
    private final int HANDWRITING          = 3;
    
    
    /*
     * @name:doPickLocalImage()
     * @Param:null
     * @return:void
     * @Function:do Pick Local image in gallery
     */
    private void doPickLocalImage()
    {
    	Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(i, IMAGE_PICK_LOCAL);  
    }   
    /*
     * @name:doCameraCapture()
     * @Param:null
     * @return:void
     * @Function:do camera shooting
     */
    private void doCameraCapture()
    {
    	String temPredix = "pic_";
		File picPath = new File(baseImagePath);
	    if(!picPath.exists())
	    {  
	    	picPath.mkdir();//没有目录先创建目录  
	    	
        }
	    
	    SimpleDateFormat formater   = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");    
		Date curDate      = new Date(System.currentTimeMillis());//获取当前时间  
		String createTime = formater.format(curDate).toString().trim(); 
	    temImageName = "";
		temImageName = temPredix + createTime + ".jpg";
	    Log.i("view",temImageName);
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	
//    	File file = new File(picPath,temImageName);
    	File file;
		file = new File(picPath,temImageName);
//		if( !file.exists() )
//		{
//			try 
//			{
//				file = File.createTempFile(temImageName, null);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		Uri uri     = Uri.fromFile(file);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, IMAGE_CAPTURE_CAMERA);

    }
    
    /*
     * @name:doDraw()
     * @Param:null
     * @return:void
     * @Function:do Draw a picture
     */
    private void doDraw()
    {
		Intent intent = new Intent(AddActivity.this,DrawBoardActivity.class);
		this.startActivityForResult(intent, IMAGE_DRAW);
    }
    
    /*
     * @name:doHandWrite()
     * @Param:null
     * @return:void
     * @Function:do Draw a picture
     */
    private void doHandWrite()
    {
		Intent intent = new Intent(AddActivity.this,HandWritingActivity.class);
		this.startActivityForResult(intent, HANDWRITING);
		handGrid.setAdapter(null);
    }
 
    /*
     * @name:onActivityResult()
     * @Param:int requestCode, int resultCode, Intent imageReturnedIntent
     * @return:void
     * @Function:do picture file saving when the activity is returned
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) 
    { 
        
        if (resultCode != RESULT_OK) 
        {        //此处的 RESULT_OK 是系统自定义得一个常量
            Log.e("view","ActivityResult resultCode error");
            return;
        }
        
        switch(requestCode)
        {
	        case IMAGE_PICK_LOCAL:
	            if(resultCode == RESULT_OK)
	            {  
	                Uri selectedImage = imageReturnedIntent.getData();
	                String[] filePathColumn = {MediaStore.Images.Media.DATA};
	
	                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	                cursor.moveToFirst();
	
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                
	                for(int i = 0; i < cursor.getColumnCount();i++)
	                {
	                	Log.i("view",cursor.getColumnName(i));
	                }
	                String filePath = cursor.getString(columnIndex);
	                cursor.close();
	                
	                File image= new File(filePath);
	        		SimpleDateFormat formater   = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");    
	        		Date curDate   = new Date(image.lastModified());//获取当前时间  
	        		String createTime = formater.format(curDate).toString().trim(); 
	
	                Bitmap bm = BitmapFactory.decodeFile(filePath);
	                if (bm.getWidth()<bm.getHeight())
	                {
		                bm = Bitmap.createScaledBitmap(bm, 540, 960, false);
	                }
	                else if(bm.getWidth()>bm.getHeight())
	                {
	                	bm = Bitmap.createScaledBitmap(bm, 960, 540, false);
	                }
	                
	                String temPredix = "pic_";
	        		File picPath = new File(baseImagePath);
	        	    if(!picPath.exists())
	        	    {  
	        	    	picPath.mkdir();//没有目录先创建目录  
	        	    	
	                }
	        	    temImageName = "";
	        		temImageName = temPredix + createTime + ".jpg";
	                FileOutputStream fos = null;
					try 
					{
						fos = new FileOutputStream(baseImagePath+temImageName);
						bm.compress(CompressFormat.JPEG, 100, fos);
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
	               
					File imagefile = new File(baseImagePath+temImageName);
					this.noteImage = imagefile;
	                this.imagePicView.setImageBitmap(bm);
	                this.setImageLayoutTitles(noteImage);
	                this.resetViewsVisibility(picLayout);
	            }
	            break;
	        case IMAGE_CAPTURE_CAMERA:
	        	 if(resultCode == RESULT_OK)
	        	 {
		                Bitmap bm = BitmapFactory.decodeFile(baseImagePath+temImageName);
		                if (bm.getWidth()<bm.getHeight())
		                {
			                bm = Bitmap.createScaledBitmap(bm, 540, 960, false);
		                }
		                else if(bm.getWidth()>bm.getHeight())
		                {
		                	bm = Bitmap.createScaledBitmap(bm, 960, 540, false);
		                }
		                //压缩图片
		                FileOutputStream fos = null;
						try 
						{
							fos = new FileOutputStream(baseImagePath+temImageName);
							bm.compress(CompressFormat.JPEG, 100, fos);
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
						
						File imagefile = new File(baseImagePath+temImageName);
						this.noteImage = imagefile;
		               
		                this.imagePicView.setImageBitmap(bm);
		                this.setImageLayoutTitles(noteImage);
		                this.resetViewsVisibility(picLayout);
	        	 }
	        	break;
	        	
	        case IMAGE_DRAW:
	        	String filepath = ""; 
	        	filepath = imageReturnedIntent.getStringExtra("filename");
	        	temImageName = filepath;
	        	Log.i("view", filepath);
	        	Bitmap bm = BitmapFactory.decodeFile(baseImagePath+filepath);
	        	temImageName = filepath;
	        	this.imagePicView.setImageBitmap(bm);
	        	
	        	File imagefile = new File(baseImagePath+temImageName);
				this.noteImage = imagefile;
	            this.setImageLayoutTitles(noteImage);
	        	break;
	        case HANDWRITING:
	 
	        	adapterVector = (MyVector)imageReturnedIntent.getSerializableExtra("vector");
	        	
	        	Log.e("view",""+adapterVector.size());
	        	handAdapter.setVector(adapterVector.getVector());
	        	handGrid.setAdapter(handAdapter);
	        	break;
        }
    }
    
    /*
     * @name:setImageLayoutTitles()
     * @Param:File
     * @return:void
     * @Function:set ImageLayout titles to show the picture's infomation
     */
    private void setImageLayoutTitles(File file)
    {
  
    	String defaultStr = "暂无图片";
    	if( file == null )
    	{
    		textPicTitle.setText(defaultStr);
            textPicTime.setText(defaultStr);
            textPicFormat.setText(defaultStr);
    		return;
    	}
    	else
    	{
    		SimpleDateFormat formater   = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");    
    		Date curDate   = new Date(file.lastModified());//获取当前时间  
    		
    		String fileName   = file.getName();
    		String createTime = formater.format(curDate).toString().trim();  
    		String fileFormat = "png";
    		
    		textPicTitle.setText(fileName);
            textPicTime.setText(createTime);
            textPicFormat.setText(fileFormat);
    	}
    }
    /*
     * @name:doScaleSmallAnimation()
     * @Param:View
     * @return:void
     * @Function:do animation
     */
    private void doScaleSmallAnimation(View view)
    {
    	ScaleAnimation myAnimation_Scale;
		myAnimation_Scale =new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
	             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		myAnimation_Scale.setDuration(300);
		view.setAnimation(myAnimation_Scale);
		view.startAnimation(myAnimation_Scale);
    }
    /*
     * @name:doScaleBigAnimation()
     * @Param:View
     * @return:void
     * @Function:do animation
     */
    private void doScaleBigAnimation(View view)
    {
    	ScaleAnimation myAnimation_Scale;
		myAnimation_Scale =new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
	             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		myAnimation_Scale.setDuration(300);
		view.setAnimation(myAnimation_Scale);
		view.startAnimation(myAnimation_Scale);
    }
    
    
    
    
    private JSONObject doGetData()
    {
    	this.noteTitle      = editTitle.getText().toString();
    	this.noteContent    = editContent.getText().toString();
    	this.noteAudio      = this.recorder.getAudioFile();
    	
    	if(!temImageName.equals(""))
    	{
    		this.noteImage      = new File(baseImagePath+temImageName);
    	}
    		
    	this.noteHandVector = this.adapterVector;
    	
    	
    	
    	JSONObject jsonNote = new JSONObject();

    	try 
    	{	
    		jsonNote.put("userid", userID);
    		jsonNote.put("bookname",this.spinnerBook.getAdapter().getItem(spinnerBook.getSelectedItemPosition()).toString());
    		jsonNote.put("noteTitle", noteTitle);       
    		jsonNote.put("noteContent", noteContent);
    		
    		if(noteAudio!=null && noteAudio.exists())
        	{

    			{
    				String b = Base64.encodeToString(TypeTransformer.FileToBytes(noteAudio), Base64.DEFAULT);
    				jsonNote.put("noteAudio", b);
    				jsonNote.put("noteAudioName", noteAudio.getName());
    			}	      		
        	}
    		if(noteAudio==null || !noteAudio.exists())
        	{
    			jsonNote.put("noteAudio", "");
				jsonNote.put("noteAudioName", "");
        	}
    		
    		if(noteImage!=null && noteImage.exists())
    		{
    	    	{
    			    String b = Base64.encodeToString(TypeTransformer.FileToBytes(noteImage), Base64.DEFAULT);
    				jsonNote.put("noteImage", b);
    	    		jsonNote.put("noteImageName", noteImage.getName());
    	    	}		
    		}
    		if(noteImage==null || !noteImage.exists())
        	{	
    			jsonNote.put("noteImage", "");
				jsonNote.put("noteImageName", "");
        	}
    		
    		if(noteHandVector!=null && !noteHandVector.isEmpty())
    		{
    	    	{
    	    		String b = Base64.encodeToString(TypeTransformer.ObjectToBytes(noteHandVector), Base64.DEFAULT);
    	    		jsonNote.put("noteHandVector", b);
    	    	}
    		}
    		
      		if(noteHandVector==null || noteHandVector.isEmpty())
        	{
    			jsonNote.put("noteHandVector", "");
        	}
    		
    		jsonNote.put("createtime", System.currentTimeMillis());
    		jsonNote.put("updatetime", System.currentTimeMillis());
    		
    		Log.i("view",jsonNote.toString());
    	}
    	catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return jsonNote;  	
    }
    
    /*
     * @name:doInsertNoteIntoLocal()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private void doInsertNoteIntoLocal(JSONObject note)
    {
    	int bookId = 0;
    	sqliteHelper.doOpen();
    	Cursor cursor = null;
    	{
    		try
    		{
		    	String table     = FinalDefinition.DATABASE_TABLE_BOOK;
		    	String[] columns = {"idnotebook"};
		    	String selection = "bookname=?";
		    	String[] selectionArgs = {note.getString("bookname")};
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
    		String title = ((note.getString("noteTitle").equals(""))?"无标题笔记":note.getString("noteTitle"));
    		String content = ((note.getString("noteContent").equals(""))?null:note.getString("noteContent"));
    		
    		cv.put(FinalDefinition.DATABASE_NOTE_IDU, note.getString("userid"));
    		cv.put(FinalDefinition.DATABASE_NOTE_IDB, bookId);
    		cv.put(FinalDefinition.DATABASE_NOTE_TITLE, title);
    		cv.put(FinalDefinition.DATABASE_NOTE_CONTENT, content);
    		if(!note.getString("noteImage").equals(""))
			{
				String image = note.getString("noteImage");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_IMAGE, b);
				cv.put(FinalDefinition.DATABASE_NOTE_IMNAME, note.getString("noteImageName"));
			}
    		
     		if(!note.getString("noteAudio").equals(""))
			{
				String image = note.getString("noteAudio");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_SOUND, b);
				cv.put(FinalDefinition.DATABASE_NOTE_SONAME, note.getString("noteAudioName"));
			}
     		if(!note.getString("noteHandVector").equals(""))
			{
				String image = note.getString("noteHandVector");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
//				ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(FinalDefinition.DATABASE_NOTE_HAND, b);
			}
     		cv.put(FinalDefinition.DATABASE_NOTE_CTIME, note.getLong("createtime"));
     		cv.put(FinalDefinition.DATABASE_NOTE_UTIME, note.getLong("updatetime"));
     		
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
    
    
    /*
     * @name:doInsertNoteIntoServer()
     * @Param:String username
     * @return:JSONObject
     * @Function:connect to server to get a JSONObject
     */
    private boolean doInsertNoteIntoServer(JSONObject note)
    {
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
    
    /*
     * @name:doAdd()
     * @Param:null
     * @return:void
     * @Function:add the note
     */
    private void doAdd()
    {
    	JSONObject note = this.doGetData();
    	if(HttpHelper.isConnect(this))
    	{
    		this.doInsertNoteIntoLocal(note);
    		this.doInsertNoteIntoServer(note);
    	}
    	else if(!HttpHelper.isConnect(this))
    	{
    		this.doInsertNoteIntoLocal(note);
    		this.doInsertSyncData(Sync.ACTION_INSERT,Sync.TABLE_NOTE,note);
    	}
    }
    
    /*
     * @name:doInsertSyncData()
     * @Param:int(ACTION),int(TABLE)
     * @return:void
     * @Function:add the note to sync
     */
    private void doInsertSyncData(int action,int table,JSONObject note)
    {
    	SyncHelper sync = new SyncHelper(this);
    	sync.doOpen();
    	try
    	{
	    	ContentValues cv = new ContentValues();
	    	cv.put(Sync.ACTION, action);
	    	cv.put(Sync.TABLE, table);
	    	cv.put(Sync.LOGTIME, System.currentTimeMillis());
	    	
	    	String title = ((note.getString("noteTitle").equals(""))?"无标题笔记":note.getString("noteTitle"));
			String content = ((note.getString("noteContent").equals(""))?null:note.getString("noteContent"));
			
			cv.put(Sync.USER_ID, note.getString("userid"));
			cv.put(Sync.BOOKNAME, note.getString("bookname"));
			cv.put(Sync.TITLE, title);
			cv.put(Sync.TEXT, content);
			if(!note.getString("noteImage").equals(""))
			{
				String image = note.getString("noteImage");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.IMAGE, b);
				cv.put(Sync.IMAGE_NAME, note.getString("noteImageName"));
			}
			
	 		if(!note.getString("noteAudio").equals(""))
			{
				String image = note.getString("noteAudio");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.AUDIO, b);
				cv.put(Sync.AUDIO_NAME, note.getString("noteAudioName"));
			}
	 		if(!note.getString("noteHandVector").equals(""))
			{
				String image = note.getString("noteHandVector");
				byte[] b = Base64.decode(image, Base64.DEFAULT);
	//			ByteArrayInputStream bis = new ByteArrayInputStream(b);
				cv.put(Sync.HAND, b);
			}
	 		cv.put(Sync.CTIME, note.getLong("createtime"));
	 		cv.put(Sync.UTIME, note.getLong("updatetime"));
	 		
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
    
    
}
