package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.util.Color;
import org.leolo.ircbot.inviteBot.util.ColorName;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console extends ListenerAdapter<PircBotX> {
	
	final Logger logger = LoggerFactory.getLogger(Console.class);

	
	private Config config;
	private Inviter inviter;
	public Console(Config config,Inviter inviter) {
		this.config=config;
		this.inviter = inviter;
	}
	
	public void onMessage(MessageEvent<PircBotX> event){
		if(!event.getMessage().startsWith(config.getEscape()))
			return;
		String msg = processMessage(
				event.getMessage().substring(config.getEscape().length()),
				event.getUser());
		if(msg.length()>0){
			String [] lines = msg.split("\n");
			for(String line:lines){
				event.respond(line);
			}
		}
	}
		
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event){
		String msg = processMessage(event.getMessage(),event.getUser());
		if(msg.length()>0){
			String [] lines = msg.split("\n");
			for(String line:lines){
				event.respond(line);
			}
		}
	}


	
	private String processMessage(String message,User user){
		message = message.toLowerCase();
		logger.debug("Reveived message "+message);
		if(message.startsWith("ping")){
			return "pong";
		}
		
		return "";
	}

}
