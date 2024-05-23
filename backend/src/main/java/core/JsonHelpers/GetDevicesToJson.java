package core.JsonHelpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.ADB;
import core.MyLogger;

import java.util.ArrayList;


/**
 * This class is used to serialize the result from ADB.getConnectedDevices() to a JSON using GSON and sent to the UI.
 * @author Hayden Brehm
 */
public class GetDevicesToJson {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ArrayList<GetDevicesToJson.Devices> devicesArrayList = new ArrayList<>();

    /**
     * Constructor that gets all devices and assigns to devicesArrayList.
     * @author Hayden Brehm
     */
    public GetDevicesToJson(){
        int i = 1;
        for(String device : ADB.getConnectedDevices()){
            devicesArrayList.add(new GetDevicesToJson.Devices("temp " + i, device));
            i++;
        }
    }

    /**
     * Gets the ArrayList of Devices.
     * @return ArrayList of Devices.
     * @author Hayden Brehm
     */
    public ArrayList<GetDevicesToJson.Devices> getDevicesArrayList() {
        return devicesArrayList;
    }

    /**
     * Takes the instance of this class and turns into a JSON.
     * @return JSON format of this class.
     * @author Hayden Brehm
     */
    public String toJson(){
        return GSON.toJson(this, UserConnected.class);
    }

    /**
     * This helper class defines how the devices will show on the JSON. Every Device will have an id and a deviceID to comply
     * with JSON format.
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
}
