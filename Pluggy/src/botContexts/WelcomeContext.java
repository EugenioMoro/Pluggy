package botContexts;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.Prop;
import business.Session;
import business.UserManager;
import model.User;

public class WelcomeContext implements context {

	private final static int PRESENTATION = 0;
	private final static int USERNAME = 1;
	private final static int SUBSCRIPTION = 2;
	private final static int KWHCOST = 3;
	private final static int FINALSTAGE = 4;
	
	private int stage=0;
	private User u;
	private final Object lock = new Object();
	private Thread worker;
	
	
	public WelcomeContext(User u) {
		this.u=u;
	}
	
	@Override
	public void work(Update update) {
		switch (stage){
		case PRESENTATION:
			worker = new Thread(new Runnable() {
				public void run() {
				presentation(update);	
				}
			});
			break;
		case USERNAME:
			worker = new Thread(new Runnable() {
				public void run() {
					username(update);
				}
			});
			break;
		case SUBSCRIPTION:
			worker = new Thread(new Runnable() {
				public void run() {
					subscription(update);
				}
			});
		case KWHCOST:
			worker = new Thread(new Runnable() {
				public void run() {
					kwhcost(update);
				}
			});
			break;
		case FINALSTAGE:
			worker = new Thread(new Runnable() {
				public void run() {
					finalStage(update);
				}
			});
			break;
		}
		worker.start();
	}
	
	private void presentation(Update update){


		SendMessage m = new SendMessage();

		u.setIsInContext(true);
		u.setCanReply(false);

		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("This is Pluggy");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) {
				lock.wait(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m.setText("You are welcome");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) {
				lock.wait(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m.setText("I'm probably the smartest plug you have ever came across. I can turn myself on and off (and I can schedule it)");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) { lock.wait(2500);}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m.setText("I can tell you how much I'm consuming right now, how much I consumed recently and how much will it cost to you");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) { lock.wait(2500);}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.setText("Actually, I can do much more than this. But first, I need to know a few things...");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) { lock.wait(2000);}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m.setText("How should I call you?");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}

		u.setCanReply(true);
		stage++;

	}


		
		
		


	
	private void username(Update update){
		u.setUsername(update.getMessage().getText());
		u.setChatId(update.getMessage().getChatId().toString());
		u.setIsAdmin(true);
		u.setIsAuth(true);
		u.setIsSub(false);
		u.setHours(1);
		
		SendMessage m = new SendMessage();
		
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("Very well, from now on I shall call you " + u.getUsername());
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		try {
			synchronized (lock) { lock.wait(1000);}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m.setText("Would you like to be notified if someone tells me to turn on or off?");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		stage++;
	}

	private void subscription(Update update){ //should know if is first turn on and act in consequence
		if (update.getMessage().getText().equalsIgnoreCase("yes")){
			u.setIsSub(true);
			stage++;
			u.setCanReply(false);
		} else if (update.getMessage().getText().equalsIgnoreCase("no")){
			u.setIsSub(false);
			stage++;
			u.setCanReply(false);
		} else {
			try {
				synchronized (lock) { lock.wait(1000);}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SendMessage m = new SendMessage();
			m.setChatId(update.getMessage().getChatId().toString());
			m.setText("Sorry, could you just tell me yes or no?");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				abort();
			}
			
		}

	}
	
	private void kwhcost(Update update){
		try {
			synchronized (lock) { lock.wait(1000);}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("Fine");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
		
		if (Session.currentSession().isFirstTurnOn()){
			m.setText("Now I need to know how much does a kilowatt per hour costs. You could check it in your last bill (I will wait for you fo find it), or just give me a standard value like 0,161668");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				abort();
			}
			try {
				synchronized (lock) { lock.wait(1000);}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			u.setCanReply(true);
			
		}
		stage++;
	}
	
	private void finalStage(Update update){
		System.out.println("finalstage");
		if (Session.currentSession().isFirstTurnOn()){
			Session.currentSession().getSysProps().setProperty("kwhcost", update.getMessage().getText());
			Prop.getInstance().getSysUpdater().run();
		}
		u.setIsInContext(false);
		u.setCanReply(true);
		UserManager.getInstance().addUser(u);
	}
	
	@Override
	public void abort() {
		u.setIsInContext(false);
		u.setCanReply(true);		
	}


	
	
	
}
