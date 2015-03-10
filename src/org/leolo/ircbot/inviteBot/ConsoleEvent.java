package org.leolo.ircbot.inviteBot;

import org.pircbotx.output.OutputIRC;

public interface ConsoleEvent {
	public boolean userEvent(String command,String target,String prefix,OutputIRC out);
	public boolean adminEvent(String command,String target,String prefix,OutputIRC out);
}
