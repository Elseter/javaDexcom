package javadexcom;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import pack.consts;


public class Dexcom {
    // Class variables
    private String base_URL;
    private String username;
    private String password;
    private String account_id = null;
    private String session_id = null;

    /**
     * Constructor: gets account_id and session_id for getting values
     * account id is required for session_id and session_id is required
     * to get BS values
     * @param username Dexcom Share username
     * @param password Dexcom Share password
     * @param ous  false if in US, true if outside US
     *
     */
    public Dexcom(String username, String password, boolean ous) throws IOException {
        if (ous) {
            this.base_URL = consts.DEXCOM_BASE_URL_OUS;
        } else {
            this.base_URL = consts.DEXCOM_BASE_URL;
        }

        this.username = username;
        this.password = password;
        this.account_id = getAccountID();
        this.session_id = getSessionID();


    }

    /**
     * The focal point of this class. post creates a URL based on the base URL
     * followed by different extensions. A json is then generated and posted to that api
     * based the provided map. The response is then returned as a string
     * @param endpoint
     * @param json
     * @return API response
     * @throws IOException
     */
    private String post(String endpoint, Map<String, Object> json) throws IOException {

        // Set up initial connection
        URL url = new URL(String.format("%s/%s", this.base_URL, endpoint));
        URLConnection con = url.openConnection();
        HttpsURLConnection https = (HttpsURLConnection) con;
        https.setRequestMethod("POST");
        https.setDoOutput(true);

        // Construct string from provided map
        StringBuilder jsonVals = new StringBuilder("{");
        for (String key : json.keySet()) {
            String temp = String.format("\"%s\":\"%s\",", key, json.get(key));
            jsonVals.append(temp);
        }
        jsonVals.deleteCharAt(jsonVals.length() - 1);
        jsonVals.append("}");

        // Turn string into json
        byte[] out = jsonVals.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        // set settings and push json to API
        https.setFixedLengthStreamingMode(length);
        https.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        https.connect();
        try (OutputStream os = https.getOutputStream()) {
            os.write(out);
        }

        // Get the response as an Input stream and read it into a string
        // The first and last chars are " and can be removed as they are implicit
        InputStream in = https.getInputStream();
        String response = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        response = response.substring(1, response.length() - 1);

        // Close connections
        in.close();
        https.disconnect();

        return response;
    }

    /**
     * Function utilizing post to get Account ID from API
     *
     * @return AccountID as String
     * @throws IOException
     */
    private String getAccountID() throws IOException {
        // Construct Json for account ID
        Map<String, Object> accountIDJson = new HashMap<>();
        accountIDJson.put("accountName", this.username);
        accountIDJson.put("password", this.password);
        accountIDJson.put("applicationId", consts.DEXCOM_APPLICATION_ID);

        return this.post(consts.DEXCOM_AUTHENTICATE_ENDPOINT, accountIDJson);
    }

    private String getSessionID() throws IOException {
        // Construct Json for Session ID
        Map<String, Object> sessionIDJson = new HashMap<>();
        sessionIDJson.put("accountId", this.account_id);
        sessionIDJson.put("password", this.password);
        sessionIDJson.put("applicationId", consts.DEXCOM_APPLICATION_ID);

        return this.post(consts.DEXCOM_LOGIN_ID_ENDPOINT, sessionIDJson);
    }

    /**
     * Black Magic to convert the string Json response into a hashmap. Series of loops to parse
     * string.
     * @param str
     * @return HashMap
     */
    private Map<Integer, Map<String, Object>> convertStringtoMap(String str){
        // Create hashmap that will store all values
        Map<Integer, Map<String, Object>> painfulOrganization = new HashMap<>();

        // Split the provided string based on right bracket
        // This splits string into list, with each entry being a reading
        String[] arr = str.split("}");

        // Create temp variables that will store various strings as we deconstruct
        // and fetch the necessary parts of these strings
        String temp;
        String[] temp2;
        String[] temp3;
        Map<String, Object> tempMap = null;

        // count keeps track of how many readings we have. We must have at least 1
        int count = 1;

        // For each of the readings, we remove the first char ( { ) or first two chars ( ,{ )
        // This helps isolate the reading info
        for(int i=0; i<arr.length; i++){
            if(i==0){
                temp = arr[i].substring(1);
            }else{
                temp = arr[i].substring(2);
            }

            // Split the strings by comma, separating the different values recorded with each reading
            temp2 = temp.split(",");

            // Erase the previous tempMap
            tempMap = new HashMap<>();

            // For each value in the reading, we separate them into key ("Value")
            // and value (115) or ("WT") and ("Date(132354345345)")
            for (String s : temp2) {
                temp3 = s.split(":");
                for (int k=0; k<temp3.length; k++) {
                    // Remove extra " that are a result of json conversion
                    temp3[k] = temp3[k].replaceAll("\"", "");
                }
                // Add these cleaned readings to the tempMap
                tempMap.put(temp3[0], temp3[1]);
            }
            // Add all the information for the reading (tempMap) and the number (count)
            // to the final map. Then increase the count
            painfulOrganization.put(count, tempMap);
            count++;
        }
        return painfulOrganization;
    }

