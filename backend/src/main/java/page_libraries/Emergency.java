package page_libraries;

import core.constants.AppInformation;

public interface Emergency {
    public static String EmergencyDeclareIcon = "//*[contains(@text, \"Declare Emergency. Long Press to Declare Emergency\")]";
    public static String EmergencyCancelIcon = "//*[contains(@text, \"Cancel Emergency. Long Press to Cancel Emergency\")]";
    public static String EmergencyDeclareSliderIcon =
            "//*[contains(@text, \"Emergency icons selected. Long press and slide right to declare Emergency\")]";
    public static String EmergencyCancelSliderIcon =
            "//*[contains(@text, \"Emergency icons selected. Long press and slide right to cancel Emergency\")]";
    public static String EmergencyDeclareSliderBar = "//*[contains(@text, \"Declare\")]";
    public static String EmergencyCancelSliderBar = "//*[contains(@text, \"Cancel\")]";
    public static String EmergencyDeclaredBanner = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Urgent\")]" : "//*[contains(@text, \"Emergency Declared\")]";
    public static String EmergencyCancelRealEmergency = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Real alert\")]" : "//*[contains(@text, \"Real emergency\")]";
    public static String EmergencyCancelFalseAlarm = "//*[contains(@text, \"False alarm\")]";
    public static String EmergencyCancelSend = "//*[contains(@text, \"Send\")]";
    public static String CancelEmergencyBanner = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Cancel urgent alert\")]" : "//*[contains(@text, \"Cancel Emergency\")]";
    public static String DeclareEmergencyBanner = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Declare urgent alert\")]" : "//*[contains(@text, \"Declare Emergency\")]";
}
