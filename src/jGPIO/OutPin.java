package jGPIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OutPin extends GPIO {

	/**
	 * @param args
	 */
	int pinMode = -1;
	
	public OutPin(String pin) throws InvalidGPIOException, RuntimeException {
		/* We need to determine if we are digital or analogue, for now, we'll use it for setup and be happy */
		super(pin, GPIO.Direction.OUTPUT);
	}
	
	public void setValue(boolean target) {
		if(target) {
			setValue("1");
		} else {
			setValue("0");
		}
	}
	
	public void setValue(String target) {
		try {
			String currentStatus = readValue();
			if (currentStatus != null && !currentStatus.equals(target)) {
				// not the current state
				writeValue(target);
			}
		} catch (FileNotFoundException e) {
			// Doesn't exist may have closed already
			System.out.println("GPIO Doesn't exist, may have already closed");
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
			return readValue();
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
