package pack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class consts {
    // Dexcom application ID
    public static final String DEXCOM_APPLICATION_ID = "d89443d2-327c-4a6f-89e5-496bbb0317db";

    // Dexcom Share API base URL (Owned and operated by pydexcom team)
    public static final String DEXCOM_BASE_URL = "https://share2.dexcom.com/ShareWebServices/Services";

    // Dexcom Share API base url for outside of the US.
    public static final String DEXCOM_BASE_URL_OUS = "https://shareous1.dexcom.com/ShareWebServices/Services";

    //Dexcom Share API endpoint used to retrieve account ID.
    public static final String DEXCOM_LOGIN_ID_ENDPOINT = "General/LoginPublisherAccountById";

    //Dexcom Share API endpoint used to retrieve session ID.
    public static final String DEXCOM_AUTHENTICATE_ENDPOINT = "General/AuthenticatePublisherAccount";

    //Dexcom Share API endpoint used to retrieve glucose values.
    public static final String DEXCOM_GLUCOSE_READINGS_ENDPOINT = "Publisher/ReadPublisherLatestGlucoseValues";

    // UUID consisting of all zeros, likely error if returned by Dexcom Share API.
    public static final String DEFAULT_UUID = "00000000-0000-0000-0000-000000000000";

    public static final Map<String, Integer> DEXCOM_TREND_DIRECTIONS = new HashMap<String, Integer>() {
        {
            put("None", 0);
            put("DoubleUp", 1);
            put("SingleUp", 2);
            put("FortyFiveUp", 3);
            put("Flat", 4);
            put("FortyFiveDown", 5);
            put("SingleDown", 6);
            put("DoubleDown", 7);
            put("NotComputable", 8);
            put("RateOutOfRange", 9);
        }};
    public static final List<String> TREND_DESCRIPTIONS = List.of(
            "",
            "rising quickly",
            "rising",
            "rising slightly",
            "steady",
            "falling slightly",
            "falling",
            "falling quickly",
            "unable to determine trend",
            "trend unavailable"
    );

    public static final List<String> TREND_ARROWS = List.of(
            "", "↑↑", "↑", "↗", "→", "↘", "↓", "↓↓", "?", "-"
    );

    public static final int MAX_MINUTES = 1440;
    public static final int MAX_MAX_COUNT = 288;
    public static final float MMOL_L_CONVERSION_FACTOR = 0.0555F;
}
