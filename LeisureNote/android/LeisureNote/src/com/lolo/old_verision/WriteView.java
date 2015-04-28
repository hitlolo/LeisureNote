package com.lolo.old_verision;

import com.lolo.dev.R;
import com.lolo.dev.R.color;
import com.lolo.dev.R.drawable;
import com.lolo.tools.BytesBitmap;
import com.lolo.tools.FinalDefinition;
import com.lolo.tools.ImageAdapter;
import com.lolo.tools.MyBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class WriteView extends View implements Runnable
{
	
	private Context      myContext;
	private Path         drawPath;
	private Paint        drawPaint,
	                     drawPen;
	private Thread       drawThread;
	private Bitmap       drawBitmap;
	private Canvas       drawCanvas;
	private ImageAdapter drawAdapter;
	private Handler      activityHandler;
	private float        mX, mY;
	
	private int          timing = 0;
	private int          textNumCur = 0;
	private final int    textNumMax = 25;
	
	private boolean   TIME_COUNTING = false;
	private boolean   isDrawed      = false;
	private boolean   FADE          = true;
	private boolean   SHOW          = false;
	private boolean   VIEW_STATE    = SHOW;

	public WriteView(Context context,ImageAdapter adapter,Handler handler) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		myContext   = context;
		drawAdapter = adapter;
		activityHandler = handler;
		initComponents();
		
//		this.setBackgroundColor(myContext.getResources().getColor(R.color.transparent_black));
		this.setBackgroundDrawable(myContext.getResources().getDrawable(R.drawable.write_board_background));
		this.start();
	}

	private void initComponents()
	{
		drawPaint  = new Paint();
		drawPen    = new Paint();
		drawPath   = new Path();
		
    	drawBitmap = Bitmap.createBitmap(400,400, Bitmap.Config.ARGB_8888);  
    	drawBitmap.eraseColor(Color.TRANSPARENT);
    	drawCanvas = new Canvas(drawBitmap);
    	//设置图片背景为白色
    	drawCanvas.drawColor(Color.TRANSPARENT);

		
		//抗锯齿
		drawPen.setAntiAlias(true);
		//防止抖动
		drawPen.setDither(true);
		//设置画笔初始颜色为红色
		drawPen.setColor(myContext.getApplicationContext().getResources().getColor(R.color.peach_red));
		//设置初始化画笔类型为画笔
		drawPen.setStyle(Paint.Style.STROKE);
		//曲线折角处圆滑
		drawPen.setStrokeJoin(Paint.Join.ROUND);
		//曲线起始和结尾笔迹类型为圆形
		drawPen.setStrokeCap(Paint.Cap.ROUND);
		//设置画笔初始宽度为15像素
		drawPen.setStrokeWidth(15);
		
		//首先为画笔模式
		drawPaint  = drawPen;
		
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		int i = 0;
		while(!Thread.currentThread().isInterrupted())
		{
			try 
			{
				if(TIME_COUNTING)
				{
					i++;
					if(i == 10)
					{
						timing += 1;
						i = 0;
					}
					//等待2秒，无动作则取字
					if(timing == 2 && !isDrawed)
					{
						Message msg = new Message();
						msg.what = FinalDefinition.MESSAGE_GET_WORD;
						handler.sendMessage(msg);
					}
					//等待5秒，无动作则隐藏写字板
					else if(timing == 5)
					{
						Message msg = new Message();
						msg.what = FinalDefinition.MESSAGE_FADE;
						handler.sendMessage(msg);
						Message msg_tem = new Message();
						msg_tem.what = FinalDefinition.MESSAGE_FADE;
						activityHandler.sendMessage(msg_tem);	
					}
					
				}			
				
				Thread.sleep(100);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
			this.postInvalidate();
			
		}
		
	}
	
	public void start()
	{
		drawThread = new Thread(this);
		drawThread.start();

	}
	
	public void stop()
	{
		drawThread.interrupt();

	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
//		canvas.drawColor(Color.TRANSPARENT);
//		canvas.drawColor(myContext.getResources().getColor(R.color.transparent_black));

//      canvas.drawBitmap(drawBitmap, 0, 0, drawPaint);
		canvas.drawPath(drawPath, drawPaint);

		
	}
	
	
	 //画笔起点位置（上一个点）
	private void touch_start(float x, float y)
	{
		drawPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	//画笔移动偏移量位置（下一个点）
	private void touch_move(float x, float y)
	{

		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if(dx!=0 && dy!=0)
		{
			//设置两点间的中间点，(x+mX)/2贝塞尔曲线，似的曲线更圆滑
			drawPath.quadTo(mX, mY, (x+mX)/2, (y+mY)/2);
		}
		mX = x;
		mY = y;

	}

	//画笔画线结束时的处理，将所有路径压缩1/2后保存到临时图片中
	private void touch_up()
	{
		//一条线的路径
		drawCanvas.drawPath(drawPath, drawPaint);
		mX = 0;
		mY = 0;
//		drawPath.reset();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		
		if(VIEW_STATE == FADE)
		{
			Message msg_tem = new Message();
			msg_tem.what = FinalDefinition.MESSAGE_SHOW;
			activityHandler.sendMessage(msg_tem);
			VIEW_STATE = SHOW;
		}
		
		
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				
				timing = 0;
				TIME_COUNTING = true;
				isDrawed      = false;
					
				invalidate();
				break;
		}
		return true;
	}
	
	/*
     * @Name:setColor()
     * @Params:color
     * @return:void
     * @Function:set Paint Color
     */
	
	public void setColor(int color)
	{
		this.drawPen.setColor(color);
		drawPaint = drawPen;
	}
	/*
     * @Name:getBitmap()
     * @Params:null
     * @return:Bitmap
     * @Function:return is the stacks empty
     */
	
	public Bitmap getBitmap()
	{	
//		return drawBitmap;	
		return Bitmap.createScaledBitmap(drawBitmap, 100, 100, false);
	}
	
	/*
     * @Name:doInsert()
     * @Params:null
     * @return:void
     * @Function:insert a void Bitmap
     */	
	public void doInsert()
	{	
		if(textNumCur < textNumMax)
		{
			MyBitmap mybitmap = new MyBitmap(BytesBitmap.getBytes(getBitmap()),null);
		 	drawAdapter.insertIntoVector(mybitmap);
	    	drawBitmap.eraseColor(Color.TRANSPARENT);
	    	drawPath.reset();
	    	isDrawed = true;
	    	
	    	textNumCur += 1;
	    	doUpdateTextInfo();
		}
		else
		{
			drawBitmap.eraseColor(Color.TRANSPARENT);
	    	drawPath.reset();
	    	isDrawed = true;
			Toast.makeText(myContext, "已达到字数上限", Toast.LENGTH_SHORT).show();
		}
    
	}
	
	/*
     * @Name:doSpace()
     * @Params:null
     * @return:void
     * @Function:insert a void Bitmap
     */	
	public void doSpace()
	{	
		if(textNumCur < textNumMax)
		{
			drawBitmap.eraseColor(Color.TRANSPARENT);
			MyBitmap mybitmap = new MyBitmap(BytesBitmap.getBytes(getBitmap()),null);
		 	drawAdapter.insertIntoVector(mybitmap);
			this.drawAdapter.insertIntoVector(mybitmap);
			textNumCur += 1;
			doUpdateTextInfo();
		}
		else
		{
			Toast.makeText(myContext, "已达到字数上限", Toast.LENGTH_SHORT).show();
		}
		
	}
	/*
     * @Name:doBackSpace()
     * @Params:null
     * @return:void
     * @Function:delete a Bitmap in adapter
     */	
	public void doBackSpace()
	{	
		this.drawAdapter.deleteInVector();
		if(textNumCur > 0)
		{
			textNumCur -= 1;
		}		
		doUpdateTextInfo();
	}
	
	/*
     * @Name:doClean()
     * @Params:null
     * @return:void
     * @Function:clean Bitmap in adapter
     */	
	public void doClean()
	{	
		this.drawAdapter.clean();
		textNumCur = 0;
		doUpdateTextInfo();
	}
	
	/*
     * @Name:doEnter()
     * @Params:null
     * @return:void
     * @Function:switch to next column
     */	
	public void doEnter()
	{	
		int rowNum = drawAdapter.getSize()%7;
		int addNum = 7 - rowNum;
		
		if((addNum+textNumCur) < textNumMax)
		{
			for(int i = 0;i<addNum;i++)
			{
				this.doSpace();
			}
			textNumCur += addNum;
			doUpdateTextInfo();
		}
		else 
		{
			Toast.makeText(myContext, "回车后将超过字数上限", Toast.LENGTH_SHORT).show();
		};
		
	}
	
	/*
     * @Name:textNumToString()
     * @Params:null
     * @return:String
     * @Function:return current text num
     */	
	public String textNumToString()
	{	
		return textNumCur+"/"+textNumMax;
	}
	
	/*
     * @Name:doUpdateTextInfo()
     * @Params:null
     * @return:void
     * @Function:send a message to activtyHandler;
     */	
	public void doUpdateTextInfo()
	{	
		Message msg_tem = new Message();
		msg_tem.what = FinalDefinition.MESSAGE_UPDATE;
		activityHandler.sendMessage(msg_tem);
	}
	
	

	
	private Handler handler = new Handler() 
	{
	        @Override
	        public void handleMessage(Message msg) 
	        {
	                switch (msg.what) 
	                {
	                        case FinalDefinition.MESSAGE_GET_WORD:
	                        	doInsert();
	                   
	                            break;
	                        case FinalDefinition.MESSAGE_FADE:
	                        	timing = 0;
                                TIME_COUNTING = false;
                                VIEW_STATE    = FADE;
	                        	break;
	                        default:
	                            super.handleMessage(msg);
	                }
	        }
	};
	 
	/*
	 * @Name:doAlphaFadeAnimation()
	 * @Param:View
	 * @return:void
	 * @Function:do animation
	 */
	 private void doAlphaFadeAnimation(View view)
	 {
	    	AlphaAnimation myAnimation_Alpha;
	    	myAnimation_Alpha = new AlphaAnimation(1.0f,0.1f);
	    	myAnimation_Alpha.setDuration(2000);
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
		    myAnimation_Alpha =new AlphaAnimation(0.0f,1.0f);
		    myAnimation_Alpha.setFillBefore(true);
		    myAnimation_Alpha.setDuration(2000);
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
	  
	 
	    
	    
}
