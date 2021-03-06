package jGPIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

public class FilePaths {

    static String MOCK = "/mock";
    static String BASE_PATH = "/sys/class/gpio";
    static String EXPORT_PATH = BASE_PATH + "/export";
    static String UNEXPORT_PATH = BASE_PATH + "/unexport";
    static String DEVICE_PATH = BASE_PATH + "/gpio%d";
    static String DIRECTION_PATH = DEVICE_PATH + "/direction";
    static String VALUE_PATH = DEVICE_PATH + "/value";
    static String ACTIVELOW_PATH = DEVICE_PATH + "/active_low";
    static String BASE_DEVICES = "/sys/devices/";

    int pinNo = -1;

    public FilePaths(int pinNumber) {
        pinNo = pinNumber;
    }

    static String getExportPath() {

        if (System.getProperty("debugGPIO") != null) {
            return MOCK + EXPORT_PATH;
        }

        return EXPORT_PATH;
    }

    static String getUnexportPath() {
        if (System.getProperty("debugGPIO") != null) {
            return MOCK + UNEXPORT_PATH;
        }

        return UNEXPORT_PATH;
    }

    public static String getDirectionPath(int pinNumber) {
        if (System.getProperty("debugGPIO") != null) {
            return String.format(MOCK + DIRECTION_PATH, pinNumber);
        }

        return String.format(DIRECTION_PATH, pinNumber);
    }

    public static String getValuePath(int pinNumber) {
        if (System.getProperty("debugGPIO") != null) {
            return String.format(MOCK + VALUE_PATH, pinNumber);
        }

        return String.format(VALUE_PATH, pinNumber);
    }

    public static String getAnalogueValuePath(int ain)
            throws InvalidGPIOException {
        try {
            Process find = Runtime.getRuntime().exec(
                    "find " + BASE_DEVICES + " -iname AIN" + ain);
            find.waitFor();
            BufferedReader is = new BufferedReader(new InputStreamReader(
                    find.getInputStream()));
            if (is.ready()) {
                String path = is.readLine();
                if (path != null) {
                    if (is.readLine() != null) {
                        // Found a path, return the first one
                        return path;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new InvalidGPIOException("Analogue input " + ain
                + " could not be found");

    }

    public static String getActiveLowPath(int pinNumber) {
        if (System.getProperty("debugGPIO") != null) {
            return String.format(MOCK + ACTIVELOW_PATH, pinNumber);
        }

        return String.format(ACTIVELOW_PATH, pinNumber);
    }
}
