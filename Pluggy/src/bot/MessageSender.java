package bot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import business.Session;
import model.User;

public class MessageSender {

	public static MessageSender instance;
	
	private SendMessage m = new SendMessage();
	
	public static MessageSender getInstance(){
		if(instance == null){
			instance = new MessageSender();
		}
		return instance;
	}
	
	public void simpleSend(String text, User u){
		m.setChatId(String.valueOf(u.getId()));
		m.setText(text);
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void simpleSend(String text, String chatId){
		m.setChatId(chatId);
		m.setText(text);
		try {
			Session.currentSession().getHandler().sendMessage(m);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
