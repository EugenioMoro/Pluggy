package model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RelayScheduledTask {
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private Boolean state;
	private int hours;
	private int minutes;
	
	
	
	
	public ScheduledExecutorService getExecutor() {
		return executor;
	}
	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	

}
