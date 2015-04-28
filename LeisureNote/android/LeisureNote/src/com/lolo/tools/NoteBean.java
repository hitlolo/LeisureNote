package com.lolo.tools;

import java.io.File;
import java.util.Date;
import java.util.Vector;

public class NoteBean 
{
	private int userId;
	private int bookId;
	private String noteTitle,
				   noteContent;
	
	private File   noteImage,
	               noteAudio;
	private String noteImageName,
	               noteAudioName;
	private Vector noteVector;
	private Date   noteCreateTime,
	               noteUpdateTime;
	
	private String noteBookName;
	
	public NoteBean()
	{
		userId        = 0;
		bookId        = 0;
		noteTitle     = null;
		noteContent   = null;
		noteImage     = null;
		noteAudio     = null;
		noteImageName = null;
		noteAudioName = null;
		noteVector    = null;
		noteCreateTime= null;
		noteUpdateTime= null;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public String getNoteTitle() {
		return noteTitle;
	}
	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}
	public String getNoteContent() {
		return noteContent;
	}
	public void setNoteContent(String noteContent) {
		this.noteContent = noteContent;
	}
	public File getNoteImage() {
		return noteImage;
	}
	public void setNoteImage(File noteImage) {
		this.noteImage = noteImage;
	}
	public File getNoteAudio() {
		return noteAudio;
	}
	public void setNoteAudio(File noteAudio) {
		this.noteAudio = noteAudio;
	}
	public String getNoteImageName() {
		return noteImageName;
	}
	public void setNoteImageName(String noteImageName) {
		this.noteImageName = noteImageName;
	}
	public String getNoteAudioName() {
		return noteAudioName;
	}
	public void setNoteAudioName(String noteAudioName) {
		this.noteAudioName = noteAudioName;
	}
	public Vector getNoteVector() {
		return noteVector;
	}
	public void setNoteVector(Vector noteVector) {
		this.noteVector = noteVector;
	}
	public Date getNoteCreateTime() {
		return noteCreateTime;
	}
	public void setNoteCreateTime(Date noteCreateTime) {
		this.noteCreateTime = noteCreateTime;
	}
	public Date getNoteUpdateTime() {
		return noteUpdateTime;
	}
	public void setNoteUpdateTime(Date noteUpdateTime) {
		this.noteUpdateTime = noteUpdateTime;
	}
	public String getNoteBookName() {
		return noteBookName;
	}
	public void setNoteBookName(String noteBookName) {
		this.noteBookName = noteBookName;
	}
	
	
	
}
