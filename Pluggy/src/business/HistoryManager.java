package business;

public class HistoryManager {
	
	private static HistoryManager instance;
	
	private int hourCount=0;
	private int dayCount=0;
	private int totalCount=0;
	private int instantConsumes=0;
	private int toHoursCount=0;
	private int toDayCount=0;
	private Boolean showHours=false;
	private Boolean showDay=false;

	
	
	public static HistoryManager getInstance(){
		if (instance==null){
			instance = new HistoryManager();
		}
		return instance;
	}
	
	
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
			showDay=true;
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



	public Boolean getShowHours() {
		return showHours;
	}



	public void setShowHours(Boolean showHours) {
		this.showHours = showHours;
	}



	public Boolean getShowDay() {
		return showDay;
	}



	public void setShowDay(Boolean showDay) {
		this.showDay = showDay;
	}

}
