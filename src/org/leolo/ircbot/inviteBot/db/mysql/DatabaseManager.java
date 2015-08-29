package org.leolo.ircbot.inviteBot.db.mysql;

public class DatabaseManager implements org.leolo.ircbot.inviteBot.DatabaseManager {
	
	private String connectionString;

	public String getConnectionString() {
		return connectionString;
	}
	
	@Override
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
		System.out.println(connectionString);
		System.exit(0);
	}

	@Override
	public int getModelLevel() {
		return 0;
	}
	
	

}
