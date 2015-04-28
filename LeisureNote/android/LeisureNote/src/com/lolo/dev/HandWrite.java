package com.lolo.dev;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.lolo.tools.FinalDefinition;
import com.lolo.tools.ImageAdapter;
import com.lolo.tools.MyBitmap;
import com.lolo.tools.MyMap;

public class HandWrite extends View implements Runnable
{
	
	private Context      myContext;
	private Path         drawPath;
	private Paint        drawPaint,
	                     drawPen;
	private Thread       drawThread;
	private Bitmap       drawBitmap;
	private Canvas       drawCanvas;
	private ImageAdapter drawAdapter;
//	private Handler      activityHandler;
	private MyMap        map;
	private float        mX, mY;
	
	private EditText     myText;
	private boolean      isRun;
	private boolean      isWrited;
	private int          startX = 0,
	                     startY = 0,
	                     endX   = 0,
	                     endY   = 0;
	
	public HandWrite(Context context,MyMap handMap,EditText text) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		myContext       = context;
//		activityHandler = handler;
		map             = handMap;
		myText          = text;
		initComponents();
		
		this.setBackgroundColor(getResources().getColor(R.color.cover));
//		this.start();
	}


	
	/**
	 * 
	 */
	private void initComponents()
	{
		drawPaint  = new Paint();
		drawPen    = new Paint();
		drawPath   = new Path();
		
    	drawBitmap = Bitmap.createBitmap(540,960, Bitmap.Config.ARGB_8888);  
    	drawBitmap.eraseColor(Color.TRANSPARENT);
    	drawCanvas = new Canvas(drawBitmap);
    	//设置图片背景为白色
    	drawCanvas.drawColor(Color.TRANSPARENT);

		
		//抗锯齿
		drawPen.setAntiAlias(true);
		//防止抖动
		drawPen.setDither(true);
		//设置画笔初始颜色为红色
		drawPen.setColor(myContext.getApplicationContext().getResources().getColor(R.color.blue_iron));
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
	
	/**
	 * 
	 */
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
//		canvas.drawColor(Color.BLACK);
//		canvas.drawColor(myContext.getResources().getColor(R.color.transparent_black));

//        canvas.drawBitmap(drawBitmap, 0, 0, drawPaint);
		canvas.drawPath(drawPath, drawPaint);

		
	}
	
	 //画笔起点位置（上一个点）
	private void touch_start(float x, float y)
	{
		isWrited = false;
		timing = 0;
		
		drawPath.moveTo(x, y);
		mX = x;
		mY = y;
		
		if(startX==0||startX >(int)x)
		{
			startX = (int)x;
		}
		if(startY==0||startY>(int)y)
		{
			startY = (int)y;
		}
		
		if(endX==0||endX < (int)x)
		{
			endX = (int)x;
		}
		if(endY==0||endY < (int)y)
		{
			endY = (int)y;
		}
	
		
	}

	//画笔移动偏移量位置（下一个点）
	private void touch_move(float x, float y)
	{
//		Log.i("new add",x+" "+y+" "+"move");
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if(dx!=0 && dy!=0)
		{
			//设置两点间的中间点，(x+mX)/2贝塞尔曲线，似的曲线更圆滑
			drawPath.quadTo(mX, mY, (x+mX)/2, (y+mY)/2);
		}
		mX = x;
		mY = y;
		
		if(startX==0||startX >(int)x)
		{
			startX = (int)x;
		}
		if(startY==0||startY>(int)y)
		{
			startY = (int)y;
		}
		
		if(endX==0||endX < (int)x)
		{
			endX = (int)x;
		}
		if(endY==0||endY < (int)y)
		{
			endY = (int)y;
		}

	}

	//画笔画线结束时的处理，将所有路径压缩1/2后保存到临时图片中
	private void touch_up()
	{
		//一条线的路径
		drawCanvas.drawPath(drawPath, drawPaint);
		mX = 0;
		mY = 0;
//		drawPath.reset();
		isWrited = true;
		

	}
	
	/**
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
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
				invalidate();
				break;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public void start()
	{
		drawThread = null;
		drawThread = new Thread(this);
		isRun      = true;
		drawThread.start();

	}
	/**
	 * 
	 */
	public void stop()
	{
		isRun = false;
		drawThread.interrupt();
		drawThread = null;

	}
	
	
	/**
	 * 
	 */
	int timing = 0;
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		while(isRun)
		{
			try 
			{
				if(isWrited)
				{

					timing++;
					if(timing >= 15)
					{
						timing = 0;
						Message msg = new Message();
						msg.what = FinalDefinition.MESSAGE_GET_WORD;
						myHandler.sendMessage(msg);
						isWrited = false;
					}
				}
				Thread.sleep(100);
			}	
			 catch (InterruptedException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.currentThread().interrupt();
			 }
			
		}
	}
	
	/**
	 * 
	 */
	private void doInsert()
	{
//		Log.i("new add",startX+" "+startY+" "+endX+" "+endY);
//		
		String creatTime = System.currentTimeMillis()+""; 
		String tempImageName = creatTime+".hand";
		Bitmap bitmap = this.getWordBitmap();
		this.cleanBitmap();
    	//do attach
		SpannableStringBuilder sbuilder = new SpannableStringBuilder(myText.getText());
//		int start = sbuilder.length();
		int start = myText.getSelectionEnd();
		int end   = start + tempImageName.length();
		sbuilder.insert(start, tempImageName);
		ImageSpan imSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);
		sbuilder.setSpan(imSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		map.put(tempImageName, new MyBitmap(bitmap));
	
		myText.setText(sbuilder);
		myText.setSelection(end);
		
	
		System.gc();  	
	}
	
	/**
     * @Name:getWordBitmap()
     * @Params:null
     * @return:Bitmap
     * @Function:return is the stacks empty
     */
	
	public Bitmap getWordBitmap()
	{	
		
		int offsetX = endX - startX;
		int offsetY = endY - startY;
		if(offsetX<100)
		{
			endX = endX + 100;
			if(endX >= drawBitmap.getWidth())
			{
				endX = drawBitmap.getWidth();
			}
			startX = startX - 100;
			if(startX<0)
			{
				startX = 0;
			}
		}
		if(offsetX>100)
		{
			startX = startX-10;
		}
		if(offsetY<100)
		{
//			endY = endY + 100;
			startY = startY - 100;
			if(startY<0)
			{
				startY = 0;
			}
		}
		if(offsetY>100)
		{
			startY = startY-10;
		}
		int width = (endX-startX)+20;
		if(startX + width>=drawBitmap.getWidth())
		{
			width = width - (startX+width - drawBitmap.getWidth());
		}
		int height = (endY-startY)+20;
		if(height>=drawBitmap.getHeight())
		{
			height = drawBitmap.getHeight();        
		}
		
//		Log.i("new add",startX+" "+startY+" "+width+" "+height);
		
		
		Bitmap bitmap =  Bitmap.createBitmap(drawBitmap, startX, startY,width ,height );
		startX = 0;
		startY = 0;
		endX   = 0;
		endY   = 0;
//		BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap b = Bitmap.
//        options.inSampleSize = 10;//缩小为1/10
//        options.inJustDecodeBounds = false;
		if(bitmap.getWidth()>=100||bitmap.getHeight()>=100)
		{
			return Bitmap.createScaledBitmap(bitmap, 100, 100, false);
		}
		else
			return bitmap;
//		return Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//		return bitmap;
		//		return Bitmap.createScaledBitmap(drawBitmap, 100, 100, false);
	}
	
	/**
     * @Name:getCleanBitmap()
     * @Params:null
     * @return:Bitmap
     * @Function:return is the stacks empty
     */
	
	public void cleanBitmap()
	{	
		drawBitmap.eraseColor(color.transparent);
		Rect rect = new Rect();
		this.getDrawingRect(rect);
		this.invalidate(0, 0, 540, 960);
		drawPath.reset();
	}
	
	/**
	 * 
	 */
	private Handler myHandler = new Handler() 
	{
	        @Override
	        public void handleMessage(Message msg) 
	        {
	                switch (msg.what) 
	                {
	                        case FinalDefinition.MESSAGE_GET_WORD:
	                        	doInsert();                   
	                            break;
	                        default:
	                            super.handleMessage(msg);
	                }
	        }
	};
}
