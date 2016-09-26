package dao;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class LedControl {
	private static LedControl instance;
	private Object Lock = new Object();
	
	private enum State {
		IDLEBLINK,
		ERRORBLINK
	}
	

	private State state;
	private GpioPinDigitalOutput led = GPIOCommunication.getInstance().getLed();
	
	private Thread blinker;
	
	public static LedControl getInstance(){
		if(instance == null){
			instance = new LedControl();
		}
		return instance;
	}
	
	public void blinkForError(){
		led.setState(false);
		blinker = new Thread(new Runnable() {
			
			@Override
			public void run() {
				led.blink(100);
			}
		});
		state=State.ERRORBLINK;
		blinker.start();
	}
	
	public void idleBlink(){
		led.setState(true);
		state=State.IDLEBLINK;
	}

	public void shortBlink(){
		led.setState(false);
		blinker = new Thread(new Runnable() {

			@Override
			public void run() {
				led.blink(250, 750);
				
				synchronized (Lock) {
				try {
					Lock.wait(750);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
				returnToState();
			}
		});
		blinker.start();
	}	
	
	private void returnToState(){
		switch (state){
		case IDLEBLINK:
			idleBlink();
			break;
		case ERRORBLINK:
			blinkForError();
			break;
		}
	}

}
