package com.lolo.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.lolo.dao.NoteDao;
import com.lolo.helper.TypeTransformer;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class InsertNoteServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public InsertNoteServlet() {
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

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
//		ServletInputStream sis = request.getInputStream();
//		byte[] b = new byte[sis.available()];
//		sis.read(b);
//		sis.close();
//		JSONObject json = (JSONObject) TypeTransformer.BytesToObject(b);
//		
//		try 
//		{
//			JSONObject note = (JSONObject) json.get("note");
//			System.out.println(note.getString("noteTitle"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
		String  line        = null;
		StringBuffer stringBuffer = new StringBuffer();		
		BufferedReader reader = request.getReader();		
		while( (line = reader.readLine())!=null )
		{
			stringBuffer.append(line);		
		}		
		System.out.println("--------InserNote------------------------");
		System.out.println("stringBuffer长度："+stringBuffer.length());
		System.out.println("request长度："+request.getContentLength());
		System.out.println("--------InserNote------------------------");
		try {
			JSONObject parameters = new JSONObject(URLDecoder.decode(stringBuffer.toString(), "utf-8"));
//			JSONObject parameters = new JSONObject(stringBuffer.toString());
			JSONObject note = parameters.getJSONObject("note");
//			System.out.println(note.getString("bookname"));
			NoteDao notedo  = new NoteDao();
			boolean isSuccess = notedo.InsertNote(note);
//			String title = note.getString("noteAudio");
//			byte[] b = Base64.decode(title);
//			File file = TypeTransformer.BytesToFile(b, "/My_WorkSpace/My_WebDevelopmentSpace/LeisureNote/tem", ".amr");
//			if(file.exists())
//			{
//				System.out.println(file.getAbsolutePath().toString());
//			}
//			System.out.println(note.toString());
			JSONObject responseJson = new JSONObject();
			responseJson.put("status", isSuccess?"success":"failure");
//			System.out.println(message);
			out.print(responseJson);
		} 
		catch (JSONException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		catch (Base64DecodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//		}

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
