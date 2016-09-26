package business;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bot.MessageSender;
import dao.GPIOCommunication;
import dao.LedControl;
import model.RelayScheduledTask;
import model.User;

public class RelayManagerSheduler {

	
	private static RelayManagerSheduler instance;
	private Thread toggleNotifier;
	
	public enum eventType {
		SWITCHED, //from on board physical switch
		SCHEDULED,
		FROMBOT //toggle requested from telegram conversation
	}
	
	
	private ArrayList<RelayScheduledTask> scheduledTasks = new ArrayList<RelayScheduledTask>();
	
	public static RelayManagerSheduler getInstance(){
		if (instance==null){
			instance = new RelayManagerSheduler();
		}
		return instance;
	}

	public void addTask(int hours, int minutes, Boolean state, Boolean repeats){
		
		final Boolean finalState = state;
		final int finalHours = hours;
		final int finalMinutes = minutes;
		
		//getting local datetime
		LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Europe/Rome");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        
        //setting next time
        ZonedDateTime zonedNext ;
        zonedNext = zonedNow.withHour(hours).withMinute(minutes).withSecond(0);
        if(zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);
        
        //getting delay
        Duration duration = Duration.between(zonedNow, zonedNext);
        long initalDelay = duration.getSeconds();
        
        Runnable switcher = new Runnable() {
			
			@Override
			public void run() {
				scheduledSwitch(finalState);
				if (!repeats){
					removeScheduler(finalHours, finalMinutes);
				}
			}
		};
		
		//setting scheduler
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);     
        if (repeats){
        scheduler.scheduleAtFixedRate(switcher, initalDelay, 24*60*60, TimeUnit.SECONDS);
        } else {
        	scheduler.schedule(switcher, initalDelay, TimeUnit.SECONDS);
        }
        
        RelayScheduledTask scheduledTask = new RelayScheduledTask(scheduler, finalState, hours, minutes, repeats);
        scheduledTasks.add(scheduledTask);
	}
	
	public void switchToggle(){ //should be used only when switching from on board switch
		GPIOCommunication.getInstance().getRelay().toggle();
		toggleNotifier(eventType.SWITCHED, null);
		LedControl.getInstance().shortBlink();
	}
	
	public void fromBotToggle(User requestingUser){
		GPIOCommunication.getInstance().getRelay().toggle();
		toggleNotifier(eventType.FROMBOT, requestingUser);
		LedControl.getInstance().shortBlink();
	}

	public void scheduledSwitch(Boolean state){
		if(getState() != state){
			GPIOCommunication.getInstance().getRelay().toggle();
			toggleNotifier(eventType.SCHEDULED, null);
			System.out.println("Executing scheduled switch");
			LedControl.getInstance().shortBlink();
		}
		
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
					//if is subbed and not user requesting switch in case of in telegram request
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

	public void removeScheduler(int hours, int minutes){
		for (int i = 0; i<scheduledTasks.size(); i++){
			if (scheduledTasks.get(i).getHours() == hours && scheduledTasks.get(i).getMinutes() == minutes){
				scheduledTasks.remove(i);
			}
		}
	}

	public Boolean taskExists(int hours, int minutes){
		if (scheduledTasks.isEmpty()){
			return false;
		}
		for (int i=0; i<scheduledTasks.size(); i++){
			if (scheduledTasks.get(i).getHours() == hours && scheduledTasks.get(i).getMinutes() == minutes){
				return true;
			}
		}
		return false;
	}

	public ArrayList<RelayScheduledTask> getScheduledTasks() {
		return scheduledTasks;
	}
}
