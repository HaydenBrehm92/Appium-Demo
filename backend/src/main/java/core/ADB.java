package core;

import core.constants.AppInformation;
import core.constants.GrepConstants;
import core.managers.HostManager;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * ADB controls all adb commands used within the program. It also executes these commands
 * in {@link ADB#adbCommand(String)}.
 * @author Hayden Brehm
 */
public class ADB {
    private String devID;
    private static final String screenshotPath = "/sdcard/Pictures/adbScreenshots/";


    /**
     * The constructor for this class.
     * @param devID the serial number of the device found in "adb devices".
     */
    public ADB(String devID)
    {
        MyLogger.log.debug("Setting devID to " + devID);
        this.devID = devID;
    }

    /**
     * Gets the device ID held by this ADB object.
     * @return the device ID (serial number found in "adb devices").
     */
    public String getDevID(){
        return devID;
    }

    /**
     * Executes the adb command provided.
     * @param command the adb command to be executed.
     * @throws RuntimeException if command provided does not start with "adb."
     * @return the output of the executed adb command.
     * @author Hayden Brehm, Victor Dang
     */
    public static String adbCommand(String command)
    {
        MyLogger.log.debug("adb command: {}", command);

        if (command.startsWith("adb"))
        {
            //command = command.replace("adb", ServerManager.getAndroidHome() + "/platform-tools/adb");
            command = HostManager.getAndroidHome() + "/platform-tools/" + command;
            String output = HostManager.executeConsoleCommandWithOutput(command);
            MyLogger.log.debug("Output of adbcommand: {}", output);
            return (output == null) ? "" : output;
        }
        else
        {
            throw new RuntimeException("Only designed to run adb commands.");
        }
    }

    /**
     * Static method that starts the adb server on the local machine.
     * @author Hayden Brehm
     */
    public static void startServer(){
        adbCommand("adb start-server");
    }

    /**
     * Static method that kills the adb server on the local machine.
     * @author Hayden Brehm
     */
    public static void killServer(){
        adbCommand("adb kill-server");
    }

    /**
     * Gets the devices connected by usb to host machine using adb command "adb devices."
     * @return ArrayList of Strings formatted with only the device address being returned.
     * @author Hayden Brehm
     */
    //adb devices in adb shell returns a line in format "XXXXXXXXX device"
    //we find the line that ends with the word device and clean it up to only take the "XXXXXXXX" part
    //we use a for loop to split up our lines using .split() method to separate by newline
    public static ArrayList<String> getConnectedDevices(){
        ArrayList<String> devices = new ArrayList<>();
        String deviceIDs = adbCommand("adb devices");

        for (String str : deviceIDs.split("\n")){
            str = str.trim(); //clean str whitespace
            if(str.endsWith("device"))
                devices.add(str.replace("device","").trim());   //cleans line up
        }
        return devices;
    }

    /**
     * Gets the amount of devices currently connected to the PC.
     * @return The number of devices connected.
     */
    public static int getConnectedDevicesCount()
    {
        return getConnectedDevices().size();
    }

    /**
     * Gets the current foreground activity of the connected device.
     * @return String adb command that gets the current activity in the foreground.
     * @author Hayden Brehm
     */
    //returns activity in foreground
    //redirects command to device shell instead of local machine
    public String getForegroundActivity(){
        return adbCommand("adb -s " + devID + " shell dumpsys window windows | grep mCurrentFocus");
    }

    /**
     * Gets the app version that is currently installed on the device.
     * @return The app version of the currently installed app.
     * @author Victor Dang
     */
    public String getInstalledPackageVersion()
    {
        String output = adbCommand("adb -s " + devID + " shell dumpsys package " + AppInformation.getUnlockPackage() + " | grep versionName");
        return output.split("=")[1].trim();
    }

    /**
     * Gets the android version of the connected device as a String.
     * @return String containing the android version of the connected device.
     * @author Hayden Brehm
     */
    public String getAndroidVersionAsStr(){
        String val = adbCommand("adb -s " + devID + " shell getprop ro.build.version.release");
        //only for adding an extra 0 on version ending.
        //just for formatting
        if(val.length() == 3){
            val += ".0";
        }
        return val;
    }

    /**
     * Gets the android version of the connected device as an Integer
     * @return Integer containing the android version of the connected device.
     * @author Hayden Brehm
     */
    public int getAndroidVersion(){
        return Integer.parseInt(getAndroidVersionAsStr().replaceAll("\r\n",""));
    }

    /**
     * Gets all the installed packages of the connected device.
     * @return An ArrayList of Strings containing all the currently installed packages of the connected device.
     * @author Hayden Brehm
     */
    public ArrayList<String> getInstalledPackages(){
        ArrayList<String> pkg = new ArrayList<>();
        String[] out = adbCommand("adb -s "+devID+" shell pm list packages").split("\n");
        for (String packageID : out){
            pkg.add(packageID.replace("package:","").trim());
        }
        return pkg;
    }

    /**
     * Opens a specific application. Used in {@link tests.BaseTest#setup(XmlTest)} to open the PTT application
     * for running tests.
     * @param pkgID the package to be opened.
     * @author Hayden Brehm
     */
    public void openApp(String pkgID)
    {
        //adbCommand("adb -s " + devID + " shell monkey -p " + pkgID + " 1");
        //adbCommand("adb -s " + devID + " shell monkey -p " + pkgID + "-c android.intent.category.LAUNCHER 1");
        adbCommand("adb -s " + devID + " shell am start -n " + pkgID + "/.StartupActivity");
    }

    /**
     * Opens a specific app activity.
     * @param pkgID the package to be opened.
     * @param actID the activity to be opened.
     * @author Hayden Brehm
     */
    //this adb command opens specific applications
    public void openAppActivity(String pkgID, String actID){
        adbCommand("adb -s " + devID +
                " shell am start -n api.android.intent.category.LAUNCHER -a api.android.intent.action.MAIN -n "+ pkgID +
                "/" + actID);
    }

    /**
     * Clears an application's storage data.
     * @param pkgID the package to be cleared.
     * @author Hayden Brehm
     */
    public void clearAppData(String pkgID){
        adbCommand("adb -s " + devID +
                " shell pm clear " + pkgID);
    }

    /**
     * Enables native bluetooth settings on the phone. This will turn off bluetooth first and then back on.
     * @deprecated No longer using bluetooth
     * @author Victor Dang
     */
    @Deprecated
    public void enableBluetooth()
    {
        try
        {
            MyLogger.log.debug("Attempting to disable Bluetooth...");
            setBluetoothState(false);

            MyLogger.log.debug("Attempting to enable Bluetooth...");
            setBluetoothState(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the state of native bluetooth setting for the connected device. Due to how Android is implemented, Android
     * 11 and below devices use airplane mode on/off as the way to reset the bluetooth connection. Android 12 and above
     * directly calls an ADB command to do the same. Note that this function also has a synchronous thread sleep to
     * ensure that bluetooth has enough time to turn off and on again before continuing on with the tool execution.
     * @param turnOn True to turn on bluetooth, false to turn off.
     * @deprecated No longer using bluetooth
     * @author Victor Dang
     */
    @Deprecated
    public void setBluetoothState(boolean turnOn)
    {
        try
        {
            // sleep needed to ensure bluetooth setting is set properly
            if (getAndroidVersion() <= 11)
            {
                adbCommand("adb -s " + devID + " shell cmd connectivity airplane-mode " + (turnOn ? "disable" : "enable"));
                Thread.sleep(5000);
            }
            else // os >= 12
            {
                adbCommand("adb -s " + devID + " shell svc bluetooth " + (turnOn ? "enable" : "disable"));
                Thread.sleep(2000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Force stops an application.
     * @param pkgID the package to be stopped.
     * @param actID the activity to be stopped.
     * @author Hayden Brehm
     */
    public void forceStopApp(String pkgID, String actID){
        adbCommand("adb -s " + devID + " shell am force-stop "+ pkgID);
    }

    /**
     * Returns whether wi-fi is connected and there if there is internet.
     * @deprecated Not in use and unreliable
     * @return true if connected and internet is working, false otherwise.
     */
    @Deprecated
    public boolean wifiStatus(){
        //result = adbCommand("adb -s " + devID + " shell dumpsys wifi | grep \"Wi-Fi is\""); //Does not work
        String result = adbCommand("adb -s " + devID + " shell dumpsys wifi | grep curState=");
        String[] split = result.split("\n");
        MyLogger.log.debug("Result Split, Line 1: " + split[0]);

        return split[0].toLowerCase().contains("enabled");
    }

    /**
     * Installs an apk on the connected device.
     * @param apk the .apk to be side-loaded onto the connected device.
     * @author Hayden Brehm
     */
    public void installApp(String apk)
    {
        adbCommand("adb -s " + devID + " install " + apk);
    }

    /**
     * Uninstalls an apk on the connected device.
     * @param pkgID the .apk to be uninstalled from the connected device.
     * @author Hayden Brehm
     */
    public void uninstallApp(String pkgID)
    {
        adbCommand("adb -s " + devID + " uninstall " + pkgID);
    }

    /**
     * Clears logcat buffer for the connected device.
     * @author Hayden Brehm
     */
    public void clearLogBuffer(){
        adbCommand("adb -s " + devID + " shell logcat -c");
    }

    /**
     * Takes a screenshot and saves to a new directory if one is not found already.
     * @author Hayden Brehm
     */
    public void takeScreenshot(){
        if (!screenshotDirExists()) adbCommand("adb mkdir " + screenshotPath);
        adbCommand("adb -s " + devID + " shell screencap -p " + screenshotPath + "screenshot.png");
    }

    /**
     * Checks if the screenshot directory exists or not.
     * @return true if the screenshot directory exists. False otherwise.
     * @author Hayden Brehm
     */
    private boolean screenshotDirExists(){
        String[] out = adbCommand("adb -s " + devID + " shell ls " + screenshotPath).split("\n");
        for (String val : out)
        {
            if (val.trim().equals("adbScreenshots"))
                return true;
        }
        return false;
    }

    /**
     * Pushes a file to a new target path on the connected device.
     * @param source path of the file to be pushed.
     * @param target path of where the source file will go.
     * @author Hayden Brehm
     */
    public void pushFile(String source, String target)
    {
        adbCommand("adb -s " + devID + " push " + source + " " + target);
    }

    /**
     * Pulls a file from the connected device to the host machine.
     * @param source path of the file to be pulled.
     * @param target path of where the source file will go.
     * @author Hayden Brehm
     */
    public void pullFile(String source, String target)
    {
        adbCommand("adb -s " + devID + " pull " + source + " " + target);
    }

    /**
     * Keyevent for the "Home Key"
     * @author Hayden Brehm
     */
    public void homeKey()
    {
        adbCommand("adb -s " + devID + " shell input keyevent 3");
    }

    /**
     * Key Event for dismissing the "Keyboard"
     * @author Hayden Brehm
     */
    public void dismissKeyboardBtn()
    {
        adbCommand("adb -s " + devID + " shell keyevent 111");
    }

    /**
     * Key Event for the "Left Soft Key"
     * @author Hayden Brehm
     */
    public void leftSoftKey(){ MyLogger.log.debug("LEFT_SOFT_KEY"); adbCommand("adb -s " + devID + " shell input keyevent 1"); }

    /**
     * Key Event for the "Right Soft Key"
     * @author Hayden Brehm
     */
    public void rightSoftKey(){ MyLogger.log.debug("RIGHT_SOFT_KEY"); adbCommand("adb -s " + devID + " shell input keyevent 2"); }

    /**
     * Command that inputs keys.
     * @param keys the keys to be inputted into some field.
     * @author Hayden Brehm
     */
    public void inputKeys(String keys){ adbCommand("adb -s " + devID + " shell input text " + keys); }

    /**
     * Key Event for the "D-pad Up"
     * @author Hayden Brehm
     */
    public void dPadUp(){ adbCommand("adb -s " + devID + " shell input keyevent 19"); }


    /**
     * Command that executes a Key Event on adb.
     * @param code Key Event to be executed.
     * @author Hayden Brehm
     */
    public void keyEvent(String code){
        adbCommand("adb -s " + devID + " shell input keyevent " + code);
    }

    /**
     * Key Event for the "D-pad Down"
     * @author Hayden Brehm
     */
    public void dPadDown(){ adbCommand("adb -s " + devID + " shell input keyevent 20"); }

    /**
     * Key Event for the "D-pad Left"
     * @author Hayden Brehm
     */
    public void dPadLeft(){ adbCommand("adb -s " + devID + " shell input keyevent 21"); }

    /**
     * Key Event for the "D-pad Right"
     * @author Hayden Brehm
     */
    public void dPadRight(){ adbCommand("adb -s " + devID + " shell input keyevent 22"); }

    /**
     * Key Event for the "Hard Button"
     * @author Hayden Brehm
     */
    public void hardButton(){ adbCommand("adb -s " + devID + " shell input keyevent 228 sleep 5"); }

    /**
     * Key Event for the "End Call"
     * @author Hayden Brehm
     */
    public void endCall(){ adbCommand("adb -s " + devID + " shell input keyevent 6"); }

    /**
     * Key Event for the "Camera Key"
     * @author Hayden Brehm
     */
    public void cameraKey(){ adbCommand("adb -s " + devID + " shell input keyevent 27"); }

    /**
     * Key Event for the "Enter Key"
     * @author Hayden Brehm
     */
    public void enterKey(){ adbCommand("adb -s " + devID + " shell input keyevent 66"); }

    /**
     * Key Event for the "D-pad Center Key"
     * @author Hayden Brehm
     */
    public void dPadCenter(){ adbCommand("adb -s " + devID + " shell input keyevent 23"); }


    /**
     * adb command to remove a specific file on the connected device.
     * @param target the file to be removed.
     * @author Hayden Brehm
     */
    public void removeFile(String target)
    {
        adbCommand("adb -s " + devID + " shell rm " + target);
    }

    /**
     * adb command to reboot the connected device.
     * @author Hayden Brehm
     */
    public void rebootDevice()
    {
        adbCommand("adb -s " + devID + " reboot");
    }


    /**
     * Gets the connected device model.
     * @return device model.
     * @author Hayden Brehm
     */
    public String getDevModel(){
        return adbCommand("adb -s " + devID + " shell getprop ro.product.model");
    }

    /**
     * Gets the serial number of the connected device.
     * @return the serial number.
     * @author Hayden Brehm
     */
    public String getDevSerialNumber(){
        return adbCommand("adb -s " + devID + " shell getprop ro.serialno");
    }

    /**
     * Gets the carrier of the connected device. Needs sim.
     * @return the carrier of the device.
     * @author Hayden Brehm
     */
    public String getDevCarrier(){
        return adbCommand("adb -s " + devID + " shell getprop gsm.sim.operator.alpha");
    }

    /**
     * Gets the logcat processes currently running on the connected device.
     * @return an ArrayList of Strings filled with process IDs related to logcat.
     * @author Hayden Brehm
     */
    //TODO: LOGCAT continuation "adb logcat *:E" for error level and higher/-v for verbose
    //this grabs the logcat process (PID)
    public ArrayList<String> getLogcatProcesses()
    {
        MyLogger.log.debug("[ADB] Getting Logcat Process ID for {}", devID);
        ArrayList<String> Arr = new ArrayList<String>();
        //String[] out = adbCommand("adb -s " + devID + " shell top -n 1 | grep -i 'logcat -v threa+'").split("\n");
        String[] out = adbCommand("adb -s " + devID + " shell top -n 1 | grep -i 'logcat'").split("\n");

        //maybe some operations here to filter logcat (not right now)
        for (String value : out){
            if (value.contains("[1m"))
                value = value.split("m")[1];
            Arr.add(value.trim().split(" ")[0]);
            Arr.removeAll(Arrays.asList("",null));
        }
        MyLogger.log.debug("Arr elements are: {}", Arr);
        return Arr;
    }

    /**
     * Starts the logcat on the connected device.
     * @param processID the process ID to be given to this logcat process.
     * @param grep the specified lines that we wish to pull from logcat. {@code null} if none wanted.
     * @return an Object containing the process id of the running logcat.
     * @author Hayden Brehm
     */
    public Object startLogcat(String processID, String grep){
        ArrayList<String> pids;
        Thread logcat;

        do{
            logcat = new Thread(() -> {
                if (grep == null)
                {
                    adbCommand("adb -s " + devID + " shell logcat -v threadtime > /sdcard/Documents/" +processID+".txt");
                }
                else
                    adbCommand("adb -s " + devID + " shell logcat -v threadtime | grep -E '" + grep + "'> /sdcard/Documents/" +
                            processID + ".txt");
            });

            logcat.setName(processID);
            logcat.start();
            logcat.interrupt();
            pids = getLogcatProcesses();

            if (pids.isEmpty()) {
                MyLogger.log.debug("Logcat failed to start, retrying.");
                /*while (logcat.isAlive())
                    logcat.interrupt();*/
                logcat.interrupt();
            }
        }while(pids.isEmpty());

        MyLogger.log.debug("[ADB] Logcat Started for {}", devID);
        return pids.get(0);

        //NEED TO REWORK THIS
        /*ArrayList<String> finalPids = getLogcatProcesses();
        Timer timer = new Timer();
        timer.start();
        // 5 seconds is PLENTY of time
        while (!timer.expired(5)){
            if(pidbefore.size() > 0)
            {
                for(String pid : pidbefore)
                    finalPids.remove(pid);
            }
            if(finalPids.size() > 0) break;
            finalPids = getLogcatProcesses();
        }

        if (finalPids.size() == 1) return finalPids.get(0);   //thread has "[1m" before actual pid
        //else if(finalPids.size() > 1) throw new RuntimeException("More than 1 logcat processes started."); Too many issues. Do this better.
        else throw new RuntimeException("Failed to start logcat process");*/
    }

    /**
     * Stops the logcat process on the connected device.
     * @param pid the process id of the logcat we are to stop.
     * @author Hayden Brehm
     */
    public void stopLogcat(Object pid){
        /*String newPID;
        String pidStr = pid.toString();
        if(pidStr.contains("m")){
            newPID = pidStr.split("m")[1];
        }
        else newPID = pidStr;*/

        //Maybe Works
        /*ArrayList<String> current = getLogcatProcesses();
        for (String currpid : current)
        {
            adbCommand("adb -s " + devID + " shell kill " + currpid);
        }*/

        adbCommand("adb -s " + devID + " kill " + pid);

    }

    public void deletePttFiles(){
        adbCommand("adb -s " + devID + " shell rm -r /sdcard/Documents/PTT");
    }

    @Test
    public void test() throws InterruptedException {
        for(int i = 0; i < 5; i++){
            ADB adb1 = new ADB("24261FDH2000RS");
            HashMap<String,Object> pid = new HashMap<>();
            adb1.deletePttFiles();
            adb1.clearLogBuffer();
            pid.put(adb1.getDevID(), adb1.startLogcat("1", GrepConstants.grepLines));
            /*ADB adb2 = new ADB("24261FDH3000W1");
            adb2.deletePttFiles();
            adb2.clearLogBuffer();
            pid.put(adb2.getDevID(), adb2.startLogcat("2", GrepConstants.grepLines));*/
            stopLogcat("1");
            //stopLogcat("2");
            killServer();
        }
    }
}
