package business;

import java.io.IOException;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

import dao.Prop;
import dao.SerialCommunication;

public class MainActivity {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento\n");
		
		//initialize session
		Session.currentSession();
		System.out.println("Session Initalized");
		
		//initialize and load properties
		Prop.getInstance();
		System.out.println("Properties initialized");
		HistoryManager.getInstance();
		
		//initializing arduino serial connection
		SerialCommunication serial = new SerialCommunication();
		serial.initialize();
		
		//system ready, registering bot handler
		 TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		 try {
			 telegramBotsApi.registerBot(Session.currentSession().getHandler());
		 } catch (TelegramApiException e) { e.printStackTrace(); }
		
		 //good luck

	}

	
}