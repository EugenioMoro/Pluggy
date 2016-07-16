package botContexts;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.Session;
import business.UserManager;
import dao.Prop;
import model.User;

public class WelcomeContext implements context {

	private final static int PRESENTATION = 0;
	private final static int USERNAME = 1;
	private final static int SUBSCRIPTION = 2;
	private final static int KWHCOST = 3;
	private final static int FINALSTAGE = 4;
	
	private int stage=0;
	private User u;
	private final Object lock = new Object(); //used to get a well behaving wait for the worker thread
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
			break;
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
	
	//just sends welcome messages
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

		//if first turn on this user (the first) ahs to be an administrator
		if(Session.currentSession().isFirstTurnOn()){
			u.setIsAdmin(true);
			u.setIsAuth(true);
		}
		
		//refers to a functionality not yet implemented
		u.setHours(1);
		
		SendMessage m = new SendMessage();
		
		//Here is checking if username is available
		if(UserManager.getInstance().getUserByName(update.getMessage().getText())==null){
		u.setUsername(update.getMessage().getText());
		u.setId(update.getMessage().getChatId());
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
		} else { //if it's not available, ask for it again but don't set next stage
			m.setChatId(update.getMessage().getChatId().toString());
			m.setText("I'm sorry, this username is already taken. Can you choose another one please?");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				abort();
			}
		}
	}

	private void subscription(Update update){ //get subscription preferences, functionality not yes completely implemented
		
		if (update.getMessage().getText().equalsIgnoreCase("yes")){
			u.setIsSub(true);
			stage++;
			u.setCanReply(false);
			//execute next stage
			worker = new Thread(new Runnable() {
				public void run() {
					kwhcost(update);
				}
			});
			worker.start();
		} else if (update.getMessage().getText().equalsIgnoreCase("no")){
			u.setIsSub(false);
			stage++;
			u.setCanReply(false);
			//execute next stage
			worker = new Thread(new Runnable() {
				public void run() {
					kwhcost(update);
				}
			});
			worker.start();
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
	
	private void kwhcost(Update update){ //asks for kwh cost value
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
			m.setText("Now I need to know how much does a kilowatt per hour costs, so I can tell you how much you will spend. You could check it in your last bill (I will wait for you fo find it), or just give me a standard value like 0.16");
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
			stage++;
			
		} else {
			finalStage(update);
		}
		
	}
	
	private void finalStage(Update update){
		System.out.println("finalstage");
		if (Session.currentSession().isFirstTurnOn()){
			//see if valid number
			try {
				Float.parseFloat(update.getMessage().getText());
				
			} catch (NumberFormatException e){
				SendMessage m = new SendMessage();
				m.setChatId(update.getMessage().getChatId().toString());
				m.setText("That was not a valid number, can you repeat please?");
				try {
					Session.currentSession().getHandler().sendMessage(m);
				} catch (TelegramApiException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					abort();
				}
				return;
			}
			//things that need to be done only if first turn on
			Session.currentSession().getSysProps().setProperty("kwhcost", update.getMessage().getText());
			Prop.getInstance().sysUpdater();
			u.setIsAdmin(true);
			u.setIsAuth(true);
		}
		
		abort(); //context reached last stage
		
		//saves user
		Prop.getInstance().userUpdater();
		
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		if (Session.currentSession().isFirstTurnOn()){ //different last message depending on first turn on
			Session.currentSession().setIsFirstTurnOn(false);
			m.setText("Ok, we are done. As my first user, you are and administrator of this system. If you want more info about, just click /security. You are in charge of setting the security level of the other users from now on, as they are set on the lowest level as a default. If you are lost, click /help");
		} else {
			m.setText("Ok, we are done. Now you need the owner of this plug to allow you to see consumes and history, just tell him. Click /security and /help for more");
		}
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			abort();
		}
	}
	
	@Override
	public void abort() {
		u.setIsInContext(false);
		u.setCanReply(true);
		u.setCurrentContext(null);
	}


	
	
	
}
