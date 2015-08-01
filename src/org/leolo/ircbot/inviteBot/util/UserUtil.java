package org.leolo.ircbot.inviteBot.util;

import org.pircbotx.User;

public class UserUtil {

	public static String getUserHostmask(User user) {
		return user.getNick()+"!"+user.getLogin()+"@"+user.getHostmask();
	}

}

