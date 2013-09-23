package jgpio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GPIO {

	static String baseOffset = "44e10800";
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
			while((pin = pinmux.readLine()) != null) {
				if(pin.contains("(MUX UNCLAIMED) (GPIO UNCLAIMED)")) {
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

}
