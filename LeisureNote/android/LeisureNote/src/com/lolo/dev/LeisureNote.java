package com.lolo.dev;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.helper.HttpHelper;
import com.lolo.helper.InputDetectionHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.tools.DensityUtil;
import com.lolo.tools.FinalDefinition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Author:Lolo
 * Date:2012/3/13
 * File Description:to implement the login function
 * Included Component:
 * 1.2 textView for username and password
 * 2.2 editText for username and password
 * 3.2 invisible hints for error input of username and password
 * 4.2 buttons for Login and regester
 * 
 * 
 */

public class LeisureNote extends Activity
{
    /** Called when the activity is first created. */

    /*
     * Modify Note by Lolo @2012/3/13 pm 23:57
     * what we need next:
     * 1.EditText. we will use the string they hold
     * 2.textView. we will use them for hints.Controlling the invisibility.
     * 3.Button.   we will add the clickLisiner for the action event.
     */
	
	
    private EditText editUsername,
    				 editPassword;
    private Button   buttonLogin,
    				 buttonRegister;
    private TextView hintUsername,
    				 hintPassword;
    
    private CheckBox boxRememberMe;
    //save the information of internet
    private String   status = null;
    
    private ProgressDialog progressLog;
    private Thread         myThread;
    private LocalSQLiteHelper sqliteHelper;

    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
      
        getComponents();
        
