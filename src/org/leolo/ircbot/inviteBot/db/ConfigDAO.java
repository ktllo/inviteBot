package org.leolo.ircbot.inviteBot.db;

public interface ConfigDAO {
	
	String get(String key);
	
	void set(String key,String value);
	
	boolean isExist(String key);
	
	void delete(String key);
	
}
