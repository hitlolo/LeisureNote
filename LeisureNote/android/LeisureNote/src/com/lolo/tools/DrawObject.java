package com.lolo.tools;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawObject 
{
	private Paint myPaint = null;
	private Path  myPath  = null;
	
	public DrawObject(Paint paint,Path path)
	{
		myPaint = paint;
		myPath  = path;
	}

	public Paint getMyPaint() 
	{
		return myPaint;
	}

	public void setMyPaint(Paint myPaint) 
	{
		this.myPaint = myPaint;
	}

	public Path getMyPath() 
	{
		return myPath;
	}

	public void setMyPath(Path myPath) 
	{
		this.myPath = myPath;
	}
	
}
