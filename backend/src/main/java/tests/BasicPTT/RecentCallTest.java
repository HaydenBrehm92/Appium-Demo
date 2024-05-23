package tests.BasicPTT;

import UI.Controllers.MainController;
import core.MyLogger;
import core.managers.DriverManager;
import org.openqa.selenium.By;
import core.managers.DriverManager;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.*;
import tests.BaseTest;
import tests.Data.DataProviders;

// TODO: In general. Does not have to be in RecentCallTest.java
//  #1 Need to make sure the devices revert back to the default screen.
//  #2 Application is going to background when switching sometimes and at the end of test. This is preventing teardown.
//  #3 Look into both ResetToDefaultLocation() and recover(). Are either affected by multiple drivers? Double check logic anyway.
//  #4 (Future) Need to associate the Device's assigned target from UI to the deviceID. Ex: (Target 1 = Pixel 7 id & Target 2 = TC27).
//  #5 getDeviceStop throws nullptr exception if rerunning a test. Have bandaid fix for now.
//  #6 If test crashes/ends unexpectedly then the last driver that is set will begin the next test if the devices are still connected.
//  #7 Exception: Session ID is null. This happens after a test is completed and you try to rerun it.
//  #8 Fix Meminfo
//  #9 Fix Asserts
public class RecentCallTest extends BaseTest {
    private boolean callConnected;

    @Test(dataProvider = "RecentCallIterations", dataProviderClass = DataProviders.class)
    public void CallTest(int iteration)
    {
        setDataProviderIterationVal(iteration);
        MyLogger.log.debug("Entering CallTest() with {} iterations", iteration);
        /*
         * Navigating to Recent History Entries
         */
        if (MainController.testManager.isSmartPhone())
        {
            if (MainController.testManager.isHandset()) {
                getUIObject(TabBar.History).waitUntilClickable(5000).tap();
                getUIObject(History.StandardMostRecent).waitUntilClickable(5000).tap();
            }

            /*
             * Setting up progressbar
             */
            MainController.testManager.getMainController().progressBarSetUp();
            MainController.testManager.getMainController()
                    .setProgressPerTask((MainController.testManager.getIterations() * iteration), 1);

            /*
             * Main Test Loop
             */
            try
            {
                testLoop();

                if(MainController.testManager.isHandset()){
                    getUIObject(Common.Back).waitUntilClickable(5000).tap();    // To return to default screen. Radio will have hamburger menu other tests can use
                }

                Assert.assertEquals(MainController.testManager.getCurrentIteration(),
                        MainController.testManager.getIterations(), "Not all calls were successful");
            }
            catch (NoSuchElementException nsee){
                MyLogger.log.debug("NoSuchElementException Occurred! ----> {}", nsee.getMessage());
                nsee.printStackTrace();
                recover();
            }
        }
    }

    @Override
    public void testLoop() {
        for (int i = MainController.testManager.getCurrentIteration(); i < getIterations(); i++)
        {
            MyLogger.log.debug("Testloop iteration {} begin", (i + 1));

            //waitSimulation(1000);

            // begin test execution
            testExecution();


            /*
             * TESTING PURPOSE
             */
            /*if(DriverManager.getAndroidDevicesCount() > 1){
                DriverManager.setCurrentAndroid(1);
                if (MainController.testManager.isHandset()) {
                    if (getPopup().isPopUpDisplayed())
                    {
                        recover();
                        return;
                    }
                    getUIObject(TabBar.History).waitUntilClickable(5000).tap();
                    //waitSimulation(3000);
                    getUIObject(History.StandardMostRecent).waitUntilClickable(5000).tap();
                    //waitSimulation(3000);
                }
                testExecution();
            }*/

            MainController.testManager.getMainController().taskComplete();
            MainController.testManager.getMainController().log("Completed Call " + (i + 1));
            if(callConnected)
            {
                MainController.testManager.updateCurrentIteration();
                callConnected = false;
            }
        }

        MainController.testManager.resetCurrentIteration();  // reset for CustomTest. Does nothing meaningful else wise
    }

