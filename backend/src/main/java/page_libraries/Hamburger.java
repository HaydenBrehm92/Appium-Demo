package page_libraries;
import core.constants.AppInformation;

public interface Hamburger {
    public static String Menu = "//*[contains(@text, \"Menu\")]";
    public static String History = "//*[contains(@text, \"History\")]";
    public static String Contacts = "//*[contains(@text, \"Contacts\")]";
    public static String Map = "//*[contains(@text, \"Map\")]";
    public static String Favorites = "//*[contains(@text, \"Favorites\")]";
    public static String ChannelsandZones = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Channels/zones\")]" : "//*[contains(@text, \"Channels/Zones\")]";
    public static String ManualDial = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Manual dial\")]" : "//*[contains(@text, \"Manual Dial\")]";
    public static String Settings = "//*[contains(@text, \"Settings\")]";
}
