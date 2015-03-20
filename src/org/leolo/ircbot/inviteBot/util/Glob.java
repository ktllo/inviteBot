package org.leolo.ircbot.inviteBot.util;

public class Glob {
	
	/**
	 * This is a simplified implementation of glob. Which supports * only
	 * 
	 * The expected time required for 0/1 *, or has 2 * only, while start and
	 * end with * is O(n). While the cost for other case will be much higher
	 * 
	 * @param pattern Pattern to be matched
	 * @param target Target to be matched
	 * @return true if matched
	 */
	public static boolean match(String pattern,String target){
		int count = 0;
		for(char c:pattern.toCharArray()){
			if(c == '*'){
				count++;
			}
		}
		if(count == 0){
			return pattern.equalsIgnoreCase(target);
		}else if(pattern.startsWith("*") && pattern.endsWith("*") && count == 2){
			String subpattern=pattern.substring(1,pattern.length()-1);
			return target.contains(subpattern);
		}else if(count >= 2){
			String out = "^";
		    for(char c: pattern.toCharArray()){
		        switch(c){
			        case '*': out += ".*"; break;
			        case '\\': out += "\\\\"; break;
			        default: out += c;
		        }
		    }
		    out += '$';
		    return target.matches(out);
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
