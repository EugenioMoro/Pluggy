package botContexts;

import org.telegram.telegrambots.api.objects.Update;

import bot.MessageSender;
import business.RelayManagerSheduler;
import model.User;

public class SchedulingContext implements context{

	private final static int START = 0;
	private final static int SCHEDULINGTYPE = 1;
	private final static int ASKFORONTIME = 2;
	private final static int CHECKONTIME = 3;
	private final static int ASKFOROFFTIME = 4;
	private final static int CHECKOFFTIME = 5;
	private final static int ASKREPETITION = 6;
	private final static int CHECKREPETITION = 7;
	private final static int CONFIRMATION = 8;
	private final static int CHECKCONFIRMATION = 9;
	
	
	
	private int stage=0;
	private User u;
	private Thread worker;
	
	private Boolean isBoth = false;
	private Integer onMinutes;
	private Integer onHours;
	private Integer offMinutes;
	private Integer offHours;

	private Boolean repeat;
	
	public SchedulingContext(User u) {
		this.u=u;
	}
	
	@Override
	public void abort() {
		u.setIsInContext(false);
		u.setCanReply(true);
		u.setCurrentContext(null);
		
	}

	@Override
	public void work(Update update) {
		switch (stage){
		case START:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					start(update);
				}
			});
			break;
		case SCHEDULINGTYPE:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					schedulingtype(update);
				}
			});
			break;
		case ASKFORONTIME:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					askForOnTime(update);;
				}
			});
			break;
		case ASKFOROFFTIME:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					askForOffTime(update);
				}
			});
			break;
		case CHECKONTIME:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					checkOnTime(update);
				}
			});
			break;
		case CHECKOFFTIME:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					checkOffTime(update);
				}
			});
			break;
		case ASKREPETITION:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					askRepetition(update);
				}
			});
			break;
		case CHECKREPETITION:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					checkRepetition(update);
				}
			});
			break;
		case CONFIRMATION:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					confirmation(update);
				}
			});
			break;
		case CHECKCONFIRMATION:
			worker = new Thread(new Runnable() {
				@Override
				public void run() {
					checkConfirmation(update);
				}
			});
			break;
		}
		worker.start();
		
	}
	
	private void start(Update update){
		MessageSender.getInstance().simpleSend("Do you want to schedule a switch /on, /off or /both?", update);
		stage++;
		u.setCanReply(true);
		u.setIsInContext(true);
	}

	private void schedulingtype(Update update){
		u.setCanReply(false);
		switch (update.getMessage().getText()){
		case "/on":
			stage=ASKFORONTIME;
			askForOnTime(update);
			break;
		case "/off":
			stage=ASKFOROFFTIME;
			askForOffTime(update);
			break;
		case "/both":
			stage=ASKFORONTIME;
			isBoth = true;
			askForOnTime(update);
			break;
		default: 
			MessageSender.getInstance().simpleSend("Sorry, I did not understand, do you want to schedule a switch /on, /off or /both?", update);
			u.setCanReply(true);
			return;
		}
		
	}

	private void askForOnTime(Update update){
		MessageSender.getInstance().simpleSend("When should I turn on the plug? ", update);
		stage++;
		u.setCanReply(true);
	}
	
	private void askForOffTime(Update update){
		MessageSender.getInstance().simpleSend("When should I turn off the plug? ", update);
		stage++;
		u.setCanReply(true);
	}
	
	private void checkOnTime(Update update){
		u.setCanReply(false);
		//message should be mm:hh
		String message = update.getMessage().getText();
		Boolean parsed = false;
		if (message.matches("(.*):(.*)") && message.length() == 5){
			try {
				onHours=Integer.parseInt(message.substring(0, 2));
				onMinutes=Integer.parseInt(message.substring(3));
				parsed = true;
			} catch (Exception e) {
			
			}
		}
		
		if (parsed){
			if (RelayManagerSheduler.getInstance().taskExists(onHours, onMinutes)){
				MessageSender.getInstance().simpleSend("This task at that time already exists, please choose another time or /cancel", update);
				u.setCanReply(true);
				return;
			}
			if (isBoth){
				stage = ASKFOROFFTIME;
				askForOffTime(update);
			} else{
				stage = ASKREPETITION;
				askRepetition(update);
			}
		} else {
			MessageSender.getInstance().simpleSend("Sorry, I did not understand. Can you use hh:mm format, please?", update);
			u.setCanReply(true);
		}
	}
	
	private void checkOffTime(Update update){
		u.setCanReply(false);
		//message should be mm:hh
				String message = update.getMessage().getText();
				Boolean parsed = false;
				
				
				if (message.matches("(.*):(.*)") && message.length() == 5){
					try {
						offHours=Integer.parseInt(message.substring(0, 2));
						offMinutes=Integer.parseInt(message.substring(3));
						parsed = true;
					} catch (Exception e) {
					
					}
				}
				if (!parsed){
					MessageSender.getInstance().simpleSend("Sorry, I did not understand. Can you use hh:mm format, please?", update);
					u.setCanReply(true);
					return;
				}
				if (RelayManagerSheduler.getInstance().taskExists(offHours, offMinutes)){
					MessageSender.getInstance().simpleSend("This task at that time already exists, please choose another time or /cancel", update);
					u.setCanReply(true);
					return;
				}
				stage++;
				askRepetition(update);
	}
	
	private void askRepetition(Update update){
		MessageSender.getInstance().simpleSend("Do you want me to /repeat this everyday or just /once?", update);
		stage++;
		u.setCanReply(true);
	}
	
	private void checkRepetition(Update update){
		u.setCanReply(false);
		switch (update.getMessage().getText()){
		case "/repeat":
			repeat=true;
			stage++;
			confirmation(update);
			break;
		case "/once":
			repeat = false;
			stage++;
			confirmation(update);
			break;
		default:
			MessageSender.getInstance().simpleSend("Sorry, I did not undestrand. Do you want me to /repeat this everyday or just /once?", update);
			u.setCanReply(true);
		}
	}

	private void confirmation(Update update){
		String message;
		message = "I'm turning ";
		if (onHours != null){
			message = message + "on the plug at " + onHours + ":" + onMinutes;
		}
		if (isBoth){
			message = message + " and turning ";
		}
		if (offHours != null){
			message = message + "off the plug at " + offHours + ":" + offMinutes;
		}
		if(repeat){
			message = message + " everyday.";
		}
		MessageSender.getInstance().simpleSend(message, update);
		MessageSender.getInstance().simpleSend("Is it /correct ? /cancel", update);
		u.setCanReply(true);
		stage++;
	}
	
	private void checkConfirmation(Update update){
		u.setCanReply(false);
		switch (update.getMessage().getText()){
		case "/correct":
			if (onHours != null){
				RelayManagerSheduler.getInstance().addTask(onHours, onMinutes, true, repeat);
			}
			if (offHours != null){
				RelayManagerSheduler.getInstance().addTask(offHours, offMinutes, false, repeat);
			}
			MessageSender.getInstance().simpleSend("Ok, everything is set", update);
			abort();
			break;
		case "/cancel":
			abort();
			break;
		default:
			MessageSender.getInstance().simpleSend("Sorry, I did not understand. ", update);
			confirmation(update);
		}
	}
	
}

