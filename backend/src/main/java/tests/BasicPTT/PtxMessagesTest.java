package tests.BasicPTT;

import UI.Controllers.MainController;
import api.Android;
import core.MyLogger;
import core.managers.TestManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.*;
import tests.BaseTest;
import tests.Data.DataProviders;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import core.managers.DriverManager;


public class PtxMessagesTest extends BaseTest {
    private String ptxMessage;
    private static int ptxMessageNum;
    private static int totalTasks = 0;
    private final TestManager.ptxManager ptxManager = new TestManager.ptxManager();


    @Test(dataProvider = "PtxMessagesIterations", dataProviderClass = DataProviders.class)
    public void MessagesTest(int iteration) {
        setDataProviderIterationVal(iteration);
        MyLogger.log.debug("Entering MessagesTest() with {} iterations", iteration);
        if (!MainController.testManager.getPTXMessage().equals("0"))
        {
            List<String> matches = new ArrayList<>();
            ptxMessage = MainController.testManager.getPTXMessage();
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher matcher = pattern.matcher(ptxMessage);

            while(matcher.find()) {
                matches.add(matcher.group());
            }

            boolean foundNum = false;
            if(!matches.isEmpty()) {
                for (String item : matches) {
                    if (!item.isEmpty()) {
                        foundNum = true;
                        ptxMessage = ptxMessage.replace(item,"").trim();
                        ptxMessageNum = Integer.parseInt(item);
                    }

                    if (!foundNum)
                        ptxMessageNum = 1;
                }
            }

            int count = String.valueOf(ptxMessageNum).length();
            if((ptxMessage.length() + count) + 1 > 300) //+1 for space when we append
            {
                int overCharLimitBy = (ptxMessage.length() + count + 1) - 300;
                ptxMessage = ptxMessage.substring(0,299 - overCharLimitBy);
            }

        } else {
            ptxMessage = "Test";
            ptxMessageNum = 1;
        }

        getTaskCount();
        MainController.testManager.getMainController().progressBarSetUp();
        MainController.testManager.
                getMainController().setProgressPerTask
                        ((MainController.testManager.getIterations() * iteration), totalTasks);

        // Most recent history entry
        testLoop();
    }

    @Override
    public void testLoop() {
        waitSimulation(1000); //testing to see if staleelement persists
        int sentMessages = 0;
        try
        {
            if (MainController.testManager.isHandset())
            {
                getUIObject(TabBar.History).waitUntilClickable(5000).tap();
                //waitSimulation(3000);
                getUIObject(History.StandardMostRecent).waitUntilClickable(5000).tap();
                //waitSimulation(3000);
            }
            else
            {
                getUIObject(Hamburger.Menu).waitUntilClickable(5000).tap();
                getUIObject(Hamburger.History).waitUntilClickable(5000).tap();
                //waitSimulation(3000);
                getUIObject(History.RadioMostRecent).waitUntilVisible(5000).tap();
                //waitSimulation(3000);
            }

            getUIObject(CallScreen.SendPTX).waitUntilClickable(5000).tap();
            waitSimulation(1000);

            for (int i = MainController.testManager.getCurrentIteration();
                 i < getIterations(); i++)
            {
                MyLogger.log.debug("Testloop iteration {} begin", (i + 1));

                // small delay between test iterations
                waitSimulation(1000);

                if (!isPtxBuilder())
                {
                    if (!executeMessage())
                        return;
                }
                else
                {
                    if (MainController.testManager.isBuildMessageEnabled())
                        if (!executeMessage())
                            return;
                    if (MainController.testManager.isBuildIPAEnabled())
                        if (!executeIPA())
                            return;
                    if (MainController.testManager.isBuildLocationEnabled())
                        if (!executeLocation())
                            return;
                    if (MainController.testManager.isBuildVoiceRecordEnabled())
                        if (!executeVoiceRecord())
                            return;
                    if (MainController.testManager.isBuildPictureEnabled())
                        if (!executePicture())
                            return;
                    if (MainController.testManager.isBuildFileEnabled())
                        if (!executeFile())
                            return;
                }

                sentMessages++;
                ptxMessageNum++;
                MyLogger.log.info("Sent Message # {}", sentMessages);
                MainController.testManager.updateCurrentIteration();

                //Don't delay after the last entry was made
                if (i != (MainController.testManager.getIterations() - 1))
                    waitSimulation(MainController.testManager.getDelay());
            }

            MainController.testManager.resetCurrentIteration(); // reset for CustomTest. Does nothing meaningful else wise
            //back out once
            getUIObject(Common.Back).waitUntilClickable(5000).tap();
            if (MainController.testManager.isHandset())
                getUIObject(Common.Back).waitUntilClickable(5000).tap(); // One more back button is required for Standard clients to exit call screen

            Assert.assertEquals(sentMessages,
                    MainController.testManager.getIterations(),
                    "Total PTX iterations does not match!");
        } catch (NoSuchElementException | StaleElementReferenceException ex){
            MyLogger.log.debug("NoSuchElementException Occurred! ----> {}", ex.getMessage());
            ex.printStackTrace();
            recover();
        }
        catch (Exception e) {
            e.printStackTrace();
            DriverManager.killAllDrivers();
        }

        MainController.testManager.getMainController().completeProgressBar(); //outside loop, just complete 100% before test ends
        //waitSimulation(10000);
        waitSimulation(1000);
    }

