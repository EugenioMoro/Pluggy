package business;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import model.User;

public class Session {

	public final static String VERSION="0.1 alpha";
	private static Session currentSession;
	
	private String token;
	private float kwhcost;
	private Boolean isMonitored;
	private Vector<User> users;
	private Properties SysProps = new Properties();
	private Vector<Properties> userVectProps = new Vector<Properties>();
	
	
	
	
	
	
	public static Session currentSession() {
		if (currentSession==null){
			currentSession = new Session();
		}
		return currentSession;
		}
	

	public Session() {	
		initialize();
	}
	
	private void initialize() {

		System.out.println("Loading system configuration");
		if (Prop.noConfig()){
			System.out.println("No configuration found, switching to default");
			SysProps = Prop.defaults();
			//TODO link to logic first turn on, first user
		} else {
			try {
				SysProps = Prop.loadSysProps();
				userVectProps = Prop.loadUsersProps();

				System.out.println("Loading users");
				User u = new User();
				users = new Vector<User>();
				Iterator<Properties> i = userVectProps.iterator();
				while (i.hasNext()){

					u.setId(Integer.parseInt(((Properties) i.next()).getProperty("id")));
					u.setUsername(((Properties) i.next()).getProperty("username"));
					u.setChatId(((Properties) i.next()).getProperty("chatid"));
					u.setIsAdmin(Integer.parseInt(((Properties) i.next()).getProperty("isadmin")) != 0);
					u.setIsAuth(Integer.parseInt(((Properties) i.next()).getProperty("isauth")) != 0);
					u.setIsSub(Integer.parseInt(((Properties) i.next()).getProperty("issub")) != 0);
					u.setHours(Integer.parseInt(((Properties) i.next()).getProperty("hours")));

					users.addElement(u);
				}
			} catch (IOException e){

			}

			token=SysProps.getProperty("token");
			kwhcost=Float.parseFloat(SysProps.getProperty("kwhcost"));
			isMonitored=(Integer.parseInt(SysProps.getProperty("isMonitored")) != 0);


		}
		System.out.println("Done\n");

	}

}