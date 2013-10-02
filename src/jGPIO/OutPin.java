package jGPIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OutPin extends GPIO {

	/**
	 * @param args
	 */
	int pinMode = -1;
	GPIO gpioPin = null;
	
	public OutPin(String pin, int mode) throws InvalidGPIOException, RuntimeException {
		/* We need to determine if we are digital or analogue, for now, we'll use it for setup and be happy */
		super(pin, GPIO.Direction.OUTPUT);
		
	}
	
	public void setValue(String target) {
		try {
			String currentStatus = gpioPin.readValue();
			if(currentStatus.equals(target)) {
				// not the current state
				gpioPin.writeValue(target);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getValue() {
		try {
			return gpioPin.readValue();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
