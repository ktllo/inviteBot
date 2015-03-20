package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Random;

import org.leolo.ircbot.inviteBot.util.Pair;

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
	private static ArrayList<Pair<Class<? extends Question>,Integer>> list;
	private static int listWeight;
	static{
		list = new ArrayList<>();
		generator = new Random();
		
		try {
			add(Subtract.class);
			add(Add.class);
		} catch (InstantiationException|IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void rebuildWeight(){
		listWeight = 0;
		for(Pair<Class<? extends Question>, Integer> q:list)
			listWeight += q.getB();
	}
	
	public static void add(Class<? extends Question> q) throws InstantiationException, IllegalAccessException{
		list.add(new Pair<Class<? extends Question>,Integer>(q,q.newInstance().getRatio()));
		rebuildWeight();
	}
	
	public static Question next(){
		int rand = generator.nextInt(listWeight);
		try{
			for(Pair<Class<? extends Question>,Integer> q:list){
				if( (rand -= q.getB()) < 0){
					return q.getA().newInstance();
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