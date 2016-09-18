package model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RelayScheduledTask {
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private Boolean state;
	private int hours;
	private int minutes;
	private Boolean repeats;
	
	
	
	public RelayScheduledTask(ScheduledExecutorService executor, Boolean state, int hours, int minutes,
			Boolean repeats) {
		super();
		this.executor = executor;
		this.state = state;
		this.hours = hours;
		this.minutes = minutes;
		this.setRepeats(repeats);
	}
	
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

	public Boolean getRepeats() {
		return repeats;
	}

	public void setRepeats(Boolean repeats) {
		this.repeats = repeats;
	}
	
	

}
