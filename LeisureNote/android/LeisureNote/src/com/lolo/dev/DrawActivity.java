package com.lolo.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lolo.tools.FinalDefinition;

public class DrawActivity extends Activity 
{
	private FrameLayout    drawLayout;
	private DrawView       drawView;
	private PopupWindow    winTools,
						   winColor;
	
	private ToggleButton   buttonTools;
	
	
	private Button         buttonBack,
						   buttonForward,
						   buttonColor,
						   buttonPen,
						   buttonEraser,
						   buttonClean,
						   buttonCancel,
						   buttonSave;

	private Button         buttonBlack,
						   buttonGreen,
						   buttonYellow,
						   buttonRed,
					       buttonPurple;
	
	private AlertDialog.Builder 
						   dialogIsExit;
	
	/**
	 * onCreate
	 */
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.draw);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.tools_bar);
        
        initComponents();
        initPopupWindows();
        initButtonListeners();
        initDialogs();
	}
	/**
	 * onStop
	 */
	public void onStop()
	{
		doRelease();
		System.gc();
		super.onStop();
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
     * @name:doExit()
     * @Param:null
     * @return:void
     * @Function:return to parent activity
     */ 
    private void doExit()
    {	
    	this.drawLayout.removeAllViews();
    	drawView.doDealloc();
    	doRelease();
    	System.gc();
    	super.onBackPressed();
    }
	
	 /**
     * @Name:doRelease()
     * @Params:null
     * @return:void
     * @Function:do release
     */
	public void doRelease()
	{
		drawLayout  = null;
		drawView    = null;
		winTools    = null;
		buttonTools = null;

		winColor    = null;
		
		buttonTools = null;
		
		
		buttonBack   = null;
		buttonForward= null;
		buttonColor  = null;
		buttonPen    = null;
		buttonEraser = null;
		buttonClean  = null;
		buttonCancel = null;
		buttonSave   = null;

		buttonBlack  = null;
		buttonGreen  = null;
		buttonYellow = null;
		buttonRed    = null;
		buttonPurple = null;
		
		dialogIsExit = null;
	}
	 /**
     * @Name:initComponents()
     * @Params:null
     * @return:void
     * @Function:do initialize
     */
    private void initComponents()
    {
    	drawLayout     = (FrameLayout)findViewById(R.id.frameDrawLayout);
    	//添加画板View
    	drawView   = new DrawView(this);
    	LayoutParams layParam = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    	drawLayout.addView(drawView, layParam);
//    	drawLayout.bringChildToFront(drawView);
    	
    	buttonTools = (ToggleButton)findViewById(R.id.button_tools);    
    	
    	buttonSave  = (Button)findViewById(R.id.button_save);
   
    }
    
    /**
     * @Name:initPopupWindows()
     * @Params:null
     * @return:void
     * @Function:do initialize
     */
    
    private void initPopupWindows()
    {
    	 // 加载popupWindow的布局文件  
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tools, null);  
    	winTools = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, false);  
    	winTools.setFocusable(true);
    	winTools.setTouchable(true);
    	BitmapDrawable bitmap = new BitmapDrawable();
    	winTools.setBackgroundDrawable(bitmap);//add back
    	winTools.setAnimationStyle(R.style.PopupToolsAnimation); 
    	
    	 
        PopupWindow.OnDismissListener dm_Listener = new PopupWindow.OnDismissListener() 
        {
			
			@Override
			public void onDismiss() 
			{
				// TODO Auto-generated method stub
			
				buttonTools.setChecked(false);
				buttonTools.setBackgroundDrawable(getResources().getDrawable(buttonTools.isChecked()?R.drawable.tools_normal:R.drawable.tools_checked));
			}
		};
		
		winTools.setOnDismissListener(dm_Listener);
    	
    	//find buttons
    	buttonBack    = (Button)contentView.findViewById(R.id.drawButton_back);
    	buttonForward = (Button)contentView.findViewById(R.id.drawButton_forward);
    	buttonColor   = (Button)contentView.findViewById(R.id.drawButton_color);
    	buttonPen     = (Button)contentView.findViewById(R.id.drawButton_pen);
    	buttonEraser  = (Button)contentView.findViewById(R.id.drawButton_eraser);
    	buttonClean   = (Button)contentView.findViewById(R.id.drawButton_clean);
    	buttonCancel  = (Button)contentView.findViewById(R.id.drawButton_cancel);
