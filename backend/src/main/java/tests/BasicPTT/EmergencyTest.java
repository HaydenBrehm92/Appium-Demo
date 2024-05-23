package tests.BasicPTT;

import UI.Controllers.MainController;
import core.MyLogger;
import core.managers.DriverManager;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.*;
import tests.BaseTest;
import tests.Data.DataProviders;


public class EmergencyTest extends BaseTest
{
    @Test(dataProvider = "EmergencyIterations", dataProviderClass = DataProviders.class)
    public void Emergency(int iteration)
    {
        setDataProviderIterationVal(iteration);
        if (MainController.testManager.isSmartPhone())
        {
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
                Assert.assertEquals(MainController.testManager.getCurrentIteration(),
                    MainController.testManager.getIterations(),
                    "Not all calls were successful");
            }
            catch (NoSuchElementException nsee){
                MyLogger.log.debug("NoSuchElementException Occurred! ----> {}", nsee.getMessage());
                nsee.printStackTrace();
                DriverManager.killAllDrivers();
            }
        }
    }

    @Override
    public void resetToDefaultLocation()
    {
        // not in use
    }

    @Override
    public void recover()
    {
        MyLogger.log.debug("Trying to Recover!");

        // Check for Popups
        if(getPopup().isPopUpDisplayed())
        {
            MainController.testManager.getMainController().
                    log("Popup " + getPopup().getPopupType().toString() + " occurred.");
            getPopup().handlePopup();
            MainController.Worker worker = MainController.testManager.getMainController().getWorker();

            //Have to check or thread, even after executorservice shutdownnow, will continue to run
            if(worker.isCancelled()) //if we get here then sleep consumed the cancel button. Interrupt the thread to stop it.
            {
                MyLogger.log.debug("Worker is cancelled. Interrupting thread {}", Thread.currentThread().getName());
                return;
            }
        }
    }

    /**
     * Send help :(
     * No.
     * PLEASE
     */
    @Override
    public void testLoop()
    {
        for (int i = MainController.testManager.getCurrentIteration();
             i < getIterations(); i++)
        {
            MyLogger.log.debug("Current test iteration = " + i);

            // small delay between tests as the program could go faster than the test
            // could keep up with
            waitSimulation(1000);

            // execute PTT/PTX
            boolean success = testExecution();

            MainController.testManager.getMainController().taskComplete();
            MainController.testManager.getMainController().log("Completed emergency test " + (i + 1));

            // only update the iteration if both declare and cancel were successful.
            // as of now, calls and PTX won't fail this test.
            if (success)
            {
                MainController.testManager.updateCurrentIteration();
                MyLogger.log.debug("Updating test iteration");
            }
            else
            {
                MyLogger.log.debug("Iteration failed!");
            }
        }

        MainController.testManager.resetCurrentIteration();  // reset for CustomTest. Does nothing meaningful else wise
    }

    /**
     * Will only work for devices connected to the PC through a USB cable.
     * @author Victor Dang
     */
    private boolean testExecution()
    {
        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }


        MainController.testManager.getMainController().log("Starting emergency test");

        boolean successfulDeclare;
        boolean successfulCancel;

        // if emergency state is already active, then the test case will fail
        // as we are unsure if emergency declaration happened or not
        if (IsEmergencyActive())
        {
            successfulDeclare = false;
            MyLogger.log.debug("Emergency State is already active! Test will continue, but test iteration will fail...");
        }
        else // not in emergency state, attempt to declare
        {
            DeclareEmergency();

            successfulDeclare = IsEmergencyActive();
            MyLogger.log.debug("Was emergency declaration successful? " + successfulDeclare);

            if (successfulDeclare)
            {
                // checking on whether receiver was able to get emergency alert.
                if (isMultipleDevices() &&
                    DriverManager.getTargetDevice().getUIObject(Alerts.EmergencyAlert).waitUntilVisible(5000).elementExists())
                {
                    MainController.testManager.getMainController().log(DriverManager.getTargetDevice().getDeviceID() + " received emergency");
                    MyLogger.log.debug("Terminator received emergency declared popup");

                    // clear emergency dialog on target device
                    DriverManager.getTargetDevice().getUIObject(Common.Dismiss).waitUntilClickable(5000).tap();
                }
            }
            else
            {
                // couldn't declare emergency, skipping this iteration, which will fail the overall test
                MyLogger.log.debug("Could not declare emergency! Failing test iteration...");
                return false;
            }
        }


        // making call

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        if (!getUIObject(CallScreen.Speaking).elementExists())
        {
            MainController.testManager.getMainController().log("Making emergency PTT call");
            MyLogger.log.debug("Making emergency PTT call");
            getUIObject(CallScreen.PTT).LongPress(10);

            if (getPopup().isPopUpDisplayed())
            {
                recover();
                return false;
            }
        }
        else // assumed to be hot mic if app is currently taking floor after declaring emergency.
        {
            MyLogger.log.debug("Hot mic is taking floor! Waiting until floor is released or for up to 10 seconds...");
            getUIObject(CallScreen.Speaking).waitUntilHidden(10000);
        }


        // sending PTX

        getUIObject(CallScreen.SendPTX).waitUntilClickable(5000).tap();

        MainController.testManager.getMainController().log("Inputting PTX message");
        MyLogger.log.debug("Inputting PTX message");
        getUIObject(History2ndLevel.MessageField).waitUntilVisible(5000).inputKeys("Emergency PTX " + (MainController.testManager.getCurrentIteration() + 1));

        if (getPopup().isPopUpDisplayed())
        {
            recover();
            return false;
        }

        MainController.testManager.getMainController().log("Sending emergency PTX Message");
        MyLogger.log.debug("Sending emergency PTX Message");
        getUIObject(History2ndLevel.SendMessage).waitUntilClickable(5000).tap();

        MyLogger.log.debug("Returning to call screen");
        getUIObject(Common.Back).waitUntilClickable(5000).tap();


        CancelEmergency(true, DriverManager.getCurrentAndroid());

        // end call if it is still going on
        if (getUIObject(CallScreen.EndCall).elementExists())
            getUIObject(CallScreen.EndCall).waitUntilClickable(5000).tap();

        // go back to home screen if we are in standard mode
        if (MainController.testManager.isHandset() && getUIObject(Common.Back).elementExists())
            getUIObject(Common.Back).waitUntilClickable(5000).tap();

        successfulCancel = !IsEmergencyActive();
        MyLogger.log.debug("Was emergency cancel successful? " + successfulCancel);

        // check in target device to clear the emergency cancel popup
        if (isMultipleDevices() &&
            DriverManager.getTargetDevice().getUIObject(Alerts.EmergencyAlert).waitUntilVisible(5000).elementExists())
        {
            MyLogger.log.debug("Terminator received emergency cancel popup");
            DriverManager.getTargetDevice().getUIObject(Common.Dismiss).waitUntilClickable(5000).tap();
        }

        return successfulDeclare && successfulCancel; // test was successful
    }
}
