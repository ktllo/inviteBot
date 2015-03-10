package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;

import org.leolo.ircbot.inviteBot.util.Glob;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputIRC;

public class Console extends ListenerAdapter<PircBotX>{
	private ArrayList<ConsoleEvent> list;
	private Config config;
	
	public Console(Config config){
		list = new ArrayList<>();
		this.config=config;
	}
	
	public void onMessage(MessageEvent<PircBotX> event){
		if(event.getMessage().startsWith(config.escape)){
			for(ConsoleEvent e:list){
				if(e.userEvent(event.getMessage().substring(config.escape.length()),
						event.getChannel().getName(),
						event.getUser().getNick()+" : ", 
						event.getBot().sendIRC())){
					return;
				}
			}
		}
		for(String a:config.admins){
			if(Glob.match(a, event.getUser().getHostmask())){
				for(ConsoleEvent e:list){
					if(e.adminEvent(event.getMessage().substring(config.escape.length()),
							event.getChannel().getName(),
							event.getUser().getNick()+" : ", 
							event.getBot().sendIRC())){
						return;
					}
				}
			}
		}
	}
	
}

class BasicConsoleEvent implements ConsoleEvent{

	@Override
	public boolean userEvent(String command, String target, String prefix,
			OutputIRC out) {
		command = command.toLowerCase();
		if(command.startsWith("version")){
			out.message(target, prefix + InviteBot.BOT_VERSION);
			return true;
		}
		return false;
	}

	@Override
	public boolean adminEvent(String command, String target, String prefix,
			OutputIRC out) {
		// TODO Auto-generated method stub
		return false;
	}
	
}