package com.lolo.dev;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.helper.HttpHelper;
import com.lolo.helper.InputDetectionHelper;
import com.lolo.helper.LocalSQLiteHelper;
import com.lolo.tools.FinalDefinition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/*
 * Class Name:RegisterActivity
 * 
 * Author:Lolo
 * Date:2012/3/15
 * File Description:to implement the Register function
 * Included Component:
 * 1.6 infomation TextView
 * 2.4 EditText,for Username,password,passwordConfirm,NickName,
 * 3.3 radioButton for gender choose,in a radioGroup
 * 4.EditText for personalTag input
 * 5.1 button for submit the form
 * 6.6 INVISIBLE TextViews for hint.(offer infomations)
 * 7.what's need to be added is a file chooser to choose a image as user's picture for headTitle.
 * (undo yet)
 * 
 */
public class RegisterActivity extends Activity 
{
	
	//get the instances first;
	EditText   editUsername,
			   editPassword,
			   editPwdConfirm,
			   editNickname,
			   editPersonalTag;
	
	TextView   hintUsername,
			   hintPassword,
			   hintPwdConfirm,
			   hintNickname,
			   hintNicknameError,
			   hintPersonalTag;
	
	RadioGroup genderRadios;
	
	Button     buttonRegister;
	
	Boolean    isUserPassed        = false,
			   isPwdPassed         = false,
			   isPwdConfirmPassed  = false,
			   isNicknamePassed    = false,
			   isPersonalTagPassed = false;
	
