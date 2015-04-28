package com.lolo.tools;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter   
{
	 private Context       mContext;
	 private int           imageWidth,
	 					   imageHeight;
	 public  MyVector      bitmapVector;
	 public ImageAdapter(Context c,int width,int height) 
	 {
	     mContext = c;
	     bitmapVector = new MyVector();
	     imageWidth   = width;
	     imageHeight  = height;
	 }

	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return bitmapVector.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		 ImageView imageView;
	     if (convertView == null) 
	     {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	   	        imageView.setClickable(false);
		        imageView.setFocusable(false);
		        imageView.setEnabled(false);
		        imageView.setFocusableInTouchMode(false);
	            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE );
	            imageView.setPadding(2, 2, 2, 2);
	     }
	     else 
	     {
	            imageView = (ImageView) convertView;
	     }

	     imageView.setImageBitmap( BytesBitmap.getBitmap(((MyBitmap)bitmapVector.elementAt(position)).getBitmapBytes()) );

	     return imageView;
	}
	
	public void insertIntoVector(MyBitmap bitmap)
	{
		bitmapVector.add(bitmap);
		this.notifyDataSetChanged();
	}
	
	public void deleteInVector()
	{
		if(!bitmapVector.isEmpty())
		{
			bitmapVector.remove(bitmapVector.lastElement());
			this.notifyDataSetChanged();
		}
	}
	
	public void clean()
	{
		bitmapVector.removeAllElements();
		this.notifyDataSetChanged();
	}
	
	public int getSize()
	{
		return bitmapVector.size();
	}
	
	public void setVector(Vector<MyBitmap> vector)
	{
		bitmapVector.setVector(vector);
	}

	

}
