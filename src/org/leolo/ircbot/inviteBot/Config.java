package org.leolo.ircbot.inviteBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.leolo.ircbot.inviteBot.util.Glob;
import org.pircbotx.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Config {
	final Logger logger = LoggerFactory.getLogger(Config.class);
	class Channel {
		private String channelName;
		private String[] exemptMask;
		private String[] exemptNick;
		private String listenChannel;
		private String reportChannel;
		private String[] admins;
		private String adminkey;
		Channel(Properties setting, String key) {
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
	private String username;
	private String password;
	private int port;
	private String server;
	private String welcomeMessage;
	private Properties prop;
	private boolean ssl;

	public Config() throws FileNotFoundException, IOException {
		this("settings.properties");
	}

	public Config(String file) throws FileNotFoundException, IOException {
		channelList = new ArrayList<>();
		java.util.Properties setting = new java.util.Properties();
		setting.load(new java.io.FileInputStream(file));
		prop = setting;
		server = setting.getProperty("server", "chat.freenode.net");
		try {
			port = Integer.parseInt(setting.getProperty("port"));
		} catch (NumberFormatException nfe) {
			port = 6667;
		}
		password = setting.getProperty("password", "");
		nick = setting.getProperty("nick", "inviteBot");
		username = setting.getProperty("user", nick);
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
		Hashtable<String,String> list = new Hashtable<>();
		for(Channel c:channelList){
			list.put(c.channelName, c.channelName);
			list.put(c.listenChannel,c.listenChannel);
			if( c.reportChannel.length() > 0)
				list.put(c.reportChannel,c.reportChannel);
		}
		String [] result = new String[list.size()];
		list.values().toArray(result);
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
		logger.warn("User {}!{}@{} checking admin for channel {}", user.getNick(),user.getLogin(),user.getHostmask(), channel);
		if(isAdmin(user))
			return true;
		for(Channel c:channelList){
//			if(channel.equalsIgnoreCase(c.channelName) ||
//					channel.equalsIgnoreCase(c.listenChannel) ||
//					channel.equalsIgnoreCase(c.reportChannel)){
				logger.warn("Checking channel {}",c.channelName);
				if(c.isAdmin(user)){
					logger.warn("Found admin right in channel {} for {}",
							c.channelName,user.getNick());
					return true;
				}
//			}	
		}
		return false;
	}
	
	public boolean isAdmin(String key,String channel){
		logger.warn("Checking admin key {} in channel {}",key,channel);
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
	
	public String writeBackup(){
		String target = "settings.properties."+Integer.toHexString(prop.hashCode());
		try {
			prop.store(new PrintWriter(target), "Backup at "+new Date());
		} catch (IOException e) {
			logger.error(e.toString(), e);
			e.printStackTrace();
		}
		return target;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}