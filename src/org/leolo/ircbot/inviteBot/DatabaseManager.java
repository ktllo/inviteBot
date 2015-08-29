package org.leolo.ircbot.inviteBot;

public interface DatabaseManager {
	
	void setConnectionString(String connectionString);
	
	int getModelLevel();
}
