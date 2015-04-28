package com.lolo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.dao.UserDao;
import com.lolo.dao.UserDo;
import com.lolo.entity.User;

public class RegisterServlet extends HttpServlet 
{

	/**
	 * Constructor of the object.
	 */
	public RegisterServlet() 
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
		//get User
		//get parameter
		//do database 
		//out
		User    user        = new User();
		UserDao dao         = new UserDo();	
		String  line        = null;
		boolean isSuccessed = false;
		
		StringBuffer stringBuffer = new StringBuffer();	
		BufferedReader reader = request.getReader();
		
		while( (line = reader.readLine())!=null )
		{
			stringBuffer.append(line);		
		}
		try 
		{
			JSONObject parameters = new JSONObject(URLDecoder.decode(stringBuffer.toString(),"utf-8"));
			
			String username    = parameters.getString("username");
			String password    = parameters.getString("password");
			String nickname    = parameters.getString("nickname");
			String gender      = parameters.getString("gender");
			String personalTag = parameters.getString("personalTag");
			
			user.setUsername(username);
			user.setPassword(password);
			user.setName(nickname);
			user.setGender(gender);
			user.setRemark(personalTag);
			
//			isSuccessed = dao.Register(user);
			User returnUser = dao.RegisterToUser(user);
			if(returnUser!=null)
			{
				isSuccessed = true;
			}
			JSONObject userObject = new JSONObject();
			userObject.put("id", returnUser.getId());
			userObject.put("username", returnUser.getUsername());
			userObject.put("password", returnUser.getPassword());
			userObject.put("name", returnUser.getName());
			userObject.put("gender", returnUser.getGender());
			userObject.put("headurl", returnUser.getHeadimg_url());
			userObject.put("remark", returnUser.getRemark());
			System.out.println(userObject);

			
			if( isSuccessed )
			{
//				out.print("{\"status\":\"success\"}");
				JSONObject result = new JSONObject();
				result.put("status", userObject);
				System.out.println(result);
				out.print(URLEncoder.encode(result.toString(),"utf-8"));
			}
			else
			{
				out.print("{\"status\":\"failure\"}");
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
	

}
