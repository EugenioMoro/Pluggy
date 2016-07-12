package business;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HistoryManager {
	
	private static HistoryManager instance;
	
	private int hourCount;
	private int dayCount;
	private int totalCount;
	private int instantConsumes;
	private int toHoursCount=0;
	private int toDayCount=0;
	private ScheduledExecutorService countsUpdater;
	
	public static HistoryManager getInstance(){
		if (instance==null){
			instance = new HistoryManager();
			
			//runnable update counters logic to be scheduled
			Runnable update = new Runnable() {
				
				@Override
				public void run() {
					if (instance.toHoursCount >= 3600){
						instance.dayCount=instance.dayCount+instance.hourCount;
						instance.hourCount=0;
					}
					if(instance.toDayCount>=24){
						instance.totalCount=instance.totalCount+instance.dayCount;
						instance.dayCount=0;
					}
					
				}
			};
			instance.countsUpdater = Executors.newSingleThreadScheduledExecutor();
			instance.countsUpdater.scheduleWithFixedDelay(update, 50, 5, TimeUnit.MINUTES);
		}
		return instance;
	}
	
	

	public void notifyWattConsumes(long consumes){
		
	}
	
	public int getInstantConsumes(){
		return this.instantConsumes;
	}
	
	public void setInstantConsumes(int consumes){
		this.instantConsumes=consumes;
	}
	
	public int getHourCount() {
		return hourCount;
	}


	public void setHourCount(int hourCount) {
		this.hourCount = hourCount;
	}


	public int getDayCount() {
		return dayCount;
	}


	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
