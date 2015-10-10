package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.db.*;

public interface DatabaseManager {
	
	void setConnectionString(String connectionString);
	
	MemberDAO getMemberDAO();
	ConfigDAO getConfigDAO();
	RoleDAO getRoleDAO();
	PermissionDAO getPermissionDAO();
	
	int getModelLevel();
}
