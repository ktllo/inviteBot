package org.leolo.ircbot.inviteBot;

import java.io.FileNotFoundException;
import java.io.IOException;

class Config{
	String server;
	int port;
	String password;
	String nick;
	String ident;
	boolean ssl;
	String [] listenList;
	public Config() throws FileNotFoundException, IOException{
		this("settings.properties");
	}
	
	public Config(String file) throws FileNotFoundException, IOException{
		java.util.Properties setting = new java.util.Properties();
		setting.load(new java.io.FileInputStream(file));
		server = setting.getProperty("server","chat.freenode.net");
		try{
			port = Integer.parseInt(setting.getProperty("port"));
		}catch(NumberFormatException nfe){
			port = 6667;
		}
		password = setting.getProperty("password","");
		nick = setting.getProperty("nick","inviteBot");
		ident = setting.getProperty("ident","pircbot");
		ssl = Boolean.parseBoolean(setting.getProperty("ssl", "false"));
		String listen = setting.getProperty("listen","");
		listenList = listen.split(",");
	}
	
}