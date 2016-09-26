package botContexts;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.SecurityManager;
import business.Session;
import business.UserManager;
import dao.Prop;
import model.User;

public class SecuritySettingsContext implements context {
	
	private final static int SELECTUSER = 0;
	private final static int SELECTACTION = 1;
	private final static int LASTACTION = 2;

	
	private String usernameToMod;
	private int stage=0;
	private User u;
	private Thread worker;

	public SecuritySettingsContext(User user) {
		this.u=user;
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
		case SELECTUSER:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					selectUser(update);	
				}
			});
			break;
		case SELECTACTION:
			worker = new Thread(new Runnable() {

				@Override
				public void run() {
					selectAction(update);
				}
			});
			break;
		case LASTACTION:
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

	private void selectUser(Update update){
		u.setIsInContext(true);
		
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.ADMIN_LEVEL)){
			m.setText("Select wich user you want to modify priviledges:");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String s="/" + Session.currentSession().getUsers().get(0).getUsername();
			if (Session.currentSession().getUsers().size()>1){
				for (int i=1; i<Session.currentSession().getUsers().size(); i++){
					s=s + " /" + Session.currentSession().getUsers().get(i).getUsername();
				}
			}
			m.setText(s);
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			u.setCanReply(true);
		stage++;
		} else {
			m.setText("You are not authorized");
			abort();
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void selectAction(Update update){
		u.setCanReply(false);
		User us = UserManager.getInstance().getUserByName(update.getMessage().getText().substring(1));
		usernameToMod=us.getUsername();
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		String s = "";
		if (us.getIsAdmin()){
			s="User " + us.getUsername() + " is an admin. How would you like to proceed? ";
			s=s+"Set as an /authorized user. /revoke all priviledges";
		} else if (us.getIsAuth()){
			s="User " + us.getUsername() + " is authorized to see consumes. How woud you like to proceed?";
			s=s+"Set as an /admin. /revoke all priviledges";
		} else {
			s="User " + us.getUsername() + " is not authorized. How would you like to proceed?";
			s=s+"Set as and /authorized user. Set as an /admin";
		}
		m.setText(s);
		u.setCanReply(true);
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stage++;
	}
	
	private void lastStage(Update update){
		u.setCanReply(false);
		switch (update.getMessage().getText().substring(1)){
		case "revoke":
			UserManager.getInstance().getUserByName(usernameToMod).setIsAdmin(false);
			UserManager.getInstance().getUserByName(usernameToMod).setIsAuth(false);
			break;
		case "admin":
			UserManager.getInstance().getUserByName(usernameToMod).setIsAdmin(true);
			UserManager.getInstance().getUserByName(usernameToMod).setIsAuth(false);
			break;
		case "authorized":
			UserManager.getInstance().getUserByName(usernameToMod).setIsAdmin(false);
			UserManager.getInstance().getUserByName(usernameToMod).setIsAuth(true);
			break;
		}
		Prop.getInstance().userUpdater();;
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("All done");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		abort();
	}

}
