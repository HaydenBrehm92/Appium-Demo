package page_libraries;

public interface Common
{
    String Back = "//*[contains(@text, 'Back')]";
    String OK = "//*[contains(@text, \"OK\")]";
    String Cancel = "//*[contains(@text, \"Cancel\")]";
    String Yes = "//*[contains(@text, \"Yes\")]";
    String NotNow = "//*[contains(@text, \"Not now\")]";
    String Dismiss = "//*[contains(@text, \"Dismiss\")]";
    String Confirm = "//*[contains(@text, \"Confirm\")]";
    String Information = "//*[contains(@text, \"Information\")]";
    String PleaseWait = "//*[contains(@text, \"Please Wait\")]";
    String Send = "//*[contains(@text, \"Send\")]";
}
