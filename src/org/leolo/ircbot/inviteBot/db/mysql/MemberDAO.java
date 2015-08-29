package org.leolo.ircbot.inviteBot.db.mysql;

import org.leolo.ircbot.inviteBot.model.Member;
import org.pircbotx.User;
import java.sql.*;

public class MemberDAO implements org.leolo.ircbot.inviteBot.db.MemberDAO {
	
	private Connection conn;
	
	MemberDAO(Connection conn){
		this.conn = conn;
		System.out.println("DAO created");
	}
	
	@Override
	public Member findMember(User user) {
		// TODO Auto-generated method stub
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