	private ProgressDialog progressLog;
	private Dialog         dialog;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.register);
		
		//get instance of components
		this.getComponents();
		SQLiteHelper = new LocalSQLiteHelper(this);
	}
	
	
	public void getComponents()
	{
		//EditText
		this.editUsername      = (EditText)findViewById(R.id.registerUsername);
		this.editPassword      = (EditText)findViewById(R.id.registerPassword);
		this.editPwdConfirm    = (EditText)findViewById(R.id.registerPasswordConfirm);
		this.editNickname      = (EditText)findViewById(R.id.registerNickName);
		this.editPersonalTag   = (EditText)findViewById(R.id.registerPersonalTag);
		//TextView
		this.hintUsername      = (TextView)findViewById(R.id.textRegisterUserError);
		this.hintPassword      = (TextView)findViewById(R.id.textRegisterPwdError);
		this.hintPwdConfirm    = (TextView)findViewById(R.id.textRegisterPwdConError);
		this.hintNickname      = (TextView)findViewById(R.id.textRegisterNickNameHint);
		this.hintNicknameError = (TextView)findViewById(R.id.textRegisterNickNameError);
		this.hintPersonalTag   = (TextView)findViewById(R.id.textRegisterPersonalTag);
		//RadioGroup
		this.genderRadios      = (RadioGroup)findViewById(R.id.genderRadioGroup);
		//Button
		this.buttonRegister    = (Button)findViewById(R.id.registerButton);
		
		//add button Listeners
		this.setProgressDialog();
		this.setDialog();
		this.setButtonListeners();
		this.setEditFocusListeners();
	}
	
    private void setProgressDialog()
    {
    	progressLog = new ProgressDialog(this);
    	progressLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressLog.setMessage("注册ing...");
    	progressLog.setIndeterminate(false);
    	
    }
    
    private void setDialog()
    {
    	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    	{
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				// TODO Auto-generated method stub
//				Intent intent = new Intent(RegisterActivity.this,LeisureNote.class);
//				startActivity(intent);
				RegisterActivity.this.setResult(RESULT_OK, getIntent());
				RegisterActivity.this.finish();
			}
		};
    	dialog = new AlertDialog.Builder(this)
    			.setTitle("注册提示")
    			.setMessage("注册成功，点击返回登录界面")
    			.setPositiveButton("确定",listener)
    			.create();
    	
    }
	
	public void setButtonListeners()
	{
//		this.buttonRegister.setFocusable(true);
		
		//add this statement to make sure the other widgts will lose the focus 
		//when we touch the button.
		this.buttonRegister.setFocusableInTouchMode(true);
		
		this.buttonRegister.setOnClickListener
		(
				new OnClickListener()
				{
					
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						
						if( HttpHelper.isConnect(RegisterActivity.this)==false ) 
				        {   
				            new AlertDialog.Builder(RegisterActivity.this) 
				            .setTitle("网络错误") 
				            .setMessage("网络连接失败，请确认网络连接") 
				            .setPositiveButton("确定", new DialogInterface.OnClickListener()
				            { 
								            @Override 
								public void onClick(DialogInterface arg0, int arg1) 
								{ 
								// TODO Auto-generated method stub 
//									android.os.Process.killProcess(android.os.Process.myPid()); 
//									System.exit(0); 
								} 
				            }).show(); 
						}
						else if( isInputValidate() )
						{
							if( RegisterSubmit() )
							{								
				
								Toast.makeText(RegisterActivity.this,"注册成功",
										Toast.LENGTH_LONG).show();
								 
								dialog.show();
								//Modified Lolo @2012/3/20
//								Intent intent = new Intent(RegisterActivity.this,LeisureNote.class);
//								startActivity(intent);
//								RegisterActivity.this.finish();
							}
							else//registerFail
							{
								Toast.makeText(RegisterActivity.this,"RegisterFail",
										Toast.LENGTH_LONG).show();
							}
							
						}
						else
						{
							Log.i("bool", ""+isInputValidate());
							Toast.makeText(RegisterActivity.this,"信息输入不正确",
									Toast.LENGTH_LONG).show();
						}
				
					}
				}
		);
		
	}
	
	/*
	 * 1.输入有效性检查
	 * 2.设置动作监听器
	 * 3.传输数据
	 */
	
	
	public void setEditFocusListeners()
	{
		
		this.editUsername.setOnFocusChangeListener
		(
				new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View arg0, boolean isFocused) 
					{
						// TODO Auto-generated method stub
						if( !isFocused )
						{
							
							if( InputDetectionHelper.isEmpty(editUsername) )
							{
								RegisterActivity.this.hintUsername.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintUsername.setText(R.string.hint_usernameEmpty);
								RegisterActivity.this.hintUsername.setVisibility(View.VISIBLE);
								
								setIsUserPassed(false);
								
							}
							else if( !InputDetectionHelper.isUsernameEmail(editUsername) )
							{
								RegisterActivity.this.hintUsername.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintUsername.setText(R.string.hint_usernameError);
								RegisterActivity.this.hintUsername.setVisibility(View.VISIBLE);
								
								setIsUserPassed(false);
								
							}
							else if( InputDetectionHelper.isUsernameOccupied(editUsername) )
							{
								RegisterActivity.this.hintUsername.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintUsername.setText(R.string.user_registed);
								RegisterActivity.this.hintUsername.setVisibility(View.VISIBLE);
								
								setIsUserPassed(false);
								
							}
							else
							{
								RegisterActivity.this.hintUsername.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.right_green));
								RegisterActivity.this.hintUsername.setText(R.string.hint_correct);							
								RegisterActivity.this.hintUsername.setVisibility(View.VISIBLE);
								
								setIsUserPassed(true);
							}
						}
						
					}
					
				}
		);
		
		this.editPassword.setOnFocusChangeListener
		(
				new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View arg0, boolean isFocused) 
					{
						// TODO Auto-generated method stub
						if( !isFocused )
						{
							if( InputDetectionHelper.isEmpty(editPassword) )
							{
								RegisterActivity.this.hintPassword.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintPassword.setText(R.string.hint_passwordEmpty);
								RegisterActivity.this.hintPassword.setVisibility(View.VISIBLE);
								
								setIsPwdPassed(false);
							}
							else if( InputDetectionHelper.isPasswordTooShort(editPassword) )
							{
								RegisterActivity.this.hintPassword.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintPassword.setText(R.string.hint_passwordError);
								RegisterActivity.this.hintPassword.setVisibility(View.VISIBLE);
							
								setIsPwdPassed(false);
							}
							else
							{
								RegisterActivity.this.hintPassword.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.right_green));
								RegisterActivity.this.hintPassword.setText(R.string.hint_correct);
								RegisterActivity.this.hintPassword.setVisibility(View.VISIBLE);
								
								setIsPwdPassed(true);
							}
						}
						
					}
					
				}
		);
		
		this.editPwdConfirm.setOnFocusChangeListener
		(
				new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View arg0, boolean isFocused) 
					{
						// TODO Auto-generated method stub
						if( !isFocused )
						{
							if( InputDetectionHelper.isEmpty(editPwdConfirm) )
							{
								RegisterActivity.this.hintPwdConfirm.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintPwdConfirm.setText(R.string.hint_passwordEmpty);
								RegisterActivity.this.hintPwdConfirm.setVisibility(View.VISIBLE);
							
								setIsPwdConfirmPassed(false);
							}
							else if( !InputDetectionHelper.isPasswordConfirmed(editPassword, editPwdConfirm) )
							{
								RegisterActivity.this.hintPwdConfirm.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintPwdConfirm.setText(R.string.hint_passwordConfirmError);
								RegisterActivity.this.hintPwdConfirm.setVisibility(View.VISIBLE);
								
								setIsPwdConfirmPassed(false);
							}
							else
							{
								RegisterActivity.this.hintPwdConfirm.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.right_green));
								RegisterActivity.this.hintPwdConfirm.setText(R.string.hint_correct);
								RegisterActivity.this.hintPwdConfirm.setVisibility(View.VISIBLE);
							
								setIsPwdConfirmPassed(true);
							}
						}
						
					}
					
				}
		);
		
		this.editNickname.setOnFocusChangeListener
		(
				new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View arg0, boolean isFocused) 
					{
						// TODO Auto-generated method stub
						if( isFocused )
						{
							RegisterActivity.this.hintNickname.setVisibility(View.VISIBLE);
						}
						else
						{
							
							RegisterActivity.this.hintNickname.setVisibility(View.GONE);
							
							if( InputDetectionHelper.isEmpty(editNickname) )
							{
								RegisterActivity.this.hintNicknameError.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintNicknameError.setText(R.string.hint_nickNameEmpty);
								RegisterActivity.this.hintNicknameError.setVisibility(View.VISIBLE);
								
								setIsNicknamePassed(false);
								
							}
							else if( InputDetectionHelper.isNicknameOccupied(editNickname) )
							{
								RegisterActivity.this.hintNicknameError.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.peach_red));
								RegisterActivity.this.hintNicknameError.setText(R.string.hint_nickNameError);
								RegisterActivity.this.hintNicknameError.setVisibility(View.VISIBLE);
								
								setIsNicknamePassed(false);
								
							}
							//Need added isUsername Occupied!
							else
							{
								RegisterActivity.this.hintNicknameError.setTextColor(RegisterActivity.this.getApplicationContext().getResources().getColor(R.color.right_green));
								RegisterActivity.this.hintNicknameError.setText(R.string.hint_correct);
								RegisterActivity.this.hintNicknameError.setVisibility(View.VISIBLE);
								
								setIsNicknamePassed(true);
								
							}
						}
					}
					
				}
		
		);
		
		this.editPersonalTag.setOnFocusChangeListener
		(
				new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View arg0, boolean isFocused) 
					{
						// TODO Auto-generated method stub
						if( isFocused )
						{
							if( InputDetectionHelper.isEmpty(editPersonalTag) )
							{
								RegisterActivity.this.hintPersonalTag.setText(R.string.hint_personalTagEmpty);
								RegisterActivity.this.hintPersonalTag.setVisibility(View.VISIBLE);
							
								setIsPersonalTagPassed(false);
							}
						}
						else//lost focused
						{						
							if( InputDetectionHelper.isEmpty(editPersonalTag) )
							{
								RegisterActivity.this.hintPersonalTag.setText(R.string.hint_personalTagEmpty);
								RegisterActivity.this.hintPersonalTag.setVisibility(View.VISIBLE);
							
								setIsPersonalTagPassed(false);
							}
							else if( !InputDetectionHelper.isEmpty(editPersonalTag) )
							{
								RegisterActivity.this.hintPersonalTag.setText(R.string.hint_personalTag);
								RegisterActivity.this.hintPersonalTag.setVisibility(View.VISIBLE);
								
								setIsPersonalTagPassed(true);
							}
						}
					}
					
				}
		);
		
		
	}
	
	//Setters and Getters
	
