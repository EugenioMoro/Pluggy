package business;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import bot.MessageSender;
import dao.GPIOCommunication;
import model.User;

public class RelayManagerSheduler {

	
	private static RelayManagerSheduler instance;
	private Thread toggleNotifier;
	
	public enum eventType {
		SWITCHED, //from on board physical switch
		SCHEDULED,
		FROMBOT //toggle requested from telegram conversation
	}
	
	
	@SuppressWarnings("unused")
	private ArrayList<ScheduledExecutorService> schedulers;
	
	public static RelayManagerSheduler getInstance(){
		if (instance==null){
			instance = new RelayManagerSheduler();
		}
		return instance;
	}

	public void addTask(int hours, int minutes, Boolean state){

	}
	
	public void switchToggle(){ //should be used only when switching from on board switch
		GPIOCommunication.getInstance().getRelay().toggle();
		toggleNotifier(eventType.SWITCHED, null);
	}
	
	public void fromBotToggle(User requestingUser){
		toggleNotifier(eventType.FROMBOT, requestingUser);
	}

	public void toggleNotifier(eventType eventType, User requestingUser){
		
		//TODO define enum as final and pass it into runnable for custom notification when relay is toggle from telegram because toggling user message should be different from non-toggling user's
		
		String onoff="off"; //parsing state into user readable info
		if (getState()){
			onoff = "on";
		}
		
		String message = new String();
		
		switch (eventType){
		case SWITCHED:
			message = "Someone just turned " + onoff + " the plug";
			break;
		case FROMBOT:
			//TODO implement this
			break;
		case SCHEDULED:
			message = "Plug turned " + onoff + " as scheduled";
			
		}
		final String finalmessage = message; //defining final variable for enclosed scope
		
		toggleNotifier = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (int i=0; i<Session.currentSession().getUsers().size(); i++){
					if (Session.currentSession().getUsers().get(i).getIsSub()){
						MessageSender.getInstance().simpleSend(finalmessage, Session.currentSession().getUsers().get(i));
					}
				}
			}
		});
		
		toggleNotifier.start();
	}
	
	public Boolean getState(){
		return GPIOCommunication.getInstance().getRelay().getState().isHigh();
	}

}
