package dao;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import business.RelaySheduler;

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
		} else if (event.getState().isLow()) {
				if (millis + 2500 < System.currentTimeMillis()){
					System.out.println("2sec button press");
				} else {
				System.out.println("Short button press");
				RelaySheduler.getInstance().instantToggle();
			}
		}
	}

}
