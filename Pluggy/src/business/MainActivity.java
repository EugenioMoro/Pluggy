package business;

import java.io.IOException;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

public class MainActivity {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento\n");
		
		//temporary testing code:
		Session.currentSession();
		System.out.println("Session Initalized");
		Prop.getInstance();
		System.out.println("Properties initialized");
		//system ready, simulating first turn on
		 TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		 try {
			 telegramBotsApi.registerBot(Session.currentSession().getHandler());
		 } catch (TelegramApiException e) { e.printStackTrace(); }
		
		
	

	}

	public static void Abort(Boolean isGraceful){
		System.exit((isGraceful) ? 1 : 0);
	}
	
}