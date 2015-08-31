package org.leolo.ircbot.inviteBot;

import java.util.TreeMap;

public class ConfigManager {
	
	private static TreeMap<String,String> cache;
	Config config;
	
	public void invalidateCache(){
		synchronized(this){
			cache.clear();
		}
	}
	
	public String get(String key){
		String value = null;
		if(cache.containsKey(key)){
			value = cache.get(key);
		}
		if(value == null){
			//Cache miss
			value = config.getDatabaseManager().getConfigDAO().get(key);
			if(value != null){
				synchronized(this){
					cache.put(key, value);
				}
			}
		}
		return value;
	}
	
	public void put(String key,String value){
		synchronized(this){
			cache.put(key, value);
		}
		config.getDatabaseManager().getConfigDAO().set(key, value);
	}
}
