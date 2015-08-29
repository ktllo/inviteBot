package org.leolo.ircbot.inviteBot.model;

public class Member {
	
	public enum PasswordMode{
		EMPTY,
		PLAINTEXT,
		CIPHERTEXT,
		PLAIN_HASH,
		SALTED_HASH;
	}
	
	private long userId;
	private String userName;
	private String password;
	private PasswordMode passwordMode;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public PasswordMode getPasswordMode() {
		return passwordMode;
	}
	public void setPasswordMode(PasswordMode passwordMode) {
		this.passwordMode = passwordMode;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
