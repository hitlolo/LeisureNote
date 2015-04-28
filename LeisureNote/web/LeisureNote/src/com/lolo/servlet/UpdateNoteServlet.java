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

import com.lolo.dao.NoteDao;

public class UpdateNoteServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public UpdateNoteServlet() {
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
		StringBuffer sb = new StringBuffer();
		BufferedReader br = request.getReader();
		if((line = br.readLine())!=null)
		{
			sb.append(line);
		}
		System.out.println("--------UpdateNote------------------------");
		System.out.println("stringBuffer长度："+sb.length());
		System.out.println("request长度："+request.getContentLength());
		System.out.println("--------UpdateNote------------------------");
		try 
		{			
			JSONObject parameters = new JSONObject(URLDecoder.decode(sb.toString(),"utf-8"));
			NoteDao notedo = new NoteDao();
			boolean isSuccess = notedo.doUpdateNote(parameters);
			if(isSuccess)
			{
				JSONObject result = new JSONObject();
				result.put("status", "success");
				out.print(result);
			}
			else
			{
				JSONObject result = new JSONObject();
				result.put("status", "fail");
				out.print(result);
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
