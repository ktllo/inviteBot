package org.leolo.ircbot.inviteBot;

import org.leolo.ircbot.inviteBot.model.Member;

public class MemberCacheItem {
	
	private Member member;
	
	private String hostmask;
	
	private long lastWrite;
	
	private MemberCacheItemType type;
	
	public static final long CACHE_VALID_TIME = 900000; //in ms, 900s = 15mintes

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getHostmask() {
		return hostmask;
	}

	public void setHostmask(String hostmask) {
		this.hostmask = hostmask;
	}

	public long getLastWrite() {
		return lastWrite;
	}

	public void setLastWrite(long lastWrite) {
		this.lastWrite = lastWrite;
	}
	
	public boolean isValid(){
		return (System.currentTimeMillis()-lastWrite) <= CACHE_VALID_TIME || type == MemberCacheItemType.PASSWORD_IDENTIFY;
	}

	public MemberCacheItemType getType() {
		return type;
	}

	public void setType(MemberCacheItemType type) {
		this.type = type;
	}
	
}
