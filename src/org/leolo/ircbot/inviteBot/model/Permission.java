package org.leolo.ircbot.inviteBot.model;

public class Permission {
	
	private PermissionType type;
	private String name;
	private int permissionID;
	
	public PermissionType getType() {
		return type;
	}
	public void setType(PermissionType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPermissionID() {
		return permissionID;
	}
	public void setPermissionID(int permissionID) {
		this.permissionID = permissionID;
	}
	
}
