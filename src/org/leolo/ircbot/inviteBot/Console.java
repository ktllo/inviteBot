package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

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
	private HashMap<String,Command> commandList;
	
	public Console(Config config,Inviter inviter) {
		this.config = config;
		this.inviter = inviter;
		commandList =  new HashMap<>();
		this.registerCommand("echo", new EchoCommand());
		this.registerCommand("ping", new PingCommand());
		this.registerCommand("pong", new PingCommand());
		
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

		processMessage(
				cmd,
				event.getUser(),
				event.getBot(),
				event.getChannel().getName(),
				false);
	}

	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event){
		processMessage(event.getMessage(),
				event.getUser(),
				event.getBot(),
				event.getUser().getNick(),
				true);
	}

	private void processMessage(String message,User user,PircBotX bot,String source, boolean pm){
		CommandContext ctx = new CommandContext(user, bot, source, pm, message);
		logger.info("{}!{}@{} send command {}",user.getNick(),user.getLogin(),user.getHostmask(),message);
		String [] input = message.split(" ");
		Command cmd = commandList.get(input[0].toLowerCase());
		if(cmd!=null){
			cmd.run(ctx);
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ctx.getBuffer().toByteArray())));
			while(true){
				String line = null;
				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(line == null){
					break;
				}
				if(cmd.includeNick()){
					line = user.getNick() + ": " + line.trim();
				}
				bot.sendIRC().message(source, line);
			}
		}
	}
	
	public void registerCommand(String commandName,Command command){
		commandList.put(commandName.toLowerCase(), command);
	}
	
}

class EchoCommand implements Command{

	@Override
	public boolean requiresGlobalAdmin() {
		return false;
	}

	@Override
	public boolean isPmOnly() {
		return false;
	}

	@Override
	public String getName() {
		return "echo";
	}

	@Override
	public boolean includeNick() {
		return false;
	}

	@Override
	public void printHelp(PrintStream out) {
		//Echo doesn't have helps
	}

	@Override
	public void run(CommandContext ctx) {
		ctx.getOut().println(ctx.getMessage().substring(4).trim());
	}
	
}

class PingCommand implements Command{

	@Override
	public boolean requiresGlobalAdmin() {
		return false;
	}

	@Override
	public boolean isPmOnly() {
		return false;
	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public boolean includeNick() {
		return true;
	}

	@Override
	public void printHelp(PrintStream out) {
		//Echo doesn't have helps
	}

	@Override
	public void run(CommandContext ctx) {
		ctx.getOut().println("pong");
	}
	
}
