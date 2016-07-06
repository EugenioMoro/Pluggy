package business;

import java.io.IOException;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

import bot.FirstTurnOn;

public class MainActivity {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento\n");
		
		//temporary testing code:
		Session.currentSession();
		System.out.println("session done");
		Prop.getInstance();
		System.out.println("prop done");
		//system ready, simulating first turn on
		 TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		 try {
			 telegramBotsApi.registerBot(new FirstTurnOn());
		 } catch (TelegramApiException e) { e.printStackTrace(); }
		
		
	

	}

	public static void Abort(Boolean isGraceful){
		System.exit((isGraceful) ? 1 : 0);
	}
	
}