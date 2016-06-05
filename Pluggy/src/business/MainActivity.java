package business;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;


public class MainActivity {

	public static void main(String[] args) throws InterruptedException {
//		Session.currentSession();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new FirstHandler());
        } catch (TelegramApiException e) {
            //BotLogger.error(LOGTAG, e);
        }//end catch()
    }//end main()
	

	

	public static void Abort(Boolean isGraceful){
		System.exit((isGraceful) ? 1 : 0);
	}
	
}