package bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import botContexts.WelcomeContext;
import business.Session;
import business.UserManager;
import model.User;

public class UpdatesHandler extends TelegramLongPollingBot {

	
	
	
	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "Pluggy";
	}

	@Override
	public void onUpdateReceived(Update update) {
		System.out.println("\nupdate " + update.getUpdateId().toString() + " recieved");
		if (update.hasMessage() && update.getMessage().hasText()){
			
			//check if new user
			User u = UserManager.getInstance().getUserById(update.getMessage().getFrom().getId());
			if(u == null){
				//if so: create new, set welcome as a context 
				u = new User();
				u.setId(update.getMessage().getFrom().getId());
				u.setCurrentContext(new WelcomeContext(u));
				u.setIsInContext(true);
				Session.currentSession().getUsers().add(u);
				System.out.println("User " + u.getId() +" is new, setting welcome context");
			}
			
			//check if expecting reply
			if(!u.canReply()){
				System.out.println("Reply not expected, ignoring update");
			} else {

				//check if user is in context, if not interpret message and assign context
				if(!u.isInContext()){
					//TODO interpreter logic
					System.out.println("No active context for user " + u.getId() + " - interpreting...");

				}

				//this should be the last thing to be called
				u.getCurrentContext().work(update);
				System.out.println("Update dispatched to worker");
			}
		}
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return Session.currentSession().getToken();
	}


	public void abort(){
		//TODO write handler level abort method
	}


}