    @Override
    public void resetToDefaultLocation()
    {
        /*if (getPopup().isRecoverFromNetworkDown() & !MainController.testManager.isLegacyExecution())
        {
            try
            {
                MainController.testManager.getMainController().connectToDeviceAndInit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/

        MyLogger.log.debug("Executing resetToDefaultLocation.");

        try
        {
            if (getUIObject(Common.Back).elementExists())
                getUIObject(Common.Back).tap();
        }
        catch (NoSuchElementException e)
        {
            MyLogger.log.debug("Back button not found. Continuing...");
        }

        try
        {
            if (getUIObject(Hamburger.Menu).elementExists())
                MyLogger.log.debug("Menu found. Acceptable location.");
        }
        catch (NoSuchElementException e)
        {
            MyLogger.log.debug("Menu not found. Continuing...");
        }

        testLoop();
    }

    //TODO: Need to recover from different exceptions/ popups/ network up-down
    @Override
    public void recover() {
        MyLogger.log.debug("Trying to Recover!");

        /*
         * Check for Popups
         */
        if(getPopup().isPopUpDisplayed()){
            MainController.testManager.getMainController().
                    log("Popup " + getPopup().getPopupType().toString() + " occurred.");
            getPopup().handlePopup();

            MainController.Worker worker = MainController.testManager.getMainController().getWorker();
            //Have to check or thread, even after executorservice shutdownnow, will continue to run
            if(worker.isCancelled()) {   //if we get here then sleep consumed the cancel button. Interrupt the thread to stop it.
                MyLogger.log.debug("Worker is cancelled {}", Thread.currentThread().getName());
                return;
            }
            resetToDefaultLocation();
            //Need to find a way to get previous entry because an IPA would overwrite the most recent entry.
            //Need to add a field to Testmanager to see if the terminator name is enough to solve this
        }

        /*
         * Check for the device being on emergency state, blocking actions like contact/group selection.
         */
        if(IsEmergencyActive()) {
            //A device on self-declared emergency state does not allow the movement to other mdns to make new calls
            //Failure will happen when script is changing to a new target and emergency state is activated.
            CancelEmergency(true, DriverManager.getCurrentAndroid());
        }

        /*
         * driver.closeApp();driver.launchApp(); Maybe use these to recover
         */


        //Mid Call Disconnect, blocking float wrap background:
        //if looking for the background:
        //Resource-ID: global-floatWrap
        //
        //If looking for the text.
        //Class: android.widget.TextView
        //Text: Reconnecting, please wait…

        /*
         * For no wifi/cell connection -> text = No Connection
         */

        /*
         * Add voicemail recovery
         */
    }

    private void testExecution()
    {
        if (MainController.testManager.isRadio())
        {
            getUIObject(Hamburger.Menu).waitUntilClickable(5000).tap();
            getUIObject(Hamburger.History).waitUntilClickable(5000).tap();
            getUIObject(History.RadioMostRecent).waitUntilClickable(5000).tap();

            //Check for interference
            /*if (getPopup().isPopUpDisplayed())
            {
                recover();
                return;
            }

            waitSimulation(1500);*/
        }

        //Check for interference
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return;
        }

        getUIObject(CallScreen.PTT).waitUntilClickable(5000).LongPress(10);

        /*
         * Switching Driver
         */
        if(isMultipleDevices()){
            DriverManager.setCurrentAndroid(1);
            MyLogger.log.debug("kSwitching Driver to {}", DriverManager.getCurrentAndroid().getDeviceID());
            MainController.testManager.getMainController().log("Switching Driver to " + DriverManager.getCurrentAndroid().getDeviceID());

            /*
             * TARGET 2
             */
            String callText = getUIObject(CallScreen.CallwithTitle).getText();
            callConnected = !callText.isEmpty() && !callText.equals("READY");   // Checking that the call connected
            MyLogger.log.debug("Call is connected -> {}", callConnected);
            MainController.testManager.getMainController().log("Call is connected -> " + callConnected);

            /*
             * TARGET 1 Switch Back
             */
            DriverManager.setCurrentAndroid(DriverManager.getMainAndroid());
            MyLogger.log.debug("Switching Driver to {}", DriverManager.getCurrentAndroid().getDeviceID());
            MainController.testManager.getMainController().log("Switching Driver to " + DriverManager.getCurrentAndroid().getDeviceID());
        }

        //Check for interference
        if (getPopup().isPopUpDisplayed()) {
            recover();
            return;
        }

        getUIObject(CallScreen.EndCall).waitUntilClickable(5000).tap();
        waitSimulation(1000);
    }
}
