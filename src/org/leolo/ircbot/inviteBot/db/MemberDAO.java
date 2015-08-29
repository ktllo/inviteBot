package org.leolo.ircbot.inviteBot.db;

import org.leolo.ircbot.inviteBot.model.Member;
import org.pircbotx.User;

public interface MemberDAO {
	
	Member findMember(User user);
	Member findMember(String username,String pasword);
	
	void updateMember(Member member);
	
	void addMember(Member member);
	
}
