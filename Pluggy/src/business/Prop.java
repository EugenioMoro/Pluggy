package business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import botContexts.FirstTOnContext;
import model.User;

public class Prop {

	private static Prop instance = null;
	private Runnable sysUpdater;
	private Runnable userUpdater;

	public static Prop getInstance(){
		if (instance == null){
			instance = new Prop();
		}
		return instance;
	}
	
	public Prop() {
		System.out.println("Initializing props");
		initialize();
		System.out.println("\ninit done");
	}
	
	private Properties defaults(){
		
		Properties p = new Properties();
		p.setProperty("token", "200488739:AAHjKEu5eG8UnqG4bItg3GCR_quW9X02MSk");
		p.setProperty("kwhcost", "0");
		p.setProperty("isMonitored", "0");
		
		return p;
		
	}
	

	
	


	private void sysUpdater() throws IOException{
		try {
			FileOutputStream out = new FileOutputStream("sysconf");
			Session.currentSession().getSysProps().store(out, null);
			out.close();
		} catch (IOException e){ e.printStackTrace(); }
	}

	private void userUpdater() throws IOException{
		Vector<User> uv = Session.currentSession().getUsers();
		System.out.print(Session.currentSession().getUsers().size());
		FileOutputStream out = null;
		Properties p = new Properties();
		int i;
		try {
		for(i=0; i<uv.size(); i++){
			out = new FileOutputStream("usr" + String.valueOf(uv.get(i).getId()));
			
			p.setProperty(String.valueOf(uv.get(i).getId()), "id");
			p.setProperty(uv.get(i).getUsername(), "username");
			p.setProperty(uv.get(i).getChatId(), "chatid");
			p.setProperty(uv.get(i).getIsAdmin().toString(), "isadmin");
			p.setProperty(uv.get(i).getIsAuth().toString(), "isauth");
			p.setProperty(uv.get(i).getIsSub().toString(), "issub");
			p.setProperty(String.valueOf(uv.get(i).getHours()), "hours");
			
			p.store(out, null);
		};
		out.close();
		} catch (IOException e){ e.printStackTrace(); }
	}
	
	public Boolean noConfig(){
		return !(new File("sysconf")).exists();
	}
	

	private void loadUsersProps() throws IOException{
		try {
			Vector<Properties> userVectProps = Session.currentSession().getUserVectProps();
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
			Properties userProps = new Properties();
			FileInputStream in = null;
			for (File file : foundConf){
				in = new FileInputStream(file);
				userProps.load(in);
				userVectProps.addElement(userProps);
				System.out.println("Loading user " + userProps.getProperty("id"));
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

		
		//Define runnables used as a separated thread prop updaters
		sysUpdater = new Runnable() {
			
			@Override
			public void run() {
				try {
					sysUpdater();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("sysupdater error\n");
				}
				
			}
		};
		
		userUpdater = new Runnable() {
			
			@Override
			public void run() {
				try {
					userUpdater();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("userupdate error");
				}
				
			}
		};
		
		
		
		System.out.println("Loading system configuration");
		//This is the point where the system knows whether is the first time it's turned on or not
		//All relative logic should be placed here
		//Note that the default config is not saved yet, this is because it should be saved ONLY after a successful new (first) user
		//TODO save defaults after first user
		if (noConfig()){ 
			System.out.println("No configuration found, switching to default");
			Session.currentSession().setSysProps(defaults());
			Session.currentSession().getHandler().setCurrentContext(FirstTOnContext.getInstance());
			
			//TODO link to logic first turn on, first user
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
		System.out.println("Done\n");

	}

	public Runnable getSysUpdater() {
		return sysUpdater;
	}

	public Runnable getUserUpdater() {
		return userUpdater;
	}

	
}
