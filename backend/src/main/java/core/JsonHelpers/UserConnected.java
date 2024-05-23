package core.JsonHelpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.ADB;
import core.MyLogger;
import core.constants.TestNGInfo;
import core.managers.DriverManager;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * This class is used upon websocket connection to turn all currently connected devices and all tests into a JSON to be
 * sent to the UI to use for user input.
 * @author Hayden Brehm
 */
public class UserConnected {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ArrayList<Devices> devicesArrayList = new ArrayList<>();
    private final ArrayList<TestInfo> testInfoArrayList = new ArrayList<>();

    /**
     * Constructor that retrieves all devices and tests and adds to their respective ArrayLists for use in JSON formatting.
     * @author Hayden Brehm
     */
    public UserConnected(){
        //TODO: implement actual Model name after pulling multiple devices branch. Will be using temp name
        int i = 1;
        for(String device : ADB.getConnectedDevices()){
            devicesArrayList.add(new Devices("temp " + i, device));
            i++;
        }
        i = 1;
        for (String test : TestNGInfo.testList){
            testInfoArrayList.add(new TestInfo(String.valueOf(i), test));
            i++;
        }
    }

    /**
     * Gets the DeviceArrayList.
     * @return the DeviceArrayList.
     * @author Hayden Brehm
     */
    public ArrayList<Devices> getDevicesArrayList() {
        return devicesArrayList;
    }

    /**
     * Gets the TestInfoArrayList.
     * @return the TestInfoArrayList.
     * @author Hayden Brehm
     */
    public ArrayList<TestInfo> getTestInfoArrayList() {
        return testInfoArrayList;
    }

    /**
     * Takes the instance of this class and turns into a JSON.
     * @return JSON format of this class.
     * @author Hayden Brehm
     */
    public String toJson(){
        return GSON.toJson(this, UserConnected.class);
    }

    @Test
    private void test(){
        UserConnected userConnected = new UserConnected();
        MyLogger.log.debug(userConnected.toJson());
    }

    /**
     * This helper class defines how the devices will show on the JSON. Every Device will have an id and a deviceID to comply
     * with JSON format.
     * @author Hayden Brehm
     */
    public static class Devices{
        private final String id;
        private final String deviceID;

        /**
         * Constructor that assigns the id and deviceID associated with this Device object.
         * @param id id to be used.
         * @param deviceID the device id from adb getdevices associated with that device.
         * @author Hayden Brehm
         */
        public Devices(String id, String deviceID){
            this.id = id;
            this.deviceID = deviceID;
        }

        /**
         * Gets the id used by the JSON.
         * @return the id.
         * @author Hayden Brehm
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the deviceID that is associated with the device from adb getdevices.
         * @return the deviceID.
         * @author Hayden Brehm
         */
        public String getDeviceID() {
            MyLogger.log.debug("Enter getDeviceID()");
            return deviceID;
        }
    }

    /**
     * This helper class defines how the Test information will show on the JSON. Every Device will have an id and a
     * deviceID to comply with JSON format.
     * @author Hayden Brehm
     */
    public static class TestInfo{
        private final String id;
        private final String testName;
        /**
         * Constructor that assigns the id and test name for each test.
         * @param id id to be used.
         * @param testName the test name to be used.
         * @author Hayden Brehm
         */
        public TestInfo(String id, String testName){
            this.id = id;
            this.testName = testName;
        }

        /**
         * Gets the id used by the JSON.
         * @return the id.
         * @author Hayden Brehm
         */
        public String getId() {
            return id;
        }


        /**
         * Gets the test name.
         * @return the test name.
         * @author Hayden Brehm
         */
        public String getTestName() {
            return testName;
        }
    }
}
