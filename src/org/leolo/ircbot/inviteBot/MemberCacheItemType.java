package org.leolo.ircbot.inviteBot;

public enum MemberCacheItemType {
	
	/**
	 * Indicates the cache is to avoid parsing the glob
	 */
	GLOB_CACHE,
	/**
	 * Indicates the cache is to keep the login by password
	 */
	PASSWORD_IDENTIFY;
}
