package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Random;

public abstract class Question {
	public abstract String getQuestion();
	
	@Deprecated
	public abstract int getSolution();
	
	public boolean verifyAnswer(String answer){
		try{
			int sol = Integer.parseInt(answer);
			return sol == getSolution();
		}catch(RuntimeException re){
			return false;
		}
	}
	
	public int getRatio(){
		return  100;
	}
	
	public static Random generator;
	private static ArrayList<Class<? extends Question>> list;
	private static int listWeight;
	static{
		list = new ArrayList<>();
		list.add(Add.class);
		list.add(Subtract.class);
		generator = new Random();
		rebuildWeight();
	}
	
	private static void rebuildWeight(){
		
		listWeight = 0;
		try{
			for(Class<? extends Question> q:list)
				listWeight += q.newInstance().getRatio();
		}catch(IllegalAccessException | InstantiationException ie){
			ie.printStackTrace();
		}
	}
	
	public static Question next(){
		int rand = generator.nextInt(listWeight);
		try{
			for(Class<? extends Question> q:list){
				if( (rand -= q.newInstance().getRatio()) < 0){
					return q.newInstance();
				}
			}
		}catch(IllegalAccessException | InstantiationException ie){
			ie.printStackTrace();
		}
		return new Add();
	}
	
	
}

enum QuestionType{
	TEXT,
	SYMBOL;
	
	public static QuestionType nextType(){
		if(Question.generator.nextDouble() > 0.5)
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

	@Override
	public boolean verifyAnswer(String answer) {
		try{
			int ans = Integer.parseInt(answer);
			return ans == getSolution();
		}catch(NumberFormatException nfe){
			return false;
		}
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

	@Override
	public boolean verifyAnswer(String answer) {
		try{
			int ans = Integer.parseInt(answer);
			return ans == getSolution();
		}catch(NumberFormatException nfe){
			return false;
		}
	}
	
}