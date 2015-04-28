package com.lolo.dev;

import java.util.Iterator;

import com.lolo.tools.DrawObject;
import com.lolo.tools.DrawStack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View implements Runnable
{
	private Context myContext;
	private Path    drawPath;
	private Paint   drawPaint,
	                drawEraser,
	                drawPen;
	private Thread  drawThread;

	private Canvas  drawCanvas;
	private float   mX, mY;
	
	private DrawStack backStack,
	                  forwardStack,
	                  backStackEraser,
	                  forwardStackEraser;
	
	public final int STATE_PEN    = 0,
	                 STATE_ERASER = 1;
	private int      StateModle;
	
	
	private Bitmap   drawBitmap;
	private Bitmap   bitmap;
	private boolean  isRun = false;
	
	public DrawView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub		
		myContext = context;		
		backStack = new DrawStack();
		forwardStack = new DrawStack();
		backStackEraser = new DrawStack();
		forwardStackEraser = new DrawStack();
		
		
		
		initComponents();
		this.start();
	}
	
	private void initComponents()
	{
		drawPaint  = new Paint();
		drawEraser = new Paint();
		drawPen    = new Paint();
		drawPath   = new Path();
		
		//background
//		BitmapFactory.Options options = new BitmapFactory.Options();
		
		Bitmap tembitmap = BitmapFactory.decodeResource(getResources(), R.drawable.draw_background);
    	bitmap     = Bitmap.createBitmap(tembitmap, 0, 0, 540, 870);
    	tembitmap.recycle();
    	
		drawBitmap = Bitmap.createBitmap(540,870, Bitmap.Config.ARGB_8888);
//		drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//		bitmap.recycle();
//		bitmap = null;
		
    	drawCanvas = new Canvas(drawBitmap);
//		drawCanvas = new Canvas();
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
		//设置画笔初始宽度为6像素
		drawPen.setStrokeWidth(10);
		drawPen.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));
		
		//抗锯齿
		drawEraser.setAntiAlias(true);
		//防止抖动
		drawEraser.setDither(true);
		//设置初始化画笔类型为画笔
		drawEraser.setStyle(Paint.Style.STROKE);
		//曲线折角处圆滑
		drawEraser.setStrokeJoin(Paint.Join.ROUND);
		//曲线起始和结尾笔迹类型为圆形
		drawEraser.setStrokeCap(Paint.Cap.ROUND);
		//设置画笔初始宽度为6像素
		drawEraser.setColor(Color.WHITE);
		drawEraser.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));
		drawEraser.setStrokeWidth(10);
//		drawEraser.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));
		
		//首先为画笔模式
		drawPaint  = drawPen;
		StateModle = STATE_PEN; 
		
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		while(isRun)
		{
			try 
			{
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
		isRun = true;
		drawThread.start();
	}
	
	public void stop()
	{
		isRun = false;
//		drawThread.interrupt();
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
//		canvas.drawColor(Color.TRANSPARENT);
		
		canvas.drawBitmap(bitmap,0,0,drawPen);//背景
//		drawPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));

		
        canvas.drawBitmap(drawBitmap, 0, 0, drawPen);
//		canvas.drawBitmap(bitmap, 0, 0,drawPaint);
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
			//设置亮点间的中间点，(x+mX)/2贝塞尔曲线，似的曲线更圆滑
			drawPath.quadTo(mX, mY, (x+mX)/2, (y+mY)/2);
		}
		mX = x;
		mY = y;

	}

	//画笔画线结束时的处理，将所有路径压缩1/2后保存到临时图片中
	private void touch_up()
	{
		//一条线的路径

		
//		drawCanvas.drawPath(drawPath, drawPaint);
//		drawPath.lineTo(400, 600);
//		drawCanvas.drawPath(drawPath, drawPaint);
//		drawCanvas.drawCircle(100, 100, 8, drawPaint);
//		Path newPath = new Path();
//		newPath      = drawPath;
		Path  temPath  = new Path(drawPath);
		Paint temPaint = new Paint(drawPaint); 
		DrawObject object = new DrawObject(temPaint,temPath);
		
		backStack.push(object);
//		switch(StateModle)
//		{
//		case STATE_PEN:
//			backStack.push(object);
//			break;
//		case STATE_ERASER:
//			backStackEraser.push(object);
//			break;
//		}
		
		
		for(Iterator<DrawObject> i = backStack.iterator();i.hasNext();)
		{
	        DrawObject draw_object = (DrawObject)i.next();
			drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());			
		}
