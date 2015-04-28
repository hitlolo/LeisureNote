package com.lolo.helper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


/*
 * Author:Lolo
 * Date:2012/3/29
 * File Description:do the recorder stuff
 */
public class RecorderHelper 
{
	
	private File          audioFile,
					      audioPath;
	private MediaRecorder mediaRecorder;
	
	private String        predixTemFile,                     
	                      basePath;
	
	private Context       myContext;
	
	private boolean       isRecording;
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public RecorderHelper(Context context)
	{
		myContext   = context;
		
		audioFile   = null;
		audioPath   = null;
		isRecording = false;
		
		init();
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	private void init()
	{
		predixTemFile = "mic_";
		
		if( Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) )
		{
//			audioPath = Environment.getExternalStorageDirectory();
			basePath  = "/mnt/sdcard/LeisureNote/audio/";
			audioPath = new File("/mnt/sdcard/LeisureNote/audio/");
		    if(!audioPath.exists())
		    {  
		    	audioPath.mkdir();//没有目录先创建目录  
            }  
		}
		else
		{
			Toast.makeText(myContext, "No SD card!", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public void startRecording()
	{
		mediaRecorder = new MediaRecorder();
		long createTime     = System.currentTimeMillis(); 
		try 
		{
			audioFile = null;
			audioFile = new File(audioPath.getAbsolutePath()+"/"+predixTemFile+createTime+".amr");
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setOutputFile(audioFile.getAbsolutePath());		
			mediaRecorder.prepare();
			mediaRecorder.start();
			isRecording = true;
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public String stopRecording()
	{
		if( audioFile!= null )
		{
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			isRecording   = false;
			return audioFile.getName().toString();
		}
		else
		{
			isRecording   = false;
			return null;
		}
			
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public File getAudioFile()
	{
		return audioFile;
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public File getAudioPath()
	{
		return audioPath;
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public String getFileName()
	{
		return audioPath.getName().toString();
	}
	
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public boolean getIsRecording()
	{
		return isRecording;
	}
	
	public void setFile(File file)
	{
		this.audioFile = file;
	}
	/*
	 * --------------------------------------------------------------------------------------------    
	 * --------------------------------------------------------------------------------------------    
	 */
	public void deleteAudio(String filename)
	{
		if( audioFile!= null && filename==null)
		{
			audioFile.delete();
			audioFile = null;
		}
		else if( filename!=null )
		{
			File temFile = new File(basePath+filename);
			if( temFile.exists() )
			{
				temFile.delete();
			}
			else if ( !temFile.exists() )
			{
				return;
			}
		}
		else
		{
			return;
		}
	}
}
