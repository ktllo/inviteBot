package org.leolo.ircbot.inviteBot;

import java.util.Vector;

public class CacheManager {

	private static Vector<Cache> cacheList;
	
	static{
		cacheList = new Vector<>();
	}
	
	public static void register(Cache c){
		cacheList.add(c);
	}
	
	public static void invalidate(){
		for(Cache c:cacheList){
			c.invalidateCache();
		}
	}
	
	private CacheManager(){
		
	}
}
