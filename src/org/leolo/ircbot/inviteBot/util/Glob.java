package org.leolo.ircbot.inviteBot.util;

public class Glob {
	
	/**
	 * This is a simplified implementation of glob. Which supports 0/1 * only, Or both start/end with *
	 * @param pattern Pattern to be matched
	 * @param target Target to be matched
	 * @return true if matched
	 */
	public static boolean match(String pattern,String target){
		if(pattern.indexOf("*") == -1){
			return pattern.equalsIgnoreCase(target);
		}else if(pattern.startsWith("*") && pattern.endsWith("*")){
			String subpattern=pattern.substring(1,pattern.length()-1);
			return target.contains(subpattern);
		}else if(pattern.endsWith("*")){
			return target.startsWith(pattern.substring(0, pattern.length()-1));
		}else if(pattern.startsWith("*")){
			return target.endsWith(pattern.substring(1));
		}else{
			int pos = pattern.indexOf("*");
			String start = pattern.substring(0,pos-1);
			String end = pattern.substring(pos+1);
			return target.startsWith(start) && target.endsWith(end);
		}
	}
}
