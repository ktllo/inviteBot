package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Iterator;

import org.leolo.ircbot.inviteBot.Config.Channel;
import org.leolo.ircbot.inviteBot.util.Color;
import org.leolo.ircbot.inviteBot.util.ColorName;
import org.leolo.ircbot.inviteBot.util.UserUtil;
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
		this.config = config;
		this.inviter = inviter;
	}
	
	/**
	 * Identifies messages that start with recognized prefixes, and shaves
	 * off those prefixes, sending the resulting message to processMessage.
	 * processMessage returns a response to send through the event object.
	 * 
	 * Message can be in the form of:
	 *
	 *   config.getEscape()
	 *   "$botnickname"
	 *   "$botnickname:"
	 *   "$botnickname,"
	 *
	 * Each can be followed by an arbitrary number of spaces, which will
	 * also be shaved off.
	 */
	public void onMessage(MessageEvent<PircBotX> event){

		String cmd = "";
		String msg = event.getMessage();
		String botnickname = event.getBot().getNick();
		String escapeSeq = config.getEscape();

		if(msg.startsWith(escapeSeq))
			cmd = msg.substring(escapeSeq.length());
		else if(msg.toLowerCase().startsWith(botnickname.toLowerCase())){
			cmd = msg.substring(botnickname.length());
			if(cmd.startsWith(":") || cmd.startsWith(","))
				cmd = cmd.substring(1);
		} else
			return;

		cmd = cmd.trim();

		String resp = processMessage(
				cmd,
				event.getUser(),
				event.getBot(),
				event.getChannel().getName());
		if(resp.length()>0){
			String [] lines = resp.split("\n");
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
		if(msg.length()==0 && config.isGlobalAdmin(event.getUser())){
			msg = processMessage(event.getMessage(),
					event.getUser(),
					event.getBot(),
					"");
		}
		if(msg.length()>0){
			String [] lines = msg.split("\n");
			for(String line:lines){
				event.respond(line);
			}
		}
	}

	private Command[] cmds = {
		new MooCommand(),
		new VersionCommand(),
		new PingCommand(),
		new InviteCommand(),
		new NickCommand(),
		new InfoCommand(),
		new ResendCommand(),
		new HelpCommand(),
		new EchoCommand(),
		new BackupCommand(),
		new WhoamiCommand(),

		new AddAdminCommand(),
		new RemoveAdminCommand(),
		new ListAdminCommand(),
		new AddExemptCommand(),
		new RemoveExemptCommand(),
		new ListExemptCommand(),
	};

	private String processMessage(String message,User user,PircBotX bot,String source){
		CommandContext ctx = new CommandContext(user, bot, source);

		String args[] = message.split(" +");
		if(args.length == 0)
			return "";

		String cmdName = args[0].toLowerCase();

		for(int i = 0; i < cmds.length; i++)
			if(cmds[i].toString().toLowerCase().equals(cmdName)) {
				if(cmds[i].requiresGlobalAdmin() && !config.isGlobalAdmin(ctx.user))
					return Color.color(ColorName.RED)+"ERROR: UNAUTHORIZED";
						
				return cmds[i].run(ctx, args);
			}

		return Color.color(ColorName.RED) + "ERROR: UNRECOGNIZED COMMAND '" + cmdName + "'";
	}

	private static class CommandContext {
		User user;
		PircBotX bot;
		String source;

		CommandContext(User user, PircBotX bot, String source) {
			this.user = user;
			this.bot = bot;
			this.source = source;
		}
	}
	
	private static abstract class Command {
		String name;
		boolean adminRequired;
		String help;

		Command(String name, boolean adminRequired, String help) {
			this.name = name;
			this.adminRequired = adminRequired;
			this.help = help;
		}

		Command(String name, boolean adminRequired) {
			this(name, adminRequired, "");
		}

		public abstract String run(CommandContext ctx, String args[]);

		public String toString() {
			return name;
		}

		public String getHelp() {
			return help;
		}

		public boolean requiresGlobalAdmin() {
			return adminRequired;
		}
	}

	private class MooCommand extends Command {

		MooCommand() { super("moo", false); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			return "mooo";
		}
	}

	private class VersionCommand extends Command {

		VersionCommand() { super("version", false, "Tells the current version."); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			return InviteBot.getName() + " version " + InviteBot.getVersion();
		}
	}

	private class PingCommand extends Command {

		PingCommand() { super("ping", false, "Responds with 'pong', to check connection status."); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			return "pong";
		}
	}

	private class InviteCommand extends Command {
		
		InviteCommand() { super("invite", false); }

		@Override
		public String getHelp() {
			StringBuilder sb = new StringBuilder();
			sb.append(Color.color(ColorName.RED));
			sb.append("ADMIN ONLY. ").append(Color.defaultColor());
			sb.append("Invite user in holding channel without requiring them to answer the question\n");
			sb.append("Parametres: List of nicks going to invite, sperated by space");
			return sb.toString();
		}

		@Override
		public String run(CommandContext ctx, String args[]) {
			logger.info(USAGE,ctx.user.getNick()+" inviting others");
			for(int i=1;i<args.length;i++){
				try{
					int count = inviter.invite(args[i], ctx.bot.sendIRC(), ctx.user, ctx.source);
					logger.info(USAGE,"Invited {} to {} channels",args[i],""+count);
				}catch(UnauthorizedOperationException uoe){
					logger.warn("User {} attempted to invite {} but he/she wasn't "
							+ "authroized to do so.");
				}
			}
			return "";
		}
	}

	private class InfoCommand extends Command {

		InfoCommand() { super("info", true, "Report uptime and the number of entry in the inviter"); }

		@Override
		public String run(CommandContext ctx, String args[]) {
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

	private class ResendCommand extends Command {

		ResendCommand() { super("resend", false); }

		@Override
		public String getHelp() {
			StringBuilder sb = new StringBuilder();
			sb.append(Color.color(ColorName.DARK_BLUE));
			sb.append("Should use in holding channel OR PM only. ").append(Color.defaultColor());
			sb.append("Send the question for the requesting user as message in channel, if used in channel.\n");
			sb.append("Send in PM if the command is sent via PM");
			return sb.toString();
		}

		@Override
		public String run(CommandContext ctx, String args[]) {
			for(JoinRecord record:inviter.pendingItems){
				if(record.getNick().equalsIgnoreCase(ctx.user.getNick())){
					return record.getQuestion().getQuestion();
				}
			}
			return "Record not found. Please part and rejoin.";
		}
	}

	private class HelpCommand extends Command {

		HelpCommand() { super("help", false, "Presents help message."); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			if(args.length == 1){
				StringBuilder resp = new StringBuilder("Available commands: ");
				boolean first = true;
				for(int i = 0; i < cmds.length; i++) {
					if(cmds[i].getHelp().length() != 0) {
						if(!first)
							resp.append(", ");
						resp.append(cmds[i]);
						first = false;
					}
				}
				return resp.toString();
			}

			int i;
			for(i = 0; i < cmds.length; i++)
				if(args[1].equals(cmds[i].toString()))
					break;

			if(i == cmds.length || cmds[i].getHelp().length() == 0)
				return "No help for '" + args[1] + "'";

			return cmds[i].getHelp();
		}
	}

	private class NickCommand extends Command {

		NickCommand() { super("nick", true); }

		@Override
		public String getHelp() {
			StringBuilder sb = new StringBuilder();
			sb.append(Color.color(ColorName.RED));
			sb.append("Global admin only ").append(Color.defaultColor());
			sb.append("Change the bot's nickname to the nickname given\n");
			return sb.toString();
		}

		@Override
		public String run(CommandContext ctx, String args[]) {
			if(args.length == 1){
				return Color.color(ColorName.RED)+"ERROR: NICKNAME REQUIRED";
			}
			ctx.bot.sendIRC().changeNick(args[1]);
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!ctx.bot.getNick().equals(args[1])){
				return Color.color(ColorName.RED)+"ERROR: Nick change failed";
			}
			return "";
		}
	}

	private class EchoCommand extends Command {

		EchoCommand() { super("echo", false); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			StringBuilder sb = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				sb.append(args[i]);
				if(i + 1 != args.length)
					sb.append(" ");
			}
			return sb.toString();
		}
	}

	private class BackupCommand extends Command {

		BackupCommand() { super("backup", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			return "backup as "+config.writeBackup();
		}
	}

	private class WhoamiCommand extends Command {

		WhoamiCommand() { super("whoami", false, "Tells if or how the bot recognizes you"); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			if(config.isGlobalAdmin(ctx.user)){
				return "Global Admin";
			}
			ArrayList<String> ch = new ArrayList<>();
			for(Channel c : config.getChannels()){
				if(c.isAdmin(ctx.user)){
					ch.add(c.getChannelName());
				}
			}
			if(ch.size() > 0){
				Iterator<String> ich = ch.iterator();
				StringBuilder sb = new StringBuilder();
				sb.append("Local admin of: ");
				while(ich.hasNext()){
					sb.append(ich.next());
					if(ich.hasNext()){
						sb.append(", ");
					}
				}
				return sb.toString();
			}
			return "I don't reconize you";
		}
	}

	private class AddAdminCommand extends Command {

		AddAdminCommand() { super("addadmin", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 3)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			String user = args[2];
			String channel = args[1];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.addAdmin(user);
					logger.info(USAGE,"{} added to admin list of {} by {}",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " added to admin list";
				}
			}

			return resp;
		}
	}

	private class RemoveAdminCommand extends Command {

		RemoveAdminCommand() { super("removeadmin", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {

			if(args.length != 3)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			String user = args[2];
			String channel = args[1];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.removeAdmin(user);
					logger.info(USAGE,"{} removed from admin list of {} by {} ",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					return user + " removed from admin list";
				}
			}

			return Color.color(ColorName.RED) + "ERROR: NOT AN ADMINISTRATOR, '" + user + "'";
		}
	}

	private class ListAdminCommand extends Command {

		ListAdminCommand() { super("listadmin", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {

			if(args.length != 2)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			for(Channel c:config.getChannels()){
				if(args[1].equals(c.getKey())){
					Iterator<String> i = c.getAdmins().iterator();
					StringBuilder sb = new StringBuilder();
					while(i.hasNext()){
						sb.append(i.next());
						if(i.hasNext()){
							sb.append(" ,");
						}
					}
					return sb.toString();
				}
			}

			return "";
		}
	}

	private class AddExemptCommand extends Command {

		AddExemptCommand() { super("addexempt", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 3)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			String user = args[2];
			String channel = args[1];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.addExempt(user);
					logger.info(USAGE,"{} added to exempt list of {} by {}",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " added to exempt list";
				}
			}

			return resp;
		}
	}

	private class RemoveExemptCommand extends Command {

		RemoveExemptCommand() { super("removeexempt", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 3)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			String user = args[2];
			String channel = args[1];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.removeExempt(user);
					logger.info(USAGE,"{} removed from exempt list of {} by {} ",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " removed from exempt list";
				}
			}

			return resp;
		}
	}

	private class ListExemptCommand extends Command {

		ListExemptCommand() { super("listexempt", true); }

		@Override
		public String run(CommandContext ctx, String args[]) {

			if(args.length != 2)
				return Color.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES";

			for(Channel c:config.getChannels()){
				if(args[1].equals(c.getKey())){
					Iterator<String> i = c.getExemptMask().iterator();
					StringBuilder sb = new StringBuilder();
					while(i.hasNext()){
						sb.append(i.next());
						if(i.hasNext()){
							sb.append(" ,");
						}
					}
					return sb.toString();
				}
			}

			return "";
		}
	}

}

