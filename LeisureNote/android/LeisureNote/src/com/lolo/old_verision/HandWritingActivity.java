package com.lolo.old_verision;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lolo.dev.R;
import com.lolo.dev.R.color;
import com.lolo.dev.R.id;
import com.lolo.dev.R.layout;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.ImageAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class HandWritingActivity extends Activity 
{
	private FrameLayout         layoutMain;
    private LinearLayout		layoutBoard;
	private RelativeLayout      layoutTools,
								layoutColorBar;
	private WriteView           writeView;
	private AlertDialog.Builder dialogIsExit;
	
	private GridView            writeBoard;
	private ImageAdapter        gridAdapter;
	
	
	private Button              buttonSave,
								buttonColor,
								buttonSpace,
								buttonDelete,
								buttonClean,
								buttonEnter,
								
								buttonBlack,
								buttonGreen,
								buttonYellow,
								buttonRed,
								buttonPurple;
	private TextView            textLimit;
	

	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.handwriting);    
        
        initComponents();
        
        initDialogs();
        initButtonListeners();
        
        
    }
    
    
    /*
     * @Name:initComponents()
     * @Params:void
     * @Return:void
     * @Function:do init
     */
    private void initComponents()
    {
    	layoutMain     = (FrameLayout)findViewById(R.id.frameHandwritingLayout);
    	layoutBoard    = (LinearLayout)findViewById(R.id.frameForWriteboard);
    	layoutTools    = (RelativeLayout)findViewById(R.id.writeToolsLayout);
    	layoutColorBar = (RelativeLayout)findViewById(R.id.barColorChoose);
    	writeBoard     = (GridView)findViewById(R.id.writeGrid);
    	gridAdapter    = new ImageAdapter(this,100,100);
    	writeView      = new WriteView(this,gridAdapter,handler);
    	writeBoard.setAdapter(gridAdapter);
    	
    	LayoutParams layParam = new FrameLayout.LayoutParams(400,400,Gravity.CENTER|Gravity.TOP);
//    	layoutMain.addView(writeView, layParam);
//    	layoutMain.bringChildToFront(layoutTools);
    	layoutBoard.addView(writeView, layParam);
    	layoutMain.bringChildToFront(layoutTools);
    	 	  	
    	//buttons
    	buttonSave    = (Button)findViewById(R.id.writeButton_save);
    	buttonColor   = (Button)findViewById(R.id.writeButton_color);
    	buttonSpace   = (Button)findViewById(R.id.writeButton_space);
    	buttonDelete  = (Button)findViewById(R.id.writeButton_backspace);
    	buttonClean   = (Button)findViewById(R.id.writeButton_clean);
    	buttonEnter   = (Button)findViewById(R.id.writeButton_enter);
    	//color buttons
    	buttonBlack   = (Button)findViewById(R.id.colorButton_brown);
    	buttonGreen   = (Button)findViewById(R.id.colorButton_green);
    	buttonYellow  = (Button)findViewById(R.id.colorButton_yellow);
    	buttonRed     = (Button)findViewById(R.id.colorButton_red);
    	buttonPurple  = (Button)findViewById(R.id.colorButton_purple);
    	
    	//textView
    	textLimit     = (TextView)findViewById(R.id.handTextLimit);
    }
    
    /*
     * @name:initButtonListeners()
     * @Param:null
     * @return:void
     * @Function:do button Listeners initialize
     */   
    private void initButtonListeners()
    {
    	OnClickListener onSaveListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				doSave();
				doExit();
			}
		};
		
		OnClickListener onColorListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				
				if( isViewVisible(layoutColorBar) )
				{
					dismissColorBar();
				}
				else if ( !isViewVisible(layoutColorBar) )
				{
					showColorBar();
				}
				
				
			}
			
		};
		
		OnClickListener onSpaceListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.doSpace();
			}
		};
		
		OnClickListener onDeleteListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.doBackSpace();
			}
		};
		
		OnClickListener onCleanListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.doClean();
			}
		};
		
		OnClickListener onEnterListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.doEnter();
			}
		};
		
		
		
		//colors
		OnClickListener onBlackListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.setColor(getApplicationContext().getResources().getColor(R.color.green_brown));
				dismissColorBar();
			}
		};
		
		OnClickListener onGreenListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.setColor(getApplicationContext().getResources().getColor(R.color.right_green));
				dismissColorBar();
			}
		};
		
		OnClickListener onYellowListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.setColor(getApplicationContext().getResources().getColor(R.color.yellow_orange));
				dismissColorBar();
			}
		};
		
		OnClickListener onRedListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.setColor(getApplicationContext().getResources().getColor(R.color.peach_red));
				dismissColorBar();
			}
		};
		OnClickListener onPurpleListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				writeView.setColor(getApplicationContext().getResources().getColor(R.color.purple));
				dismissColorBar();
			}
		};
		
		
		addButtonListener(buttonSave,onSaveListener);
		addButtonListener(buttonColor,onColorListener);
		addButtonListener(buttonSpace,onSpaceListener);
		addButtonListener(buttonDelete,onDeleteListener);
		addButtonListener(buttonClean,onCleanListener);
		addButtonListener(buttonEnter,onEnterListener);
		//colors
		addButtonListener(buttonBlack,onBlackListener);
		addButtonListener(buttonGreen,onGreenListener);
		addButtonListener(buttonYellow,onYellowListener);
		addButtonListener(buttonRed,onRedListener);
		addButtonListener(buttonPurple,onPurpleListener);
    	
    }
    
    /*
     * @Name:addButtonListener()
     * @Param:Button
     * @Param:Listener
     * @return:void
     * @Function:do add listeners
     */
    private void addButtonListener(Button button, OnClickListener listener)
    {
    	button.setOnClickListener(listener);
    }
    
    /*
     * @Name:onBackPressed()
     * @Params:null
     * @return:void
     * @Function:over write the super class's onBackPresss, to stop the thread
     */
    public void onBackPressed()
    {
    	if( gridAdapter.getSize()!=0 )
    	{
    		dialogIsExit.show();
    	}
    	else
    	{
    		writeView.stop();
        	super.onBackPressed();
    	}
		
    	
    }
    
    /*
     * @name:initDialogs()
     * @Param:null
     * @return:void
     * @Function:do dialog initialize
     */   
    private void initDialogs()
    {
    	dialogIsExit = new AlertDialog.Builder(this) 
        .setTitle("提示") 
        .setMessage("图片尚未保存，是否退出前保存？") 
        .setPositiveButton
        (
        	"确定", new DialogInterface.OnClickListener()
            { 
    			            @Override 
    			public void onClick(DialogInterface arg0, int arg1) 
    			{ 
    			// TODO Auto-generated method stub 
    			        doSave();
    			        writeView.stop();
    		        	HandWritingActivity.super.onBackPressed();
    			} 
    		}
        	
        )
        .setNegativeButton
        (
        	"取消", new DialogInterface.OnClickListener() 
        	{
    			
    			@Override
    			public void onClick(DialogInterface arg0, int arg1)
    			{
    				// TODO Auto-generated method stub
    				arg0.dismiss();
    				writeView.stop();
    				HandWritingActivity.super.onBackPressed();
    			}
    		}
        	
        );
    	
    }
    
    
    public BaseAdapter getAdapter()
    {
    	return gridAdapter;
    }
     
    /*
	 * @Name:doAlphaFadeAnimation()
	 * @Param:View
	 * @return:void
	 * @Function:do animation
	 */
	 private void doAlphaFadeAnimation(View view)
	 {
	    	AlphaAnimation myAnimation_Alpha;
	    	myAnimation_Alpha =new AlphaAnimation(1.0f,0.01f);
	    	myAnimation_Alpha.setDuration(2000);
	    	myAnimation_Alpha.setFillAfter(true);
	    	myAnimation_Alpha.setAnimationListener
	    	(
	    			new AnimationListener() 
		    		{
			    		@Override
			    		public void onAnimationStart(Animation animation) {
			    		Log.i("view", "start");
			    		}
			    		
			    		@Override
			    		public void onAnimationRepeat(Animation animation) {
			    			Log.i("view", "repeat");
			    		}
			    		
			    		@Override
			    		public void onAnimationEnd(Animation animation) {
			    			Log.i("view", "end");
			    		//从viewGroup中移除imageView
			    		
			    		}
		    		}
	    	);
			view.startAnimation(myAnimation_Alpha);
	 }
		/*
		 * @Name:doAlphaShowAnimation()
		 * @Param:View
		 * @return:void
		 * @Function:do animation
		 */
	 private void doAlphaShowAnimation(View view)
	 {
		 AlphaAnimation myAnimation_Alpha;
		    myAnimation_Alpha =new AlphaAnimation(0.01f,1.0f);
		    myAnimation_Alpha.setFillBefore(true);
		    myAnimation_Alpha.setDuration(1000);
	    	myAnimation_Alpha.setAnimationListener
	    	(
	    			new AnimationListener() 
		    		{
			    		@Override
			    		public void onAnimationStart(Animation animation) {
			    		Log.i("view", "start show");
			    		}
			    		
			    		@Override
			    		public void onAnimationRepeat(Animation animation) {
			    			Log.i("view", "repeat");
			    		}
			    		
			    		@Override
			    		public void onAnimationEnd(Animation animation) {
			    			Log.i("view", "end show");
			    		//从viewGroup中移除imageView
			    		
			    		}
		    		}
	    	);
			view.startAnimation(myAnimation_Alpha);
				
	 }	 
	 
	 
	private Handler handler = new Handler() 
	 {
	        @Override
	        public void handleMessage(Message msg) 
	        {
	                switch (msg.what) 
	                {	                  
	                        case FinalDefinition.MESSAGE_FADE:	                        	                        	
	                        	doAlphaFadeAnimation(writeView);
	                        	break;
	                        case FinalDefinition.MESSAGE_SHOW:	                        	                        	
	                        	doAlphaShowAnimation(writeView);
	                        	break;
	                        case FinalDefinition.MESSAGE_UPDATE:
	                        	textLimit.setText(writeView.textNumToString());
	                        	break;
	                        default:
	                            super.handleMessage(msg);
	                }
	        }
	 };
	 
	    /*
	     * @Name:isViewVisible()
	     * @Param:View
	     * @return:boolean
	     * @Function:is View Visible
	     */
	    private boolean isViewVisible(View view)
	    {
	    	if( view.getVisibility()==View.VISIBLE)
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return false;
	    	}
	    }
	    
	    /*
	     * @doScaleSmallAnimation()
	     * @Param:View
	     * @return:void
	     * @Function:do animation
	     */
	    private void doScaleSmallAnimation(View view)
	    {
	    	ScaleAnimation myAnimation_Scale;
			myAnimation_Scale =new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
		             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			myAnimation_Scale.setDuration(200);
			view.setAnimation(myAnimation_Scale);
			view.startAnimation(myAnimation_Scale);
			
	    }
	    /*
	     * @doScaleBigAnimation()
	     * @Param:View
	     * @return:void
	     * @Function:do animation
	     */
	    private void doScaleBigAnimation(View view)
	    {
	    	AnimationSet animationSet = new AnimationSet(true);
	    	ScaleAnimation myAnimation_Scale;
			myAnimation_Scale =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f,
		             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			myAnimation_Scale.setDuration(200);
			
			animationSet.addAnimation(myAnimation_Scale);
		
			
			myAnimation_Scale =new ScaleAnimation(1.4f, 0.8f, 1.4f, 0.8f,
		             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			myAnimation_Scale.setDuration(200);
			
			animationSet.addAnimation(myAnimation_Scale);
			
			myAnimation_Scale =new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f,
		             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			myAnimation_Scale.setDuration(200);
			animationSet.addAnimation(myAnimation_Scale);
			
			view.setAnimation(animationSet);
			view.startAnimation(animationSet);
	    }
	    
	    /*
	     * @name:dismissColorBar()
	     * @Param:null
	     * @return:void
	     * @Function:do animation and dismiss ColorBar
	     */
	    private void dismissColorBar()
	    {
	    	if( isViewVisible(layoutColorBar) )
	    	{
	    		doScaleSmallAnimation(layoutColorBar);
	    		layoutColorBar.setVisibility(View.GONE);
	    	}
	    	
	    }
	    /*
	     * @name:showColorBar()
	     * @Param:null
	     * @return:void
	     * @Function:do animation and show ColorBar
	     */
	    private void showColorBar()
	    {
	    	doScaleBigAnimation(layoutColorBar);
	    	layoutColorBar.setVisibility(View.VISIBLE);
	    }
	    
	    /*
	     * @name:doSave()
	     * @Param:null
	     * @return:void
	     * @Function:save the picture
	     */ 
	    private void doSave()
	    {
//				drawView.doClear();
			
				Intent intent = this.getIntent();
//				Bundle mBundle = new Bundle();  
//			    mBundle.putSerializable("vector",gridAdapter.bitmapVector);
				Log.e("view",gridAdapter.getSize()+"");
			    intent.putExtra("vector", gridAdapter.bitmapVector);
//			    intent.putExtras(mBundle);  
//				intent.putExtra("vector",gridAdapter.bitmapVector);

				this.setResult(RESULT_OK, intent);
			
	    }
	    
	    
	    /*
	     * @name:doExit()
	     * @Param:null
	     * @return:void
	     * @Function:exit the activity
	     */ 
	    private void doExit()
	    {
//				drawView.doClear();
	    	writeView.stop();
        	super.onBackPressed();
	    }
}
