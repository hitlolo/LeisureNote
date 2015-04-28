package com.lolo.helper;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lolo.tools.FinalDefinition;

public class Player 
{
	private Context     myContext;
	private SeekBar     myBar;
	private MediaPlayer myPlayer;
	private Timer       myTimer;  
    private TimerTask   myTimerTask;  
    private boolean     isChanging = false;
    private Handler     myHandler;
	
    int total_second = 0;
    int start_second = 0;
    int start_minute = 0;
    int end_minute   = 0;
	int end_second   = 0;
	
	public Player(Context context,SeekBar seekbar,Handler handler)
	{
		myContext = context;
		myBar     = seekbar;
		myHandler = handler;
		init();
	}
	
    /*
     * @name:init
     * @Param:
     * @return:
     * @Function:
     */
	
	private void init()
	{
		myPlayer = new MediaPlayer();
		myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{  
		            @Override  
		            public void onCompletion(MediaPlayer arg0) 
		            {  
		            	myBar.setProgress(0);
		            	start_second = 0;
		            	start_minute = 0;
		            	end_second   = total_second - start_second;
		    	    	if(end_second>=60)
		    	    	{
		    	    		end_second  = end_second%60;
		    	    		end_minute += end_second/60;
		    	    	}
		    	    	
		    	    	Message msg = new Message();
			            msg.what = FinalDefinition.MESSAGE_PROGRESS;
			            myHandler.sendMessage(msg);
			            
		                Toast.makeText(myContext, "播放结束！", Toast.LENGTH_SHORT).show();  
		            }  
			}
		);
		myBar.setOnSeekBarChangeListener(new SeekBarChangeEvent()); 
		
		myTimer = new Timer();  		
	    myTimerTask = new TimerTask() 
	    {  
	            @Override  
	            public void run() 
	            {   
	                if(isChanging==true)  
	                {
	                	return;  
	                }	                
	                if(myPlayer.isPlaying())
	                {                    
	                	myBar.setProgress(myPlayer.getCurrentPosition());
		                Message msg = new Message();
		                msg.what = FinalDefinition.MESSAGE_PROGRESS;
		                myHandler.sendMessage(msg);
		                
	                }

	            }  
	    };  	  
	    myTimer.schedule(myTimerTask, 0, 10);  
	}
	
	 /*
     * @name:LoadAndPrepare
     * @Param:
     * @return:
     * @Function:
     */
	
	public void LoadAndPrepare(File file)
	{
		myPlayer.reset();
		
		if(file==null||!file.exists())
		{
			Toast.makeText(myContext, "文件异常！", Toast.LENGTH_SHORT).show();
			return;
		}
		else if(file!=null && file.exists())
		{
			try 
			{
				myPlayer.setDataSource(file.getAbsolutePath().toString());
				myPlayer.prepare();
				myBar.setMax(myPlayer.getDuration());
				total_second = myPlayer.getDuration()/1000;
				myBar.setProgress(0);
            	start_second = 0;
            	start_minute = 0;
            	end_second   = total_second - start_second;
    	    	if(end_second>=60)
    	    	{
    	    		end_second  = end_second%60;
    	    		end_minute += end_second/60;
    	    	}   	    	
    	    	Message msg = new Message();
	            msg.what = FinalDefinition.MESSAGE_PROGRESS;
	            myHandler.sendMessage(msg);
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	 /*
     * @name:play
     * @Param:
     * @return:
     * @Function:
     */	
	public void play()
	{
		if(myPlayer.isPlaying())
		{
			myPlayer.stop();
		}
		try 
		{
//			myPlayer.prepare();
			myPlayer.seekTo(myBar.getProgress());
			myPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
			

	}
	
	 /*
     * @name:pause
     * @Param:
     * @return:
     * @Function:
     */
	public void pause()
	{
		if(myPlayer.isPlaying())
		{
			myPlayer.pause();
		}
		else
		{
			Toast.makeText(myContext, "无正在播放的录音...", Toast.LENGTH_SHORT).show();
		}	
	}
	
	 /*
     * @name:stop
     * @Param:
     * @return:
     * @Function:
     */
	public void stop()
	{
		if(myPlayer.isPlaying())
		{
			myPlayer.stop();
		}
			
	}
	
	/*
     * @name:doStopRelease()
     * @Param:
     * @return:
     * @Function:
     */
	public void doStopRelease()
	{
		myTimerTask.cancel();
		myTimer.cancel();
	
		
		if(myPlayer==null)
		{
			return;
		}
		if(myPlayer!=null)
		{
			if(myPlayer.isPlaying())
			{
				myPlayer.stop();
				myBar.setProgress(0);
			}
			myPlayer.release();
		}
	}
	
	/*
     * @name:doStopOnly()
     * @Param:
     * @return:
     * @Function:
     */
	public void doStopOnly()
	{
		if(myPlayer==null)
		{
			return;
		}
		if(myPlayer!=null)
		{
			if(myPlayer.isPlaying())
			{
				myPlayer.stop();
				myBar.setProgress(0);
			}
		}
	}
	
	 /*
     * @name:release
     * @Param:
     * @return:
     * @Function:
     */
	
	public void release()
	{
		if(myPlayer==null)
		{
			return;
		}
		if(myPlayer!=null)
		{
			myPlayer.release();
			myPlayer = null;
			myTimer.cancel();
		}
		
	}
	/* 
	   * SeekBar进度改变事件 
	   */  
	
	
	 /*
     * @name:getStartedSecond()
     * @Param:
     * @return:
     * @Function:
     */
	
	public String getStartedSecond()
	{
		return String.format("%02d:%02d",start_minute,start_second);
	}
	
	 /*
     * @name:getEndSecond()
     * @Param:
     * @return:
     * @Function:
     */
	
	public String getEndSecond()
	{
		return String.format("%02d:%02d",end_minute,end_second);
	}
	 /*
     * @name:isPlaying()
     * @Param:
     * @return:
     * @Function:
     */
	public boolean getIsPlaying()
	{
		return myPlayer.isPlaying();
	}
	
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener
	{  
	  
	    @Override  
	    public void onProgressChanged(SeekBar seekBar, int progress,  
	            boolean fromUser) 
	    {  
	        // TODO Auto-generated method stub  
	    	start_second = myBar.getProgress()/1000;
	    	if(start_second>=60)
	    	{
	    		start_second  = start_second%60;
	    		start_minute += start_second/60;
	    	}
	    	end_second   = total_second - start_second;
	    	if(end_second>=60)
	    	{
	    		end_second  = end_second%60;
	    		end_minute += end_second/60;
	    	}
	    }  
	  
	    @Override  
	    public void onStartTrackingTouch(SeekBar seekBar) 
	    {  
	        isChanging=true;  
	    
	    }  
	  
	    @Override  
	    public void onStopTrackingTouch(SeekBar seekBar) 
	    {  
	        myPlayer.seekTo(seekBar.getProgress());  
	        isChanging=false;
	        Log.i("new add","touch out");
	        if(!myPlayer.isPlaying())
	        {
	        	try 
	        	{
	        		play();
					Log.i("new add","touch in");
				} 
	        	catch (IllegalStateException e)   	 
	        	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        }
	    }  
	        
	 }  
}
