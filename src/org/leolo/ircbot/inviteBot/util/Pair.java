package org.leolo.ircbot.inviteBot.util;

public class Pair<A,B>{
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	private A a;
	private B b;
	public A getA() {
		return a;
	}
	public void setA(A a) {
		this.a = a;
	}
	public B getB() {
		return b;
	}
	public void setB(B b) {
		this.b = b;
	}
}