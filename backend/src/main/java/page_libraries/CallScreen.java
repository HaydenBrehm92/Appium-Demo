package page_libraries;

public interface CallScreen {
    public static String PTT = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[2]/android.view.View/android.widget.TextView";
    public static String EndCall = "//*[contains(@text, \"call end\")]";
    public static String Title = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/" +
            "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.webkit.WebView/android.webkit.WebView/android.view.View/android.view.View/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[3]/android.view.View/android.view.View/android.widget.TextView";
    public static String Speaking = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/" +
            "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.webkit.WebView/android.webkit.WebView/android.view.View/android.view.View/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/" +
            "android.view.View/android.view.View/android.widget.TextView";
    public static String ContactsList = "//*[contains(@text, \"Select Contact\")]";
    public static String ChannelsAndZonesList = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[1]/android.view.View/android.view.View[2]/" +
            "android.widget.TextView";
    public static String AdHocAdd = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[1]/android.view.View/android.view.View[2]/" +
            "android.widget.TextView";
    public static String TalkGroupScan = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[3]/android.view.View/android.view.View[1]/" +
            "android.widget.TextView";
    public static String VolumeSlider = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[3]/android.view.View/android.view.View[2]/" +
            "android.widget.TextView";
    public static String Earpiece = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[6]/android.view.View/android.view.View[3]/android.view.View/android.view.View[3]/" +
            "android.widget.TextView";
    public static String CallwithTitle = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[7]/android.view.View/android.view.View/android.view.View/android.widget.TextView";
    public static String SendPTX = "//*[contains(@text, \"Send text\")]";
    public static String SendIPA = "//*[contains(@text, \"Send alert\")]";
    public static String MapLocation = "//*[contains(@text, \"Send location\")]";
    public static String Camera = "//*[contains(@text, \"Send image or video\")]";
    public static String VideoCall = "//*[contains(@text, \"Video Call\")]";
}
