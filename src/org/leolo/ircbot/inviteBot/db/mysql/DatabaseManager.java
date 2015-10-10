package org.leolo.ircbot.inviteBot.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.leolo.ircbot.inviteBot.db.ConfigDAO;
import org.leolo.ircbot.inviteBot.db.MemberDAO;
import org.leolo.ircbot.inviteBot.db.PermissionDAO;
import org.leolo.ircbot.inviteBot.db.RoleDAO;
import org.slf4j.LoggerFactory;

public class DatabaseManager implements org.leolo.ircbot.inviteBot.DatabaseManager {
	
	final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private String connectionString;
	
	Connection conn = null;
	BasicDataSource datasource = null;
	public String getConnectionString() {
		return connectionString;
	}
	
	@Override
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public int getModelLevel() {
		return 0;
	}

	@Override
	public MemberDAO getMemberDAO() {
		if(datasource == null){
			makeConnection();
		}
		return new org.leolo.ircbot.inviteBot.db.mysql.MemberDAO(datasource);
	}
	
	private void makeConnection(){
		datasource = new BasicDataSource();
		datasource.setDriverClassName("com.mysql.jdbc.Driver");
		datasource.setUrl(connectionString);
	}

	@Override
	public ConfigDAO getConfigDAO() {
		if(datasource == null){
			makeConnection();
		}
		return new org.leolo.ircbot.inviteBot.db.mysql.ConfigDAO(datasource);
	}

	@Override
	public RoleDAO getRoleDAO() {
		if(datasource == null){
			makeConnection();
		}
		return null;
	}

	@Override
	public PermissionDAO getPermissionDAO() {
		if(datasource == null){
			makeConnection();
		}
		return null;
	}

}
