package business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class Prop {

	
	public static Properties defaults(){
		
		Properties p = new Properties();
		p.setProperty("token", "200488739:AAHjKEu5eG8UnqG4bItg3GCR_quW9X02MSk");
		p.setProperty("kwhcost", "0");
		p.setProperty("isMonitored", "0");
		
		return p;
		
	}
	
	public static Boolean noConfig(){
		return !(new File("sysconf")).exists();
	}
	
//	public static Properties loadProps() throws IOException{
//		Boolean defaults = false;
//		//try load previous conf, if not load default; 
//		defaults = !(new File("sysconf")).exists();
//		if (defaults){
//			System.out.println("Config not found, loading defaults");
//			return defaults();
//		} else {
//			loadSysProps();
//			loadUsersProps();
//			System.out.println("Configuration loaded");
//		}
//		
//	}
	
	public static Vector<Properties> loadUsersProps() throws IOException{
		Vector<Properties> userVectProps = new Vector<Properties>();
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
		return userVectProps;
		
	}
	
	public static Properties loadSysProps() throws IOException {
		FileInputStream in = new FileInputStream("sysconf");
		Properties p = new Properties();
		p.load(in);
		in.close();
		return p;
	}


	
}
