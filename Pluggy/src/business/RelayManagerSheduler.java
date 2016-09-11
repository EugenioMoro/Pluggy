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
		GPIOCommunication.getInstance().getRelay().toggle();
		toggleNotifier(eventType.FROMBOT, requestingUser);
	}

	public void scheduledToggle(){
		//TODO implement
	}
	public void toggleNotifier(eventType eventType, User requestingUser){
		
		final eventType e = eventType;
		
		if (requestingUser == null){ //this is to avoid null pointer exp. in case of a manual switch
			requestingUser = new User();
			requestingUser.setId(0);
		}
		
		final User u = requestingUser;

		toggleNotifier = new Thread(new Runnable() {

			@Override
			public void run() {
				
				
				String onoff="off"; //parsing state into user readable info
				if (getState()){
					onoff = "on";
				}

				//creating message
				String message = new String();

				switch (e){
				case SWITCHED:
					message = "Someone just turned " + onoff + " the plug from the switch";
					break;
				case FROMBOT:
					//notifies the user requesting toggle and crafts message for all the others
					MessageSender.getInstance().simpleSend("The plug is now switched "+ onoff, u);
					message="User " + u.getUsername() + " just switched "+ onoff + " the plug";
					break;
				case SCHEDULED:
					message = "Plug turned " + onoff + " as scheduled";

				}

				for (int i=0; i<Session.currentSession().getUsers().size(); i++){
					//if is subbed and not user requesting switch
					if (Session.currentSession().getUsers().get(i).getIsSub() && Session.currentSession().getUsers().get(i).getId() != u.getId()){
						MessageSender.getInstance().simpleSend(message, Session.currentSession().getUsers().get(i));
						
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
