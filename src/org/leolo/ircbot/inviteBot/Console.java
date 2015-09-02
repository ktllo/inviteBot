package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.leolo.ircbot.inviteBot.Config.Channel;
import org.leolo.ircbot.inviteBot.util.ColorName;
import org.leolo.ircbot.inviteBot.util.UserUtil;
import org.leolo.ircbot.inviteBot.util.Font;
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
				event.getChannel().getName(),
				false);
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
				event.getUser().getNick(),
				true);
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
		new PongCommand(),
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

	private String processMessage(String message,User user,PircBotX bot,String source, boolean pm){
		CommandContext ctx = new CommandContext(user, bot, source);

		String args[] = message.split(" +");
		if(args.length == 0)
			return "";

		String cmdName = args[0].toLowerCase();

		// Remove first entry in array
		args = Arrays.asList(args).subList(1, args.length).toArray(new String[args.length - 1]);

		for(int i = 0; i < cmds.length; i++)
			if(cmds[i].toString().toLowerCase().equals(cmdName)) {
				if(cmds[i].requiresGlobalAdmin() && !config.isGlobalAdmin(ctx.user))
					return Font.color(ColorName.RED)+"ERROR: UNAUTHORIZED";
				if(cmds[i].isPmOnly() && !pm)
					return Font.color(ColorName.RED)+"ERROR: COMMAND CAN ONLY BE USED THROUGH PRIVATE MESSAGES";
						
				cmds[i].main(ctx, args);
				return ctx.getOutput();
			}

		return Font.color(ColorName.RED) + "ERROR: UNRECOGNIZED COMMAND '" + cmdName + "'";
	}

	private static class CommandContext {
		User user;
		PircBotX bot;
		String source;
		PrintStream out;

		private ByteArrayOutputStream buffer;

		CommandContext(User user, PircBotX bot, String source) {
			this.user = user;
			this.bot = bot;
			this.source = source;

			buffer = new ByteArrayOutputStream();
			out = new PrintStream(buffer, true);
		}

		public String getOutput() {
			return buffer.toString();
		}
	}
	
	private static abstract class Command {
		String name;
		boolean adminRequired;
		boolean pmOnly;
		String help;

		Command(String name, boolean adminRequired, String help) {
			this(name, adminRequired, false, help);
		}

		Command(String name, boolean adminRequired) {
			this(name, adminRequired, false, "");
		}

		Command(String name, boolean adminRequired, boolean pmOnly, String help) {
			this.name = name;
			this.adminRequired = adminRequired;
			this.pmOnly = pmOnly;
			this.help = help;
		}

		Command(String name, boolean adminRequired, boolean pmOnly) {
			this(name, adminRequired, pmOnly, "");
		}

		public abstract void main(CommandContext ctx, String args[]);

		public String toString() {
			return name;
		}

		public void printHelp(PrintStream out) {
			out.print(help);
		}

		public boolean hasHelp() {

			if(help.length() != 0)
				return true;

			// This is a perfectly reasonable way to do this, because I say so
			ByteArrayOutputStream testBuffer = new ByteArrayOutputStream();
			PrintStream testOut = new PrintStream(testBuffer, true);
			printHelp(testOut);
			return testBuffer.size() != 0;
		}

		public boolean requiresGlobalAdmin() {
			return adminRequired;
		}

		public boolean isPmOnly() {
			return pmOnly;
		}
	}

	private class MooCommand extends Command {

		MooCommand() { super("moo", false); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			ctx.out.print("mooo");
		}
	}

	private class VersionCommand extends Command {

		VersionCommand() { super("version", false, "Tells the current version."); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			ctx.out.print(InviteBot.getName() + " version " + InviteBot.getVersion());
		}
	}

	private class PingCommand extends Command {

		PingCommand() { super("ping", false, "Responds with 'pong', to check connection status."); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			ctx.out.print("pong");
		}
	}

	private class PongCommand extends Command {

		PongCommand() { super("pong", false); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			ctx.out.print("ping");
		}
	}

	private class InviteCommand extends Command {
		
		InviteCommand() { super("invite", false); }

		@Override
		public void printHelp(PrintStream out) {
			out.print(Font.color(ColorName.RED));
			out.print("ADMIN ONLY. " + Font.defaultColor());
			out.print("Invite user in holding channel without requiring them to answer the question\n");
			out.print("Parametres: List of nicks going to invite, sperated by space");
		}

		@Override
		public void main(CommandContext ctx, String args[]) {
			logger.info(USAGE,ctx.user.getNick()+" inviting others");
			for(int i=0;i<args.length;i++){
				try{
					int count = inviter.invite(args[i], ctx.bot.sendIRC(), ctx.user, ctx.source);
					logger.info(USAGE,"Invited {} to {} channels",args[i],""+count);
				}catch(UnauthorizedOperationException uoe){
					logger.warn("User {} attempted to invite {} but he/she wasn't "
							+ "authroized to do so.");
				}
			}
		}
	}

	private class InfoCommand extends Command {

		InfoCommand() { super("info", true, "Report uptime and the number of entry in the inviter"); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			long uptime = System.currentTimeMillis() - inviter.START;
			int upD = (int)(uptime/86400000);
			int upH = ((int)(uptime/3600000))%24;
			int upM = ((int)(uptime/60000))%60;
			int upS = ((int)(uptime/1000))%60;
			ctx.out.print("Inviter size is "+inviter.pendingItems.size()+"\n");
			ctx.out.print("Uptime is ");
			if( uptime > 86400000 )
				ctx.out.printf("%d days ", upD);
			if( uptime > 3600000 )
				ctx.out.printf("%d hours ", upH);
			if( uptime > 60000 )
				ctx.out.printf("%d minutes ", upM);
			if( uptime > 1000 )
				ctx.out.printf("%d seconds ", upS);
		}
	}

	private class ResendCommand extends Command {

		ResendCommand() { super("resend", false); }

		@Override
		public void printHelp(PrintStream out) {
			out.print(Font.color(ColorName.DARK_BLUE));
			out.print("Should use in holding channel OR PM only. " + Font.defaultColor());
			out.print("Send the question for the requesting user as message in channel, if used in channel.\n");
			out.print("Send in PM if the command is sent via PM");
		}

		@Override
		public void main(CommandContext ctx, String args[]) {
			for(JoinRecord record:inviter.pendingItems){
				if(record.getNick().equalsIgnoreCase(ctx.user.getNick())){
					ctx.out.print(record.getQuestion().getQuestion());
					return;
				}
			}
			ctx.out.print("Record not found. Please part and rejoin.");
		}
	}

	private class HelpCommand extends Command {

		HelpCommand() { super("help", false, "Presents help message."); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			if(args.length == 0){
				ctx.out.print("Available commands: ");
				boolean first = true;
				for(int i = 0; i < cmds.length; i++) {
					if(cmds[i].hasHelp()) {
						if(!first)
							ctx.out.print(", ");
						ctx.out.print(cmds[i]);
						first = false;
					}
				}
				return;
			}

			int i;
			for(i = 0; i < cmds.length; i++)
				if(args[0].equals(cmds[i].toString()))
					break;

			if(i == cmds.length || !cmds[i].hasHelp()) {
				ctx.out.print("No help for '" + args[0] + "'");
				return;
			}

			cmds[i].printHelp(ctx.out);
		}
	}

	private class NickCommand extends Command {

		NickCommand() { super("nick", true); }

		@Override
		public void printHelp(PrintStream out) {
			out.print(Font.color(ColorName.RED));
			out.print("Global admin only " + Font.defaultColor());
			out.print("Change the bot's nickname to the nickname given\n");
		}

		@Override
		public void main(CommandContext ctx, String args[]) {
			if(args.length == 0){
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: NICKNAME REQUIRED");
				return;
			}
			ctx.bot.sendIRC().changeNick(args[0]);
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!ctx.bot.getNick().equals(args[0])){
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: Nick change failed");
				return;
			}
		}
	}

	private class EchoCommand extends Command {

		EchoCommand() { super("echo", false); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			for(int i = 0; i < args.length; i++) {
				ctx.out.print(args[i]);
				if(i + 1 != args.length)
					ctx.out.print(" ");
			}
		}
	}

	private class BackupCommand extends Command {

		BackupCommand() { super("backup", true); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			ctx.out.print("backup as "+config.writeBackup());
		}
	}

	private class WhoamiCommand extends Command {

		WhoamiCommand() { super("whoami", false, "Tells if or how the bot recognizes you"); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			if(config.isGlobalAdmin(ctx.user)){
				ctx.out.print("Global Admin");
				return;
			}
			ArrayList<String> ch = new ArrayList<>();
			for(Channel c : config.getChannels()){
				if(c.isAdmin(ctx.user)){
					ch.add(c.getChannelName());
				}
			}
			if(ch.size() > 0){
				Iterator<String> ich = ch.iterator();
				ctx.out.print("Local admin of: ");
				while(ich.hasNext()){
					ctx.out.print(ich.next());
					if(ich.hasNext())
						ctx.out.print(", ");
				}
				return;
			}
			ctx.out.print("I don't reconize you");
		}
	}

	private class AddAdminCommand extends Command {

		AddAdminCommand() { super("addadmin", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 2) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			String user = args[1];
			String channel = args[0];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.addAdmin(user);
					logger.info(USAGE,"{} added to admin list of {} by {}",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " added to admin list";
				}
			}

			ctx.out.print(resp);
		}
	}

	private class RemoveAdminCommand extends Command {

		RemoveAdminCommand() { super("removeadmin", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {

			if(args.length != 2) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			String user = args[1];
			String channel = args[0];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.removeAdmin(user);
					logger.info(USAGE,"{} removed from admin list of {} by {} ",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					ctx.out.print(user + " removed from admin list");
					return;
				}
			}

			ctx.out.print(Font.color(ColorName.RED) + "ERROR: NOT AN ADMINISTRATOR, '" + user + "'");
		}
	}

	private class ListAdminCommand extends Command {

		ListAdminCommand() { super("listadmin", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {

			if(args.length != 1) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			for(Channel c:config.getChannels()){
				if(args[0].equals(c.getKey())){
					Iterator<String> i = c.getAdmins().iterator();
					while(i.hasNext()){
						ctx.out.print(i.next());
						if(i.hasNext())
							ctx.out.print(", ");
					}
				}
			}
		}
	}

	private class AddExemptCommand extends Command {

		AddExemptCommand() { super("addexempt", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 2) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			String user = args[1];
			String channel = args[0];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.addExempt(user);
					logger.info(USAGE,"{} added to exempt list of {} by {}",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " added to exempt list";
				}
			}

			ctx.out.print(resp);
		}
	}

	private class RemoveExemptCommand extends Command {

		RemoveExemptCommand() { super("removeexempt", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {
			String resp = "";

			if(args.length != 2) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			String user = args[1];
			String channel = args[0];

			for(Channel c:config.getChannels()){
				if(channel.equals(c.getKey())){
					c.removeExempt(user);
					logger.info(USAGE,"{} removed from exempt list of {} by {} ",
							user,channel,UserUtil.getUserHostmask(ctx.user));
					resp = user + " removed from exempt list";
				}
			}

			ctx.out.print(resp);
		}
	}

	private class ListExemptCommand extends Command {

		ListExemptCommand() { super("listexempt", true, true); }

		@Override
		public void main(CommandContext ctx, String args[]) {

			if(args.length != 1) {
				ctx.out.print(Font.color(ColorName.RED)+"ERROR: INCORRECT NUMBER OF PARAMETRES");
				return;
			}

			for(Channel c:config.getChannels()){
				if(args[0].equals(c.getKey())){
					Iterator<String> i = c.getExemptMask().iterator();
					while(i.hasNext()){
						ctx.out.print(i.next());
						if(i.hasNext())
							ctx.out.print(", ");
					}
					return;
				}
			}
		}
	}

}

