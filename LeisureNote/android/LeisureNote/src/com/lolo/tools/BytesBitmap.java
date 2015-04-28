package com.lolo.tools;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class BytesBitmap 
{
	public static Bitmap getBitmap(byte[] data) 
	{
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static byte[] getBytes(Bitmap bitmap) 
	{
		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, baops);
		return baops.toByteArray();
	}
	
}
