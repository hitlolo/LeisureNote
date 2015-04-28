package com.lolo.helper;

import java.io.UnsupportedEncodingException;
import java.util.Observable;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckDatabase extends Observable
{
	public boolean isOccupied(int tag,String str)
	{
		String     url       = HttpHelper.Base_Url+"servlet/CheckOccupiedServlet";
		JSONObject parameter = new JSONObject();
		JSONObject result    = null;
		String     resultStr = null;
		String     key       = null;
		String     value     = str;
		if( tag == InputDetectionHelper.TagUsername )
		{
			key = "username";
		}
		if( tag == InputDetectionHelper.TagNickname )
		{
			key = "nickname";
		}
		
		try 
		{
			parameter.put("parameterIs", key);
			parameter.put(key, value);
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
						return true;
					}
					else if( resultStr.equals("free") )
					{
						return false;
					}
					else 
					{
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
		return true;
		
	}
}
