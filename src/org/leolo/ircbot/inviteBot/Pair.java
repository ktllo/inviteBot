package org.leolo.ircbot.inviteBot;

class Pair{
	private String nick;
	private int answer;
	private String channel;
	public Pair(String nick, int answer, String channel) {
		this.nick = nick;
		this.answer = answer;
		this.channel = channel;
	}
	public String getNick() {
		return nick;
	}
	public int getAnswer() {
		return answer;
	}
	public String getChannel() {
		return channel;
	}
}