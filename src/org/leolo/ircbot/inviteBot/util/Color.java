package org.leolo.ircbot.inviteBot.util;

public class Color {
	private static final String ESCAPE = "\u0003";
	
	public static String color(ColorName foreground){
		return ESCAPE+foreground.getCode();
	}
	
	public static String color(ColorName foreground,ColorName background){
		return ESCAPE+background.getCode()+","+foreground.getCode();
	}
	
	public static String defaultColor(){
		return color(ColorName.WHITE,ColorName.BLACK);
	}
}
