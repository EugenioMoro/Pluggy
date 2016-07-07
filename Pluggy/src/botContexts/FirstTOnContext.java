package botContexts;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;


import business.Session;
import business.UserManager;
import model.User;

public class FirstTOnContext extends context {

	private final static int PRESENTATION = 0;
	private final static int USERCONFIG = 1;
	private final static int SYSCONFIG = 2;
	
	private int stage=0;
	private static FirstTOnContext getThis;
	private final Object lock = new Object();
	
	public static FirstTOnContext getThis() {
		if (getThis == null){
			getThis = new FirstTOnContext();
		}
		return getThis;
	}


	
	@Override
	public void work(Update update) {
		switch (stage){
		case PRESENTATION:
			presentation(update);
			break;
		case USERCONFIG:
			userConfig(update);
			break;
		case SYSCONFIG:
			sysConfig(update);
			break;
		}
		
	}
	
	private void presentation(Update update){
		
		class Presentation implements Runnable{

			@Override
			public void run() {
				SendMessage m = new SendMessage();
				Session.currentSession().getHandler().setReplyExpected(false);
				Session.currentSession().getHandler().setIsInContext(true);
				
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
				try {
					synchronized (lock) { lock.wait(1000);}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Session.currentSession().getHandler().setReplyExpected(true);
				stage++;
				
			}
			
		}
		
		Presentation p = new Presentation();
		p.run();
		
		
		
	}

	
	private void userConfig(Update update){
		User u = new User();
		u.setId(update.getMessage().getFrom().getId());
		u.setUsername(update.getMessage().getText());
		u.setChatId(update.getMessage().getChatId().toString());
		u.setIsAdmin(true);
		u.setIsAuth(true);
		u.setIsSub(false);
		u.setHours(1);
		UserManager.getInstance().addUser(u);
		
		Session.currentSession().getHandler().setReplyExpected(false);
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
		
		stage++;
	}

	private void sysConfig(Update update){
		System.out.println("will call abort");
		abort();
	}
	
	@Override
	public void abort() {
		stage=0;
		Session.currentSession().getHandler().setIsInContext(false);
		
	}
	
	
	
}
