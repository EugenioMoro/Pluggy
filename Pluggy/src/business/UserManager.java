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
		Prop.getInstance().updateUsers();
	}
	
	public void buildFromProps(){
		System.out.println("Loading users");
		User u = new User();
		Iterator<Properties> i = Session.currentSession().getUserVectProps().iterator();
		while (i.hasNext()){

			u.setId(Integer.parseInt(((Properties) i.next()).getProperty("id")));
			u.setUsername(((Properties) i.next()).getProperty("username"));
			u.setChatId(((Properties) i.next()).getProperty("chatid"));
			u.setIsAdmin(Integer.parseInt(((Properties) i.next()).getProperty("isadmin")) != 0);
			u.setIsAuth(Integer.parseInt(((Properties) i.next()).getProperty("isauth")) != 0);
			u.setIsSub(Integer.parseInt(((Properties) i.next()).getProperty("issub")) != 0);
			u.setHours(Integer.parseInt(((Properties) i.next()).getProperty("hours")));

			Session.currentSession().getUsers().addElement(u);
		}
	}
	
}
