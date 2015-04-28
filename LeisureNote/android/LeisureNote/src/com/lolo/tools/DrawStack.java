package com.lolo.tools;

import java.util.Iterator;
import java.util.Stack;

public class DrawStack 
{
	
	private Stack<DrawObject> stack;
	
	public DrawStack()
	{
		stack = new Stack<DrawObject>();
	}
	
	public void push(DrawObject object)
	{
		stack.push(object);
	}
	
	public DrawObject pop()
	{
		return stack.pop();
	}
	
	public DrawObject firstElement()
	{
		return stack.firstElement();
	}
	
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}
	
	public int size()
	{
		return stack.size();
	}
	
	public Stack<DrawObject> getStack()
	{
		return stack;
	}
	
	public Iterator<DrawObject> iterator()
	{
		return stack.iterator();
	}
	
	public void clear()
	{
		stack.clear();
	}
	

}
