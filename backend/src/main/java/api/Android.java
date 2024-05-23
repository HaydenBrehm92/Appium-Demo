package api;

import core.ADB;
import core.MyLogger;
import core.constants.AppInformation;
import core.constants.PackageInfo;
import core.managers.FileManager;
import core.managers.HostManager;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import tests.Popups;
import java.net.URL;
import java.time.Duration;
import core.UiObject;


public class Android
{
    // just used to synchronize creating a screen mirroring process
    private final static Object driverLock = new Object();

    private final AndroidDriver driver;
    private final ADB adb;
    private final String deviceID;
    private final Popups popup;
    private final Process mirroringProcess;

    public enum Contexts
    {
        Native,
        WebView
    }


    public Android(String deviceID, URL serverURL)
    {
        // these two need to be created first
        this.deviceID = deviceID;
        this.adb = new ADB(deviceID);

        synchronized (driverLock)
        {
            // create and maintain process for screen capturing
            Process p = null;
            String scrcpyPath = FileManager.getDependenciesFolder() + FileManager.scrcpyEXE;

            if (FileManager.checkIfExists(scrcpyPath))
            {
                try
                {
                    p = HostManager.executeConsoleCommand(scrcpyPath + " -s " + deviceID);
                    Thread.sleep(1000);
                    MyLogger.log.debug("Created mirroring process for {}", deviceID);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            this.mirroringProcess = p;
        }

        // call these methods after creating an ADB instance
        uninstallAppiumApps();
        forceStopOtherClients();

        // Popups class will require the driver, so create the driver first
        this.driver = new AndroidDriver(serverURL, getCapabilities());

        // 1 second wait to check for unexpected popups, in all other cases when expecting an element to appear, use
        // explicit waits (see UiObject class: waitUntilVisible(), waitUntilHidden(), waitUntilClickable() methods)
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        // popups needs the driver so create this after creating the drivers.
        this.popup = new Popups(driver);

        MyLogger.log.debug("Device: {} driver created.", deviceID);
    }

    public AndroidDriver getDriver(){
        return driver;
    }

    public ADB getADB(){
        return adb;
    }

    public String getDeviceID(){
        return deviceID;
    }

    public Popups getPopup(){
        return popup;
    }

    public UiObject getUIObject(String xpath){
        return new UiObject(xpath, driver);
    }


    /**
     * Returns the desired capabilities of the new driver which are tied to the device connected.
     * The capabilities set include: platformName, automationName, appPackage, appActivity, noReset, and newCommandTimeout.
     * Carrier specific application is also set to later be used.
     * @return the desired capabilities for the driver
     * @author Hayden Brehm
     */
    public DesiredCapabilities getCapabilities()
    {
        // phone
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("appium:platformName", "Android");
        cap.setCapability("appium:automationName", "UiAutomator2");
        cap.setCapability("appium:deviceName", adb.getDevModel());
        cap.setCapability("appium:platformVersion", adb.getAndroidVersionAsStr());
        cap.setCapability("appium:udid", deviceID);

        // application
        String unlockPkg = AppInformation.getUnlockPackage();
        MyLogger.log.debug("Unlock Package is: {}", unlockPkg);
        cap.setCapability("appium:appPackage", unlockPkg);
        cap.setCapability("appium:appActivity", unlockPkg + ".StartupActivity");

        // other arguments
        cap.setCapability("appium:noReset", true);
        cap.setCapability("appium:newCommandTimeout", 0);
        cap.setCapability("appium:fullReset", false);
        //cap.setCapability("uiautomator2ServerLaunchTimeout", 50000);

        return cap;
    }

    /**
     * Uninstalls all auto-installed Appium apps for the specified device.
     * @author Victor Dang
     */
    public void uninstallAppiumApps()
    {
        adb.uninstallApp("io.appium.settings");
        adb.uninstallApp("io.appium.uiautomator2.server");
        adb.uninstallApp("io.appium.uiautomator2.server.test");
        MyLogger.log.debug("Device " + deviceID + ": uninstalling apps");
    }

    /**
     * Force stop all other clients for the specified device.
     * @author Victor Dang
     */
    public void forceStopOtherClients()
    {
        for (PackageInfo info : AppInformation.getSupportedPackages())
        {
            if (!info.packageName.equals(AppInformation.getUnlockPackage()))
            {
                adb.forceStopApp(info.packageName, null);
                MyLogger.log.debug("Device " + deviceID + ": force stopping " + info.packageName);
            }
        }
    }

    /**
     * Gets the current context the driver is in.
     * @return context of how the driver is interacting with the apk on the device. Between "NATIVE" and
     * "[insert unlockPackage here]_WEBVIEW"
     * @author Hayden Brehm
     */
    public Contexts getContext()
    {
        return (driver.getContext().equals("NATIVE_APP"))
            ? Contexts.Native
            : Contexts.WebView;
    }

    /**
     * Sets the context based on the enum value provided. Done this way to reduce potential issues.
     * @param context The context to switch to.
     * @author Hayden Brehm
     */
    public void setContext(Contexts context)
    {
        switch (context)
        {
            case Native:
                driver.context("NATIVE_APP");
                break;
            case WebView:
                driver.context("WEBVIEW_" + AppInformation.getUnlockPackage());
                break;
        }
    }

    /**
     * Kills the driver and properly shutdowns this device's driver. This should not be called outside of the
     * DriverManager class.
     * @author Hayden Brehm, Victor Dang
     */
    public void killDriver()
    {
        if (mirroringProcess != null)
        {
            MyLogger.log.debug("Killing mirroring process for {}", deviceID);
            mirroringProcess.destroy();
        }

        if (driver != null)
        {
            MyLogger.log.debug("Killing driver for {}", deviceID);
            driver.quit();
        }
    }

    /**
     * Overridden the built-in equals() method to compare between the specified Android device's deviceID.
     * @param other The other Android device to compare against.
     * @return True if the deviceID of both Android objects are equal, false otherwise.
     * @author Victor Dang
     */
    @Override
    public boolean equals(Object other)
    {
        return (other instanceof Android) && this.deviceID.equals(((Android) other).getDeviceID());
    }

    /*public interface EventListener {

        String onTrigger();

        void respondToTrigger();
    }

    public class AsynchronousEventListenerImpl implements EventListener {

        @Override
        public String onTrigger(){
            respondToTrigger();
            return "Asynchronously running callback function";
        }
        @Override
        public void respondToTrigger(){
           makeDriver(new AndroidDriver(getAppiumServer().getAppiumURL(), DriverManager.getCapabilities(deviceID)));
        }
    }*/
}