     	/*
    	 * Next:Set the clickListeners;
    	 */
    	setProgressDialog();
    	setButtonListeners();   
        doAutoFill();
    }
    
    
    /*
     * Method name:getComponents
     * Method function:initialize the widgets we defined; 
     */
    private void getComponents()
    {
    	
    	editUsername      = (EditText)findViewById(R.id.userEdit);
    	editPassword      = (EditText)findViewById(R.id.passwordEdit);
    	
    	hintUsername      = (TextView)findViewById(R.id.userInputError);
    	hintPassword      = (TextView)findViewById(R.id.passwordInputError);
    	
    	buttonLogin       = (Button)findViewById(R.id.buttonLogin);
    	buttonRegister    = (Button)findViewById(R.id.buttonRegiste);
    	
    	boxRememberMe     = (CheckBox)findViewById(R.id.checkboxRememberME);
    	
   	
    }
    
    private void setProgressDialog()
    {
    	progressLog = new ProgressDialog(this);
    	progressLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressLog.setMessage("登录ing...");
    	progressLog.setIndeterminate(false);
    	
    }

    /*
     * Method name:isInputValidate
     * Method function:check the input of two editText
     * Return value:boolean 
     */
    private boolean isInputValidate()
    {  	
    	hintUsername.setVisibility(View.GONE);
    	hintPassword.setVisibility(View.GONE);
    	/*
    	 * Version 1.
    	 * not use the helper class
    	 */
//    	
//    	/*
//    	 * 1.get the 2 string of editText
//    	 */
//    	String username = this.editUsername.getText().toString();
//    	String password = this.editPassword.getText().toString();  	
//    	/*
//    	 * 2.check the input type.
//    	 */
//    	if(username.equals(""))
//    	{
//    		hintUsername.setText(R.string.hint_usernameEmpty);
//    		hintUsername.setVisibility(View.VISIBLE);
//    		return false;
//    	}
//    	if(password.equals(""))
//    	{
//    		hintPassword.setText(R.string.hint_passwordEmpty);
//    		hintPassword.setVisibility(View.VISIBLE);
//    		return false;
//    	}
//    	if(!isUsernameEmail(username))
//    	{
//    		hintUsername.setText(R.string.hint_usernameError);
//    		hintUsername.setVisibility(View.VISIBLE);
//    		return false;
//    	}
//    	
//    	//else:
//    	hintUsername.setVisibility(View.GONE);
//    	hintPassword.setVisibility(View.GONE);
//    	return true;
    	
    	
    	/*
    	 * Version 2.
    	 * Modifed:2012/3/17
    	 * use the helper class
    	 */
    	
    	if ( InputDetectionHelper.isEmpty(editUsername) )
    	{
    		hintUsername.setText(R.string.hint_usernameEmpty);
    		hintUsername.setVisibility(View.VISIBLE);
    		return false;
    	}
    	if ( !InputDetectionHelper.isUsernameEmail(editUsername) )
    	{
    		hintUsername.setText(R.string.hint_usernameError);
    		hintUsername.setVisibility(View.VISIBLE);
    		return false;
    	}
    	if ( InputDetectionHelper.isEmpty(editPassword) )
    	{
    		hintPassword.setText(R.string.hint_passwordEmpty);
    		hintPassword.setVisibility(View.VISIBLE);
    		return false;
    	}
    	
    	hintUsername.setVisibility(View.GONE);
    	hintPassword.setVisibility(View.GONE);
    	return true;

    	
    }
    
    /*
     * Method name:isUsernameEmal
     * Method function:check the input of userName
     * Return value:boolean 
     */
    private boolean isUsernameEmail(String username)
    {
    	
    	 Pattern pattern = Pattern
		 .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		 Matcher mc = pattern.matcher(username);
		 return mc.matches();
    }
    
    /*
     * Method name:setButtonListeners
     * Method function:initialize the two buttons' ClickListener; 
     */
    private void setButtonListeners()
    {  		
    	buttonLogin.setOnClickListener(new OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				//判断是否有网络连接
				if( isInputValidate() )
				{	
					//url
					//JSON		
			    	progressLog.show();
					if( doLogin() )
					{
						
						Toast.makeText(LeisureNote.this,"登录成功",
								Toast.LENGTH_LONG).show();
						//Modify needed
						Intent intent = new Intent(LeisureNote.this,MainActivity.class);
						intent.putExtra("username", LeisureNote.this.editUsername.getText().toString());
						startActivity(intent);
						LeisureNote.this.finish();
					}
					else
					{
						//Modify needed
						
						Toast.makeText(LeisureNote.this,status,
								Toast.LENGTH_LONG).show();
					}			
					Message msg = new Message();
			    	msg.what = FinalDefinition.MESSAGE_DISMISS;
			    	handler.sendMessage(msg);
				}
				
			}
		}
    	);
    	
    	buttonRegister.setOnClickListener(new OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				LeisureNote.this.editUsername.setText("");
				LeisureNote.this.editPassword.setText("");
				//goto Register Activity
				Intent intent  = new Intent(LeisureNote.this,RegisterActivity.class);
				startActivity(intent);
			}}
    	);
    	
    }
    
    /*
     * Method name:queryToServer
     * Method function:query infomation to server
     * Return value:String 
     */
    private String queryToServer(String username,String password)
    {
    	
    	String queryString = "username="+username+"&password="+password;
    	String url         = HttpHelper.Base_Url+"servlet/LoginServlet?"+queryString;
    	return HttpHelper.queryForPost(url);
    	
    }
    
    /*
     * Method name:LoginLocal
     * Method function:Login
     * Return value:Boolean
     */
    
    private boolean LoginLocal()
    {
    
    	String username = this.editUsername.getText().toString();
    	String password = this.editPassword.getText().toString();
    	
    	sqliteHelper = new LocalSQLiteHelper(this);
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
    		Log.i("Login","本地记录");
    		cursor.close();
    	 	sqliteHelper.doClose();
    		return true; 
    	}
    	else 
    	{
    		cursor.close();
    	 	sqliteHelper.doClose();
    		Log.i("Login","本地无记录");
    		return false;
    	}
    	
    }
    
    
    
    
    /*
     * Method name:queryToServer
     * Method function:query infomation to server
     * Return value:String 
     */
    private JSONObject queryToServerByJSON(String username,String password)
    {
    	
    	String url           = HttpHelper.Base_Url+"servlet/LoginServlet";   	
    	JSONObject parameter = new JSONObject();
    	JSONObject result    = null;
    	
    	try 
    	{
			parameter.put("username", username);
			parameter.put("password", password);
			
		} 
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{			
			HttpEntity entity = new StringEntity(parameter.toString());
	    	return HttpHelper.getResponseByHttpPostWithJSON(url, entity);		
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			String tem_result;
			tem_result = "{"+"\"status\":\"HttpRequest UnSuccessful\""+"}";
			
			try 
			{
				result = new JSONObject(tem_result);
				return result;
			} 
			catch (JSONException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return result;
			}
			
		}
		  	
    }
    
    /*
     * Method name:LoginToServer With JSON
     * Method function:Login
     * Return value:Boolean
     */
    
    private boolean LoginByJSON()
    {
    	JSONObject result = null;
    	  	
    	String username = this.editUsername.getText().toString();
    	String password = this.editPassword.getText().toString();
    	
    	result = this.queryToServerByJSON(username, password);
    	  	
    	if(result != null)
    	{
    		try 
    		{
				status = result.getString("status");
			} 
    		catch (JSONException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(status != null && status.equals("success"))
    		{
    			//Need add:加入本地配置文件
    			Log.i("Login", ""+status);
        		return true;
    		}
    		else
    		{
    			try 
    			{
    				
    				Log.i("Login", ""+status);
					Log.i("Login", ""+URLDecoder.decode(status,"utf-8"));
    		
				} 
    			catch (UnsupportedEncodingException e) 
    			{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			return false;
    		}
    	}
    	else//result == null
    	{
    		Log.i("Login", "null");
    		return false;
    	}    
    }
    
    private Handler handler = new Handler()
    {
    	 @Override
	        public void handleMessage(Message msg) 
	        {
	                switch (msg.what) 
	                {
	                        case FinalDefinition.MESSAGE_DISMISS:	                        	
	                        	progressLog.dismiss();
//	                        	myThread.interrupt();
	                            break;
	                        default:
	                            super.handleMessage(msg);
	                }
	        }
    };

    
    private void doAutoFill()
    {
    	SharedPreferences settings = getSharedPreferences(FinalDefinition.PREFS_NAME, 0);
    	boolean isAutoFill = settings.getBoolean("isautofill", false);
    	if(isAutoFill)
    	{
    		String username = settings.getString("username", "");
        	String password = settings.getString("password", "");
        	editUsername.setText(username);
        	editPassword.setText(password);
        	boxRememberMe.setChecked(true);
    	}
    	else return;
    
    }
    
    @Override
    protected void onStop()
    {
    

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(FinalDefinition.PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      boolean isAutoFill = this.boxRememberMe.isChecked();
      editor.putBoolean("isautofill", isAutoFill);
 
      editor.putString("username", this.editUsername.getText().toString());
      editor.putString("password", this.editPassword.getText().toString());
      // Commit the edits!
      editor.commit();
      
//      doDealloc();
      super.onStop();
      
    }
    
    protected void onDestroy()
    {
        doDealloc();
        System.gc();
    	super.onDestroy();
    }
    
    
    
    private boolean doLogin()
    {
    	if( LoginLocal())
    	{
    		return true;
    	}
    	else if( HttpHelper.isConnect(LeisureNote.this) )
    	{
    		return LoginByJSON();
    	}
    	else if( !HttpHelper.isConnect(LeisureNote.this) )
    	{
    		new AlertDialog.Builder(LeisureNote.this) 
            .setTitle("登陆错误") 
            .setMessage("本地无数据且网络连接失败，请确认网络连接") 
            .setPositiveButton("确定", new DialogInterface.OnClickListener()
            { 
				            @Override 
				public void onClick(DialogInterface arg0, int arg1) 
				{ 
				// TODO Auto-generated method stub 
//					android.os.Process.killProcess(android.os.Process.myPid()); 
//					System.exit(0); 
				} 
			}).show(); 
    		return false;
    	}
    	return false;
    }
    
    
    private void doDealloc()
    {
        editUsername   = null;
		editPassword   = null;
		buttonLogin    = null;
		buttonRegister = null;
		hintUsername   = null;
		hintPassword   = null;
		
		boxRememberMe  = null;

		
		progressLog    = null;
		myThread       = null;
		sqliteHelper   = null;
		System.gc();
    }
}