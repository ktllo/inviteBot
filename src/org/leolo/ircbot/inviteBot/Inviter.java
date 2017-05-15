package org.leolo.ircbot.inviteBot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;




import org.leolo.ircbot.inviteBot.util.ColorName;
import org.leolo.ircbot.inviteBot.util.Font;
import org.leolo.ircbot.inviteBot.util.UserUtil;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
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
	final long START = System.currentTimeMillis();
	private Config config;
	
	Inviter(Config config){
		this.config = config;
		new ItemCleaner(config,this).start();
	}
	
	public void onJoin(JoinEvent<PircBotX> event){
		System.err.println(event.getUser());
		if(event.getUser().getNick().equalsIgnoreCase(config.getNick())){
			return;
		}
		JoinRecord remove = null;
		for(JoinRecord record:pendingItems){
			if(record.getNick().equalsIgnoreCase(event.getUser().getNick())){
				record.remove(event.getChannel().getName());
				if(record.getTargetList().size() == 0){
					remove = record;
					if(!(config.isAdmin(event.getUser(),event.getChannel().getName()) ||
					config.isExempted(event.getUser(), event.getChannel().getName()))){
						event.getBot().sendRaw().rawLine("remove "+
								record.getSource()+" "+
								event.getUser().getNick()+
								" :Removed from holding channel");
						record.setStatus(JoinRecord.Status.REMOVE_PENDING);
						logger.info(USAGE,"User {} is removed from {}",
							UserUtil.getUserHostmask(event.getUser()),
							record.getSource()
						);
					}
				}
			}
		}
		if(remove != null ){
			pendingItems.remove(remove);
			return;
		}
		ArrayList<String> targetList = new ArrayList<>();
		for(Config.Channel c:config.getChannels()){
			if(event.getChannel().getName().equalsIgnoreCase(c.getListenChannel())){
				logger.info(USAGE, event.getUser().getNick()+"@"+event.getUser().getHostmask()+" joined "+event.getChannel().getName());
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
	
	private int invite(String nick,OutputIRC out){
		int count = 0;
		logger.debug("Handling "+nick);
		for(JoinRecord record:pendingItems){
			logger.debug("Processing "+record.getNick());
			if(!record.getNick().equalsIgnoreCase(nick))
				continue;
			logger.debug("Nick matched");
			for(String channel:record.getTargetList()){
				logger.debug("Status is "+record.getStatus().name());
				if(record.getStatus() != JoinRecord.Status.NORMAL)
					continue;
				out.invite(nick, channel);
				out.notice(nick, "You may now join "+channel);
				long time = (new Date().getTime() - record.getCreated().getTime())/1000;
				logger.info(USAGE,"Invited {} into channel {}"
						+ "( Time used {} seconds)",
						nick,channel,time);
				if(config.getReportChannel(channel) != null)
					out.message(config.getReportChannel(channel), 
							"User "+nick+" invited to "+channel);
				count++;
			}
			record.setStatus(JoinRecord.Status.INVITED);
		}
		return count;
	}
	
	protected int invite(String nick,OutputIRC out, User user, String source){
		int count = 0;
		logger.debug("Handling "+nick);
		boolean canDo = false;
		for(JoinRecord record:pendingItems){
			logger.debug("Processing "+record.getNick());
			if(!record.getNick().equalsIgnoreCase(nick))
				continue;
			logger.debug("Nick matched");
			if(config.isAdmin(user, source) || config.isListenChannel(source)){
				canDo = true;
			}else{
				continue;//Not authorized, but still may authorized in other case
			}
			for(String channel:record.getTargetList()){
				logger.debug("Status is "+record.getStatus().name());
				if(record.getStatus() != JoinRecord.Status.NORMAL)
					continue;
				out.invite(nick, channel);
				out.notice(nick, "You may now join "+channel);
				long time = (new Date().getTime() - record.getCreated().getTime())/1000;
				logger.info(USAGE,"Invited {} by {} into channel {}"
						+ "( Time used {} seconds)",
						nick,user.getNick(),channel,time);
				count++;
			}
			record.setStatus(JoinRecord.Status.INVITED);
		}
		if(!canDo)
			throw new UnauthorizedOperationException();
		return count;
	}
	
	public void onPart(PartEvent<PircBotX> event){
		for(JoinRecord r:pendingItems){
			if(r.getNick().equalsIgnoreCase(event.getUser().getNick()) &&
					r.getSource().equalsIgnoreCase(event.getChannel().getName())){
				r.setStatus(JoinRecord.Status.PARTED);
			}
		}
	}
	
	public void onConnect(final ConnectEvent<PircBotX> event){
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
				if(event.getReason().equalsIgnoreCase("Changing host")){
					r.setStatus(JoinRecord.Status.HOST_CHANGE);
				}else{
					r.setStatus(JoinRecord.Status.PARTED);
				}
			}
		}
	}
	
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event){
		for(JoinRecord record:pendingItems){
			if(record.getNick().equalsIgnoreCase(event.getUser().getNick())){
				//Target matched
				if(record.getQuestion().verifyAnswer(event.getMessage())){
					//Answer correct
					logger.info(USAGE, "User {} answer question correctly.",event.getUser().getNick());
					this.invite(event.getUser().getNick(), event.getBot().sendIRC());
				}else{
					//Answer incorrect
					logger.info(USAGE, "User {} answer question incorrectly.",event.getUser().getNick());
					event.getBot().sendIRC().notice(event.getUser().getNick(), "Sorry, the answer is incorrect. Please try again.");
				}
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
			
			record.setStatus(JoinRecord.Status.NORMAL);
			if(config.getReportChannel(event.getChannel().getName())!=null){
				event.getBot().sendIRC().message(
						config.getReportChannel(event.getChannel().getName()), 
						"User " + Font.color(ColorName.RED)+ Font.setBold() +
						UserUtil.getUserHostmask(event.getUser())
						+ Font.reset()+
						" joined "+event.getChannel().getName()+" Q:" + 
								record.getQuestion().getQuestion()+" Sol:"+
								record.getQuestion().getSolution()
				);
			}
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
		this.source = source;
	}

	public void remove(String name) {
		for(int i=0;i<targetList.size();i++){
			if(targetList.get(i).equalsIgnoreCase(name)){
				targetList.remove(i);
				return;
			}
		}
	}

	private ArrayList<String> targetList;
	private String source;
	private Question question;
	private String nick;
	private Date created;
	private Status status;
	
	
	enum Status{
		WAIT,
		HOST_CHANGE,
		NORMAL,
		INVITED,
		REMOVE_PENDING,
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
