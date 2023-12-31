package javadexcom;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.consts;

public class GlucoseReading {
    private Map<String, Object> json;  // Json is actually a map, preferring similar docs

    private int value;
    private String trend_direction;
    private int trend;
    private LocalDateTime datetime;

    /**
     * Function to convert json WT date value into java datetime object
     * Parameter: String
     * Return: void
     */
    public void getDatetimeFromJson(String jsonWT){
            Pattern pattern = Pattern.compile("[^0-9]");
            Matcher matcher = pattern.matcher(jsonWT);
            String digitsOnly = matcher.replaceAll("");
            long timestamp = Long.parseLong(digitsOnly) / 1000;
            Instant instant = Instant.ofEpochSecond(timestamp);
            this.datetime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
    /**
     * Constructor for GlucoseReading. Takes map and converts to values
     * Parameter: Map<String, Object>
     * Return: void
     */
    public GlucoseReading(Map<String, Object> jsonGlucoseReading){
        // Assign values to variables
        this.json = jsonGlucoseReading;
        try {
            this.value = Integer.parseInt((this.json.get("Value").toString()));
            this.trend_direction = this.json.get("Trend").toString();
            this.trend = consts.DEXCOM_TREND_DIRECTIONS.get(this.trend_direction);
            getDatetimeFromJson(this.json.get("WT").toString());
        }
        catch (Exception e){
            System.out.println("Glucose Reading Invalid");
            System.out.println(e);
            System.out.println(this.json.toString());
        }
    }
    public int getValue(){return this.value;}
    public int getMG_DL(){return this.value;}
    public float getMMOL_DL(){return this.value * consts.MMOL_L_CONVERSION_FACTOR;}
    public int getTrend(){return this.trend;}
    public String getTrend_direction(){return this.trend_direction;}
    public Optional<String> getTrendDescription(){
        return consts.TREND_DESCRIPTIONS.get(this.trend).describeConstable();
    }
    public String getTrendArrow(){
        return consts.TREND_ARROWS.get(this.trend);
    }
    public LocalDateTime getDatetime(){return this.datetime;}
    public Map<String, Object> getJson() {return json;}

    @Override
    public String toString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        if (getTrendDescription().isPresent()) {
            return String.format("Time: %s\nValue: %d\nTrend Direction: %s\nTrend Description: %s" +
                            "\nTrend Arrow: %s\n",
                    dtf.format(getDatetime()),
                    getValue(),
                    getTrend_direction(),
                    getTrendDescription().get(),
                    getTrendArrow());
        } else{
            return String.format("Time: %s\nValue: %d\nTrend Direction: %s" +
                            "\nTrend Arrow: %s\n",
                    dtf.format(getDatetime()),
                    getValue(),
                    getTrend_direction(),
                    getTrendArrow());
        }
    }

}
