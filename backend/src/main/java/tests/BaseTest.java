package tests;

import UI.Controllers.MainController;
import api.Android;
import core.*;
import core.constants.AppInformation;
import core.constants.ElementCoordsInfo;
import core.constants.GrepConstants;
import core.managers.FileManager;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlTest;
import page_libraries.*;
import core.managers.DriverManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipOutputStream;


public abstract class BaseTest {
    protected final String kpiEnable = "999999";
    protected final String kpiDisable = "000000";
    protected HashMap<String, Object> pid = new HashMap<>();
    private String testName = null;
    private int iteration;

    /**
     * Setup executes before the test class @Test method is run.
     * @param xmlTest the test name to be executed.
     * @author Hayden Brehm
     */
    @BeforeSuite
    protected void setup(XmlTest xmlTest){
        MyLogger.log.debug("Entering setup()");

        try{
            // Threading preconditions for all devices to complete before test can start
            CountDownLatch latch = new CountDownLatch(DriverManager.getAndroidDevicesCount());

            for (Android android : DriverManager.getAllAndroidDevices()){
                MyLogger.log.debug(android.getDeviceID() + " app version: " + android.getADB().getInstalledPackageVersion());
                new Thread(()-> {
                    MyLogger.log.debug("Starting Precondition Thread For {}", android.getDeviceID());
                    preconditions(kpiEnable, android);
                    latch.countDown();
                    MyLogger.log.debug("Precondition Thread completed for {} | Waiting for {} more device(s)", android.getDeviceID(), latch.getCount());
                }).start();
            }
            latch.await();
            DriverManager.setCurrentAndroid(0);
        } catch (Exception e){
            MyLogger.log.debug("Exception Occurred! ----> {}", e.getMessage());
            e.printStackTrace();
            DriverManager.killAllDrivers();
        }
    }

    @BeforeClass
    protected void setupBeforeClass(XmlTest xmlTest){
        MyLogger.log.debug("Entering setupBeforeClass()");
        testName = xmlTest.getName();
        MyLogger.log.debug("Test Name = {}", testName);

        // Cleaning up old PTT files for all devices
        for(Android device: DriverManager.getAllAndroidDevices())
        {
            device.getADB().deletePttFiles();
            MyLogger.log.debug("Clearing logcat buffer and starting logcat capture for {}", device.getDeviceID());
            device.getADB().clearLogBuffer();
            pid.put(device.getDeviceID(), device.getADB().startLogcat(testName, GrepConstants.grepLines));
        }
        MyLogger.log.debug("Finished Starting Logcat");
    }

    @AfterClass
    protected void teardownAfterClass(XmlTest xmlTest)
    {
        MyLogger.log.debug("Entering teardownAfterClass()");
        testName = xmlTest.getName();
        MyLogger.log.debug("Test Name = {}", testName);

        for(Android device : DriverManager.getAllAndroidDevices())
        {
            device.getADB().stopLogcat(pid.get(device.getDeviceID()));
            //String folder = FileManager.getCurrentWorkingDirectory() + "/" + FileManager.logsFolder + device.getDeviceID();
            String folder = FileManager.getLogFolderForDevice(device.getDeviceID());
            //String filename = testName + "_" + device.getDeviceID();
            device.getADB().pullFile("sdcard/Documents/" + testName + ".txt", "\"" + folder + "\"");
            MyLogger.log.debug("Saving logcat file to " + folder);
        }

        MainController.testManager.resetCurrentIteration();
        MainController.testManager.getMainController().resetProgressBar();
    }