//    	buttonSave    = (Button)contentView.findViewById(R.id.drawButton_save);
   
    	/**
    	 * --------------------------------------------------------------------
    	 */
    	View colorView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.color_bar, null);  
    	winColor = new PopupWindow(colorView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, false);  
    	winColor.setFocusable(true);
    	winColor.setTouchable(true);
    	winColor.setBackgroundDrawable(bitmap);//add back
    	winColor.setAnimationStyle(R.style.PopupAttachAnimation); 
    	
    	//color buttons
    	buttonBlack   = (Button)colorView.findViewById(R.id.colorButton_brown);
    	buttonGreen   = (Button)colorView.findViewById(R.id.colorButton_green);
    	buttonYellow  = (Button)colorView.findViewById(R.id.colorButton_yellow);
    	buttonRed     = (Button)colorView.findViewById(R.id.colorButton_red);
    	buttonPurple  = (Button)colorView.findViewById(R.id.colorButton_purple);
    }
    
    
    /**
     * @Name:initButtonListeners()
     * @Params:null
     * @return:void
     * @Function:do initialize
     */
    
    private void initButtonListeners()
    {
    	this.buttonTools.setOnClickListener
    	(
    			new ToggleButton.OnClickListener()
    			{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
//						winTools.showAtLocation(drawLayout, Gravity.RIGHT, 0, 0);
						buttonTools.setChecked(true);
						buttonTools.setBackgroundDrawable(getResources().getDrawable(buttonTools.isChecked()?R.drawable.tools_normal:R.drawable.tools_checked));
						winTools.showAsDropDown(buttonTools);
					}
    				
    			}
    	);
    	
    	this.buttonSave.setOnClickListener
    	(
    			new Button.OnClickListener()
    			{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						doSave();
						doExit();
					}
    				
    			}
    	);
    	
    	/**
    	 * ===============================================================
    	 */
    	OnClickListener onBackListener = new Button.OnClickListener()
    	{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.doBack();
			}
		};
		
		OnClickListener onForwardListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.doForward();
			}
			
		};
		
		OnClickListener onColorListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				winColor.showAtLocation(drawLayout, Gravity.RIGHT|Gravity.CENTER_HORIZONTAL, 180, 0);		
			}
			
		};
		
		OnClickListener onCleanListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.doClean();
			}
		};
		
		OnClickListener onEraserListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.doEraser();
			}
		};
		
		OnClickListener onPenListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
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
		
		OnClickListener onCancelListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				winTools.dismiss();
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
				winColor.dismiss();
				winTools.dismiss();
			}
		};
		
		OnClickListener onGreenListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.right_green));
				winColor.dismiss();
				winTools.dismiss();
			}
		};
		
		OnClickListener onYellowListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.yellow_orange));
				winColor.dismiss();
				winTools.dismiss();
			}
		};
		
		OnClickListener onRedListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.peach_red));
				winColor.dismiss();
				winTools.dismiss();
			}
		};
		OnClickListener onPurpleListener = new Button.OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				drawView.setColor(getApplicationContext().getResources().getColor(R.color.purple));
				winColor.dismiss();
				winTools.dismiss();
			}
		};
		
		//do add
		addButtonListener(buttonBack, onBackListener);
		addButtonListener(buttonForward,onForwardListener);
		addButtonListener(buttonColor,onColorListener);
		addButtonListener(buttonClean,onCleanListener);
		addButtonListener(buttonEraser,onEraserListener);
		addButtonListener(buttonPen,onPenListener);
		addButtonListener(buttonCancel,onCancelListener);
		/**
		 * ==========================================
		 */
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
    		        	DrawActivity.super.onBackPressed();
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
		        	DrawActivity.super.onBackPressed();
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

			
			Intent intent = this.getIntent();
			intent.putExtra("filename", temImageName);
			this.setResult(RESULT_OK, intent);
			
			drawView.stop();
			drawView.doClear();
//			drawView.doDealloc();
			drawBitmap.recycle();
			
			doRelease();
			System.gc();
		}
    }
}
