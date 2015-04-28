package com.lolo.tools;

import java.io.File;
import java.io.FilenameFilter;

public class MusicFilter implements FilenameFilter
{

	@Override
	public boolean accept(File dir, String name) 
	{
		// TODO Auto-generated method stub
		String suffix = ".mp3";
		return (name.endsWith(suffix));
	}

}
