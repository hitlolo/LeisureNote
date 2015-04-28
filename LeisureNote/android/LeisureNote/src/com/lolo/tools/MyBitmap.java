package com.lolo.tools;

import java.io.Serializable;

import android.graphics.Bitmap;

public class MyBitmap implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] bitmapBytes = null;
	private String name = null;

	public MyBitmap(byte[] bitmapBytes, String name) 
	{
	// TODO Auto-generated constructor stub
	this.bitmapBytes = bitmapBytes;
	this.name = name;
	}
	
	public MyBitmap(Bitmap bitmap) 
	{
	// TODO Auto-generated constructor stub
	this.bitmapBytes = BytesBitmap.getBytes(bitmap);
	}
	
	
	public byte[] getBitmapBytes() 
	{
		return this.bitmapBytes;
	}
	
	public Bitmap getBitmap()
	{
		return BytesBitmap.getBitmap(bitmapBytes);
	}
	public String getName() 
	{
		return this.name;
	}
	
}
