package page_libraries;

public interface Activation {
    public static String Allow = "//*[contains(@text, \"Allow\")]";
    public static String Agree = "//*[contains(@text, \"AGREE\")]";
    //public static String ShareLocation = "//*[contains(@text, \"OK\")]";
    public static String DeviceLocation = "//*[contains(@text, \"Allow all the time\")]";
    //public static String Login = "//*[contains(@text, \"Yes\")]";
    public static String EULA = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/" +
            "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/" +
            "android.webkit.WebView/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[2]/android.view.View/android.view.View[2]/android.view.View/android.widget.Button[1]";
}
