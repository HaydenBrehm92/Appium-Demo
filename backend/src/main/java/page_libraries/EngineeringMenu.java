package page_libraries;

public interface EngineeringMenu {
    public static String WebDebug = "//*[contains(@text, 'Web Content Debug')]/parent::*/parent::*/parent::*/parent::" +
            "*/child::*[contains(@class, 'android.widget.CheckBox')]";
    public static String EnableConsoleLogs = "//*[contains(@text, 'Enable Console Logs')]/parent::*/parent::*/parent::" +
            "*/parent::*/child::*[contains(@class, 'android.widget.CheckBox')]";
    public static String CopyPttLogs = "//*[contains(@text, \"Copy PTT files\")]";
}
