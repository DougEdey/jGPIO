package jGPIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GPIO {

	static String baseOffset = "44e10800";

	FilePaths gpioFiles = null;
	int pinNumber = -1;
	String pinName = null;
	Direction pinDirection = null;
	
	/* These are dupes from DTO */
	public enum Direction {
		INPUT("in"),
		OUTPUT("out"),
		PWM("pwm"),
		ANALOGUE("analogue");
		
		private String value;

		Direction(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}	
	}
	
	/* Regex Pattern matching */
	public static Pattern pinPattern = Pattern.compile("(gpio)([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);
	public static Pattern pinPatternAlt = Pattern.compile("(gpio)(_)?([\\d]+)$",Pattern.CASE_INSENSITIVE);
	
	/* Beaglebone Regex */
	public static Pattern pinPatternBeagle = Pattern.compile("(p)([0-9])_([0-9]+)", Pattern.CASE_INSENSITIVE);
	
	boolean closing = false;
	/**
	 * @param args
	 */
	
	public static JSONArray freeGPIOs() {
		JSONArray freeList = new JSONArray();
		DTO dtoCreator = new DTO();
		
		String pinmuxFile = "/sys/kernel/debug/pinctrl/"+baseOffset+".pinmux/pinmux-pins";
		try {
			BufferedReader pinmux = new BufferedReader(new FileReader(pinmuxFile));
			// get rid of the first two lines
			String pin = null;
			while ((pin = pinmux.readLine()) != null) {
				if (pin.contains("(MUX UNCLAIMED) (GPIO UNCLAIMED)")) {
					// add this to our list
					// pin is of form "pin 8 (44e10820): (MUX UNCLAIMED) (GPIO UNCLAIMED)"
					try {
					
						String[] pinExplode = pin.split(" ");
						String address = pinExplode[2].replace("(", "");
						address = address.replace("):", "");
						
						String offset = String.format("0x%03x", (Integer.decode("0x"+address) - Integer.decode("0x"+baseOffset)));
						
						// we now have the offset
						JSONObject pinJSON = dtoCreator.findDetailsByOffset(offset);
						
						if(pinJSON != null) {
							freeList.add(pinJSON);
						}
					} catch (NumberFormatException e) {
						// ignore these, these are the leading lines
						System.out.println(e.getMessage());
					}
				}
			}
			return freeList;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	
	public GPIO(String name, Direction direction) throws InvalidGPIOException, RuntimeException {
		pinName = name;
		pinNumber = getPinNumber(name);
		
		// we have the pin to set the direction on for the digital IO or use analogue IO 
		if (direction == Direction.INPUT || direction == Direction.OUTPUT) {
		    gpioFiles = new FilePaths(pinNumber);
		    // Check to see if it doesn't exist before tryping to export it.
		    if (!new File(FilePaths.getValuePath(pinNumber)).exists()) {
		        writeFile(FilePaths.getExportPath(), String.valueOf(pinNumber));
		    }
			writeFile(FilePaths.getDirectionPath(pinNumber), direction.value);
		} else if (direction == Direction.ANALOGUE) {
			System.out.println("Analogue called with a string constructor, not implemented");
		} else {
			System.out.println("Direction " + direction.value + " does not have an implemented int constructor");
		}
	}
	
	public GPIO(int pinNumber, Direction direction) throws InvalidGPIOException {
		pinDirection = direction;
		gpioFiles = new FilePaths(pinNumber);
		if (direction == Direction.INPUT || direction == Direction.OUTPUT) {
		    if (!new File(FilePaths.getValuePath(pinNumber)).exists()) {
                writeFile(FilePaths.getExportPath(), String.valueOf(pinNumber));
            }
			writeFile(FilePaths.getDirectionPath(pinNumber), direction.value);
		} else if (direction == Direction.ANALOGUE) {
			pinName = FilePaths.getAnalogueValuePath(pinNumber);
		} else {
			System.out.println("Direction " + direction.value + " does not have an implemented int constructor");
		}
	}
	
	public static int getPinNumber(String pinName) throws InvalidGPIOException{

		int pinNumber = -1;
		// See if we have the JSON for this Pin
		JSONObject pinJSON = DTO.findDetails(pinName);
		if (pinJSON != null && pinJSON.containsKey("gpio")) {
			try {
				pinNumber = Integer.parseInt((String) pinJSON.get("gpio"));
			} catch (ClassCastException f) {
				pinNumber = ((Long)pinJSON.get("gpio")).intValue();
			}
		} else {
			Matcher matcher = pinPatternAlt.matcher(pinName);
			if (matcher.find()) {
				pinNumber = Integer.parseInt(matcher.group(matcher.groupCount()));
			} else {
				matcher = pinPattern.matcher(pinName);
				if (matcher.find()) {
					pinNumber = Integer.parseInt(matcher.group(3));
					pinNumber += Integer.parseInt(matcher.group(2))*32;
				} else {
					throw new InvalidGPIOException("Could not match " + pinName + ". As a valid gpio pinout number");
				}
			}
			
		}
		
		return pinNumber;
	
	}
	
	protected void writeFile(String fileName, String value) throws RuntimeException {
		try {
			// check for permission
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(value.getBytes());
			fos.close();
		} catch (FileNotFoundException fnfe) {
		    throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
		} catch (SecurityException e) {
		    throw new RuntimeException("Permission denied to GPIO file: " + e.getMessage());
		} catch (IOException e) {
            System.out.println("GPIO is already exported, continuing");
        }
	}
	
	protected String readFile(String filename) throws FileNotFoundException, RuntimeException, IOException {
		BufferedReader fis = null;
		try {
			fis = new BufferedReader(new FileReader(filename));
			if (fis.ready()) {
				return fis.readLine();
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return null;
	}
	
	public void close() {
		closing = true;
		writeFile(FilePaths.getUnexportPath(), Integer.toString(pinNumber));
	}

	public String readValue() throws FileNotFoundException, RuntimeException, IOException  {
		if (pinDirection == Direction.ANALOGUE) {
			return readFile(pinName);
		}
		return readFile(FilePaths.getValuePath(pinNumber));
	}
	
	public void writeValue(String incoming) {
		writeFile(FilePaths.getValuePath(pinNumber), incoming);
		try {
		    String readVal = readValue();
            if (!readVal.equals(incoming)) {
                throw new RuntimeException("Tried to change pin " + pinNumber + 
                        " but failed to write: " + incoming + ", got " + readVal);
            }
		} catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
        } catch (SecurityException e) {
            throw new RuntimeException("Permission denied to GPIO file: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("IO Exception to GPIO file: " + e.getMessage());
        }
	}
	
	public String getGPIOName() {
		return pinName;
	}
	

}
