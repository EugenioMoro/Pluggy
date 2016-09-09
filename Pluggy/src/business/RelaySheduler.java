package business;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import dao.GPIOCommunication;

public class RelaySheduler {

	
	private static RelaySheduler instance;
	
	
	@SuppressWarnings("unused")
	private ArrayList<ScheduledExecutorService> schedulers;
	
	public static RelaySheduler getInstance(){
		if (instance==null){
			instance = new RelaySheduler();
		}
		return instance;
	}

	public void addTask(int hours, int minutes, Boolean state){

	}
	
	public void instantToggle(){
		GPIOCommunication.getInstance().getRelay().toggle();
	}
	
	

}