//	isUserPassed,
	private void setIsUserPassed(boolean isPassed)
	{
		this.isUserPassed = isPassed;
	}
	private boolean getIsUserPassed()
	{
		return isUserPassed;
	}
//	isPwdPassed,
	private void setIsPwdPassed(boolean isPassed)
	{
		this.isPwdPassed = isPassed;
	}
	private boolean getIsPwdPassed()
	{
		return isPwdPassed;
	}
//	isPwdConfirmPassed,
	private void setIsPwdConfirmPassed(boolean isPassed)
	{
		this.isPwdConfirmPassed = isPassed;
	}
	private boolean getIsPwdConfirmPassed()
	{
		return isPwdConfirmPassed;
	}
//	isNicknamePassed,
	
	private void setIsNicknamePassed(boolean isPassed)
	{
		this.isNicknamePassed = isPassed;
	}
	private boolean getIsNicknamePassed()
	{
		return isNicknamePassed;
	}
//	isPersonalTagPassed;
	private void setIsPersonalTagPassed(boolean isPassed)
	{
		this.isPersonalTagPassed = isPassed;
	}
	private boolean getPersonalTagPassed()
	{
		return isPersonalTagPassed;
	}
	
	//check the input validate
	private boolean isInputValidate()
	{
		if( getIsUserPassed() && getIsPwdPassed() && getIsPwdConfirmPassed() && getIsNicknamePassed() && getPersonalTagPassed() )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//a helper method
	private String getGender()
	{
		int        genderId   = this.genderRadios.getCheckedRadioButtonId();
		
		if( genderId == R.id.genderBoyButton )
		{
			return "boy";
		}
		else if( genderId == R.id.genderGirlButton )
		{
			return "girl";
		}
		else if( genderId == R.id.genderNoneButton )
		{
			return "confused";
		}
		else 
		{
			return null;
		}
	}
	
	//requestToServerByJSON
	private JSONObject requestToServerByJSON()
	{
		String     url        = HttpHelper.Base_Url+"servlet/RegisterServlet";
		JSONObject result     = null;
		JSONObject parameters = new JSONObject();
		
		//get the parameters need to send
		String     userName    = this.editUsername.getText().toString();
		String     pwdWord     = this.editPassword.getText().toString();
		String     nickName    = this.editNickname.getText().toString();
		String     gender      = this.getGender();
		String     personalTag = this.editPersonalTag.getText().toString();
//		try 
//		{
//			userName    = URLEncoder.encode(userName, "utf-8");
//			pwdWord     = URLEncoder.encode(userName, "utf-8");
//			nickName    = URLEncoder.encode(userName, "utf-8");
//			gender      = URLEncoder.encode(userName, "utf-8");
//			
//			personalTag = URLEncoder.encode(personalTag, "utf-8");
//		} 
//		catch (UnsupportedEncodingException e2) 
//		{
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
		if
		( 
			   userName    != null 
			&& pwdWord     != null 
			&& nickName    != null 
			&& gender      != null 
			&& personalTag != null				
		)//do
		{
			try 
			{
				parameters.put("username", userName);
				parameters.put("password", pwdWord);
				parameters.put("nickname", nickName);
				parameters.put("gender", gender);
				parameters.put("personalTag", personalTag);
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//send request
		try 
		{
			HttpEntity entity = new StringEntity(URLEncoder.encode(parameters.toString(),"utf-8"));
			return HttpHelper.getResponseByHttpPostWithJSON(url, entity);
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			String tem_result = "{"+"\"status\""+":"+"\"HttpRequest UnSuccessful\""+"}";
			
			try 
			{
				result = new JSONObject(tem_result);
				//report failure infomation
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
	
	
	//Method:RegisterSubmit
	
	private boolean RegisterSubmit()
	{
		String     resultStr  = null;
		JSONObject resultJSON = this.requestToServerByJSON();
		Log.i("Login1", ""+resultJSON);
		
		if( resultJSON != null)
		{			
			try 
			{
				resultStr = resultJSON.getString("status");
				
				Log.e("Login",resultStr);
//				try 
//				{
//					resultStr = URLDecoder.decode(resultStr, "utf-8");
//					
//				} 
//				catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( resultStr != null && resultStr.equals("failure") )
			{
				Log.i("Login", ""+resultStr);
        		return false;
			}
			else 
			{
				if ( resultStr == null )
				{
					Log.i("Login", ""+"null");
	        		return false;
				}
				else
				{
					try 
					{
						JSONObject user = new JSONObject(resultStr);
						
						doRegisterUpdate(user);
						Log.e("Login", user.getInt("id")+"");
						Log.e("Login",user.getString("username"));
					}
					catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			}
			
		}
		else//result == null
    	{
    		Log.i("Login", "null");
    		return false;
    	}  
	}
	
	
	
	private LocalSQLiteHelper SQLiteHelper;
	
	
	private void doRegisterUpdate(JSONObject jsonUser)
	{
		this.SQLiteHelper.doOpen();
		ContentValues cv = new ContentValues();
		try 
		{
			cv.put(FinalDefinition.DATABASE_ID, jsonUser.getInt("id"));
			cv.put(FinalDefinition.DATABASE_USERNAME, jsonUser.getString("username"));
			cv.put(FinalDefinition.DATABASE_PASSWORD, jsonUser.getString("password"));
			cv.put(FinalDefinition.DATABASE_GENDER, jsonUser.getString("gender"));
			cv.put(FinalDefinition.DATABASE_NICKNAME, jsonUser.getString("name"));
			cv.put(FinalDefinition.DATABASE_REMARK, jsonUser.getString("remark"));
		}
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long result = SQLiteHelper.doInsert(FinalDefinition.DATABASE_TABLE_USER, cv);
		Log.i("view","insert"+" "+result);
	
		try 
		{
			cv.clear();
			cv.put(FinalDefinition.DATABASE_BOOK_USER, jsonUser.getInt("id"));
			cv.put(FinalDefinition.DATABASE_BOOK_NAME, jsonUser.getString("name")+"的默认笔记本");
			cv.put(FinalDefinition.DATABASE_BOOK_CTIME, System.currentTimeMillis());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SQLiteHelper.doInsert(FinalDefinition.DATABASE_TABLE_BOOK, cv);
		
		SQLiteHelper.doClose();
		
	}

}




