package botContexts;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import bot.MessageSender;
import model.User;

public class SchedulingContext implements context{

	private final static int START = 0;
	private final static int SCHEDULINGTYPE = 1;
	private final static int ASKFORONTIME = 2;
	private final static int ASKFOROFFTIME = 3;
	private final static int CONFIRMATION = 4;
	private final static int GOODBYE = 5;
	
	
	
	private int stage=0;
	private User u;
	private Thread worker;
	
	private Boolean isBoth = false;
	
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
					schedulingtype(update);;
				}
			});
			break;
		case ASKFORONTIME:
			break;
		case ASKFOROFFTIME:
			break;
		case CONFIRMATION:
			break;
		case GOODBYE:
			break;
		}
		worker.start();
		
	}
	
	private void start(Update update){
		MessageSender.getInstance().simpleSend("Do you want to schedule a switch /on, /off or /both?", update);
		stage++;
	}
	
	private void schedulingtype(Update update){
		switch (update.getMessage().getText()){
		case "/on":
			stage=ASKFORONTIME;
			break;
		case "/off":
			stage=ASKFOROFFTIME;
			break;
		case "/both":
			stage=ASKFORONTIME;
			isBoth = true;
			break;
			default: 
				MessageSender.getInstance().simpleSend("Sorry, I did not understand, do you want to schedule a switch /on, /off or /both?", update);
		}
	}
}
