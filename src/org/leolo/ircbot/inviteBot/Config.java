package org.leolo.ircbot.inviteBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class Config {
	class Channel {
		private String channelName;
		private String[] exemptMask;
		private String[] exemptNick;
		private String listenChannel;

		Channel(java.util.Properties setting, String key) {
			this.channelName = setting.getProperty(key + ".join");
			this.listenChannel = setting.getProperty(key + ".listen");
			exemptMask = setting.getProperty(key + ".exemptMask", "")
					.split(",");
			exemptNick = setting.getProperty(key + ".exempt", "").split(",");

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

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Channel [channelName=");
			builder.append(channelName);
			builder.append(", listenChannel=");
			builder.append(listenChannel);
			builder.append(", exemptMask=");
			builder.append(Arrays.toString(exemptMask));
			builder.append(", exemptNick=");
			builder.append(Arrays.toString(exemptNick));
			builder.append("]");
			return builder.toString();
		}

	}
	private String[] admins;
	private String escape;
	private String ident;
	private ArrayList<Channel> list;
	private String nick;
	private String password;
	private int port;
	private String server;

	private boolean ssl;

	public Config() throws FileNotFoundException, IOException {
		this("settings.properties");
	}

	public Config(String file) throws FileNotFoundException, IOException {
		list = new ArrayList<>();
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
			list.add(new Channel(setting, s));
		}
		escape = setting.getProperty("escape", "!");
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
		return list;
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

}