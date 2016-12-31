package jGPIO;

public class InvalidGPIOException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Parameterless Constructor */
	public InvalidGPIOException() {
	}
	
	/* Create with an error message */
	public InvalidGPIOException(String message) {
		super(message);
	}
	
}
