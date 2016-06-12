package business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import model.User;

public class Prop {

	private  Boolean updateSys=false;
	private  Boolean updateUsers=false;
	private static Prop instance = null;

	public static Prop getInstance() throws IOException, InterruptedException{
		if (instance == null){
			instance = new Prop();
		}
		return instance;
	}
	
	public Prop() throws IOException, InterruptedException{
		update();
	}
	
	private Properties defaults(){
		
		Properties p = new Properties();
		p.setProperty("token", "200488739:AAHjKEu5eG8UnqG4bItg3GCR_quW9X02MSk");
		p.setProperty("kwhcost", "0");
		p.setProperty("isMonitored", "0");
		
		return p;
		
	}
	
	public synchronized void updateSys(){
		updateSys=true;
		notifyAll();
	}
	
	public synchronized void updateUsers(){
		updateUsers=true;
		notifyAll();
	}
	
	private synchronized void update() throws IOException, InterruptedException{
		while(true){
			while(updateSys | updateUsers){ 
				wait();
			}
			if(updateSys){
				sysUpdater();
				updateSys=false;
			} else {
				userUpdater();
				updateUsers=false;
			}
		}
	}

	private void sysUpdater() throws IOException{
		FileOutputStream out = new FileOutputStream("sysconf");
		Session.currentSession().getSysProps().store(out, null);
		out.close();
	}
	
	private void userUpdater() throws IOException{
		Vector<User> uv = Session.currentSession().getUsers();
		Iterator<User> i = uv.iterator();
		FileOutputStream out = null;
		Properties p = new Properties();
		while (i.hasNext()){
			out = new FileOutputStream("usr" + String.valueOf(i.next().getId()));
			
			p.setProperty(String.valueOf(i.next().getId()), "id");
			p.setProperty(i.next().getUsername(), "username");
			p.setProperty(i.next().getChatId(), "chatid");
			p.setProperty(i.next().getIsAdmin().toString(), "isadmin");
			p.setProperty(i.next().getIsAuth().toString(), "isauth");
			p.setProperty(i.next().getIsSub().toString(), "issub");
			p.setProperty(String.valueOf(i.next().getHours()), "hours");
			
			p.store(out, null);
		};
		out.close();
	}
	
	public Boolean noConfig(){
		return !(new File("sysconf")).exists();
	}
	
	
	private void loadUsersProps() throws IOException{
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
		}
		in.close();
	}
	
	private void loadSysProps() throws IOException {
		FileInputStream in = new FileInputStream("sysconf");
		Properties p = Session.currentSession().getSysProps();
		p.load(in);
		in.close();
	}

	public void initialize() {

		System.out.println("Loading system configuration");
		if (noConfig()){
			System.out.println("No configuration found, switching to default");
			Session.currentSession().setSysProps(defaults());
			//TODO link to logic first turn on, first user
		} else {
				
				loadSysProps();
				loadUsersProps();
				UserManager.getInstance().buildFromProps();
				
				token=SysProps.getProperty("token");
				kwhcost=Float.parseFloat(SysProps.getProperty("kwhcost"));
				isMonitored=(Integer.parseInt(SysProps.getProperty("isMonitored")) != 0);


		}
		System.out.println("Done\n");

	}

	
}
