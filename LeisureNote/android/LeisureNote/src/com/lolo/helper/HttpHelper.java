package com.lolo.helper;

import java.io.IOException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/*
 * Author:Lolo
 * Date:2012/3/14
 * File Description:The HttpHelper Class is a tool class to 
 * help the https connection
 * 
 * ITS Methods are STATIC.
 */

public class HttpHelper 
{
	
	//the url of Server
	public static final String Base_Url = "http://192.168.1.107:8080/LeisureNote/";
	
	/*
	 * get HttpGet Object
	 */
	public static HttpGet getHttpGet(String url)
	{		
		HttpGet request = new HttpGet(url);
		return request;		
	}
	/*
	 * get HttpPost Object
	 */
	public static HttpPost getHttpPost(String url)
	{
		HttpPost request = new HttpPost(url);
		return request;
		
	}
	
	/*
	 * get HttpResponse by HttpGet
	 */
	public static HttpResponse getHttpResponse(HttpGet request)throws ClientProtocolException,IOException
	{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;	
	}
	/*
	 * get HttpResponse by HttpPost
	 */
	public static HttpResponse getHttpResponse(HttpPost request)throws ClientProtocolException,IOException
	{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;		
	}
	
	/*
	 * Query for Post
	 */
	public static String queryForPost(String url)
	{		
		//get HttpPost
		String   result  = null;
		HttpPost request = HttpHelper.getHttpPost(url);
		try
		{
			HttpResponse response = HttpHelper.getHttpResponse(request);
			
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{//200 means success
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
			
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
			result = "Client_E_NetWorking UnSuccessful!";
			return result;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			result = "IOE_NetWorking UnSuccessful!" + e.getMessage();
			return result;
		}
		return null;		
	}
	
	/*
	 * Query for Get
	 */
	
	public static String queryForGet(String url)
	{		
		String  result  = null;
		HttpGet request = HttpHelper.getHttpGet(url);		
		try{
			
			HttpResponse response = HttpHelper.getHttpResponse(request);
			if( response.getStatusLine().getStatusCode()== HttpStatus.SC_OK )
			{
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
			result = "Client_E_NetWorking UnSuccessful!";
			return result;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			result = "IOE_NetWorking UnSuccessful!";
			return result; 
		}
		return null;
		
	}
	
	/*
	 * JSON for INTERNET INFO EXCHANGE
	 */
	
	
	public static HttpGet getHttpGetRequestWithJSON(String url)
	{
		HttpGet request = new HttpGet(url);
		return request;	
	}
	
	public static HttpPost getHttpPostRequestWithJSON(String url)
	{
		HttpPost request = new HttpPost(url);
		return request;		
	}
	
	public static JSONObject getResponseByHttpPostWithJSON(String url,HttpEntity entity)
	{		
		JSONObject  result  = null;
		HttpPost request = HttpHelper.getHttpPostRequestWithJSON(url);
		request.setEntity(entity);		
		try{
			HttpResponse response = HttpHelper.getHttpResponse(request);
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				String tem_result;
				tem_result = EntityUtils.toString(response.getEntity());
				//decode
				tem_result = URLDecoder.decode(tem_result,"utf-8");
				try 
				{
					result = new JSONObject(tem_result);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}
			if (response.getStatusLine().getStatusCode() == 404 )
			{
				String tem_result;
				tem_result = "{"+"\"status\":\"服务器端错误。\""+"}";
				try 
				{
					result = new JSONObject(tem_result);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return result;
			}
			if (response.getStatusLine().getStatusCode() == 413 )
			{
				
				Log.i("sync","请求实体过大");
			}
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
			String tem_result;
//			tem_result = "{"+"\"status\":\"CLIENT_NETWORKING UnSuccessful\""+"}";
			tem_result = "{"+"\"status\":\""+e.toString()+"\""+"}";
			try 
			{
				result = new JSONObject(tem_result);
			} 
			catch (JSONException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return result;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			String tem_result;
//			tem_result = "{"+"\"status\":\"IOE_NETWORKING UnSuccessful\""+"}";
			tem_result = "{"+"\"status\":\""+e.toString()+"\""+"}";
			try 
			{
				result = new JSONObject(tem_result);
			} 
			catch (JSONException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return result;
		}
		return null;
		
	}
	
	 /*
     * Method name:isConnect
     * Method function:is network connected
     * Return value:Boolean
     */
    public static boolean isConnect(Context context)
    { 
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理） 
	    try 
	    { 
	        ConnectivityManager connectivity = (ConnectivityManager) context 
	                .getSystemService(Context.CONNECTIVITY_SERVICE); 
	        if (connectivity != null) 
	        { 
	            // 获取网络连接管理的对象 
	            NetworkInfo info = connectivity.getActiveNetworkInfo(); 
	            if (info != null&& info.isConnected()) 
	            { 
	                // 判断当前网络是否已经连接 
	                if (info.getState() == NetworkInfo.State.CONNECTED) 
	                { 
	                    return true; 
	                } 
	            } 
	        } 
	    } 
	    catch (Exception e) 
	    { 
	// TODO: handle exception 
	    	Log.v("error",e.toString()); 
	    } 
        return false; 
    } 
    


    //modifed 2012/3/22
    //准备修改的，其实现在除名字以外 和上一个函数一模一样
    public static JSONObject getBookResponseByHttpPostWithJSON(String url,HttpEntity entity)
	{		
    	JSONObject  result  = null;
		HttpPost request = HttpHelper.getHttpPostRequestWithJSON(url);
		request.setEntity(entity);		
		try{
			HttpResponse response = HttpHelper.getHttpResponse(request);
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				String tem_result;
				tem_result = EntityUtils.toString(response.getEntity());
				//decode
				tem_result = URLDecoder.decode(tem_result,"utf-8");
				Log.i("tem_result", tem_result);
				try 
				{
					result = new JSONObject(tem_result);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}
		
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
}
