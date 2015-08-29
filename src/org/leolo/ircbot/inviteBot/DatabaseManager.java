package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.db.MemberDAO;

public interface DatabaseManager {
	
	void setConnectionString(String connectionString);
	
	MemberDAO getMemberDAO();
	
	int getModelLevel();
}
