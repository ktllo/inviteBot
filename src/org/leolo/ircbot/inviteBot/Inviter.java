package org.leolo.ircbot.inviteBot;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

public class Inviter extends ListenerAdapter<PircBotX>{
	private Config config;
	
	Inviter(Config config){
		this.config = config;
	}
	
	public void onJoin(JoinEvent<PircBotX> event){
		System.err.println(event.getUser().getNick()+" has joined "+event.getChannel().getName());
	}
	
	public void onMessage(MessageEvent<PircBotX> event){
		if(event.getMessage().equalsIgnoreCase(config.escape+"next")){
			Question q = Question.next();
			event.respond(q.getQuestion());
			event.respond(Integer.toString(q.getSolution()));
		}
	}
}
