package org.leolo.ircbot.inviteBot.model;

public enum PermissionType {
	POSSTIVE(0),
	NEGATIVE(1);
	
	public final int typeID;
	
	PermissionType(int typeID){
		this.typeID = typeID;
	}
}
