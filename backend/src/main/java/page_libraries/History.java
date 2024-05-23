package page_libraries;

public interface History {
    //public static String Back = "//*[contains(@text, \"Back\")]";
    public static String StandardMostRecent = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[3]/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View";
    public static String RadioMostRecent = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[3]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/" +
            "android.view.View/android.view.View/android.view.View";
    public static String Search = "(//*[contains(@placeholder, \"Search\")])[3]";
}
