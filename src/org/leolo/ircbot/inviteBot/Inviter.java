package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.util.Color;
import org.leolo.ircbot.inviteBot.util.ColorName;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

public class Inviter extends ListenerAdapter<PircBotX>{
	private Config config;
	
	Inviter(Config config){
		this.config = config;
	}
	
	public void onJoin(JoinEvent<PircBotX> event){
		System.err.println(event.getUser().getNick()+" has joined "+event.getChannel().getName());
		event.respond(Color.color(ColorName.RED)+"Welcome!");
	}
	
	public void onMessage(MessageEvent<PircBotX> event){
		
	}
	
	public void onConnect(ConnectEvent<PircBotX> event){
		for(String s:config.getChannelList())
			event.getBot().sendIRC().joinChannel(s);
	}
}
