package bot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import business.Session;
import model.User;

public class MessageSender {

	private static MessageSender instance;
	
	private final Object lock = new Object();
	
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
	
	public void simpleSend(String text, Update update){
		simpleSend(text, update.getMessage().getChatId().toString());
	}
	
	public void delayedSend(String text, Update update, long delay){
		try {
			lock.wait(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		simpleSend(text, update);
	}
	
}
