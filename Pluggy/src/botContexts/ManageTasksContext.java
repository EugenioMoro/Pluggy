package botContexts;

import org.telegram.telegrambots.api.objects.Update;

import bot.MessageSender;
import business.RelayManagerSheduler;
import business.UserManager;
import model.RelayScheduledTask;
import model.User;

public class ManageTasksContext implements context {
	
	private static final int START = 0;
	private static final int CONFIRMDELETE = 1;

	
	private int stage=0;
	private User u;
	private Thread worker;

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
		case CONFIRMDELETE:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					confirmDelete(update);
				}
			});
			break;
		}
		worker.start();

	}

	private void start(Update update){
		u=UserManager.getInstance().getUserByUpdate(update);
		u.setIsInContext(true);
		u.setCanReply(false);

		if (RelayManagerSheduler.getInstance().getScheduledTasks().isEmpty()){
			MessageSender.getInstance().simpleSend("There is not any sheduled task", u);
			abort();
			return;
		}
		
		MessageSender.getInstance().simpleSend("Select which sheduled task you want to delete", u);
		RelayScheduledTask task;
		
		for (int i=0; i<RelayManagerSheduler.getInstance().getScheduledTasks().size(); i++){
			task = RelayManagerSheduler.getInstance().getScheduledTasks().get(i);
			String state;
			if (task.getState()){
				state = "on";
			} else {
				state = "off";
			}
			MessageSender.getInstance().simpleSend("/" + (i+1) + " turning " + state + " at " + devilIsInTheDetails(task.getHours()) + ":" + devilIsInTheDetails(task.getMinutes()) , u);
		}
		MessageSender.getInstance().simpleSend("/cancel", u);
		u.setCanReply(true);
		stage++;
		
	}
	
	private void confirmDelete(Update update){
		u.setCanReply(false);
		String recievedMessage = update.getMessage().getText();
		Boolean parsed = true;
		int i;
		try {
			i = Integer.parseInt(recievedMessage.substring(1));
			parsed = true;
		} catch (NumberFormatException e){
			
		}
		i = Integer.parseInt(recievedMessage.substring(1));
		i--;
		if(recievedMessage.startsWith("/") && parsed && i<=RelayManagerSheduler.getInstance().getScheduledTasks().size()){
			RelayManagerSheduler.getInstance().getScheduledTasks().remove(i);
			MessageSender.getInstance().simpleSend("Task removed", u);
			abort();
		} else {
			MessageSender.getInstance().simpleSend("Sorry, I did not understand", u);
			stage = 0;
			start(update);
		}
	}
	
	private String devilIsInTheDetails(int i){
		//converting int <=9 into string with a leading 0, else just parse
		if(i<=9){
			return "0" + String.valueOf(i); 
		}
		return String.valueOf(i);
	}

}
