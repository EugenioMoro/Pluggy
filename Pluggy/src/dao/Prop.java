package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import business.Session;
import business.UserManager;
import model.User;
/*
 * This class provides methods to save-load properties
 * When initialized, it checks if the system is turned on for the first time and loads (defaults if first turn on)
 * sys settings and user, saving it in Session. UserManager is meant to build users from every userprop loaded here
 * 
 * Updating methods are called whenever there is a change or new sys or user parameter that needs to be saved
 * They pretty much just save everything, so it's not very efficient
 */
public class Prop {

	private static Prop instance = null;


	public static Prop getInstance(){
		if (instance == null){
			instance = new Prop();
		}
		return instance;
	}
	
	public Prop() {
		initialize();
	}
	
	private Properties defaults(){
		
		Properties p = new Properties();
		p.setProperty("token", "200488739:AAHjKEu5eG8UnqG4bItg3GCR_quW9X02MSk");
		p.setProperty("kwhcost", "0");
		p.setProperty("isMonitored", "0");
		
		return p;
		
	}
	
	public void sysUpdater() {
		try {
			FileOutputStream out = new FileOutputStream("sysconf");
			Session.currentSession().getSysProps().store(out, null);
			out.close();
		} catch (IOException e){ e.printStackTrace(); }
	}

	public void userUpdater() {
		Vector<User> uv = Session.currentSession().getUsers();
		System.out.println(Session.currentSession().getUsers().size() + "users found");
		FileOutputStream out = null;
		Properties p = new Properties();
		int i;
		try {
		for(i=0; i<uv.size(); i++){
			out = new FileOutputStream("usr" + String.valueOf(uv.get(i).getId()));
			
			p.setProperty("username", uv.get(i).getUsername());
			p.setProperty("chatid", String.valueOf(uv.get(i).getId()));
			p.setProperty("issub", uv.get(i).getIsSub().toString());
			p.setProperty("hours", String.valueOf(uv.get(i).getHours()));
			p.setProperty("isadmin", uv.get(i).getIsAdmin().toString());
			p.setProperty("isauth", uv.get(i).getIsAuth().toString());
			System.out.println(p.toString());
			
			p.store(out, null);
		};
		out.close();
		} catch (IOException e){ e.printStackTrace(); }
	}
	
	private Boolean noConfig(){
		return !(new File("sysconf")).exists();
	}
	
	private void loadUsersProps() throws IOException{
		try {
			//Retrive working directory
			File dir = new File(System.getProperty("user.dir").toString());

			//retrive files starting with usr, configuration file names will be usr<id>
			File[] foundConf = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("usr");
				}
			});

			//load properties from files retrived
			FileInputStream in = null;
			for (File file : foundConf){
				in = new FileInputStream(file);
				//userProps.load(in);
				Properties p = new Properties();
				p.load(in);
				System.out.println(p.toString());
				Session.currentSession().getUserVectProps().add(p);
				System.out.println("Loading user " + p.getProperty("chatid"));
			}
			in.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	private void loadSysProps() throws IOException {
		try {
			FileInputStream in = new FileInputStream("sysconf");
			Properties p = Session.currentSession().getSysProps();
			p.load(in);
			in.close();
		} catch (IOException e) { e.printStackTrace(); }
	}

	public void initialize() {

		
	
		System.out.println("Loading system configuration");
		//This is the point where the system knows whether is the first time it's turned on or not
		//All relative logic should be placed here
		//Note that the default config is not saved yet, this is because it should be saved ONLY after a successful new (first) user
		if (noConfig()){ 
			System.out.println("No configuration found, switching to default");
			Session.currentSession().setSysProps(defaults());
			Session.currentSession().setIsFirstTurnOn(true);
		} else {
			try {
				loadSysProps();
				//if first turn on no user should be loaded
				if (!noConfig()){
					loadUsersProps();
				}
				} catch (IOException e) { e.printStackTrace(); }
				UserManager.getInstance().buildFromProps();

		}
		System.out.println("Properties loaded");

	}

}
