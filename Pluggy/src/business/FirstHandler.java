package business;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class FirstHandler extends TelegramLongPollingBot {

	@Override
	public String getBotUsername() {
		return null;
	}

	@Override
	public void onUpdateReceived(Update update) {
		 if(update.hasMessage()){
             Message message = update.getMessage();

             //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
             if(message.hasText()){

                     //create a object that contains the information to send back the message
                     SendMessage sendMessageRequest = new SendMessage();
                     sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message? the sender from which we got the message...
                     sendMessageRequest.setText("you said: " + message.getText());
                     try {
                             sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                     } catch (TelegramApiException e) {
                             //do some error handling
                     }//end catch()
             }//end if()
     }//end  if()

		
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return Session.TOKEN;
	}

}
