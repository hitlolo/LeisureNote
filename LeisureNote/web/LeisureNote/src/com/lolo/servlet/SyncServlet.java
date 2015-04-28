package com.lolo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.dao.Syncer;
import com.lolo.dao.UserDao;
import com.lolo.dao.UserDo;

public class SyncServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SyncServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonResult = new JSONObject();
		
		String line = null;
		StringBuffer stringBuffer = new StringBuffer();		
		BufferedReader reader = request.getReader();	
		
		while( (line = reader.readLine())!=null )
		{
			stringBuffer.append(line);		
		}
		System.out.println("stringBuffer长度："+stringBuffer.length());
		System.out.println("request长度："+request.getContentLength());
		
		try 
		{			
			JSONObject logs = new JSONObject(URLDecoder.decode(stringBuffer.toString(),"utf-8"));		
			Syncer syncer = new Syncer();
			boolean isSuccess = syncer.doSync(logs);
			if(isSuccess)
			{
				jsonResult.put("status", "success");
			}
			else if(!isSuccess)
			{
				jsonResult.put("status", "fail");
			}
			out.print(jsonResult);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		this.doGet(request, response);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
