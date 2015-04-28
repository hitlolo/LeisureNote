package com.lolo.tools;

import android.R;
import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyImageButton extends FrameLayout {

	
	// ----------------private attribute----------------------------- 
	 
	private ImageView   myButtonImage = null; 
	private TextView    myButtonText  = null; 
	
	public MyImageButton(Context context, int imageResId, int textResId) 
	{ 
		super(context); 
	 
		myButtonImage = new ImageView(context); 
	    myButtonText  = new TextView(context); 
	 
	    setImageResource(imageResId); 
	    myButtonImage.setPadding(0, 0, 0, 0); 
	 
	    setText(textResId); 
	    setTextColor(0xFF000000); 
	    myButtonText.setPadding(0, 0, 0, 0); 
//	    setTextColor(0xFFFFFFFF); 
	    myButtonText.setGravity(Gravity.CENTER);
	 
	    //设置本布局的属性 
	    setClickable(true);  //可点击 
	    setFocusable(true);  //可聚焦 
	    myButtonImage.setClickable(true);
	    setBackgroundResource(android.R.color.transparent);   //布局才用普通按钮的背景 
//	    setOrientation(LinearLayout.VERTICAL);  //垂直布局 
	     
	    //首先添加Image，然后才添加Text 
	    //添加顺序将会影响布局效果 
	    addView(myButtonImage); 
	    addView(myButtonText); 
	  } 
	
	 public MyImageButton(Context context, int imageResId, String text) 
	 { 
		super(context); 
	 
		myButtonImage = new ImageView(context); 
	    myButtonText  = new TextView(context); 
	 
	    setImageResource(imageResId); 
	    myButtonImage.setPadding(0, 0, 0, 0);
	    myButtonImage.setScaleType(ImageView.ScaleType.CENTER);
	 
	    setText(text); 
	    
//	    setTextColor(0xFF000000); 
	    setTextColor(0xFFFFFFFF);
	    myButtonText.setPadding(0, 0, 0, 0); 
//	    myButtonText.setGravity(Gravity.CENTER|Gravity.BOTTOM);
	 
	    //设置本布局的属性 
	    setClickable(true);  //可点击 
	    setFocusable(true);  //可聚焦 
	    myButtonImage.setClickable(true);
	    setBackgroundResource(android.R.color.transparent);  //布局才用普通按钮的背景 
//	    setBackgroundResource(android.R.color.background_dark);
//	    setOrientation(LinearLayout.VERTICAL);  //垂直布局 
	     
	    //首先添加Image，然后才添加Text 
	    //添加顺序将会影响布局效果 
	    LayoutParams  lpi = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL|Gravity.TOP);
	    LayoutParams  lpt = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
	    addView(myButtonImage,lpi); 
	    addView(myButtonText,lpt); 
	  } 
	 
	  // ----------------public method----------------------------- 
	  /* 
	   * setImageResource方法 
	   */ 
	  public void setImageResource(int resId) 
	  { 
	    myButtonImage.setBackgroundResource(resId);
	  } 
	 
	  /* 
	   * setText方法 
	   */ 
	  public void setText(int resId) 
	  { 
	    myButtonText.setText(resId); 
	  } 
	  
	  public void setText(String text) 
	  { 
	    myButtonText.setText(text); 
	  } 
	  
	  public void setText(CharSequence buttonText) 
	  { 
	    myButtonText.setText(buttonText); 
	  } 
	 
	  /* 
	   * setTextColor方法 
	   */ 
	  public void setTextColor(int color) 
	  { 
	    myButtonText.setTextColor(color); 
	  
	  }
	  public ImageView getImageButton()
	  {
		  return myButtonImage;
	  }
	  
	  public void setOnClickListener(OnClickListener l)
	  {
		  myButtonImage.setOnClickListener(l);
	  }
	
	 
}