    /**
     * Private method that actually fetches glucose information from the API using
     * session ID, minutes, and count. Then uses convert String to map to return this result as
     * a hashmap ordered from most recent (1) to oldest (largest num)
     * @return HashMap
     * @throws IOException
     */
    private Map<Integer, Map<String, Object>> getGlucoseReadingsPrivate() throws IOException {
        // Construct Json for Blood Sugars
        Map<String, Object> sJson = new HashMap<>();
        sJson.put("sessionId", this.session_id);
        sJson.put("minutes", consts.MAX_MINUTES);
        sJson.put("maxCount", consts.MAX_MAX_COUNT);

        String response = this.post(consts.DEXCOM_GLUCOSE_READINGS_ENDPOINT, sJson);
        Map<Integer, Map<String, Object>> painfulOrganization = this.convertStringtoMap(response);

        return painfulOrganization;
    }
    /**
     * Overloaded version of getGlucoseReadingsPrivate
     * @param max_count
     * @param minutes
     * @return
     * @throws IOException
     */
    private Map<Integer, Map<String, Object>> getGlucoseReadingsPrivate(int minutes, int max_count) throws IOException {
        // Construct Json for Blood Sugars
        Map<String, Object> sJson = new HashMap<>();
        sJson.put("sessionId", this.session_id);
        sJson.put("minutes", minutes);
        sJson.put("maxCount", max_count);

        String response = this.post(consts.DEXCOM_GLUCOSE_READINGS_ENDPOINT, sJson);
        Map<Integer, Map<String, Object>> painfulOrganization = this.convertStringtoMap(response);

        return painfulOrganization;
    }
    /**
     * Checks to see if current session ID exists and is not default
     * @return boolean
     */
    private boolean validate_session_id(){
        if (this.session_id != null) {
            if (!this.session_id.equals(consts.DEFAULT_UUID)) {
                return true;
            }
            System.out.println("Error: Session ID not valid");
        }
        else{
            System.out.println("Error: Session ID Default");
        }
        return false;
    }

    /**
     * Checks to see if current account ID exists and is not default
     * @return boolean
     */
    private boolean validate_account_id(){
        if (this.account_id != null) {
            if (!this.account_id.equals(consts.DEFAULT_UUID)) {
                return true;
            }
            System.out.println("Error: Account ID not valid");
        }
        else{
            System.out.println("Error: Account ID Default");
        }
        return false;
    }


    public List<GlucoseReading> getGlucoseReadings() throws IOException {
        // Create map to temp hold values
        Map<Integer, Map<String, Object>> tempMap;
        List<GlucoseReading> returnList = new ArrayList<>();

        try{
            if (!this.validate_session_id()){
                throw new Exception();
            }
            tempMap = this.getGlucoseReadingsPrivate();
        } catch (Exception e) {
            System.out.println(e);
            this.getAccountID();
            this.getSessionID();

            tempMap = this.getGlucoseReadingsPrivate();

        }
        for (Map<String, Object> val : tempMap.values()){
            returnList.add(new GlucoseReading(val));
        }

        return returnList;

    }

    public List<GlucoseReading> getGlucoseReadings(int minutes, int max_count) throws IOException {
        // Create map to temp hold values
        Map<Integer, Map<String, Object>> tempMap;
        List<GlucoseReading> returnList = new ArrayList<>();

        try{
            if (!this.validate_session_id()){
                throw new Exception();
            }
            tempMap = this.getGlucoseReadingsPrivate(minutes, max_count);
        } catch (Exception e) {
            System.out.println(e);
            this.getAccountID();
            this.getSessionID();

            tempMap = this.getGlucoseReadingsPrivate(minutes, max_count);
        }
        for (Map<String, Object> val : tempMap.values()){
            returnList.add(new GlucoseReading(val));
        }
        return returnList;
    }

    public GlucoseReading getLatestGlucoseReading() throws IOException {
        return this.getGlucoseReadings(consts.MAX_MINUTES, 1).get(0);
    }
    public GlucoseReading getCurrentGlucoseReading() throws IOException {
        return this.getGlucoseReadings(10, 1).get(0);
    }

}
