package com.lolo.tools;

import java.io.Serializable;
import java.util.Vector;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MyVector implements Serializable
{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Vector<MyBitmap> bitmapVector;
	 
	public MyVector()
	{
		 bitmapVector = new Vector<MyBitmap>();
	}
	
//	@Override
//	public int describeContents() 
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel p, int flags) 
//	{
//		// TODO Auto-generated method stub
//		p.writeList(bitmapVector);
//	}
//	public static final Parcelable.Creator<MyVector> CREATOR
//    = new Parcelable.Creator<MyVector>() 
//    {
//		public MyVector createFromParcel(Parcel in) 
//		{
//			return new MyVector(in);
//		}
//
//		@Override
//		public MyVector[] newArray(int arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//    };
//    
//    private MyVector(Parcel in) 
//    {
//    	bitmapVector = new Vector<Bitmap>();
//    	in.readList(bitmapVector,ClassLoader.getSystemClassLoader());
//    }
    
    public int size()
    {
    	return bitmapVector.size();
    }
    
    public MyBitmap elementAt(int location)
    {
    	return bitmapVector.elementAt(location);
    }
    public void add(MyBitmap bitmap)
    {
    	bitmapVector.add(bitmap);
    }
    public boolean isEmpty()
    {
    	return bitmapVector.isEmpty();
    }
    
    public MyBitmap lastElement()
    {
    	return bitmapVector.lastElement();
    }
    public void remove(MyBitmap bitmap)
    {
    	bitmapVector.remove(bitmap);
    }
    public void removeAllElements()
    {
    	bitmapVector.removeAllElements();
    }
    public void setVector(Vector<MyBitmap> vector)
    {
    	bitmapVector.removeAllElements();
    	bitmapVector = null;
    	bitmapVector = vector;
    }
    public Vector<MyBitmap> getVector()
    {
    	return bitmapVector;
    }
}
