package com.lolo.old_verision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lolo.dev.DrawView;
import com.lolo.dev.R;
import com.lolo.dev.R.color;
import com.lolo.dev.R.id;
import com.lolo.dev.R.layout;
import com.lolo.tools.FinalDefinition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DrawBoardActivity extends Activity 
{
	private FrameLayout    drawLayout;
	private RelativeLayout drawToolLayout,
	                       drawColorBar;
	
	private DrawView drawView;
	private Button   buttonBack,
	                 buttonForward,
	                 buttonColor,
	                 buttonPen,
	                 buttonEraser,
	                 buttonClean,
	                 buttonSave;
	
	private Button   buttonBlack,
	                 buttonGreen,
	                 buttonYellow,
	                 buttonRed,
	                 buttonPurple;
	
	private AlertDialog.Builder dialogIsExit;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.draw);
      
        LoadComponents();
        initButtonListeners();
        initDialogs();
    }
    
    /**
     * @Name:LoadComponents()
     * @Params:null
     * @return:void
     * @Function:do initialize
     */
    private void LoadComponents()
    {
    	drawLayout     = (FrameLayout)findViewById(R.id.frameDrawLayout);
    	drawToolLayout = (RelativeLayout)findViewById(R.id.drawToolsLayout);  
    	drawColorBar   = (RelativeLayout)findViewById(R.id.barColorChoose);
    	//添加画板View
    	drawView   = new DrawView(this);
    	LayoutParams layParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 800);
    	drawLayout.addView(drawView, layParam);
    	drawLayout.bringChildToFront(drawToolLayout);
    	
    	//find buttons
    	buttonBack    = (Button)findViewById(R.id.drawButton_back);
    	buttonForward = (Button)findViewById(R.id.drawButton_forward);
    	buttonColor   = (Button)findViewById(R.id.drawButton_color);
    	buttonPen     = (Button)findViewById(R.id.drawButton_pen);
    	buttonEraser  = (Button)findViewById(R.id.drawButton_eraser);
    	buttonClean   = (Button)findViewById(R.id.drawButton_clean);
    	buttonSave    = (Button)findViewById(R.id.drawButton_save);
    	
    	//color buttons
    	buttonBlack   = (Button)findViewById(R.id.colorButton_brown);
    	buttonGreen   = (Button)findViewById(R.id.colorButton_green);
    	buttonYellow  = (Button)findViewById(R.id.colorButton_yellow);
    	buttonRed     = (Button)findViewById(R.id.colorButton_red);
    	buttonPurple  = (Button)findViewById(R.id.colorButton_purple);
    	
    }
    
    /**
     * @Name:onBackPressed()
     * @Params:null
     * @return:void
     * @Function:over write the super class's onBackPresss, to stop the thread
     */
    public void onBackPressed()
    {
    	if( !drawView.isBitmapEmpty() )
    	{
    		dialogIsExit.show();
    	}
    	else
    	{
    		doExit();
    	}
    	
    }
    
    /**
     * 
     * 
     */
    public void onStop()
    {
    	drawView = null;
    	System.gc();
    	super.onStop();
    }
    
    
    /**
     * @Name:initButtonListeners()
     * @Params:null
     * @return:null
     * @Function:do Listeners initialize
     */
    private void initButtonListeners()
    {
    	OnClickListener onBackListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissAllViews();
				drawView.doBack();
			}
		};
		
		OnClickListener onForwardListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissAllViews();
				drawView.doForward();
			}
			
		};
		
		OnClickListener onColorListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				
				if( isViewVisible(drawColorBar) )
				{
					dismissColorBar();
				}
				else if ( !isViewVisible(drawColorBar) )
				{
					dismissAllViews();
					showColorBar();
				}
				
				
			}
			
		};
		
		OnClickListener onCleanListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissAllViews();
				drawView.doClean();
			}
		};
		
		OnClickListener onEraserListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissAllViews();
				drawView.doEraser();
			}
		};
		
		OnClickListener onPenListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissAllViews();
				if( drawView.getModle()==drawView.STATE_ERASER )
				{
					drawView.doPen();
				}
				else if( drawView.getModle()==drawView.STATE_PEN )
				{
//					drawView.doPenScale();
				}
			}
		};
		
		//colors
		OnClickListener onBlackListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.green_brown));
				dismissColorBar();
			}
		};
		
		OnClickListener onGreenListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.right_green));
				dismissColorBar();
			}
		};
		
		OnClickListener onYellowListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.yellow_orange));
				dismissColorBar();
			}
		};
		
		OnClickListener onRedListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.peach_red));
				dismissColorBar();
			}
		};
		OnClickListener onPurpleListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.purple));
				dismissColorBar();
			}
		};
		
		OnClickListener onSaveListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismissColorBar();
				doSave();
				doExit();
			}
		};
		
		
		
		//do add
		addButtonListener(buttonBack, onBackListener);
		addButtonListener(buttonForward,onForwardListener);
		addButtonListener(buttonColor,onColorListener);
		addButtonListener(buttonClean,onCleanListener);
		addButtonListener(buttonEraser,onEraserListener);
		addButtonListener(buttonPen,onPenListener);
		addButtonListener(buttonSave,onSaveListener);
		
		addButtonListener(buttonBlack,onBlackListener);
		addButtonListener(buttonGreen,onGreenListener);
		addButtonListener(buttonYellow,onYellowListener);
		addButtonListener(buttonRed,onRedListener);
		addButtonListener(buttonPurple,onPurpleListener);
		
    }
    
    
    /**
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
    
    /**
     * @Name:dismissAllViews()
     * @Param:null
     * @return:void
     * @Function:invisible all sub views
     */
    
    public void dismissAllViews()
    {
    	dismissColorBar();
    }
    
    /**
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
    
    /**
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
    /**
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
    
    /**
     * @name:dismissColorBar()
     * @Param:null
     * @return:void
     * @Function:do animation and dismiss ColorBar
     */
    private void dismissColorBar()
    {
    	if( isViewVisible(drawColorBar) )
    	{
    		doScaleSmallAnimation(drawColorBar);
        	drawColorBar.setVisibility(View.INVISIBLE);
    	}
    	
    }
    /**
     * @name:showColorBar()
     * @Param:null
     * @return:void
     * @Function:do animation and show ColorBar
     */
    private void showColorBar()
    {
    	doScaleBigAnimation(drawColorBar);
    	drawColorBar.setVisibility(View.VISIBLE);
    }
    
    /**
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
    			        drawView.stop();
    		        	DrawBoardActivity.super.onBackPressed();
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
    				drawView.stop();
		        	DrawBoardActivity.super.onBackPressed();
    			}
    		}
        	
        );
    }
    
    
    /**
     * @name:doSave()
     * @Param:null
     * @return:void
     * @Function:save the picture
     */ 
    private void doSave()
    {
    	String  temImageName   = "";
		File picPath = new File(FinalDefinition.BASE_IMAGE_PATH);
	    if(!picPath.exists())
	    {  
	    	picPath.mkdir();//没有目录先创建目录      	
        }    
		String createTime = System.currentTimeMillis()+"";
		temImageName = createTime + ".jpg";
		
		FileOutputStream fos = null;
		Bitmap drawBitmap = drawView.getBitmap();
		if( drawBitmap!=null )
		{
			try 
			{
				fos = new FileOutputStream(FinalDefinition.BASE_IMAGE_PATH + temImageName);
				drawBitmap.compress(CompressFormat.JPEG, 80, fos);
	            try 
	            {
					fos.close();
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fos = null;
//			drawBitmap.recycle();
			drawView.doClear();
		
			Intent intent = this.getIntent();
			intent.putExtra("filename", temImageName);
			this.setResult(RESULT_OK, intent);
		}
    }
    
    /**
     * @name:doExit()
     * @Param:null
     * @return:void
     * @Function:return to parent activity
     */ 
    private void doExit()
    {
		drawView.stop();
//		drawView = null;
    	super.onBackPressed();
    }
}
