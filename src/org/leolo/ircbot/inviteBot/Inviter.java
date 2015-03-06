package org.leolo.ircbot.inviteBot;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;

public class Inviter extends ListenerAdapter<PircBotX>{
	private Config config;
	
	Inviter(Config config){
		this.config = config;
	}
	
	public void onJoin(JoinEvent<PircBotX> event){
		System.err.println(event.getUser().getNick()+" has joined "+event.getChannel().getName());
	}
}
