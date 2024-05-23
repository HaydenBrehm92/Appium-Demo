package page_libraries;

import core.constants.AppInformation;

public interface Alerts
{
    public static String EmergencyAlert = (AppInformation.isVerizonPackage())
        ? "//*[contains(@text, \"Urgent alert\")]" : "//*[contains(@text, \"Emergency Alert\")]";
    public static String ConnectionUnavailable = "//*[contains(@text, \"Connection is unavailable\")]";
    public static String NoConnection = "//*[contains(@text, \"No Connection\")]";
    public static String Reconnecting = "//*[contains(@text, \"Reconnecting\")]";
    public static String MCA = "//*[contains(@text, \"Missed Call Alert\")]";
    public static String IPA = "//*[contains(@text, \"sent you an Instant Personal Alert\")]";
}
