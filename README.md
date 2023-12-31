# javaDexcom
A java port of the pydexcom API created by gagebenne and hosted at https://github.com/gagebenne/pydexcom

## What is this Project
This is a Java port of an API that reaches out to the Dexcom Share servers and requests Dexcom CGM data. It was designed to give potential developers access to their own Dexcom CGM data in a format that would be conducive to Android app development. If you do not have a substantial preference for Java, I highly recommend using the original Python version of this project by gagebenne linked above. 
Requirements:
- Enable the Dexcom Share service on your account and share it with at least one follower
- Use your credentials (not the followers) with the pydexcom or javadexcom packages
- For testing, I recommend downloading the .java files from this project and compiling them within your chosen IDE

```java
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
```
## Beta
This is still a very early build. In particular, error handling is almost entirely missing.
Please be very careful in using this service. It is NOT a replacement for your Dexcom app.
