package org.leolo.ircbot.inviteBot.model;

import org.leolo.salt.HashType;
import org.leolo.salt.Salt;

public class Member {
	
	
	
	private long userId;
	private String userName;
	private String password;
	private boolean enabled;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean verifyPassword(String password){
		return Salt.verify(password, this.password);
	}
	public void updatePassword(String password){
		this.password = Salt.createHash(password, HashType.HMAC_SHA512);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
