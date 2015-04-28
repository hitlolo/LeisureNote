package com.lolo.tools;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

public class MyImageVector  implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<MyMap> vector;
	/**
	 * 
	 */
	public MyImageVector()
	{
		vector = new Vector<MyMap>();
	}
	
	/**
	 * 
	 * @return
	 */
	public int size()
    {
    	return vector.size();
    }
    
	/**
	 * 
	 * @param location
	 * @return
	 */
    public MyMap elementAt(int location)
    {
    	return vector.elementAt(location);
    }
    /**
     * 
     * @param map
     */
    public void add(MyMap map)
    {
    	vector.add(map);
    }
    /**
     * 
     * @return
     */
    public boolean isEmpty()
    {
    	return vector.isEmpty();
    }
    /**
     * 
     * @return
     */
    public MyMap lastElement()
    {
    	return vector.lastElement();
    }
    /**
     * 
     * @param map
     */
    public void remove(MyMap map)
    {
    	vector.remove(map);
    }
    /**
     * 
     */
    public void removeAllElements()
    {
    	vector.removeAllElements();
    }
    /**
     * 
     * @return
     */
    public Vector<MyMap> getVector()
    {
    	return vector;
    }

}
