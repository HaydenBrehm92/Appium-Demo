package page_libraries;
import core.constants.AppInformation;

public interface ManualDial {
    public static String NumberField = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/" +
            "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.webkit.WebView/android.webkit.WebView/android.view.View/android.view.View/" +
            "android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/" +
            "android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View/" +
            "android.widget.EditText";
    public static String Call = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"PTT call\")]" : "//*[contains(@text, \"PTT Call\")]";
    //public static String Back = "//*[contains(@text, 'Back')]";
}
