package org.leolo.ircbot.inviteBot;

import java.util.Vector;

import org.jibble.pircbot.*;

public class InviteBot extends PircBot {
	
	public InviteBot(){
		this.setName("BotI");
		this.setLogin("invitebot");
		answerList = new Vector<>();
	}
	
	public static void main(String[] args) throws Exception {    
        // Now start our bot up.
        InviteBot bot = new InviteBot();
        // Enable debugging output.
        bot.setVerbose(true);
        // Connect to the IRC server.
        bot.connect("chat.freenode.net");
        bot.start();
    }
	
	public void start(){
		for(int i=0;i<listen.length;i++){
			this.joinChannel(listen[i]);
			this.joinChannel(join[i]);
		}
	}
	
	private String [] listen = {"###ktlloBotTest2"}; 
	private String [] join = {"###ktlloBotTest"};
	
	private Vector<Pair> answerList;
	
	private int search(String channel,String [] list){
		for(int i=0;i<list.length;i++){
			if(list[i].equalsIgnoreCase(channel))
				return i;
		}
		return -1;
	}
	
	public void onJoin(String channel, String sender, String login, String hostname){
		if(search(channel,listen)>=0){
			this.sendNotice(sender, "Welcome to "+join[search(channel,listen)]);
			this.sendNotice(sender, "Please answer the following question by /msg "+this.getNick()+" <answer> to join the channel");
			this.sendNotice(sender, "Twenty - 9 = ?");
			answerList.add(new Pair(sender,11,join[search(channel,listen)]));
		}
	}
	
	public void onPrivateMessage(String sender, String login, String hostname, String message){
		int solution;
		try{
			solution = Integer.parseInt(message);
		}catch(NumberFormatException nfe){
			this.sendNotice(sender, "Illegal answer");
			return;
		}
		for(int i=0;i<answerList.size();i++){
			if(answerList.get(i).getNick().equalsIgnoreCase(sender)){
				if(answerList.get(i).getAnswer() == solution){
					this.sendInvite(sender, answerList.get(i).getChannel());
					
					this.sendNotice(sender, "You may now join "+answerList.get(i).getChannel());
					answerList.remove(i);
				}
			}
		}
	}
}