//		for(Iterator<DrawObject> i = backStackEraser.iterator();i.hasNext();)
//		{
//	        DrawObject draw_object = (DrawObject)i.next();
//			drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());
//			
//		}
		
		
		drawPath.reset();
		
	}
	
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
     * @Name:isBackable()
     * @Params:null
     * @return:boolean
     * @Function:check if backStack empty
     */
	
	public boolean isBackable()
	{
//		switch(StateModle)
//		{
//		case STATE_PEN:
//			return ( !backStack.isEmpty() );
//		case STATE_ERASER:
//			return ( !backStackEraser.isEmpty() );
//		default:
//			return false;
//		}		
		
		return !backStack.isEmpty();
	}
    /**
     * @Name:isForwardable()
     * @Params:null
     * @return:boolean
     * @Function:check if ForwarStack empty
     */
	
	public boolean isForwardable()
	{
//		switch(StateModle)
//		{
//		case STATE_PEN:
//			return ( !forwardStack.isEmpty() );
//		case STATE_ERASER:
//			return ( !forwardStackEraser.isEmpty() );
//		default:
//			return false;
//		}
		return !forwardStack.isEmpty();
	}

    /**
     * @Name:doback()
     * @Params:null
     * @return:void
     * @Function:do back, push path,paint into forwarStack
     */
	
	public void doBack()
	{
		if( isBackable() )
		{
			
			forwardStack.push( backStack.pop() );
//			switch(StateModle)
//			{
//			case STATE_PEN:
//				forwardStack.push( backStack.pop() );
//				break;
//			case STATE_ERASER:
//				forwardStackEraser.push( backStackEraser.pop() );
//				break;
//			}
//			forwardStack.push( backStack.pop() );
			drawBitmap.eraseColor(Color.TRANSPARENT);
//			drawCanvas.drawColor(Color.TRANSPARENT);
			
			for(Iterator<DrawObject> i = backStack.iterator();i.hasNext();)
			{
		        DrawObject draw_object = (DrawObject)i.next();
				drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());				
			}
//			for(Iterator<DrawObject> i = backStackEraser.iterator();i.hasNext();)
//			{
//		        DrawObject draw_object = (DrawObject)i.next();
//				drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());				
//			}
			this.invalidate();
		}
		
	}
    /**
     * @Name:doforward()
     * @Params:null
     * @return:void
     * @Function:do forward
     */
	
	public void doForward()
	{
		if( isForwardable() )
		{
			
			backStack.push( forwardStack.pop() );
//			switch(StateModle)
//			{
//			case STATE_PEN:
//				backStack.push( forwardStack.pop() );
//				break;
//			case STATE_ERASER:
//				backStackEraser.push( forwardStackEraser.pop() );
//				break;
//			}
//			backStack.push( forwardStack.pop() );
		
//			drawCanvas.drawColor(Color.TRANSPARENT);
			drawBitmap.eraseColor(Color.TRANSPARENT);
			
			for(Iterator<DrawObject> i = backStack.iterator();i.hasNext();)
			{
		        DrawObject draw_object = (DrawObject)i.next();
				drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());
				
			}
//			for(Iterator<DrawObject> i = backStackEraser.iterator();i.hasNext();)
//			{
//		        DrawObject draw_object = (DrawObject)i.next();
//				drawCanvas.drawPath(draw_object.getMyPath(), draw_object.getMyPaint());
//				
//			}
			this.invalidate();
		}
		
	}
	
    /**
     * @Name:doClean()
     * @Params:null
     * @return:void
     * @Function:do clean the drawBitmap
     */	
	public void doClean()
	{
//		drawCanvas.drawColor(Color.TRANSPARENT);
		drawBitmap.eraseColor(Color.TRANSPARENT);
		//clean the stacks
		backStack.clear();
		forwardStack.clear();
		backStackEraser.clear();
		forwardStackEraser.clear();
		invalidate();
	}
	
    /**
     * @Name:doEraser() 
     * @Params:null
     * @return:void
     * @Function:switch to eraser
     */	
	
	public void doEraser()
	{
		//1,2,3
		
		if(StateModle == STATE_PEN)
		{
			drawPaint = drawEraser;
			StateModle = STATE_ERASER; 
		}
		else
			return;
		
//		drawEraser = drawPen;
//		drawPen    = drawPaint;
//		drawPaint  = drawEraser;
			
		
		
	}
    
	/**
     * @Name:doPen()
     * @Params:null
     * @return:void
     * @Function:switch to eraser
     */		
	public void doPen()
	{
		drawPaint  = drawPen;
		
		StateModle = STATE_PEN;
	}
	/**
     * @Name:getModle()
     * @Params:null
     * @return:int
     * @Function:return currentModle
     */
	
	public int getModle()
	{
		return StateModle;
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
	
	/**
     * @Name:isBitmapEmpty()
     * @Params:null
     * @return:boolean
     * @Function:return is the stacks empty
     */
	
	public boolean isBitmapEmpty()
	{
		
		return backStack.isEmpty();
	}
	
	/**
     * @Name:getBitmap()
     * @Params:null
     * @return:Bitmap
     * @Function:return is the stacks empty
     */
	
	public Bitmap getBitmap()
	{
		if( !backStack.isEmpty() )
		{
			
			Bitmap tem_b = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			
			Canvas c = new Canvas(tem_b);
			c.drawBitmap(drawBitmap,0,0, drawPen);
			c = null;
			return tem_b;
		}	
		else return null;
	}
	
    /**
     * @Name:doClear()
     * @Params:null
     * @return:void
     * @Function:do clean the drawBitmap and clear stacks
     */	
	public void doClear()
	{
//		drawCanvas.drawColor(Color.TRANSPARENT);
		drawBitmap.eraseColor(Color.TRANSPARENT);
		backStack.clear();
		forwardStack.clear();
//		backStackEraser.clear();
//		forwardStackEraser.clear();
	}
	
	
	public void doDealloc()
	{
		this.stop();
		drawPath = null;
		drawPaint= null;
		drawEraser= null;
		drawPen= null;
		drawThread = null;

		drawCanvas = null;
		
		
		backStack= null;
		forwardStack= null;
		backStackEraser= null;
		forwardStackEraser= null;
		
		drawBitmap.recycle();
		drawBitmap = null;
		bitmap.recycle();
		bitmap = null;
		System.gc();
	}

}
