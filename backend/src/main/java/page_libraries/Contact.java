package page_libraries;
import core.constants.AppInformation;

public interface Contact {
    public static String AddContact = "//*[@text=\"Add\" and @class=\"android.widget.Button\"]";
    public static String NewContact = (AppInformation.isVerizonPackage())
            ? "//*[@text=\"New PTT contact\"]" : "//*[@text=\"New PTT Contact\"]";
    public static String EnterContactName = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/" +
            "android.webkit.WebView/android.view.View/android.view.View/android.view.View/" +
            "android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/" +
            "android.view.View/android.view.View[1]/android.view.View/android.view.View/" +
            "android.view.View/android.view.View/android.view.View[1]/android.widget.EditText";
    public static String EnterPhoneNumber = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/" +
            "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.webkit.WebView/android.webkit.WebView/android.view.View/android.view.View/" +
            "android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/" +
            "android.view.View/android.view.View[5]/android.view.View/android.view.View/android.view.View/" +
            "android.view.View/android.view.View/android.widget.EditText";
    public static String ContactMaxInformationPopup = "//*[@text=\"Information\"]";
    //public static String ContactMaxInformationPopupConfirmation = "//*[@text=\"OK\"]";
    public static String SaveContact = "//*[@text=\"Save\"]";
}
