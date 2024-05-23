package tests.BasicPTT;

import UI.Controllers.MainController;
import api.Android;
import core.MyLogger;
import core.managers.DriverManager;
import core.managers.TestManager;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.*;
import tests.BaseTest;
import tests.Data.DataProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HistoryLoadTest extends BaseTest {
    private String ptxMessage;
    private static int ptxMessageNum;
    private static int totalTasks = 0;
    private TestManager.ptxManager ptxManager = new TestManager.ptxManager();
    @Test(dataProvider = "HistoryLoadIterations", dataProviderClass = DataProviders.class)
    public void HistoryLoad(int iteration) {
        setDataProviderIterationVal(iteration);
        if (!MainController.testManager.getPTXMessage().equals("0"))
        {
            List<String> matches = new ArrayList<>();
            ptxMessage = MainController.testManager.getPTXMessage();
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher matcher = pattern.matcher(ptxMessage);
            while(matcher.find()) {
                matches.add(matcher.group());
            }

            if(!matches.isEmpty()) {
                for (String item : matches) {
                    if (!item.equals("")) {
                        ptxMessage = ptxMessage.replace(item,"").trim();
                        ptxMessageNum = Integer.parseInt(item);
                    }
                }
            } else ptxMessageNum = 1;
        } else {
            ptxMessage = "Test";
            ptxMessageNum = 1;
        }

        if (MainController.testManager.isSmartPhone()) {
            try {
                if (MainController.testManager.isHandset()) {
                    getUIObject(TabBar.History).waitUntilClickable(5000).tap();
                    getUIObject(History.StandardMostRecent).waitUntilClickable(5000).tap();
                } else {
                    getUIObject(Hamburger.Menu).waitUntilClickable(5000).tap();
                    getUIObject(Hamburger.History).waitUntilClickable(5000).tap();
                    getUIObject(History.RadioMostRecent).waitUntilClickable(5000).tap();
                }

                waitSimulation(3000);
                getTaskCount();
                MainController.testManager.getMainController().progressBarSetUp();
                MainController.testManager.
                        getMainController().setProgressPerTask
                        (MainController.testManager.getIterations(), 1000);

                // Making Calls from most recent ptx entry
                testLoop();
            } catch (Exception e) {
                e.printStackTrace();
                DriverManager.killAllDrivers();
            }
            MainController.testManager.getMainController().completeProgressBar(); //outside loop, just complete 100% before test ends
            waitSimulation(1000);
        }
    }

    private boolean isPtxBuilder(){
        return MainController.testManager.getBuildMessage().equals("1") ||
                MainController.testManager.getBuildIPA().equals("1") ||
                MainController.testManager.getBuildLocation().equals("1") ||
                MainController.testManager.getBuildVoiceRecord().equals("1") ||
                MainController.testManager.getBuildPicture().equals("1") ||
                MainController.testManager.getBuildFile().equals("1");
    }


    @Override
    public void resetToDefaultLocation() {

    }

    @Override
    public void recover() {

    }

    @Override
    public void testLoop() {
        int sentMessages = 0;
        try {
            getUIObject(CallScreen.SendPTX).tap();
            waitSimulation(3000);

            for (int i = 0; i < getIterations(); i++) {
                for (int j = 0; j < (1000 / totalTasks); j++) {
                    if (!isPtxBuilder())
                        executeMessage();
                    else {
                        if (isBuildMessageEnabled())
                            executeMessage();
                        if (isBuildIPAEnabled())
                            executeIPA();
                        if (isBuildLocationEnabled())
                            executeLocation();
                        if (isBuildVoiceRecordEnabled())
                            executeVoiceRecord();
                        if (isBuildPictureEnabled())
                            executePicture();
                        if (isBuildFileEnabled())
                            executeFile();
                    }

                    sentMessages++;
                    ptxMessageNum++;
                    MyLogger.log.info("Sent Message # {}", sentMessages);
                    if (i != (MainController.testManager.getIterations() - 1)) {
                        waitSimulation(MainController.testManager.getDelay());
                    }

                }
                // Selecting next history
                //waitSimulation(1000);
                getUIObject(Common.Back).waitUntilClickable(5000).tap();
                //waitSimulation(1000);
                getUIObject(Common.Back).waitUntilClickable(5000).tap();
                //waitSimulation(1000);

                DriverManager.getCurrentAndroid().setContext(Android.Contexts.WebView);
                MyLogger.log.debug("Context Changed to ----> {}", DriverManager.getCurrentAndroid().getContext());
                //waitSimulation(1000);
                getUIObject(History.Search).waitUntilClickable(5000).tap();
                //waitSimulation(1000);

                if ((i + 2) <= MainController.testManager.getIterations())
                    getUIObject(History.Search).waitUntilVisible(5000).inputKeys("Automation " + (i + 2));

                DriverManager.getCurrentAndroid().setContext(Android.Contexts.Native);
                if (MainController.testManager.isHandset())
                    getUIObject(History.StandardMostRecent).waitUntilClickable(5000).tap();
                else
                    getUIObject(History.RadioMostRecent).waitUntilClickable(5000).tap();

                getUIObject(CallScreen.SendPTX).waitUntilClickable(5000).tap();
                //waitSimulation(1000);
                MainController.testManager.updateCurrentIteration();

            }

            MainController.testManager.resetCurrentIteration();  // reset for CustomTest. Does nothing meaningful else wise
            getUIObject(Common.Back).waitUntilClickable(5000).tap(); // Just to go back to call screen
            Assert.assertEquals(sentMessages, MainController.testManager.getIterations(), "Missing Messages!!!");
        } catch (Exception e) {
            MainController.testManager.getMainController().log("Exception Occurred! Something went wrong!");
            MyLogger.log.debug("Exception Occurred! ----> {}", e.getMessage());
            e.printStackTrace();
        }

        waitSimulation(1000);
    }

    private void getTaskCount(){
        if(isPtxBuilder()) {
            if (isBuildMessageEnabled())
                totalTasks++;
            if (isBuildIPAEnabled())
                totalTasks++;
            if(isBuildLocationEnabled())
                totalTasks++;
            if(isBuildVoiceRecordEnabled())
                totalTasks++;
            if(isBuildPictureEnabled())
                totalTasks++;
            if(isBuildFileEnabled())
                totalTasks++;
        } else totalTasks = 1;
    }

    private boolean isBuildMessageEnabled(){
        return MainController.testManager.getBuildMessage().equals("1");
    }
    private boolean isBuildIPAEnabled(){
        return MainController.testManager.getBuildIPA().equals("1");
    }

    private boolean isBuildLocationEnabled(){
        return MainController.testManager.getBuildLocation().equals("1");
    }

    private boolean isBuildVoiceRecordEnabled(){
        return MainController.testManager.getBuildVoiceRecord().equals("1");
    }

    private boolean isBuildPictureEnabled(){
        return MainController.testManager.getBuildPicture().equals("1");
    }

    private boolean isBuildFileEnabled(){
        return MainController.testManager.getBuildFile().equals("1");
    }

    private void executeMessage(){
        /*
         * PTX Message
         */
        MainController.testManager.getMainController().log("Working PTX Message # " + (ptxMessageNum));
        getUIObject(History2ndLevel.MessageField).waitUntilVisible(5000).inputKeys(ptxMessage + " " + ptxMessageNum);
        //waitSimulation(500);
        getUIObject(History2ndLevel.SendMessage).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent PTX Message # " + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();
        waitSimulation(1000);
    }

    private void executeIPA(){
        /*
         * IPA ptx
         */
        if (ptxManager.getIPA()) {
            if (getUIObject(History2ndLevel.IPA).elementExists()) {
                MainController.testManager.getMainController().log("Working IPA..." + (ptxMessageNum));
                //waitSimulation(1000);
                getUIObject(History2ndLevel.IPA).tap();
                MainController.testManager.getMainController().log("Sent IPA" + (ptxMessageNum));
                MainController.testManager.getMainController().taskComplete();
            } else {
                ptxManager.setIPA(false);
                MainController.testManager.getMainController().
                    setProgressPerTask(MainController.testManager.getIterations(), (1000/(totalTasks - 1)));
            }
        }

        waitSimulation(1000);
    }

    private void executeLocation(){
        /*
         * Map location ptx
         */
        MainController.testManager.getMainController().log("Working Map Location..." + (ptxMessageNum));
        //waitSimulation(2000);
        getUIObject(History2ndLevel.MapLocation).waitUntilClickable(5000).tap();
        //waitSimulation(2000);
        getUIObject(History2ndLevel.SelfLocation).waitUntilClickable(5000).tap();
        DriverManager.getCurrentAndroid().setContext(Android.Contexts.WebView);
        MyLogger.log.debug("Context Changed to ----> {}", DriverManager.getCurrentAndroid().getContext());
        //waitSimulation(2000);
        getUIObject(History2ndLevel.MapLocationPin).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent Map Location..." + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();
        DriverManager.getCurrentAndroid().setContext(Android.Contexts.Native);

        waitSimulation(1000);
    }

    private void executeVoiceRecord(){
        /*
         * Voice Recording (10 seconds)
         */
        MainController.testManager.getMainController().log("Working Voice Recording..." + (ptxMessageNum));
        getUIObject(History2ndLevel.VoiceMessage).waitUntilClickable(5000).tap();
        //waitSimulation(2000);
        getUIObject(History2ndLevel.VoiceMessageRecord).waitUntilClickable(5000).LongPress(5);
        getUIObject(Common.OK).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent Voice Recording..." + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();
        waitSimulation(1000);
    }

    private void executePicture(){
        /*
         * Picture PTX
         */
        MainController.testManager.getMainController().log("Working Picture..." + (ptxMessageNum));
        getUIObject(History2ndLevel.Camera).waitUntilClickable(5000).tap();
        //waitSimulation(1000);
        getUIObject(History2ndLevel.CameraPhotoSelection).waitUntilClickable(5000).tap();
        waitSimulation(2000);
        DriverManager.getCurrentAndroid().getADB().cameraKey();
        waitSimulation(2000);

        if (DriverManager.getCurrentAndroid().getADB().getDevModel().contains("Pixel"))
            DriverManager.getCurrentAndroid().getADB().cameraKey();
        else
            getUIObject(Common.OK).waitUntilClickable(5000).tap();

        //waitSimulation(2000);
        getUIObject(Common.OK).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent Picture..." + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();
        waitSimulation(2000);
    }

    private void executeFile(){
        /*
         * File PTX
         */
        MainController.testManager.getMainController().log("Working File..." + (ptxMessageNum));
        getUIObject(History2ndLevel.File).waitUntilClickable(5000).tap();
        if (getUIObject(History2ndLevel.FileFirstEntry).elementExists())
        {
            getUIObject(History2ndLevel.FileFirstEntry).waitUntilClickable(5000).tap();
            //waitSimulation(1000);
            getUIObject(Common.OK).waitUntilClickable(5000).tap();
            MainController.testManager.getMainController().log("Sent File..." + (ptxMessageNum));
        }
    }
}
