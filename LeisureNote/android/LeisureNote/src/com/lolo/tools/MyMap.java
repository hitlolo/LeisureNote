package com.lolo.tools;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyMap implements Serializable
{

	/**
	 * 
	 */
	private Map<String,MyBitmap> map;
	
	private static final long serialVersionUID = 1L;
	
	public MyMap()
	{
		map = new HashMap<String,MyBitmap>();
	}
	
	/**
	 * void	 clear()
		Removes all mappings from this hash map, leaving it empty.
	 *	Object	 clone()
		Returns a shallow copy of this map.
	 *	boolean	 containsKey(Object key)
		Returns whether this map contains the specified key.
	 *	boolean	 containsValue(Object value)
		Returns whether this map contains the specified value.
   	 *	Set<Entry<K, V>>	 entrySet()
		Returns a set containing all of the mappings in this map.
	 *	V	 get(Object key)
		Returns the value of the mapping with the specified key.
	 *	boolean	 isEmpty()
		Returns whether this map is empty.
	 *	Set<K>	 keySet()
		Returns a set of the keys contained in this map.
	 *	V	 put(K key, V value)
		Maps the specified key to the specified value.
	 *	void	 putAll(Map<? extends K, ? extends V> map)
		Copies all the mappings in the specified map to this map.
	 *	V	 remove(Object key)
		Removes the mapping with the specified key from this map.
	 *	int	 size()
		Returns the number of elements in this map.
	 *	Collection<V>	 values()
		Returns a collection of the values contained in this map.
	 * 
	 */
	
	
	/**
	 * void	 clear()
		Removes all mappings from this hash map, leaving it empty.
	*/
	public void clear()
	{
		map.clear();
	}
	
	/**
	 *  V	 get(Object key)
		Returns the value of the mapping with the specified key.
	 */
	public MyBitmap get(Object key)
	{
		return map.get(key);
	}
	
	/**
	 * *	boolean	 isEmpty()
		Returns whether this map is empty.
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}
	/**
	 *  *	Set<K>	 keySet()
		Returns a set of the keys contained in this map.
	 */	
	public Set<String> keySet()
	{
		return map.keySet();
	}
	/**
	 *	V	 put(K key, V value)
		Maps the specified key to the specified value.
	*/
	public MyBitmap put(String key,MyBitmap bitmap)
	{
		return map.put(key, bitmap);
	}
	/**
	 * *	V	 remove(Object key)
		Removes the mapping with the specified key from this map.
	 */
	
	public MyBitmap remove(String key)
	{
		return map.remove(key);
	}
	/**
	 * 	 *	int	 size()
		Returns the number of elements in this map.
	 */
	public int size()
	{
		return map.size();
	}
	/**
	 * 	 *	Collection<V>	 values()
		Returns a collection of the values contained in this map.
	 */
	
	public Collection<MyBitmap> values()
	{
		return map.values();
	}
}