    /**
     * Teardown executes after the test class @Test method is run.
     * Stops the adb logcat and pulls to the local machine. It also kills the adb server and kills the driver.
     * @author Hayden Brehm
     */
    @AfterSuite
    protected void teardown()
    {
        MyLogger.log.debug("Entering teardown()");
        try {
            if (MainController.testManager.isSmartPhone()) {
                CountDownLatch latch = new CountDownLatch(DriverManager.getAndroidDevicesCount());

                for (Android android : DriverManager.getAllAndroidDevices()) {
                    new Thread(() -> {
                        MyLogger.log.debug("Starting teardown thread for {}", android.getDeviceID());
                        preconditions(kpiDisable, android);
                        latch.countDown();
                    }).start();
                }
                latch.await();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Enables/Disables KPI Logs as well as enabling other features from engineering menu based on Test being run.
     * @param code the String code to pass to manual dial.
     * @author Hayden Brehm
     */
    private void preconditions(String code, Android android) {
        if (MainController.testManager.isSmartPhone()) {
            // check for popups, may have some popups displayed on test end
            if (getPopup(android).isPopUpDisplayed())
                getPopup(android).handlePopup();

            // Check For Emergency Before Beginning Preconditions
            if (android.getUIObject(Emergency.EmergencyDeclaredBanner).elementExists())
            {
                CancelEmergency(true, android);
            }
            else if (android.getUIObject(Emergency.CancelEmergencyBanner).elementExists()
                && android.getUIObject(Emergency.EmergencyCancelSliderBar).elementExists())
            {
                CancelEmergency(false, android);
            }
            else if (android.getUIObject(Emergency.EmergencyDeclareSliderIcon).elementExists())
            {
                android.getDriver().navigate().back(); // simulates a back button press
            }

            if(code.equals(kpiDisable))
            {
                try{
                    // end call if still in one
                    if (android.getUIObject(CallScreen.EndCall).elementExists())
                    {
                        android.getUIObject(CallScreen.EndCall).waitUntilClickable(5000).tap();
                        waitSimulation(2000);
                    }

                    if (android.getUIObject(Common.Back).elementExists())
                    {
                        android.getUIObject(Common.Back).waitUntilClickable(5000).tap();
                        waitSimulation(2000);

                        //sometimes there is another back button depending on screen.
                        if (android.getUIObject(Common.Back).elementExists())
                        {
                            android.getUIObject(Common.Back).waitUntilClickable(5000).tap();
                            waitSimulation(2000);
                        }
                    }
                }catch (NoSuchElementException nsee){
                    nsee.printStackTrace();
                }
            }

            MyLogger.log.debug("Driver is {}", DriverManager.getCurrentAndroid().getDeviceID());
            android.getUIObject(Hamburger.Menu).waitUntilClickable(45000).tap();

            try{
                android.getUIObject(Hamburger.ManualDial).swipeFind();
            }catch (NoSuchElementException nsee){
                android.getUIObject(Hamburger.History).waitUntilClickable(5000).tap();
                android.getUIObject(Common.Back).waitUntilClickable(5000).tap();
                android.getUIObject(Hamburger.Menu).waitUntilClickable(5000).tap();
                android.getUIObject(Hamburger.ManualDial).swipeFind();
            }

            android.getUIObject(Hamburger.ManualDial).waitUntilClickable(5000).tap();
            android.getUIObject(ManualDial.NumberField).waitUntilVisible(5000).inputKeys(code);
            android.getUIObject(ManualDial.Call).waitUntilClickable(5000).tap();

            // Copy Logs using copy log code except for WOC. Will do that in engineering menu.
            if(code.equals(kpiDisable)){
                android.getUIObject(ManualDial.NumberField).waitUntilVisible(5000)
                        .inputKeys(AppInformation.getCopyLogCode());
                android.getUIObject(ManualDial.Call).waitUntilClickable(5000).tap();

            }

            waitSimulation(5000); // bandaid to wait for copy logs popup to go away.
            android.getUIObject(ManualDial.NumberField).waitUntilVisible(5000).inputKeys(AppInformation.getEngineeringCode());
            android.getUIObject(ManualDial.Call).waitUntilClickable(5000).tap();

            /*
             * Get logs and zip to logs folder
             */
            if(code.equals(kpiDisable)) {
                String model = android.getADB().getDevModel().replace(" ", "_").trim();
                String timeStamp = new SimpleDateFormat("MM-dd_HH-mm-ss").format(new java.util.Date());
                //String folder = FileManager.getCurrentWorkingDirectory() + "/" + FileManager.logsFolder +
                //        model + "_" + timeStamp;
                String folder = FileManager.getLogFolderForDevice(android.getDeviceID().replace(":", "_"));
                String zipPath = folder + model + "_PTT_" + timeStamp;
                android.getADB().pullFile("sdcard/Documents/PTT", "\"" + zipPath + "\"");

                /*
                 * Zip PTT Folder
                 */
                try (FileOutputStream fos = new FileOutputStream(zipPath + ".zip");
                     ZipOutputStream zipOut = new ZipOutputStream(fos))
                {
                    MainController.testManager.getMainController().log("Zipping PTT Folder for " + model);
                    File fileToZip = new File(zipPath);
                    FileManager.zipFile(fileToZip, fileToZip.getName(), zipOut);
                    MainController.testManager.getMainController().log("PTT Folder Zipped");
                    MainController.testManager.getMainController().log("Deleting Old PTT Folder");
                    FileManager.deleteFolder(zipPath); // delete previous folder
                    MainController.testManager.getMainController().log("Old PTT Folder Deleted");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            android.getUIObject(EngineeringMenu.EnableConsoleLogs).swipeFind();
            if(!android.getUIObject(EngineeringMenu.EnableConsoleLogs).isCheckboxChecked())
                android.getUIObject(EngineeringMenu.EnableConsoleLogs).waitUntilClickable(5000).tap();

            android.getUIObject(EngineeringMenu.WebDebug).swipeFind();
            if(!android.getUIObject(EngineeringMenu.WebDebug).isCheckboxChecked())
                android.getUIObject(EngineeringMenu.WebDebug).waitUntilClickable(5000).tap();

            android.getUIObject(Common.Back).waitUntilClickable(5000).tap();

            if(code.equals(kpiEnable)) {
                android.getUIObject(Common.Back).waitUntilClickable(5000).tap();
            }
        }
    }

    public Popups getPopup()
    {
        waitSimulation(1000);
        return DriverManager.getCurrentAndroid().getPopup();
    }

    public Popups getPopup(Android android){
        waitSimulation(1000);
        return android.getPopup();
    }

    /**
     * Boolean that gets if there are multiple devices used for the test.
     * @return boolean true if there are multiple devices, otherwise false.
     * @author Hayden Brehm
     */
    public boolean isMultipleDevices(){
        return DriverManager.getAndroidDevicesCount() > 1;
    }

    /**
     * Check whether if there is an emergency active.
     * @return True if in emergency, false otherwise.
     * @author Victor Dang
     */
    public boolean IsEmergencyActive() { return IsEmergencyActive(DriverManager.getCurrentAndroid()); }

    /**
     * Overloaded method to check if emergency is active on a particular android device.
     * @param android the android device to check.
     * @return true if in emergency, false otherwise.
     */
    public boolean IsEmergencyActive(Android android)
    {
        return android.getUIObject(Emergency.EmergencyDeclaredBanner).elementExists();
    }

    /**
     * Declares emergency on the currently selected android device.
     * @author Victor Dang
     */
    public void DeclareEmergency()
    {
        DeclareEmergency(DriverManager.getCurrentAndroid());
    }

    /**
     * Declares emergency by bringing up the emergency slider and then sliding the icon to declare. There
     * is no further action needed to declare an emergency. It is highly recommended to use this method to
     * declare an emergency as there is a discrepancy in xpath between some versions of Verizon client regarding
     * the xpaths for some emergency elements.
     * @author Victor Dang
     */
    public void DeclareEmergency(Android android)
    {
        android.getUIObject(Emergency.EmergencyDeclareIcon).waitUntilClickable(5000).LongPress(3);
        MyLogger.log.debug("Performing long press to declare emergency...");

        /** some discrepancy with earlier version of VZW client having the usual xpath text of
         *  {@link Emergency.EmergencyDeclareSliderIcon}, when the emergency slider is visible,
         *  while in later versions, it used {@link Emergency.EmergencyDeclareIcon}
         */
        String xpath = (getUIObject(Emergency.EmergencyDeclareSliderIcon).waitUntilVisible(2500).elementExists())
            ? Emergency.EmergencyDeclareSliderIcon
            : Emergency.EmergencyDeclareIcon;

        ElementCoordsInfo declareSliderIconCoords =
                new ElementCoordsInfo(getUIObject(xpath).getElement());
        ElementCoordsInfo declareSliderCoords =
                new ElementCoordsInfo(getUIObject(Emergency.EmergencyDeclareSliderBar).getElement());

        UiObject.drag(declareSliderIconCoords.HalfWidth(), declareSliderIconCoords.HalfHeight(),
                declareSliderCoords.endX, declareSliderCoords.endY, 250, DriverManager.getCurrentAndroid().getDriver());
        MyLogger.log.debug("Declaring emergency...");
        android.getUIObject(Emergency.EmergencyDeclaredBanner).waitUntilVisible(10000);
    }

    /**
     * Overloaded method to long press then cancel emergency on the currently selected device.
     * @author Victor Dang
     */
    public void CancelEmergency()
    {
        CancelEmergency(true, DriverManager.getCurrentAndroid());
    }

    /**
     * Cancel emergency by bringing up the emergency slider and then sliding the icon to cancel. This will
     * also select a cancel reason so no further action is needed to fully cancel an emergency. It is highly
     * recommended to use this method to cancel an emergency as there is a discrepancy in xpath between some
     * versions of Verizon client regarding the xpaths for some emergency elements.
     * @author Victor Dang
     */
    public void CancelEmergency(boolean longPressCancel, Android android)
    {
        Random rand = new Random();

        if (longPressCancel)
        {
            android.getUIObject(Emergency.EmergencyCancelIcon).waitUntilClickable(5000).LongPress(3);
            MyLogger.log.debug("Performing long press to cancel emergency...");
        }

        /** some discrepancy with earlier version of VZW client having the usual xpath text of
         *  {@link Emergency.EmergencyCancelSliderIcon} when the emergency slider is visible,
         *  while in later versions, it used {@link Emergency.EmergencyCancelIcon}
         */
        String xpath = (getUIObject(Emergency.EmergencyCancelSliderIcon).elementExists())
                ? Emergency.EmergencyCancelSliderIcon
                : Emergency.EmergencyCancelIcon;

        ElementCoordsInfo cancelSliderIconCoords =
                new ElementCoordsInfo(getUIObject(xpath).getElement());
        ElementCoordsInfo cancelSliderCoords =
                new ElementCoordsInfo(getUIObject(Emergency.EmergencyCancelSliderBar).getElement());

        UiObject.drag(cancelSliderIconCoords.HalfWidth(), cancelSliderIconCoords.HalfHeight(),
                cancelSliderCoords.endX, cancelSliderCoords.endY, 250, android.getDriver());
        MyLogger.log.debug("Cancel emergency...");

        // cancel emergency, 50% chance to either be real or false emergency, just to spice things up :)
        MyLogger.log.debug("Selecting a random cancel reason...");
        android.getUIObject(Emergency.EmergencyCancelSend).waitUntilVisible(10000);
        if (rand.nextFloat() < 0.5f)
            android.getUIObject(Emergency.EmergencyCancelRealEmergency).waitUntilClickable(5000).tap();
        else
            android.getUIObject(Emergency.EmergencyCancelFalseAlarm).waitUntilClickable(5000).tap();

        android.getUIObject(Emergency.EmergencyCancelSend).tap();
        android.getUIObject(Emergency.EmergencyDeclaredBanner).waitUntilHidden(10000);

        //waitSimulation(5000);
    }

    protected int getIterations(){
        return (MainController.testManager.getMainController().getIsCustomTest())
                ? getDataProviderIterationVal() : MainController.testManager.getIterations();
    }

    protected void setDataProviderIterationVal(int iteration){
        this.iteration = iteration;
    }

    protected int getDataProviderIterationVal(){return iteration;}

    protected void waitSimulation(int ms) {
        try{
            Thread.sleep(ms);
        }catch (InterruptedException ex){
            MyLogger.log.debug("InterruptedException Occurred! ----> {}", ex.getMessage());
            MainController.Worker worker = MainController.testManager.getMainController().getWorker();
            ex.printStackTrace();
            if(worker.isCancelled()) {   //if we get here then sleep consumed the cancel button. Interrupt the thread to stop it.
                MyLogger.log.debug("Thread name is {}", Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
        }catch (IllegalArgumentException ae){
            MyLogger.log.debug("IllegalArgumentException Occurred! ----> {}", ae.getMessage());
            ae.printStackTrace();
        }
    }

    protected UiObject getUIObject(String xpath){
        return DriverManager.getCurrentAndroid().getUIObject(xpath);
    }

    public abstract void resetToDefaultLocation();

    public abstract void recover();

    public abstract void testLoop();
}
