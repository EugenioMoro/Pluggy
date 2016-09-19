package dao;

public class LedControl {
	private static LedControl instance;
	
	public static LedControl getInstance(){
		if(instance == null){
			instance = new LedControl();
		}
		return instance;
	}
	
	public void blinkForError(){
		
	}
	
	public void idleBlink(){
		
	}
}
