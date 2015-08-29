package org.leolo.ircbot.inviteBot.db.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.leolo.ircbot.inviteBot.model.Member;
import org.pircbotx.User;
import java.sql.*;

public class MemberDAO implements org.leolo.ircbot.inviteBot.db.MemberDAO {
	
	private Connection conn;
	private BasicDataSource datasource;
	
	
	MemberDAO(BasicDataSource datasource){
		this.datasource = datasource;
	}
	
	@Override
	public Member findMember(User user) {
		try {
			Connection conn = datasource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT NOW() AS date;");
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			System.out.println(rs.getString("date"));
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Member findMember(String username, String pasword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMember(Member member) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMember(Member member) {
		// TODO Auto-generated method stub

	}

}
