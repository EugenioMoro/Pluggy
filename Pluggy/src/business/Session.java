package business;

import java.util.Properties;
import java.util.Vector;

import model.User;

public class Session {

	public final static String VERSION="0.1 alpha";
	private static Session currentSession;
	
	private Vector<User> users = new Vector<User>();
	private Properties SysProps = new Properties();
	private Vector<Properties> userVectProps = new Vector<Properties>();
	
	
	
	
	
	
	public static Session currentSession() {
		if (currentSession==null){
			currentSession = new Session();
		}
		return currentSession;
		}
	


	public String getToken() {
		return SysProps.getProperty("token");
	}


	public void setToken(String token) {
		SysProps.setProperty("token", token);
		Prop.getInstance().updateSys();
	}


	public float getKwhcost() {
		return Float.parseFloat(SysProps.getProperty("kwhcost"));
	}


	public void setKwhcost(float kwhcost) {
		SysProps.setProperty("kwhcost", Float.toString(kwhcost));
		Prop.getInstance().updateSys();
	}


	public Boolean getIsMonitored() {
		return Boolean.valueOf(SysProps.getProperty("ismonitored"));
	}


	public void setIsMonitored(Boolean isMonitored) {
		SysProps.setProperty("ismonitored", Boolean.toString(isMonitored));
		Prop.getInstance().updateSys();
	}


	public Vector<User> getUsers() {
		return users;
	}


	public Properties getSysProps() {
		return SysProps;
	}


	public void setSysProps(Properties sysProps) {
		SysProps = sysProps;
	}


	public Vector<Properties> getUserVectProps() {
		return userVectProps;
	}


	public void setUserVectProps(Vector<Properties> userVectProps) {
		this.userVectProps = userVectProps;
	}

}