package org.leolo.ircbot.inviteBot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class CommandContext {
	private User user;
	private PircBotX bot;
	private String source;
	private PrintStream out;
	private boolean fromPM;
	private String message;

	private ByteArrayOutputStream buffer;

	CommandContext(User user, PircBotX bot, String source, boolean fromPM,String message) {
		this.user = user;
		this.bot = bot;
		this.source = source;
		this.fromPM = fromPM;
		this.message = message;
		buffer = new ByteArrayOutputStream();
		out = new PrintStream(buffer, true);
	}

	public User getUser() {
		return user;
	}

	public PircBotX getBot() {
		return bot;
	}

	public String getSource() {
		return source;
	}

	public PrintStream getOut() {
		return out;
	}

	public boolean isFromPM() {
		return fromPM;
	}

	public ByteArrayOutputStream getBuffer() {
		return buffer;
	}

	public String getMessage() {
		return message;
	}

	
}