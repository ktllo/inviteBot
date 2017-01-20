package org.leolo.ircbot.inviteBot.util;

import java.lang.Character;

public class CaseMapping {

	public static String toLowerCase(String string) {
		String result = "";
		for(char c: string.toCharArray()){
			switch(c){
				case '[': result += "{"; break;
				case ']': result += "}"; break;
				case '\\': result += "|"; break;
				case '^': result += "~"; break;
				default: result += Character.toLowerCase(c);
			}
		}
		return result;
	}

	public static String toUpperCase(String string) {
		String result = "";
		for(char c: string.toCharArray()){
			switch(c){
				case '{': result += "["; break;
				case '}': result += "]"; break;
				case '|': result += "\\"; break;
				case '~': result += "^"; break;
				default: result += Character.toUpperCase(c);
			}
		}
		return result;
	}
}
