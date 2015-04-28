package com.lolo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

public class GetLogtimeServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GetLogtimeServlet() {
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
			JSONObject parameters = new JSONObject(stringBuffer.toString());		
			int userid = parameters.getInt("userid");
			System.out.println("userid="+userid);
			UserDao userdao = new UserDo();
			long logtime = userdao.getLogtime(userid);
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("logtime", logtime);
			
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
		doGet(request,response);
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
