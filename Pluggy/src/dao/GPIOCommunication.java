package dao;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GPIOCommunication {

	private static GPIOCommunication instance;
	
	
	final GpioController gpio;
	private GpioPinDigitalInput button;
	
	public GPIOCommunication() {
		//creating gpio controller and button
		this.gpio = GpioFactory.getInstance();

		this.button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,             // PIN NUMBER
                "MyButton",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
		
		//setting an high reference pin (3.3v gpio pin already used)
		@SuppressWarnings("unused")
		GpioPinDigitalOutput reference = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03,   // PIN NUMBER
                PinState.HIGH);      // PIN STARTUP STATE (optional)
		
		this.button.addListener(ButtonEventHandler.getInstance());
			}
	
	public static GPIOCommunication getInstance(){
		if (instance == null){
			instance = new GPIOCommunication();
		}
		return instance;
	}


	
	
}
