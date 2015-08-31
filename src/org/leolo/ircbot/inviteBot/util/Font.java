package org.leolo.ircbot.inviteBot.util;

public class Font {
	
	private static final String ESCAPE_COLOR = "\u0003";
	private static final String ESCAPE_BOLD = "\u0002";
	private static final String ESCAPE_ITALIC = "\u001d";
	private static final String ESCAPE_UNDERLINE = "\u001f";
	private static final String ESCAPE_RESET="\u000f";
	private static final String ESCAPE_REVERSE="\u0016";
	
	public static String color(ColorName foreground){
		return ESCAPE_COLOR+foreground.getCode();
	}
	
	public static String color(ColorName foreground,ColorName background){
		return ESCAPE_COLOR+background.getCode()+","+foreground.getCode();
	}
	
	public static String defaultColor(){
		return color(ColorName.DEFAULT,ColorName.DEFAULT);
	}
	
	public static String setBold(){
		return ESCAPE_BOLD;
	}
	
	public static String setItalic(){
		return ESCAPE_ITALIC;
	}
	
	public static String setUnderline(){
		return ESCAPE_UNDERLINE;
	}
	
	public static String setReverseColor(){
		return ESCAPE_REVERSE;
	}
	
	public static String reset(){
		return ESCAPE_RESET;
	}
}
