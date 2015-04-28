package com.lolo.helper;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.EditText;

/*
 * Author:Lolo
 * Date:2012/3/17
 * ClassName:InputDectectionHelper
 * Description:a tool class to help checking the input of Login and Sign In Activity.
 * 
 */
public class InputDetectionHelper
{
	//is Input Empty

	public static final int TagUsername = 1;
	public static final int TagNickname = 2;
	
	public static boolean isEmpty(EditText editText)
	{
		String input = editText.getText().toString();
		if( input.equals(""))
		{
			return true;
		}
		else 
			return false;
	}
	
	//is Input Too Short
	public static boolean isPasswordTooShort(EditText editText)
	{
		String input = editText.getText().toString();
		if( input.length() <= 5)
		{
			return true;
		}
		else 
			return false;
	}
	
	//is the Input of password confirm Error
	public static boolean isPasswordConfirmed(EditText password,EditText confirm)
	{
		String first_input  = password.getText().toString();
		String second_input = confirm.getText().toString();
		if( first_input.equals("") || second_input.equals("") )
		{
			return false;
		}
		else if( first_input.equals(second_input) )
		{
			return true;
		}
		else 
			return false;			
	}
	
	//is the account a Email address
	public static boolean isUsernameEmail(EditText editText)
	{
		String username = editText.getText().toString();
		
	   	Pattern pattern = Pattern
		.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher mc = pattern.matcher(username);
		
		return mc.matches();
	}
	
	/*
	 * Need Added.
	 */
	
	//is the account Occupied 
	public static boolean isUsernameOccupied(EditText editText)
	{
		String     username  = editText.getText().toString();
		String     url       = HttpHelper.Base_Url+"servlet/CheckOccupiedServlet";
		JSONObject parameter = new JSONObject();
		JSONObject result    = null;
		String     resultStr = null;
		String     key       = null;
		
		key = "username";
		
		try 
		{
			parameter.put("parameterIs", key);
			parameter.put(key, username);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//send
		try 
		{
			HttpEntity entity = new StringEntity(parameter.toString());
			result = HttpHelper.getResponseByHttpPostWithJSON(url, entity);
			
			if( result != null)
			{
				try 
				{
					resultStr = result.getString("status");
					if(resultStr == null)
					{
						Log.i("occupied", "null");
						return true;
					}
					else if( resultStr.equals("free") )
					{
						Log.i("occupied", "free");
						return false;
					}
					else 
					{
						Log.i("occupied", "occupied");
						return true;
					}
					
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//occupied
		Log.i("occupied", "null");
		return true;
		
	}
		
	
	
	//is the nickname Occupied 
	
	public static boolean isNicknameOccupied(EditText editText)
	{
		String     nickname  = editText.getText().toString();
		String     url       = HttpHelper.Base_Url+"servlet/CheckOccupiedServlet";
		JSONObject parameter = new JSONObject();
		JSONObject result    = null;
		String     resultStr = null;
		String     key       = null;
		
		key = "nickname";
		
		try 
		{
			parameter.put("parameterIs", key);
			parameter.put(key, nickname);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//send
		try 
		{
			HttpEntity entity = new StringEntity(parameter.toString());
			result = HttpHelper.getResponseByHttpPostWithJSON(url, entity);
			
			if( result != null)
			{
				try 
				{
					resultStr = result.getString("status");
					if(resultStr == null)
					{
						Log.i("occupied", "null");
						return true;
					}
					else if( resultStr.equals("free") )
					{
						Log.i("occupied", "free");
						return false;
					}
					else 
					{
						Log.i("occupied", "occupied");
						return true;
					}
					
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//occupied
		Log.i("occupied", "null");
		return true;
		
		
	}

}
