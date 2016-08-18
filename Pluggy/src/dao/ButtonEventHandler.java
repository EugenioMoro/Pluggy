package dao;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonEventHandler implements GpioPinListenerDigital {
	
	private static ButtonEventHandler instance;
	
	private long millis;
	
	public static ButtonEventHandler getInstance(){
		if (instance == null){
			instance = new ButtonEventHandler();
		}
		return instance;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		
		if (event.getState().isHigh()){
			millis = System.currentTimeMillis();
			System.out.println(millis);
		} else if (event.getState().isLow()) {
				if (millis + 4999 < System.currentTimeMillis()){
					System.out.println("5sec button press");
				} else {
				System.out.println("Short button press");
			}
		}
	}

}
