package bot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import botContexts.KwhSettingsContext;
import botContexts.SecuritySettingsContext;
import business.HistoryManager;
import business.SecurityManager;
import business.Session;
import business.UserManager;

/*
 * This class interpretes commands and give response to the user or sets the appropriate context
 */

public class CommandHandler {
	
	private static CommandHandler instance;
	@SuppressWarnings("unused")
	private final Object lock = new Object();
	
	public static CommandHandler getInstance(){
		if(instance == null){
			instance = new CommandHandler();
		}
		return instance;
	}

	
	public void commandInterpreter(Update update){
		switch (update.getMessage().getText().toString()){
		case "/help":
			SendHelp(update);
			break;
		case "/consumes":
			sendConsumes(update);
			break;
		case "/history":
			sendHistory(update);
			break;
		case "/security":
			sendSecInfo(update);
			break;
		case "/mysecurity":
			sendAuthLevel(update);
			break;
		case "/securitysettings":
			UserManager.getInstance().getUserById(update.getMessage().getChatId()).setCurrentContext(new SecuritySettingsContext(UserManager.getInstance().getUserById(update.getMessage().getChatId())));
			break;
		case "/settings":
			UserManager.getInstance().getUserById(update.getMessage().getFrom().getId()).setCurrentContext(new KwhSettingsContext(UserManager.getInstance().getUserById(update.getMessage().getFrom().getId())));
			break;
		default:
			unrecognized(update);
		}
	}
	
	
	public void SendHelp(Update update){
		System.out.println("Help command, sending help");
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("/consumes will tell you my power usage in watts and /history will help you check what happened in the past. /turnon /turnoff if you want me to give power (or cut it) to whatever is plugged into me. /security if you want more info about how I'll try to stop unauthorized people from using me. /settings and you can tweak me up");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendSecInfo(Update update){
		System.out.println("Sending security info");
		String s ="Only an *authorized* user will obtain consumes and history. An *admin* user is allowed to do what an *authorized* user can, plus it can permit or revoke those priviledges. /mysecurity to check your security level. /securitysettings to manage users security";
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText(s);
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void unrecognized(Update update){
		System.out.println("Unrecognized command");
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("I double checked, this is not something I can do");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendConsumes(Update update){
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.ADMIN_LEVEL)){
			System.out.println("Sending consumes");
			m.setText("Right now I'm consuming " + String.valueOf(HistoryManager.getInstance().getInstantConsumes()) + " watts");
		} else {
			m.setText("You are not authorized. To check your security level click: /mysecurity");
		}
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendHistory(Update update){
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.ADMIN_LEVEL)){
			System.out.println("Sending history");
			m.setText(HistoryManager.getInstance().getConsumesHistoryMessage());
		} else {
			m.setText("You are not authorized. To check your security level click: /mysecurity");
		}
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendAuthLevel(Update update){
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		if(UserManager.getInstance().getUserById(update.getMessage().getChatId()).getIsAdmin()){
			m.setText("You are and admin. You can see /consumes, you can manage /settings and set priviledges to other users");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(UserManager.getInstance().getUserById(update.getMessage().getChatId()).getIsAuth()){
			m.setText("You are authorized to see /consumes and /history");
			try {
				Session.currentSession().getHandler().sendMessage(m);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		m.setText("You are not authorized to do anything, you should talk with the owner of this plug");
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
