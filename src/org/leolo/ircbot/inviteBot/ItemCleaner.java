package org.leolo.ircbot.inviteBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import java.util.ArrayList;

class ItemCleaner extends Thread {
	
	final Logger logger = LoggerFactory.getLogger(ItemCleaner.class);
	final static Marker USAGE = MarkerFactory.getMarker("usage");
	final static Marker JOIN = MarkerFactory.getMarker("join");
	
	public ItemCleaner(Config config, Inviter inviter) {
		this.config = config;
		this.inviter = inviter;
	}
	private Config config;
	private Inviter inviter;
	
	public void run(){
		while(true){
			ArrayList<JoinRecord> pending = new ArrayList<>();
			for(JoinRecord record:inviter.pendingItems){
				if(record.getStatus() == JoinRecord.Status.PARTED ||
						record.getStatus() == JoinRecord.Status.REMOVE_PENDING){
					pending.add(record);
				}	
			}
			inviter.pendingItems.removeAll(pending);
			if( pending.size() > 0)
				logger.info(USAGE, ""+pending.size()+" items removed form pending items");
			try{
				sleep(60000);//Sleep for a minutes
			}catch(InterruptedException ie){
				logger.error("Interrupted", ie);
			}
		}
	}
}
