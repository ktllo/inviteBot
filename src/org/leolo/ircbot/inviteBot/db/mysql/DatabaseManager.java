package org.leolo.ircbot.inviteBot.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.leolo.ircbot.inviteBot.db.MemberDAO;
import org.slf4j.LoggerFactory;

public class DatabaseManager implements org.leolo.ircbot.inviteBot.DatabaseManager {
	
	final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private String connectionString;
	
	Connection conn = null;
	
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
		if(conn == null){
			makeConnection();
		}
		return new org.leolo.ircbot.inviteBot.db.mysql.MemberDAO(conn);
	}
	
	private void makeConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(connectionString);
		} catch (ClassNotFoundException e) {
			logger.error("Cannot load driver");
		} catch (SQLException e) {
			logger.error("Cannot connect to database",e);
		}
	}

}
