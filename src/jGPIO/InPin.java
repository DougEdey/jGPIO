package jGPIO;

public class InPin extends GPIO {

	public InPin(String number) throws InvalidGPIOException, RuntimeException {
		super(number, GPIO.Direction.INPUT);
	}

}
