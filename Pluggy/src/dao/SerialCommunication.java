package dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;

import business.HistoryManager;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/*
 * This class is pretty much copied and adapted from an arduino playground guide about
 * managing serial connection in java
 * 
 * At the moment, it offers support for windows and linux (just usb emulated serial connection for linux)
 */


public class SerialCommunication implements SerialPortEventListener{
	
	SerialPort serialPort;
	
	private static final String PORT_NAMES[]={
			"COM3",
			"/dev/ttyAMA0"
	};
	private static final int TIME_OUT = 2000;
	private final static int BAUD_RATE = 9600;
	private static int wattConsumes;

	private BufferedReader input;
	
	public static int getConsumes(){
		return wattConsumes;
	}


	public void initialize(){
		
		

		System.out.println("Initializing serial connection");
		CommPortIdentifier portId = null;
		//Get an enum of avaiable ports and search for port as in port names--- os indipendent
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()){
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		
		if (portId == null) {
			System.out.println("Unable to find port identifier");
			return;
		}
		
		
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(BAUD_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the stream
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//not sure if necessary after all
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				if (!inputLine.equalsIgnoreCase("cal")){
					float amperes = Float.valueOf(inputLine);
					//there is some offset that can't be calibrated by arduino, empirically 0.03 amps
					if (amperes>=0.03){
					HistoryManager.getInstance().setNewConsumes(((int) (amperes*220 +0.5)));
					} else {
						HistoryManager.getInstance().setNewConsumes(0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
