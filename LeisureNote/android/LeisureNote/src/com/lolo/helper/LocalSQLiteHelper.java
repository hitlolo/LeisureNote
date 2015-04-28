package com.lolo.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.lolo.dev.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalSQLiteHelper 
{
	final String basePath  = "/mnt/sdcard/LeisureNote/database/";
	final String baseName  = "leisurenote.db";
	
	private final Context  myContext;
	
	private SQLiteDatabase myDatabase = null;
	
	public LocalSQLiteHelper(Context context)
	{
		myContext = context;
	}
	
	
	private SQLiteDatabase openDatabase()
	{
		String databaseFilename = basePath + baseName;
		try
		{
			// 获得dictionary.db文件的绝对路径
			File   dir = new File(basePath);
			if (!dir.exists())
			{
				dir.mkdir();
			}				
			// 如果在/sdcard/dictionary目录中不存在
			// dictionary.db文件，则从res\raw目录中复制这个文件到			
			if (!(new File(databaseFilename)).exists())
			{
				// 获得封装dictionary.db文件的InputStream对象
				InputStream is = myContext.getResources().openRawResource(R.raw.leisurenote);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制dictionary.db文件
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			// 打开/sdcard/dictionary目录中的dictionary.db文件
		}
		catch (Exception e)
		{
		}
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
		return database;
	}
	
	
	public void doOpen()
	{
	
		myDatabase = openDatabase();
		
	
	}
	
	
	public long doInsert(String tableName,ContentValues contentValues)
	{
		return this.myDatabase.insert(tableName, null, contentValues);
	}
	
	public void doDelete(String table,String whereClause)
	{
		this.myDatabase.delete(table, whereClause, null);
	}
	
	public int doDelete(String table,String whereClause,String[] args)
	{
		return myDatabase.delete(table, whereClause, args);
	}
	
	public int doUpdate(String table, ContentValues values, String whereClause,String[] clauseArgs)
	{
		return this.myDatabase.update(table, values, whereClause, clauseArgs);
	}
	
	
	/*	
	 * public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)

	 * Since: API Level 1
	 * Query the given table, returning a Cursor over the result set.
	 * Parameters

	 * table	The table name to compile the query against.
	 * columns	A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * selection	A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * selectionArgs	You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * groupBy	A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * having	A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * orderBy	How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 */
	public Cursor doQuery(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
	{
		return this.myDatabase.query(table,columns,selection,selectionArgs,groupBy,having,orderBy);
	}
	
	public void doClose()
	{
		this.myDatabase.close();
		myDatabase = null;
	}
	
	public SQLiteDatabase doGetDatabase()
	{
		return this.myDatabase;
	}
}
