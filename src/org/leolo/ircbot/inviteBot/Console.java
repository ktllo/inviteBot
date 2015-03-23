package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.util.Color;
import org.leolo.ircbot.inviteBot.util.ColorName;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Console extends ListenerAdapter<PircBotX> {
	
	final Logger logger = LoggerFactory.getLogger(Console.class);
	final static Marker USAGE = MarkerFactory.getMarker("usage");
	
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
				event.getUser(),
				event.getBot(),
				event.getChannel().getName());
		if(msg.length()>0){
			String [] lines = msg.split("\n");
			for(String line:lines){
				event.respond(line);
			}
		}
	}
		
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event){
		String msg = processMessage(event.getMessage(),
				event.getUser(),
				event.getBot(),
				event.getUser().getNick());
		if(msg.length()>0){
			String [] lines = msg.split("\n");
			for(String line:lines){
				event.respond(line);
			}
		}
	}


	
	private String processMessage(String message,User user,PircBotX bot,String source){
		message = message.toLowerCase();
		logger.debug("Reveived message "+message);
		if(message.startsWith("ping")){
			return "pong";
		}
		if(message.startsWith("invite")){
			if(config.isAdmin(user,source) || config.isListenChannel(source)){
				logger.info(USAGE,user.getNick()+" inviting others");
				String [] list = message.split(" ");
				for(int i=1;i<list.length;i++){
					int count = inviter.invite(list[i], bot.sendIRC(), user, source);
					logger.info(USAGE,"Invited {} to {} channels",list[i],""+count);
				}
			}else{
				logger.info(USAGE,user.getNick()+" attempted to invite others");
				return "Only admin can do this";
			}
		}
		
		return "";
	}

}
