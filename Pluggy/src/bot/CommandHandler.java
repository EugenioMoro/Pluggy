package bot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import botContexts.KwhSettingsContext;
import botContexts.ManageTasksContext;
import botContexts.SchedulingContext;
import botContexts.SecuritySettingsContext;
import business.HistoryManager;
import business.RelayManagerSheduler;
import business.SecurityManager;
import business.Session;
import business.UserManager;

/*
 * This class interpr. commands and give response to the user or sets the appropriate context
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
			securitySettings(update);
			break;
		case "/settings":
			settings(update);
			break;
		case  "/turnon":
			turnon(update);
			break;
		case "/turnoff":
			turnoff(update);
			break;
		case "/schedule":
			schedule(update);
			break;
		case "/tasksettings":
			taskSettings(update);
			break;
		case "/whatcanido":
			whatCanIDo(update);
			break;
		case "/costsettings":
			costSettings(update);
			break;
		default:
			unrecognized(update);
		}
	}
	
	
	public void SendHelp(Update update){
		System.out.println("Help command, sending help");
		MessageSender.getInstance().simpleSend("Here is a basic list of commands. If you want to know more about everything, hit /whatcanido\n/consumes\n/history\n/turnon /turnoff\n/settings\n/security\n/schedule", update);
	}
	
	public void sendSecInfo(Update update){
		System.out.println("Sending security info");
		String s ="Only an *authorized* user can check consumes,history and switch the plug. An *admin* user is allowed to do what an *authorized* user can, plus it can permit or revoke those priviledges. /mysecurity to check your security level. /securitysettings to manage users security";
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

	private void turnon(Update update){
		System.out.println("Turn on command");
		if(RelayManagerSheduler.getInstance().getState()){
			MessageSender.getInstance().simpleSend("The plug is already on", update.getMessage().getChatId().toString());
			return;
		}
		RelayManagerSheduler.getInstance().fromBotToggle(UserManager.getInstance().getUserByUpdate(update));
		
	}
	
	private void turnoff(Update update){
		System.out.println("Turn off command");
		if(!RelayManagerSheduler.getInstance().getState()){
			MessageSender.getInstance().simpleSend("The plug is already off", update.getMessage().getChatId().toString());
			return;
		}
		RelayManagerSheduler.getInstance().fromBotToggle(UserManager.getInstance().getUserByUpdate(update));
	}
	
	private void whatCanIDo(Update update){
		String s ="I can display consumes and monitor them over time. /consumes /history\n\nI can turn on or off the plug when you ask me to (/turnon /turnoff)\nI can /schedule that too\n\nSecurity is important to me, check it out: /security\n\nSee /settings to manage khw cost value, security and scheduled tasks";
		MessageSender.getInstance().simpleSend(s, update);
	}

	private void securitySettings(Update update){
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.ADMIN_LEVEL)){
			UserManager.getInstance().getUserById(update.getMessage().getChatId()).setCurrentContext(new SecuritySettingsContext(UserManager.getInstance().getUserById(update.getMessage().getChatId())));
		} else {
			MessageSender.getInstance().simpleSend("You are not authorized to perform this action", update);
		}
	}
	
	private void settings(Update update){
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.AUTHORIZED_LEVEL)){
			MessageSender.getInstance().simpleSend("/costsettings\n/securitysettings\n/tasksettings", update);
		} else {
			MessageSender.getInstance().simpleSend("You are not authorized to perform this action", update);
		}
	}
	
	private void schedule(Update update){
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.AUTHORIZED_LEVEL)){
			UserManager.getInstance().getUserByUpdate(update).setCurrentContext(new SchedulingContext(UserManager.getInstance().getUserByUpdate(update)));
		} else {
			MessageSender.getInstance().simpleSend("You are not authorized to perform this action", update);
		}
	}
	
	private void taskSettings(Update update){
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.AUTHORIZED_LEVEL)){
			UserManager.getInstance().getUserByUpdate(update).setCurrentContext(new ManageTasksContext());
		} else {
			MessageSender.getInstance().simpleSend("You are not authorized to perform this action", update);
		}
	}
	
	private void costSettings(Update update){
		if(SecurityManager.getInstance().securityCheck(update, SecurityManager.AUTHORIZED_LEVEL)){
			UserManager.getInstance().getUserByUpdate(update).setCurrentContext(new KwhSettingsContext(UserManager.getInstance().getUserByUpdate(update)));
		} else {
			MessageSender.getInstance().simpleSend("You are not authorized to perform this action", update);
		}
	}
}
