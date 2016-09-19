package business;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import dao.LedControl;
import dao.Prop;
import dao.SerialCommunication;

public class MainActivity {
	
	private final static Object lock = new Object();

	public static void main(String[] args) throws InterruptedException, IOException {
		
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento\n");
		
		//initialize session
		Session.currentSession();
		System.out.println("\nSession Initalized");
		
		//initialize and load properties
		Prop.getInstance();
		System.out.println("Properties initialized");
		HistoryManager.getInstance();
		
		//initializing arduino serial connection
		SerialCommunication serial = new SerialCommunication();
		serial.initialize();

		//checking internet connection
		synchronized(lock){
			while(!isConnected()){
				LedControl.getInstance().blinkForError();
				System.out.println("No internet connection, next try in 5 seconds");
				lock.wait(5000);
			}
		}

		//system ready, registering bot handler
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		BotSession botSession = null;
		 try {
			 botSession=telegramBotsApi.registerBot(Session.currentSession().getHandler());
			 System.out.println("Bot api ready");
		 } catch (TelegramApiException e) {e.printStackTrace(); }
		 LedControl.getInstance().idleBlink();
		 
		 
		 synchronized (lock){
			 while(true){ //loop for entire runtime
				 if(isConnected()){ //check internet connection every 10 seconds
					 lock.wait(1000);
					 continue;
				 }
				 //if connection lost, unregister bot api
				 botSession.close();
				 while(true){ //and loop until is connected
					 if(isConnected()){
						 try {
							 telegramBotsApi = new TelegramBotsApi();
							 telegramBotsApi.registerBot(Session.currentSession().getHandler());
							 System.out.println("Bot api ready");
						 } catch (TelegramApiException e) {e.printStackTrace(); }
						 LedControl.getInstance().idleBlink();
						 continue;
					 }
					 System.out.println("Connection lost, next try in 5 seconds");
					 LedControl.getInstance().blinkForError();
					 lock.wait(5000);
				 }
			 }
		 }
		 
		 //init gpio
		 //GPIOCommunication.getInstance();

		 //good luck
		 
		 

	}
	
	private static boolean isConnected() {
		//checks internet connetion with google dns
	    try {
	        try (Socket soc = new Socket()) {
	            soc.connect(new InetSocketAddress("8.8.8.8", 53), 500);
	        }
	        return true;
	    } catch (IOException ex) {
	        return false;
	    }
	}


	
}