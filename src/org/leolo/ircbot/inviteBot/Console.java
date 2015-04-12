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
		if(event.getMessage().startsWith(config.getEscape())){
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
		}else if(event.getMessage().toLowerCase().startsWith(event.getBot().getNick().toLowerCase()+" ")){
			String msg = processMessage(
					event.getMessage().substring(event.getBot().getNick().length()+1),
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
			logger.info(USAGE,user.getNick()+" inviting others");
			String [] list = message.split(" ");
			for(int i=1;i<list.length;i++){
				try{
					int count = inviter.invite(list[i], bot.sendIRC(), user, source);
					logger.info(USAGE,"Invited {} to {} channels",list[i],""+count);
				}catch(UnauthorizedOperationException uoe){
					logger.warn("User {} attempted to invite {} but he/she wasn't "
							+ "authroized to do so.");
				}
			}
		}
		if(message.startsWith("info")){
			if(config.isGlobalAdmin(user) || config.isListenChannel(source)){
				long uptime = System.currentTimeMillis() - inviter.START;
				int upD = (int)(uptime/86400000);
				int upH = ((int)(uptime/3600000))%24;
				int upM = ((int)(uptime/60000))%60;
				int upS = ((int)(uptime/1000))%60;
				StringBuilder sb = new StringBuilder();
				sb.append("Inviter size is "+inviter.pendingItems.size()+"\n");
				sb.append("Uptime is ");
				if( uptime > 86400000 )
					sb.append(upD).append(" days ");
				if( uptime > 3600000 )
					sb.append(upH).append(" hours ");
				if( uptime > 60000 )
					sb.append(upM).append(" minutes ");
				if( uptime > 1000 )
					sb.append(upS).append(" seconds ");
				
				return sb.toString();
			}
		}
		if(message.startsWith("version")){
			return "v1.0-preview (bowl cut)";
		}
		if(message.startsWith("resend")){
			for(JoinRecord record:inviter.pendingItems){
				if(record.getNick().equalsIgnoreCase(user.getNick())){
					return record.getQuestion().getQuestion();
				}
			}
			return "Record not found. Please part and rejoin.";
		}
		
		return "";
	}

}
