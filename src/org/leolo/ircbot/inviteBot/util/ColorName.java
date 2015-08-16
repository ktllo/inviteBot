package org.leolo.ircbot.inviteBot.util;

public enum ColorName {
	WHITE(0),
	BLACK(1),
	DARK_BLUE(2),
	DARK_GREEN(3),
	RED(4),
	BROWN(5),
	PURPLE(6),
	OLIVE(7),
	YELLOW(8),
	GREEN(9),
	TEAL(10),
	CYAN(11),
	BLUE(12),
	MAGENTA(13),
	DARK_GRAY(14),
	LIGHT_GRAY(15),
	DEFAULT(99);
	
	private int code;
	private ColorName(int code){
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}
