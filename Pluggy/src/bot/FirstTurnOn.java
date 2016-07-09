package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import business.Session;

public class FirstTurnOn extends TelegramLongPollingBot {


	
	
	@Override
	public String getBotUsername() {
		return null;
	}

	@Override
	public void onUpdateReceived(Update update) {
		System.out.println("update recived");
		SendMessage m = new SendMessage();
		m.setChatId(update.getMessage().getChatId().toString());

		
//		switch (){
//		case "zero":
//			m.setText("Testing start: insert username:");
//			
//			try {
//				sendMessage(m);
//			} catch (TelegramApiException e) { e.printStackTrace(); }
//		case "username":
//			u.setUsername(update.getMessage().getText());
//			u.setChatId(update.getMessage().getChatId().toString());
//			//u.setId(update.getMessage().getContact().getUserID());
//			m.setText("Username got, set privileges, done");
//			try {
//				sendMessage(m);
//				
//			} catch (TelegramApiException e) { e.printStackTrace(); }
//		case "third":
//			m.setText("third");
//			try {
//				sendMessage(m);
//				
//			} catch (TelegramApiException e) { e.printStackTrace(); }
			
		}
//		 if(update.hasMessage()){
//             Message message = update.getMessage();

//             //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
//             if(message.hasText()){
//
////                     //create a object that contains the information to send back the message
////                     SendMessage sendMessageRequest = new SendMessage();
////                     sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message? the sender from which we got the message...
////                     sendMessageRequest.setText("you said: " + message.getText());
//                     try {
//                             sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
//                     } catch (TelegramApiException e) {
//                             //do some error handling
//                     }//end catch()
//             }//end if()
//     }//end  if()

		
	

	@Override
	public String getBotToken() {
		return Session.currentSession().getToken();
	}

}
