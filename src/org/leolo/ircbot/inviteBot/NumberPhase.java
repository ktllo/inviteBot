package org.leolo.ircbot.inviteBot;

public abstract class NumberPhase implements Comparable<NumberPhase>{
	public abstract String getString();
	public abstract int getNumber();
	
	public static NumberPhase next(){
		if(Math.random() > 0.5){
			return EnglishNumber.next();
		}else{
			return NumericalNumber.next();
		}
	}
	
	public int compareTo(NumberPhase num){
		if(this.getNumber() == num.getNumber())
			return 0;
		if(this.getNumber() >  num.getNumber())
			return 1;
		else
			return -1;
	}
}
