package org.leolo.ircbot.inviteBot.db.derby;
import java.sql.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.leolo.ircbot.inviteBot.db.ConfigDAO;
import org.leolo.ircbot.inviteBot.db.MemberDAO;
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
		if(connectionString == null || connectionString.length() == 0)
			connectionString  = "jdbc:derby:inviteBot.db;create=true";
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
		return null;
	}
	
	private void makeConnection(){
		datasource = new BasicDataSource();
		datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		datasource.setUrl(connectionString);
		datasource.setEnableAutoCommitOnReturn(false);
		buildDatabase();
	}

	@Override
	public ConfigDAO getConfigDAO() {
		if(datasource == null){
			makeConnection();
		}
		return new org.leolo.ircbot.inviteBot.db.derby.ConfigDAO(datasource);
	}
	
	private void buildDatabase(){
		Connection conn = null;
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		Statement stmt = null;
		try{
			conn = datasource.getConnection();
			dbmd = conn.getMetaData();
			rs = dbmd.getTables(null, null, "CONFIG",null);
			if(rs.next()){
				//Check schema version
			}else{
				logger.info("Creating Database");
				stmt = conn.createStatement();
				stmt.execute("CREATE TABLE CONFIG("
						+ "keyName VARCHAR(255) PRIMARY KEY,"
						+ "keyValue LONG VARCHAR NOT NULL)");
				stmt.execute("CREATE TABLE MEMBER("
						+ "member_id INT PRIMARY KEY,"
						+ "username VARCHAR(255) UNIQUE NOT NULL,"
						+ "password VARCHAR(255) NOT NULL)");
				stmt.execute("CREATE TABLE member_hostmask("+
						"member_id INT NOT NULL,"+
					    "hostmask_id INT NOT NULL,"+
					    "hostmask VARCHAR(512),"+
					    "PRIMARY KEY(member_id,hostmask_id)"+
						")");
				//Set a schema version, for future DB schema upgrade
				stmt.execute("INSERT INTO CONFIG (keyName,keyValue) VALUES (\'SCHEMA_VERSION\',\'1\')");
			}
		}catch(SQLException sqle){
			logger.error("SQLException",sqle);
		}finally{
			try{
				conn.commit();
				if(stmt!=null){
					stmt.close();
					stmt = null;
				}
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(conn!=null){
					conn.close();
					conn = null;
				}
			}catch(SQLException sqle){
				logger.error("SQLException",sqle);
			}
		}
	}

}