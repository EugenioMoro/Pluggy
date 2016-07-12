package bot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.HistoryManager;
import business.Session;
import dao.dummyConsumes;

public class CommandHandler {
	
	private static CommandHandler instance;
	
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
		default:
			unrecognized(update);
		}
	}
	
	public void SendHelp(Update update){
		System.out.println("Help command, sending help");
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText("/consumes will tell you my power usage in watts. /turnon /turnoff if you want me to give power (or cut it) to whatever is plugged into me. /security if you want more info about how I'll try to stop unauthorized people from using me. /settings and you can tweak me up");
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
		System.out.println("Sending consumes");
		
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());
		m.setText(String.valueOf(HistoryManager.getInstance().getInstantConsumes()));
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
