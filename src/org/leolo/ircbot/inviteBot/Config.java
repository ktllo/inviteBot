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
		private String[] admins;
		private String adminkey;
		
		Channel(java.util.Properties setting, String key) {
			this.channelName = setting.getProperty(key + ".join");
			this.listenChannel = setting.getProperty(key + ".listen");
			exemptMask = setting.getProperty(key + ".exemptMask", "")
					.split(",");
			exemptNick = setting.getProperty(key + ".exempt", "").split(",");
			this.reportChannel = setting.getProperty(key + ".report","");
			this.admins = setting.getProperty(key+",admin", "").split(",");
			this.adminkey = setting.getProperty(key+".key", null);
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

		public boolean isAdmin(User user){
			for(String admin:admins){
				if(Glob.match(admin, user.getNick()+"!"+user.getLogin()+"@"+user.getHostmask()))
					return true;
				
			}
			return false;
		}

		protected String getAdminkey() {
			return adminkey;
		}
		
		public boolean isExempted(User user){
			for(String nick:exemptNick){
				if(user.getNick().equalsIgnoreCase(nick))
					return true;
			}
			String hostmask = user.getNick()+"!"+user.getLogin()+"@"+user.getHostmask();
			for(String mask:exemptMask){
				if(Glob.match(mask, hostmask))
					return true;
			}
			return false;
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
	
	public java.util.List<Channel> getChannels(){
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
	
	/**
	 * To see is user <b>GLOBAL ADMIN</b>
	 * @param user User to be checked
	 * @return true if user is super admin
	 */
	@Deprecated
	public boolean isAdmin(User user){
		if(user.isIrcop())
			return true;
		for(String admin:admins){
			if(Glob.match(admin, user.getNick()+"!"+user.getLogin()+"@"+user.getHostmask()))
				return true;
			
		}
		return false;
	}
	
	public boolean isAdmin(User user,String channel){
		if(isAdmin(user))
			return true;
		for(Channel c:channelList){
			if(channel.equalsIgnoreCase(c.channelName) ||
					channel.equalsIgnoreCase(c.listenChannel) ||
					channel.equalsIgnoreCase(c.reportChannel)){
				if(c.isAdmin(user))
					return true;
			}	
		}
		return false;
	}
	
	public boolean isAdmin(String key,String channel){
		for(Channel c:channelList){
			if(channel.equalsIgnoreCase(c.channelName) ||
					channel.equalsIgnoreCase(c.listenChannel) ||
					channel.equalsIgnoreCase(c.reportChannel)){
				if(c.adminkey.equalsIgnoreCase(key))
					return true;
			}	
		}
		return false;
	}
	
	public boolean isGlobalAdmin(User user){
		return isAdmin(user);
	}
	
	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public boolean isListenChannel(String source) {
		for(Channel channel:channelList){
			if(channel.listenChannel.equalsIgnoreCase(source))
				return true;
		}
		return false;
	}
	
	public boolean isExempted(User user,String target){
		for(Channel channel:channelList){
			if(channel.channelName.equalsIgnoreCase(target)){
				return channel.isExempted(user);
			}
		}
		return false;
	}
	
	public String getReportChannel(String channel){
		for(Channel c:channelList){
			if(c.channelName.equalsIgnoreCase(channel) || c.listenChannel.equalsIgnoreCase(channel)){
				String target = c.reportChannel;
				if(target.length() == 0)
					return null;
				return target;
			}
		}
		return null;
	}
	
}