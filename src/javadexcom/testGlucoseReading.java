package javadexcom;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pack.consts;

public class testGlucoseReading {
    public static void main(String[] args) throws IOException {

        // This section tests the Glucose Reading class
        //      - Creates a test map, in the same format as would be returned by API
        //      - Reads values from that Map and converts them into a Glucose Reading Object
        Map<String, Object> test = new HashMap<>();
        test.put("WT", "Date(1703706711000)");
        test.put("ST", "Date(1703706711000)");
        test.put("DT", "Date(1703706711000-0500)");
        test.put("Value", "112");
        test.put("Trend", "Flat");

        GlucoseReading gr = new GlucoseReading(test);
        System.out.println(gr);


        // This section test the Dexcom class
        //      - Creates a Dexcom object using Dexcom share username and password
        //      - final value is true if outside US, and false if inside the US
        Dexcom vals = new Dexcom("USERNAME", "PASSWORD", false);
        List<GlucoseReading> allReadings = vals.getGlucoseReadings(); // get past 24 hours
        GlucoseReading latest = vals.getLatestGlucoseReading();
        GlucoseReading current = vals.getCurrentGlucoseReading();
        System.out.println(latest);
        System.out.println();
        System.out.println(current);




    }
}
