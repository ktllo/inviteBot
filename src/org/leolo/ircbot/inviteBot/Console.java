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
		}else if(event.getMessage().toLowerCase().startsWith(event.getBot().getNick().toLowerCase())){
			String cmd = event.getMessage().substring(event.getBot().getNick().length());
			logger.debug("msg: {}", cmd);
			while(cmd.startsWith(" ") || cmd.startsWith(":") || cmd.startsWith(",")){
				logger.debug("Current msg: {}", cmd);
				cmd = cmd.substring(1);
			}
			logger.debug("msg: {}", cmd);
			String msg = processMessage(
					cmd,
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
		String rmessage = message;
		message = message.toLowerCase();
		logger.debug(USAGE,"Reveived message {} from {}!{}@{}",message,user.getNick(),user.getLogin(),user.getHostmask());
		if(message.startsWith("ping")){
			return "pong";
		}else if(message.startsWith("invite")){
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
		}else if(message.startsWith("info")){
			//if(config.isGlobalAdmin(user) || config.isListenChannel(source)){
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
			//}
		}else if(message.startsWith("version")){
			return InviteBot.getName() + " version " + InviteBot.getVersion();
		}else if(message.startsWith("resend")){
			for(JoinRecord record:inviter.pendingItems){
				if(record.getNick().equalsIgnoreCase(user.getNick())){
					return record.getQuestion().getQuestion();
				}
			}
			return "Record not found. Please part and rejoin.";
		}else if(message.startsWith("help")){
			String [] cmd = message.split(" ");
			if(cmd.length == 1){
				return "Available command: ping, invite, info, version, resend, nick";
			}else if(cmd[1].equalsIgnoreCase("ping")){
				return "Check is the bot alive. No parametres";
			}else if(cmd[1].equalsIgnoreCase("invite")){
				StringBuilder sb = new StringBuilder();
				sb.append(Color.color(ColorName.RED));
				sb.append("ADMIN ONLY. ").append(Color.defaultColor());
				sb.append("Invite user in holding channel without requiring them to answer the question\n");
				sb.append("Parametres: List of nicks going to invite, sperated by space");
				return sb.toString();
			}else if(cmd[1].equalsIgnoreCase("info")){
				return "Report uptime and the number of entry in the inviter";
			}else if(cmd[1].equalsIgnoreCase("version")){
				return "Version of the bot";
			}else if(cmd[1].equalsIgnoreCase("resend")){
				StringBuilder sb = new StringBuilder();
				sb.append(Color.color(ColorName.DARK_BLUE));
				sb.append("Should use in holding channel OR PM only. ").append(Color.defaultColor());
				sb.append("Send the question for the requesting user as message in channel, if used in channel.\n");
				sb.append("Send in PM if the command is sent via PM");
				return sb.toString();
			}else if(cmd[1].equalsIgnoreCase("nick")){
				StringBuilder sb = new StringBuilder();
				sb.append(Color.color(ColorName.RED));
				sb.append("Global admin only ").append(Color.defaultColor());
				sb.append("Change the bot's nickname to the nickname given\n");
				return sb.toString();
			}
		}else if(message.startsWith("nick")){
			String [] cmd = rmessage.split(" ");
			if(cmd.length == 1){
				return Color.color(ColorName.RED)+"ERROR: NICKNAME REQUIRED";
			}
			if(config.isGlobalAdmin(user)){
				bot.sendIRC().changeNick(cmd[1]);
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!bot.getNick().equals(cmd[1])){
					return Color.color(ColorName.RED)+"ERROR: Nick change failed";
				}
			}else{
				logger.warn(USAGE, "User {}!{}@{} tried to change bot's nick but unauthorized", user.getNick(),user.getLogin(),user.getHostmask());
				return Color.color(ColorName.RED)+"ERROR: UNAUTHORIZED";
			}
		}else if(message.startsWith("backup")){
			if(config.isGlobalAdmin(user)){
				return "backup as "+config.writeBackup();
			}
		}
		return "";
	}

}
