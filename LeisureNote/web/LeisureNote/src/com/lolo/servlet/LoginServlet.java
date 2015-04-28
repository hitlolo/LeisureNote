package com.lolo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.dao.UserDao;
import com.lolo.dao.UserDo;
import com.lolo.entity.User;

public class LoginServlet extends HttpServlet 
{

	/**
	 * Constructor of the object.
	 */
	public LoginServlet() 
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() 
	{
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
			throws ServletException, IOException 
	{
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		
//		UserDao dao = new GetUser();
//		//get client request "parameter"
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//
//		
//		User user   = dao.Login(username, password);
//		if(user!=null){
//			out.print("success");
//		}
//		else{
//			String message = "用户名或密码错误";
//			out.print(URLEncoder.encode(message, "utf-8"));
//		}
//		
	
		String line = null;
		UserDao dao = new UserDo();
		StringBuffer stringBuffer = new StringBuffer();
		
		
		BufferedReader reader = request.getReader();
		
		while( (line = reader.readLine())!=null )
		{
			stringBuffer.append(line);		
		}
		
		try 
		{
			
			JSONObject parameters = new JSONObject(stringBuffer.toString());
			
			String username = parameters.getString("username");
			String password = parameters.getString("password");
			
			User user   = dao.Login(username, password);
			if(user != null)
			{
				
				out.print(new JSONObject("{\"status\":\"success\"}"));
				
			}
			else if(user == null)
			{
				String message = "{\"status\":\"用户名或密码错误\"}";
//				System.out.println(message);
				out.print(URLEncoder.encode(message, "utf-8"));
//				System.out.println(URLEncoder.encode(message, "utf-8"));
			}
			
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
			throws ServletException, IOException 
	{

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
	public void init() throws ServletException 
	{
		// Put your code here
	}
	
	/*
	 * Date:2012/3/14
	 * Method:to translate the user object to a String;
	 */

	private String ObjectToString(User user)
	{
		String msg = "";
		msg += "id=" + user.getId();
		msg += ";";
		msg += "name=" + user.getUsername();
		return msg;			
	}
	
}
