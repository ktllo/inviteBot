package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.leolo.ircbot.inviteBot.util.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.output.OutputIRC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Inviter extends ListenerAdapter<PircBotX>{
	
	final Logger logger = LoggerFactory.getLogger(Inviter.class);
	final static Marker USAGE = MarkerFactory.getMarker("usage");
	final static Marker JOIN = MarkerFactory.getMarker("join");
	
	protected Vector<JoinRecord> pendingItems = new Vector<>();
	
	private Config config;
	
	Inviter(Config config){
		this.config = config;
	}
	
	public void onJoin(JoinEvent<PircBotX> event){
		ArrayList<String> targetList = new ArrayList<>();
		for(Config.Channel c:config.getChannels()){
			if(event.getChannel().getName().equalsIgnoreCase(c.getListenChannel())){
				logger.info(USAGE, event.getUser().getNick()+"@"+event.getUser().getHostmask()+" joined "+event.getChannel());
				targetList.add(c.getChannelName());
			}
		}
		if(targetList.size() == 0)
			return;
		JoinRecord record = new JoinRecord(targetList,event.getUser().getNick(),event.getChannel().getName());
		pendingItems.add(record);
		new PendingMessage(event,record).start();
	}
	
	public void onMessage(MessageEvent<PircBotX> event){
		
	}
	
	public void onPart(PartEvent<PircBotX> event){
		for(JoinRecord r:pendingItems){
			if(r.getNick().equalsIgnoreCase(event.getUser().getNick()) &&
					r.getSource().equalsIgnoreCase(event.getChannel().getName())){
				r.setStatus(JoinRecord.Status.PARTED);
			}
		}
	}
	
	public void onConnect(ConnectEvent<PircBotX> event){
		for(String s:config.getChannelList()){
			event.getBot().sendIRC().joinChannel(s);
		}
	}
	
	public void onNickChange(NickChangeEvent<PircBotX> event){
		for(JoinRecord r:pendingItems){
			if(r.getNick().equalsIgnoreCase(event.getOldNick())){
				r.setNick(event.getNewNick());
			}
		}
	}
	
	public void onQuit(QuitEvent<PircBotX> event){
		for(JoinRecord r:pendingItems){
			if(r.getNick().equalsIgnoreCase(event.getUser().getNick())){
				r.setStatus(JoinRecord.Status.PARTED);
			}
		}
	}
	
	class PendingMessage extends Thread{
		
		private JoinEvent<PircBotX> event;
		private JoinRecord record;
		
		PendingMessage(JoinEvent<PircBotX> event,JoinRecord record){
			this.event = event;
			this.record = record;
		}
		
		public void run(){
			logger.info(USAGE, event.getUser().getNick()+" entering. Sleep for 2000ms");
			try{
				sleep(2000);
			}catch(InterruptedException ie){
				logger.error("Interrupt Received", ie);
			}
			if(record.getStatus() == JoinRecord.Status.PARTED){
				logger.info(USAGE, event.getUser().getNick()+" parted before sending notice.");
				pendingItems.remove(record);
				return;
			}
			ArrayList<String> removeList = new ArrayList<>();
			for(org.pircbotx.Channel c:event.getUser().getChannels()){
				for(String s:record.getTargetList()){
					if(c.getName().equalsIgnoreCase(s))
						removeList.add(s);
				}
			}
			record.getTargetList().removeAll(removeList);
			if(record.getTargetList().size() == 0){
				pendingItems.remove(record);
				logger.info(USAGE, event.getUser().getNick()+" already in all target");
				return;
			}
			logger.info(USAGE, "Sending notice to "+event.getUser().getNick());
			OutputIRC out = event.getBot().sendIRC();
			out.notice(event.getUser().getNick(), 
					config.getWelcomeMessage().replaceAll(
							"%t", record.getTargetList().get(0)
							));
			out.notice(event.getUser().getNick(), 
					"Please type /msg "+event.getBot().getNick()+" <answer> to answer the question");
			out.notice(event.getUser().getNick(), record.getQuestion().getQuestion());
			
			
		}

		public JoinEvent<PircBotX> getEvent() {
			return event;
		}

		public void setEvent(JoinEvent<PircBotX> event) {
			this.event = event;
		}

		public JoinRecord getRecord() {
			return record;
		}

		public void setRecord(JoinRecord record) {
			this.record = record;
		}
	}
	
}



class JoinRecord{
	public JoinRecord(ArrayList<String> targetList, String nick,String source) {
		this.targetList = targetList;
		this.nick = nick;
		this.created = new Date();
		this.status = Status.WAIT;
		this.question = Question.next();
		id = random.nextLong();
		this.source = source;
	}

	private static java.util.Random random;
	
	static{
		random = new java.util.Random();
	}
	
	private ArrayList<String> targetList;
	private String source;
	private Question question;
	private String nick;
	private Date created;
	private Status status;
	public final long id;
	
	
	enum Status{
		WAIT,
		HOST_CHANGE,
		NORMAL,
		INVITED,
		REMOVED,
		PARTED;
	}



	public ArrayList<String> getTargetList() {
		return targetList;
	}



	public String getNick() {
		return nick;
	}



	public Date getCreated() {
		return created;
	}



	public Status getStatus() {
		return status;
	}



	public Question getQuestion() {
		return question;
	}



	public String getSource() {
		return source;
	}



	protected void setStatus(Status status) {
		this.status = status;
	}



	protected void setTargetList(ArrayList<String> targetList) {
		this.targetList = targetList;
	}



	protected void setSource(String source) {
		this.source = source;
	}



	protected void setQuestion(Question question) {
		this.question = question;
	}



	protected void setNick(String nick) {
		this.nick = nick;
	}



	protected void setCreated(Date created) {
		this.created = created;
	}
}
