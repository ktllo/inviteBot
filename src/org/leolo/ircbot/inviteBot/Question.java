package org.leolo.ircbot.inviteBot;

public abstract class Question {
	public abstract String getQuestion();
	public abstract int getSolution();
	public static Question next(){
		if(Math.random() > 0.5)
			return new Add();
		return new Subtract();
	}
	
	
}

enum QuestionType{
	TEXT,
	SYMBOL;
	
	public static QuestionType nextType(){
		if(Math.random() > 0.5)
			return TEXT;
		return SYMBOL;
	}
}

class Add extends Question{
	
	private NumberPhase op1,op2;
	QuestionType type;
	
	Add(){
		op1 = NumberPhase.next();
		op2 = NumberPhase.next();
		type = QuestionType.nextType();
	}
	
	@Override
	public String getQuestion() {
		if(type == QuestionType.SYMBOL){
			return op1.getString() + " + "+ op2.getString()+" = ?";
		}
		return op1.getString() + " plus "+ op2.getString()+" equals?";
	}

	@Override
	public int getSolution() {
		return op1.getNumber() + op2.getNumber();
	}
	
}

class Subtract extends Question{
	
	private NumberPhase op1,op2;
	QuestionType type;
	
	Subtract(){
		op1 = NumberPhase.next();
		op2 = NumberPhase.next();
		if(op1.compareTo(op2) == -1){
			NumberPhase tmp = op1;
			op1=op2;
			op2 = tmp;
		}
		type = QuestionType.nextType();
	}
	
	@Override
	public String getQuestion() {
		if(type == QuestionType.SYMBOL){
			return op1.getString() + " - "+ op2.getString()+" = ?";
		}
		return op1.getString() + " subtract "+ op2.getString()+" equals?";
	}

	@Override
	public int getSolution() {
		return op1.getNumber() - op2.getNumber();
	}
	
}