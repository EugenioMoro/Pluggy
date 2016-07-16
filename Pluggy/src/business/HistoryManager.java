package business;

/*
 * This class manages an history of consumes
 * Instant consumes are updated at every serial event from serial communication class
 * The logic is pretty straightforward: average consume per minute are given in the last m minutes and it overflows every 59 minutes
 * If the system is running for more than an hour, average consumes for the total uptime are given, along with costs
 */

public class HistoryManager {
	
	private static HistoryManager instance;
	
	private int hourCount=0;
	private int dayCount=0;
	private int totalCount=0;
	private int instantConsumes=0;
	private int toHoursCount=0;
	private int toDayCount=0;


	
	
	public static HistoryManager getInstance(){
		if (instance==null){
			instance = new HistoryManager();
		}
		return instance;
	}
	
	
	//This method is called whenever a new consume update is available
	public void setNewConsumes(int wattConsumes){
		instantConsumes=wattConsumes;
		hourCount=hourCount+instantConsumes;
		toHoursCount++;
		if (toHoursCount >= 3600){
			dayCount=dayCount+hourCount;
			hourCount=0;
			toDayCount++;
		}
		if(toDayCount>=24){
			totalCount=totalCount+dayCount;
			dayCount=0;
		}
	}
	
	public String getConsumesHistoryMessage(){
		String s = new String();
		s=("In the last " + toHoursCount/60+ " minutes I consumed ");
		if (hourCount/toHoursCount>=1000){
			s=s+(hourCount/(1000*toHoursCount) + " kilowatts per second.");
		} else {
			s=s+(hourCount/toHoursCount + " watts per second.");
		}
		if(toDayCount>0){
			s=s+("In the last " + toDayCount + "hours I consumed ");
			if (dayCount/toDayCount>=1000){
				s=s+(dayCount/(1000*toDayCount) + " kilowatts per hour ");
			} else {
				s=s+(dayCount/toDayCount + " watts per hour ");
			}
			s=s+("at a cost of " + (dayCount/3600000)*Session.currentSession().getKwhcost() + " euros");
		}
		return s;
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
