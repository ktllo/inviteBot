package org.leolo.ircbot.inviteBot;

public class NumericalNumber extends NumberPhase {
	
	private int num;
	
	public static NumberPhase next() {
		NumericalNumber nn = new NumericalNumber();
		nn.num = Question.generator.nextInt(100);
		return nn;
	}

	@Override
	public String getString() {
		return Integer.toString(num);
	}

	@Override
	public int getNumber() {
		return num;
	}


}
