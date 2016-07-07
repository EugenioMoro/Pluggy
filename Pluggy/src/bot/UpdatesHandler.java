package bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import botContexts.FirstTOnContext;
import botContexts.context;
import business.Session;

public class UpdatesHandler extends TelegramLongPollingBot {

	private Boolean replyExpected=true;
	private Boolean isInContext;
	private context currentContext;
	
	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "Pluggy";
	}

	@Override
	public void onUpdateReceived(Update update) {
		System.out.println("update recieved");
		System.out.println(update.getMessage().getText());
		if (replyExpected && update.hasMessage() && update.getMessage().hasText()){
			System.out.println("update to worker " + update.getMessage().getText());
			FirstTOnContext.getThis().work(update);
		}
		
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return Session.currentSession().getToken();
	}

	public Boolean getReplyExpected() {
		return replyExpected;
	}

	public void setReplyExpected(Boolean replyExpected) {
		this.replyExpected = replyExpected;
	}
	
	public void abort(){
		//TODO write handler level abort method
	}

	public Boolean isInContext() {
		return isInContext;
	}

	public void setIsInContext(Boolean isInContext) {
		this.isInContext = isInContext;
	}

	public context getCurrentContext() {
		return currentContext;
	}

	public void setCurrentContext(context currentContext) {
		this.currentContext = currentContext;
	}

}