    @Override
    public void resetToDefaultLocation() {
        MyLogger.log.debug("Executing resetToDefaultLocation.");

        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        try{
            MyLogger.log.debug("Checking for Back button");
            while (getUIObject(Common.Back).elementExists()) {
            //while (driver.findElement(By.xpath("//*[contains(@text, \"Back\")]")).isDisplayed()) {
                //waitSimulation(1000);
                MyLogger.log.debug("Back button is displayed");
                //driver.findElement(By.xpath("//*[contains(@text, \"Back\")]")).click();
                getUIObject(Common.Back).tap();

                if (getPopup().isPopUpDisplayed())
                    return;
            }
        }catch (NoSuchElementException e){
            MyLogger.log.debug("Back button not found. Continuing...");
        }
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(45));
        testLoop();
    }

    @Override
    public void recover() {
        if(getPopup().isPopUpDisplayed()){
            MainController.testManager.getMainController().
                    log("Popup " + getPopup().getPopupType().toString() + " occurred.");
            getPopup().handlePopup();
            MainController.Worker worker = MainController.testManager.getMainController().getWorker();

            //Have to check or thread, even after executorservice shutdownnow, will continue to run
            if(worker.isCancelled()) {   //if we get here then sleep consumed the cancel button. Interrupt the thread to stop it.
                MyLogger.log.debug("Worker is cancelled. Interrupting thread {}", Thread.currentThread().getName());
                return;
            }

            resetToDefaultLocation();
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

    private void getTaskCount(){
        if(isPtxBuilder()) {
            if (MainController.testManager.isBuildMessageEnabled())
                totalTasks++;
            if (MainController.testManager.isBuildIPAEnabled())
                totalTasks++;
            if(MainController.testManager.isBuildLocationEnabled())
                totalTasks++;
            if(MainController.testManager.isBuildVoiceRecordEnabled())
                totalTasks++;
            if(MainController.testManager.isBuildPictureEnabled())
                totalTasks++;
            if(MainController.testManager.isBuildFileEnabled())
                totalTasks++;
        } else totalTasks = 1;
    }

    private boolean executeMessage(){
        /*
         * PTX Message
         */
        //For > 300 char limit. No reason to handle char limit here...app handles this, just click "OK"
        // this slows down considerably...maybe handle before
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Working PTX Message # " + (ptxMessageNum));

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        getUIObject(History2ndLevel.MessageField).waitUntilVisible(5000).inputKeys(ptxMessage);
        //waitSimulation(500);

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        getUIObject(History2ndLevel.SendMessage).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent PTX Message # " + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();

        //switchDriverPtxConfirm(); //checking on other driver

        waitSimulation(1000);
        return true;
    }

    private boolean executeIPA(){
        /*
         * IPA ptx
         */
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        if (ptxManager.getIPA()) {
            if (getUIObject(History2ndLevel.IPA).elementExists()) {
                MainController.testManager.getMainController().log("Working IPA..." + (ptxMessageNum));

                if (getPopup().isPopUpDisplayed())
                {
                    recover();
                    return false;
                }

                getUIObject(History2ndLevel.IPA).waitUntilClickable(5000).tap();
                MainController.testManager.getMainController().log("Sent IPA" + (ptxMessageNum));
                MainController.testManager.getMainController().taskComplete();
            } else {
                ptxManager.setIPA(false);
                MainController.testManager.getMainController().
                        setProgressPerTask(MainController.testManager.getIterations(), (1000/(totalTasks - 1)));
            }
        }

        waitSimulation(1000);
        return true;
    }

    private boolean executeLocation(){
        /*
         * Map location ptx
         */
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Working Map Location..." + (ptxMessageNum));
        //waitSimulation(2000);
        getUIObject(History2ndLevel.MapLocation).waitUntilClickable(5000).tap();
        //waitSimulation(2000);
        //history2ndLevel.SelfLocation().waitUntilClickable(5000).tap();

        // sometimes the map can be completely white, this will cause issues.
        // if that's the case, go back out from the maps tab then back in
        if (!getUIObject(History2ndLevel.MapLocationPin).elementExists())
        {
            MyLogger.log.debug("Map location pin is not visible! Map may have an issue. Going back...");
            getUIObject(Common.Back).waitUntilClickable(5000).tap();
            getUIObject(History2ndLevel.MapLocation).waitUntilClickable(5000).tap();
            getUIObject(History2ndLevel.SelfLocation).waitUntilClickable(5000).tap();
        }
        else
        {
            MyLogger.log.debug("Centering map...");
            getUIObject(History2ndLevel.SelfLocation).waitUntilClickable(5000).tap();
        }

        DriverManager.getCurrentAndroid().setContext(Android.Contexts.WebView);
        MyLogger.log.debug("Context Changed to ----> {}", DriverManager.getCurrentAndroid().getContext());
        //waitSimulation(2000);

        getUIObject(History2ndLevel.MapLocationPin).waitUntilClickable(5000).tap();
        MainController.testManager.getMainController().log("Sent Map Location..." + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();
        DriverManager.getCurrentAndroid().setContext(Android.Contexts.Native);

        waitSimulation(1000);
        return true;
    }

    private boolean executeVoiceRecord(){
        /*
         * Voice Recording (10 seconds)
         */
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Working Voice Recording..." + (ptxMessageNum));
        getUIObject(History2ndLevel.VoiceMessage).waitUntilClickable(5000).tap();
        //waitSimulation(2000);

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        // sending voice mail
        getUIObject(History2ndLevel.VoiceMessageRecord).waitUntilClickable(5000).LongPress(5);
        getUIObject(Common.OK).waitUntilClickable(5000).tap();

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Sent Voice Recording..." + (ptxMessageNum));
        MainController.testManager.getMainController().taskComplete();

        waitSimulation(1000);
        return true;
    }

    // still needs work
    private boolean executePicture(){
        /*
         * Picture PTX
         */
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MyLogger.log.debug("Taking picture");
        MainController.testManager.getMainController().log("Working Picture..." + (ptxMessageNum));
        getUIObject(History2ndLevel.Camera).waitUntilClickable(5000).tap();
        //waitSimulation(1000);

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        if (DriverManager.getCurrentAndroid().getADB().getInstalledPackageVersion().startsWith("13."))
        {
            MyLogger.log.debug("Sending picture from 13.x client.");
            getUIObject(History2ndLevel.Camera).waitUntilClickable(5000).tap();
            //getUIObject(Common.Send).waitUntilClickable(10000);
            getUIObject(Emergency.EmergencyDeclareIcon).waitUntilClickable(10000).tap();
        }
        else
        {
            MyLogger.log.debug("Sending picture from 12.x and below client.");

            getUIObject(History2ndLevel.CameraPhotoSelection).tap();
            waitSimulation(2000);
            DriverManager.getCurrentAndroid().getADB().cameraKey();
            waitSimulation(2000);

            if (DriverManager.getCurrentAndroid().getADB().getDevModel().contains("Pixel"))
            {
                DriverManager.getCurrentAndroid().getADB().cameraKey();
                waitSimulation(2000);
            } else
            {
                getUIObject(Common.OK).waitUntilClickable(5000).tap();
            }
        }

        getUIObject(Common.OK).waitUntilClickable(5000).tap();

        MyLogger.log.debug("Picture was sent");
        MainController.testManager.getMainController().log("Sent Picture..." + (ptxMessageNum));

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().taskComplete();
        waitSimulation(1000);
        return true;
    }

    private boolean executeFile(){
        /*
         * File PTX
         */
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Working File..." + (ptxMessageNum));
        getUIObject(History2ndLevel.File).waitUntilClickable(5000).tap();
        if (getUIObject(History2ndLevel.FileFirstEntry).elementExists())
        {
            getUIObject(History2ndLevel.FileFirstEntry).waitUntilClickable(5000).tap();
            //waitSimulation(1000);

            if (getPopup().isPopUpDisplayed())
            {
                recover();
                return false;
            }

            getUIObject(Common.OK).waitUntilClickable(5000).tap();
            MainController.testManager.getMainController().log("Sent File..." + (ptxMessageNum));
        }

        waitSimulation(1000);
        return true;
    }

    private void switchDriverPtxConfirm(){
        if(isMultipleDevices()){
            DriverManager.setCurrentAndroid(1);
            /*MyLogger.log.debug("kSwitching Driver to {}", DriverManager.getCurrentAndroid().getDeviceID());
            MainController.testManager.getMainController().log("Switching Driver to " + DriverManager.getCurrentAndroid().getDeviceID());
*/
            /*
             * TARGET 2
             */
            // ENTER CHECK FOR SENT PTX HERE
            /*DriverManager.getCurrentAndroid().setContext(Android.Contexts.WebView);
            MyLogger.log.debug("Android {} Context Changed to ----> {}",
                    DriverManager.getCurrentAndroid().getDeviceID(), DriverManager.getCurrentAndroid().getContext());
            MainController.testManager.getMainController().
                    log("Context Changed to -> " + DriverManager.getCurrentAndroid().getContext());*/
            String temp = getUIObject(History2ndLevel.lastPtxMessage).getText();
            MyLogger.log.debug("PTX text is: {}", temp);
            MainController.testManager.getMainController().log("PTX text is: " + temp);
            /*DriverManager.getCurrentAndroid().setContext(Android.Contexts.Native);*/
            /*MyLogger.log.debug("Android {} Context Changed to ----> {}",
                    DriverManager.getCurrentAndroid().getDeviceID(), DriverManager.getCurrentAndroid().getContext());
            MainController.testManager.getMainController().
                    log("Context Changed to -> " + DriverManager.getCurrentAndroid().getContext());*/

            /*
             * TARGET 1 Switch Back
             */
            DriverManager.setCurrentAndroid(DriverManager.getMainAndroid());
            MyLogger.log.debug("Switching Driver to {}", DriverManager.getCurrentAndroid().getDeviceID());
            MainController.testManager.getMainController().log("Switching Driver to " + DriverManager.getCurrentAndroid().getDeviceID());
        }
    }
}
