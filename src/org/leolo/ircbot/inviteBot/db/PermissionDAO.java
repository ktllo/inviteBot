package org.leolo.ircbot.inviteBot.db;

import java.util.List;
import java.util.Map;

import org.leolo.ircbot.inviteBot.model.Permission;

public interface PermissionDAO {

	
	public Map<String,Permission> getPermissionList();
}
