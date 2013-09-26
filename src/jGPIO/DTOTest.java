package jGPIO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DTOTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DTO dtoHolder = new DTO();
		JSONArray freeList = GPIO.freeGPIOs();
		
		if(args.length > 0) {
			// these'll all be outputs
			for(String arg : args) {
				Iterator freeListIterator = freeList.iterator();
				
				while (freeListIterator.hasNext()) {
					JSONObject freePin = (JSONObject) freeListIterator.next();
					String key = (String) freePin.get("key");
					
					
					if(key.equalsIgnoreCase(arg)) {
					
						if(dtoHolder.addGPIO(arg, DTO.OUTPUT) == false) {
							System.out.println("Couldn't find " + arg);
							System.exit(1);
						} 
					} else {
						if(freePin.containsKey("options")) {
							JSONArray options = (JSONArray) freePin.get("options");
							for (int i = 0; i < options.size(); i++) {
								String option = (String) options.get(i);
								if(option.equalsIgnoreCase(arg)) {
									if(dtoHolder.addGPIO(arg, DTO.OUTPUT) == false) {
										System.out.println("Couldn't find " + arg);
										System.exit(1);
									}
								}
							}
						} else {
							System.out.println(arg + " is not a free pin");
						}
					}
				}
			
			}
			
			String fileContents = dtoHolder.createFileContents();
			FileWriter dtoOutput;
			try {
				dtoOutput = new FileWriter("jgpio-00A0.dto");
				dtoOutput.write(fileContents);
				dtoOutput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} else {
			
			Iterator<JSONObject> freeIterator = freeList.iterator();
			
			while(freeIterator.hasNext()) {
				JSONObject current = freeIterator.next();
				System.out.print(" " + (String) current.get("key"));
			}
			
			System.out.println("");
		}
	}

}
