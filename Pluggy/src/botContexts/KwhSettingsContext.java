package botContexts;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.Session;
import model.User;

public class KwhSettingsContext implements context {

	private final static int ASKFORVALUE = 0;
	private final static int GETALUE = 1;
	private final static int LASTSTAGE = 2;
	
	private int stage=0;
	private User u;
	private Thread worker;
	private float kwhCost;

	
	@Override
	public void abort() {
		u.setIsInContext(false);
		u.setCanReply(true);
		u.setCurrentContext(null);
		
	}
	
	public KwhSettingsContext(User u) {
		this.u=u;
	}

	private void askForValue(Update update){
		u.setIsInContext(true);
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("The current value for kwh cost is " + Session.currentSession().getKwhcost() + " --please, give me a new value");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			abort();
		}
		stage++;
	}
	
	private void getValue(Update update){
		Boolean validNumber = true;
		try{
		kwhCost=Float.parseFloat(update.getMessage().getText());
		} catch (NumberFormatException e){
			SendMessage m = new SendMessage();
			m.setText("Sorry, not a valid number. Can you retry?");
			m.setChatId(update.getMessage().getChatId().toString());
			validNumber = false;
			
			//send
			try {
				Session.currentSession().getHandler().sendMessage(m);
				
			} catch (TelegramApiException e1) {
				// TODO Auto-generated catch block
				abort();
			}
		}//close first catch
		if(validNumber){
			stage++;
			lastStage(update);
		}
	}
	
	private void lastStage(Update update){
		u.setCanReply(false);
		Session.currentSession().setKwhcost(kwhCost);
		SendMessage m = new SendMessage();
		m.setText("Fine");
		m.setChatId(update.getMessage().getChatId().toString());
		try {
			Session.currentSession().getHandler().sendMessage(m);
			
		} catch (TelegramApiException e1) {
			// TODO Auto-generated catch block
			abort();
		}
		abort();
	}
	
	@Override
	public void work(Update update) {
		switch (stage){
		case ASKFORVALUE:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					askForValue(update);
				}
			});

			break;
		case GETALUE:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					getValue(update);
				}
			});

			break;
		case LASTSTAGE:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					lastStage(update);
				}
			});

			break;
		}
		worker.start();
	}

}
