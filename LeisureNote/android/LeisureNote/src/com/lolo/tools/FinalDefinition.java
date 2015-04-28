package com.lolo.tools;

public class FinalDefinition 
{
	public static final String NOTE_MODEL         = "note_model";
	public static final int NOTE_NEW              = 0;
	public static final int NOTE_UPDATE           = 1;
	
	public static final String BASE_IMAGE_PATH    = "/mnt/sdcard/LeisureNote/image/";
	public static final String BASE_SOUND_PATH    = "/mnt/sdcard/LeisureNote/audio/";
	
	public static final int INTENT_CODE_NONE      = -1;
	public static final int INTENT_CODE_GALLERY   = 0;
	public static final int INTENT_CODE_CAMERA    = 1;
	public static final int INTENT_CODE_DRAW      = 2;
	public static final int INTENT_CODE_LOCAL     = 3;
    
    
	public static final int MESSAGE_TIMING        = 0;
	public static final int MESSAGE_STOP_TIMING   = 1;
	public static final int MESSAGE_PROGRESS      = 2;
	public static final int MESSAGE_JOBDONE       = 3;
	public static final int MESSAGE_LOCAL         = 4;
	public static final int MESSAGE_CLOUD         = 5;
	public static final int MESSAGE_SYNC          = 6;
	//inner class Thread
	public static final int MESSAGE_GET_WORD   = 7;
	public static final int MESSAGE_FADE       = 8;
	public static final int MESSAGE_SHOW       = 9;
	public static final int MESSAGE_UPDATE     = 10;
	public static final int MESSAGE_DISMISS    = 11;
	public static final int MESSAGE_NO_SYNC    = 12;
	public static final int MESSAGE_SUC_SYNC   = 13;
	public static final int MESSAGE_FAIL_SYNC  = 14;
	public static final int MESSAGE_INIT_DONE  = 15;
	
	//DataBase

	public static final String DATABASE_TABLE_USER = "userTable";
	public static final String DATABASE_TABLE_NOTE = "note";
	public static final String DATABASE_TABLE_BOOK = "notebook";
	
	public static final String DATABASE_ID       = "id";
	public static final String DATABASE_USERNAME = "username";
	public static final String DATABASE_PASSWORD = "password";
	public static final String DATABASE_NICKNAME = "name";
	public static final String DATABASE_GENDER   = "gender";
	public static final String DATABASE_REMARK   = "remark";
	
	public static final String DATABASE_BOOk_ID  = "idnotebook";
	public static final String DATABASE_BOOK_NAME= "bookname";
	public static final String DATABASE_BOOK_USER= "iduser";
	public static final String DATABASE_BOOK_CTIME="createtime";
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public static final String DATABASE_NOTE_IDU   = "iduser";
	public static final String DATABASE_NOTE_IDB   = "idbook";
	public static final String DATABASE_NOTE_TITLE = "title";
	public static final String DATABASE_NOTE_CONTENT="text";
	public static final String DATABASE_NOTE_IMAGE = "image";
	public static final String DATABASE_NOTE_IMNAME= "imagename";
	public static final String DATABASE_NOTE_SOUND = "sound";
	public static final String DATABASE_NOTE_SONAME= "soundname";
	public static final String DATABASE_NOTE_HAND  = "handwriting";
	public static final String DATABASE_NOTE_CTIME = "createtime";
	public static final String DATABASE_NOTE_UTIME = "updatetime";
	
	
	
	public static final boolean ACTION_NEW   = true;
	public static final boolean ACTION_EDITE = false;
	
}
