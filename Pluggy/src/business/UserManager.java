package business;

import java.util.Properties;

import org.telegram.telegrambots.api.objects.Update;

import dao.Prop;
import model.User;

public class UserManager {
	
	private static UserManager instance;
	
	public static UserManager getInstance(){
		if (instance == null){
			instance = new UserManager();
		}
		return instance;
	}
	
	public void addUser(User u){
		Session.currentSession().getUsers().add(u);
		Prop.getInstance().userUpdater();
	}
	
	public void buildFromProps(){
		System.out.println("Loading users");
		User u = new User();
		for (int i=0; i<Session.currentSession().getUserVectProps().size(); i++){
			Properties p = Session.currentSession().getUserVectProps().get(i);
			u=new User();
			u.setUsername(p.getProperty("username"));
			u.setId(Long.parseLong(p.getProperty("chatid")));
			u.setIsAdmin(Boolean.parseBoolean(p.getProperty("isadmin").toString()));
			u.setIsAuth(Boolean.parseBoolean(p.getProperty("isauth").toString()));
			u.setIsSub(Boolean.parseBoolean(p.getProperty("isauth").toString()));
			u.setHours(Integer.parseInt(p.getProperty("hours")));

			Session.currentSession().getUsers().addElement(u);
		}

	}
	
	public User getUserById(long id){
		if (Session.currentSession().getUsers().isEmpty()){
			return null;
		}
		for(int i=0; i<Session.currentSession().getUsers().size(); i++){
			if(Session.currentSession().getUsers().get(i).getId()==id){
				
				return Session.currentSession().getUsers().get(i);
			}
		}
		return null;
	}
	
	public User getUserByUpdate(Update update){
		return getUserById(update.getMessage().getChatId());
	}
	
	public User getUserByName(String username){
		if (Session.currentSession().getUsers().isEmpty()){
			return null;
		}
		for(int i=0; i<Session.currentSession().getUsers().size(); i++){
			try {if(Session.currentSession().getUsers().get(i).getUsername().equals(username)){
				return Session.currentSession().getUsers().get(i);
			}}
			catch (NullPointerException e){
				return null;
			}
		}
		return null;
	}
	
}
