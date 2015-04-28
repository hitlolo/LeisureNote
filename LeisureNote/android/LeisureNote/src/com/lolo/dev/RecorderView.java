package com.lolo.dev;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;


/*
 * Author:Lolo
 * 暂未用。
 */
public class RecorderView extends FrameLayout 
{
	private LinearLayout recorderLayout;
	private Context      myContext;
	private TextView     textTitle,
	                     textTime,
	                     textFormat;
	private Button       buttonPlay,
						 buttonRecorder;
	
	public RecorderView(Context context) 
	{
		super(context);
		myContext = context;
		// TODO Auto-generated constructor stub
		init();
	}
	
	public RecorderView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
	}
	
	private void init()
	{
		LayoutInflater inflater = LayoutInflater.from(myContext);
//		recorderLayout = (FrameLayout)inflater.inflate(R.layout.recorder, this,true).findViewById(R.id.recorderLayout);
//		recorderLayout.setFocusable(true);
//		recorderLayout.setClickable(true);
//		LayoutParams param = new LayoutParams(400, 250);
//		
//		
//		
//		this.initComponents(inflater);
//		this.setButtonListeners();
//		
//		this.addView(recorderLayout,param);
		
		inflater.inflate(R.layout.recorder, this,true);
		this.setFocusable(true);
		this.setClickable(true);
		this.initComponents(inflater);
		this.setButtonListeners();
	}
	
	private void initComponents(LayoutInflater inflater)
	{
		textTitle = (TextView)inflater.inflate(R.layout.recorder, null).findViewById(R.id.textRecordTitle);
		textTime  = (TextView)inflater.inflate(R.layout.recorder, null).findViewById(R.id.textRecordTime);
		textFormat= (TextView)inflater.inflate(R.layout.recorder, null).findViewById(R.id.textRecordFormat);
		
		buttonPlay     = (Button)inflater.inflate(R.layout.recorder, null).findViewById(R.id.buttonRecorderPlay);
		buttonRecorder = (Button)inflater.inflate(R.layout.recorder, null).findViewById(R.id.buttonRecorder);
	
//		recorderLayout = (LinearLayout)inflater.inflate(R.layout.recorder, null).findViewById(R.id.recorderLayout);
//		
//		recorderLayout.setFocusable(true);
//		recorderLayout.setClickable(true);
	}
	
	private void setButtonListeners()
	{
		String tag = buttonRecorder.getText().toString();
		Log.i("view", tag);
		buttonRecorder.setText("你滚");
		tag = buttonRecorder.getText().toString();
		Log.i("view", tag);
//		Log.i("view", recorderLayout.isClickable()+" "+recorderLayout.isFocusable());
		Log.i("view", buttonRecorder.isClickable()+" "+buttonRecorder.isFocusable());
		buttonPlay.setOnClickListener
		(
				new Button.OnClickListener()
				{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Log.i("view", "i am coming");
					}
					
				}
		);
		
		buttonRecorder.setOnClickListener
		(
				new Button.OnClickListener()
				{

					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						Log.i("view", "true");
						if( buttonRecorder.getText().toString().equals("按住录音"))
						{
							
							buttonRecorder.setText("完成");
						}
						else if( buttonRecorder.getText().toString().equals("完成"))
						{
							buttonRecorder.setText("按住录音");
						}
						
					}
					
				}
		);
	}
	
	  protected void onDraw(Canvas canvas)  
      {  
          super.onDraw(canvas); 
      }
	   
	
}
