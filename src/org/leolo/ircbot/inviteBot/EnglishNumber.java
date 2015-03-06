package org.leolo.ircbot.inviteBot;

public final class EnglishNumber extends NumberPhase{
	
	private int number;
	private String text;
	public static final String[] WORD_LIST = {
			"zero",
			"one",
			"two",
			"three",
			"four",
			"five",
			"six",
			"seven",
			"eight",
			"nine",
			"ten",
			"eleven",
			"twelve",
			"thirteen",
			"fourteen",
			"fifteen",
			"sixteen",
			"seventeen",
			"eighteen",
			"nineteen",
			"<Placeholder>",
			"twenty",
			"thirty",
			"forty",
			"fifty",
			"sixty",
			"seventy",
			"eighty",
			"ninety",
			"<Placeholder>"
	};
	public static NumberPhase next() {
		return new EnglishNumber((int)(Math.random()*100));
	}

	@Override
	public String getString() {
		return text;
	}

	@Override
	public int getNumber() {
		return number;
	}
	
	private EnglishNumber(int number){
		this.number = number;
		if(number < 20 ){
			text = WORD_LIST[number];
		}else{
			if(number % 10 == 0){
				text = WORD_LIST[19+(number/10)];
			}else{
				text = WORD_LIST[19+(number/10)]+" "+WORD_LIST[(number%10)];
			}
		}
	}

	
	
	
}
