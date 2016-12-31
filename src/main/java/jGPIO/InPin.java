package jGPIO;

import java.io.FileNotFoundException;
import java.io.IOException;

public class InPin extends GPIO {

	public InPin(String number) throws InvalidGPIOException, RuntimeException {
		super(number, GPIO.Direction.INPUT);
	}
	
	public InPin(int number, Direction direction) throws InvalidGPIOException {
		super(number, direction);
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
