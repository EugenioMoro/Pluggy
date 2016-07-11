package business;

import java.util.Properties;

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
		Prop.getInstance().getUserUpdater().run();
	}
	
	public void buildFromProps(){
		System.out.println("Loading users");
		User u = new User();
		
		for (int i=0; i<Session.currentSession().getUserVectProps().size(); i++){
			Properties p = new Properties(Session.currentSession().getUserVectProps().get(i));
			u.setUsername(p.getProperty("username"));
			u.setChatId(Long.parseLong(p.getProperty("chatid")));
			u.setIsAdmin(Boolean.getBoolean(p.getProperty("isadmin").toString()));
			u.setIsAuth(Boolean.getBoolean(p.getProperty("isauth").toString()));
			u.setIsSub(Boolean.getBoolean(p.getProperty("isauth").toString()));
			u.setHours(Integer.parseInt(p.getProperty("hours")));

			Session.currentSession().getUsers().addElement(u);
		}

	}
	
	public User getUserById(int id){
		if (Session.currentSession().getUsers().isEmpty()){
			return null;
		}
		for(int i=0; i<Session.currentSession().getUsers().size(); i++){
			if(Session.currentSession().getUsers().get(i).getChatId()==id){
				return Session.currentSession().getUsers().get(i);
			}
		}
		return null;
	}
	
}
