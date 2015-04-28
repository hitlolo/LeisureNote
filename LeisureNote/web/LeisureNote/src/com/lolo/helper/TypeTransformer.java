package com.lolo.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;

public class TypeTransformer 
{

	public static byte[] FileToBytes(File file)
	{
		try 
		{
			FileInputStream fis = new FileInputStream(file);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			return b;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
		
	}
	
	public static File BytesToFile(byte[] b,String path,String suffix)
	{	
		try 
		{
			File dir  = new File(path);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			File file = File.createTempFile("tem_",suffix,dir);
			FileOutputStream fstream = new FileOutputStream(file);   
			fstream.write(b);
			fstream.flush();
			fstream.close();
			return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;	
	}
	
	public static byte[] ObjectToBytes(Object object)
	{
		try
		{
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(object);  
			oo.close();
			byte[] b = bo.toByteArray(); 
			return b; 
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object BytesToObject(byte[] b)
	{
		try
		{
			
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream oi = new ObjectInputStream(bi);
			
			return oi.readObject();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/** 
	* 把Blob类型转换为byte数组类型 
	* @param blob 
	* @return 
	*/  
	public static byte[] blobToBytes(Blob blob) 
	{  
	  
		BufferedInputStream is = null;  
		  
		try 
		{  
			is = new BufferedInputStream(blob.getBinaryStream());  
			byte[] bytes = new byte[(int) blob.length()];  
			int len = bytes.length;  
			int offset = 0;  
			int read = 0;  	
		  
			while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0)
			{  
				offset += read;  
			}  
			return bytes;  
		}
		catch (Exception e) 
		{  
			return null;  
		} 
		finally 
		{  
			try 
			{  
				is.close();  
				is = null;  
			} 
			catch (IOException e) 
			{  
				return null;  
			}  
		}  
	}  
	
}
