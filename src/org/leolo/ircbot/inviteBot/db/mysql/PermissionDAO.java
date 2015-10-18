package org.leolo.ircbot.inviteBot.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.dbcp2.BasicDataSource;
import org.leolo.ircbot.inviteBot.model.Permission;
import org.leolo.ircbot.inviteBot.model.PermissionType;
import org.slf4j.LoggerFactory;

public class PermissionDAO implements org.leolo.ircbot.inviteBot.db.PermissionDAO {
	
	private BasicDataSource datasource;
	final org.slf4j.Logger logger = LoggerFactory.getLogger(PermissionDAO.class);
	
	
	PermissionDAO(BasicDataSource datasource){
		this.datasource = datasource;
	}
	
	@Override
	public Map<String, Permission> getPermissionList() {
		String sql = "SELECT `permission_id`,`permission_name`,`permission_type` FROM permissio WHERE `key`=?;";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs =  null;
		Map<String,Permission> result = new HashMap<>();
		try{
			conn = datasource.getConnection();
			pstmt  = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Permission p  =  new Permission();
				p.setName(rs.getString(2));
				p.setPermissionID(rs.getInt(1));
				p.setType(rs.getInt(3)==1?PermissionType.POSSTIVE:PermissionType.NEGATIVE);
				result.put(rs.getString(2), p);
			}
		}catch(SQLException sqle){
			logger.error(sqle.getMessage(),sqle);
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(pstmt!=null){
					pstmt.close();
					pstmt = null;
				}
				if(conn!=null){
					conn.close();
					conn  = null;
				}
			}catch(SQLException sqle){
				logger.error(sqle.getMessage(),sqle);
			}
		}
		return result;
	}

}
