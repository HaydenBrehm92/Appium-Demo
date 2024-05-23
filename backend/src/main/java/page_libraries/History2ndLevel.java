package page_libraries;
import core.constants.AppInformation;

public interface History2ndLevel {
    String MessageField = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
            "android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/" +
            "android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/" +
            "android.view.View[3]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/" +
            "android.widget.EditText";
    String SendMessage = "//*[contains(@text, \"Send PTX Message\")]";
    String IPA = "//*[contains(@text, \"Send alert\")]";
    String MapLocation = "//*[contains(@text, \"Send location\")]";
    String MapLocationPin = "//*[@id=\"share_location_div\"]"; // Requires WEBVIEW Context
    String SelfLocation = "//*[contains(@text, \"Self Location\")]";
    String Camera = "//*[contains(@text, \"Send image or video\")]";
    String CameraPhotoSelection = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Take photo\")]" : "//*[contains(@text, \"Take Photo\")]";
    String VoiceMessage = "//*[contains(@text, \"Send voice message\")]";
    String VoiceMessageRecord = (AppInformation.isVerizonPackage())
            ? "//*[contains(@text, \"Voice messagerecord\")]" : "//*[contains(@text, \"Voice Messagerecord\")]";
    String RecordVoice = "//*[contains(@text, \"record\")]"; // Requires WEBVIEW Context
    String File = "//*[contains(@text, \"SendFile\")]";
    String FileFirstEntry = "//*[contains(@resource-id, \"com.google.android.documentsui:id/item_root\")]";
    //String Back = "//*[contains(@text, \"Back\")]";
    String lastPtxMessage = "(//*[contains(@resource-id, \"ptxMsgBubble\")])[last()]";
    //String LastPtxMessage = "(//*[contains(@resource-id, \"container\")]//*[contains(@resource-id, \"ptxMsgBubble\")])[last()]"; //still needs last
    //String lastPtxMessage = "//*[contains(@id, \"ptxMsgBubble\")]";
}
