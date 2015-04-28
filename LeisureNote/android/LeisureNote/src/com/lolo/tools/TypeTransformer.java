package com.lolo.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Log;

public class TypeTransformer 
{

	public static byte[] FileToBytes(File file)
	{
		try 
		{
			Log.i("view","file to bytes[]");
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
				Log.i("view","make dir");
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
	
	public static File BytesToFile(byte[] b,String name)
	{	
		try 
		{
			File dir  = new File(FinalDefinition.BASE_SOUND_PATH + name);
			File file = null;
			if(!dir.exists())
			{
				
				file = File.createTempFile(name,null,new File(FinalDefinition.BASE_SOUND_PATH));
				FileOutputStream fstream = new FileOutputStream(file);   
				fstream.write(b);
				fstream.flush();
				fstream.close();
			}
			else
			{
				file = dir;
			}
			
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
			Log.i("view","object to bytes[]");
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(object);  
			oo.close();
			byte[] b = bo.toByteArray(); 
			Log.i("view","object to bytes[]"+" "+b.length);
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
}
