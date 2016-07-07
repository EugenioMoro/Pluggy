package business;

import java.util.Iterator;
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
		Iterator<Properties> i = Session.currentSession().getUserVectProps().iterator();
		while (i.hasNext()){
			Properties p = new Properties(i.next());
			u.setId(Integer.parseInt(((Properties) p).getProperty("id")));
			u.setUsername(((Properties) p).getProperty("username"));
			u.setChatId(((Properties) p).getProperty("chatid"));
			u.setIsAdmin(Integer.parseInt(((Properties) p).getProperty("isadmin")) != 0);
			u.setIsAuth(Integer.parseInt(((Properties) p).getProperty("isauth")) != 0);
			u.setIsSub(Integer.parseInt(((Properties) p).getProperty("issub")) != 0);
			u.setHours(Integer.parseInt(((Properties) p).getProperty("hours")));

			Session.currentSession().getUsers().addElement(u);
		}
	}
	
}
