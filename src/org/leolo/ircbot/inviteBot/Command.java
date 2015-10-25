package org.leolo.ircbot.inviteBot;

import java.io.PrintStream;

public interface Command {
	
	public boolean requiresGlobalAdmin();
	public boolean isPmOnly();
	public String getName();
	public boolean includeNick();
	public void printHelp(PrintStream out);
	
	public void run(CommandContext ctx);
}
