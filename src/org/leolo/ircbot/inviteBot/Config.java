package org.leolo.ircbot.inviteBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.leolo.ircbot.inviteBot.util.Glob;
import org.pircbotx.User;

class Config {
	class Channel {
		private String channelName;
		private String[] exemptMask;
		private String[] exemptNick;
		private String listenChannel;
		private String reportChannel;

		Channel(java.util.Properties setting, String key) {
			this.channelName = setting.getProperty(key + ".join");
			this.listenChannel = setting.getProperty(key + ".listen");
			exemptMask = setting.getProperty(key + ".exemptMask", "")
					.split(",");
			exemptNick = setting.getProperty(key + ".exempt", "").split(",");
			this.reportChannel = setting.getProperty(key + ".report","");
		}

		public String getChannelName() {
			return channelName;
		}

		public String[] getExemptMask() {
			return exemptMask;
		}

		public String[] getExemptNick() {
			return exemptNick;
		}

		public String getListenChannel() {
			return listenChannel;
		}

		public String getReportChannel() {
			return reportChannel;
		}


	}
	private String[] admins;
	private String escape;
	private String ident;
	private ArrayList<Channel> channelList;
	private String nick;
	private String password;
	private int port;
	private String server;
	private String welcomeMessage;

	private boolean ssl;

	public Config() throws FileNotFoundException, IOException {
		this("settings.properties");
	}

	public Config(String file) throws FileNotFoundException, IOException {
		channelList = new ArrayList<>();
		java.util.Properties setting = new java.util.Properties();
		setting.load(new java.io.FileInputStream(file));
		server = setting.getProperty("server", "chat.freenode.net");
		try {
			port = Integer.parseInt(setting.getProperty("port"));
		} catch (NumberFormatException nfe) {
			port = 6667;
		}
		password = setting.getProperty("password", "");
		nick = setting.getProperty("nick", "inviteBot");
		ident = setting.getProperty("ident", "pircbot");
		ssl = Boolean.parseBoolean(setting.getProperty("ssl", "false"));
		String[] keys = setting.getProperty("key", "").split(",");
		for (String s : keys) {
			channelList.add(new Channel(setting, s));
		}
		escape = setting.getProperty("escape", "!");
		welcomeMessage = setting.getProperty("welcome", "Welcome to %t!");
		admins = setting.getProperty("admin", "").split(",");
	}

	public String[] getAdmins() {
		return admins;
	}

	public String getEscape() {
		return escape;
	}

	public String getIdent() {
		return ident;
	}

	public ArrayList<Channel> getList() {
		return channelList;
	}

	public String getNick() {
		return nick;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getServer() {
		return server;
	}

	public boolean isSSL() {
		return ssl;
	}
	
	protected java.util.List<Channel> getChannels(){
		return channelList;
	}
	
	public String[] getChannelList(){
		ArrayList<String> list = new ArrayList<>();
		//TODO: Create list
		for(Channel c:channelList){
			if(!list.contains(c.channelName))
				list.add(c.channelName);
			if(!list.contains(c.listenChannel))
				list.add(c.listenChannel);
			if(!list.contains(c.reportChannel) && c.reportChannel.length() > 0)
				list.add(c.reportChannel);
		}
		String [] result = new String[list.size()];
		for(int i=0;i<list.size();i++){
			result[i]=list.get(i);
		}
		return result;
		
	}
	
	public boolean isAdmin(User user){
		for(String admin:admins){
			if(Glob.match(admin, user.getNick()))
				return true;
			if(Glob.match(admin, user.getHostmask()))
				return true;
		}
		return false;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

}