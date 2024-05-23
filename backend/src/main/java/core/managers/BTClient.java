/*
package core.managers;
//import UI.AddToListTask;
import UI.Controllers.DeviceController;
import UI.Controllers.MainController;
import com.intel.bluetooth.BlueCoveConfigProperties;
import com.intel.bluetooth.BlueCoveImpl;
import core.ADB;
import core.MyLogger;
import javax.bluetooth.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.bluetooth.UUID;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.swing.*;

*/
/*Implementation of client from https://itecnote.com/tecnote/android-initialize-a-bluetooth-connection-between-android-server-and-bluecove-on-pc-client/
Similar to bluecove documentation for searching for Remote device discovery and Services search: http://bluecove.org/bluecove/apidocs/index.html
This just has good formatting and user input that I will be replacing.
*//*


*/
/**
 * Runs the bluecove bluetooth client needed to use with feature phones.
 * @deprecated Bluetooth no longer in use
 * @author Hayden Brehm
 *//*

@Deprecated
public class BTClient implements DiscoveryListener, Runnable{
    //Atomics used for threads...maybe not needed unless multiple threads at the same time
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    public static OutputStream outStream;
    public static InputStream inStream;
    //object used for waiting
    private static Object lock=new Object();
    //object used for waiting
    private static Object BTLock = new Object();
    //vector containing the devices discovered
    private static Vector<RemoteDevice> vecDevices = new Vector<>();
    private static String connectionURL = null;
    private static final String targetConnectionName = "KodiakPoCApplication";
    private final UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);
    private static final int btAttrServiceName = 0x0100;
    private static final List<String> urls = new ArrayList<String>();
    public static StreamConnection streamConnection;
    private RemoteDevice remoteDevice;
    private DiscoveryAgent agent;
    private JList<Object> jList;
    private static int connectRetry = 0;
    private static String name;
    public BTClient(){MyLogger.log.info("-------------------------");}  //DO NOT REMOVE LOGGER LINE. LOGGER BREAKS IF REMOVED FOR SOME REASON.

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        BTClient.name = name;
    }

    */
/**
     * Starts the connection process by gathering nearby devices and updating the GUI element with a list of
     * these nearby devices. Devices are updated to the GUI using a SwingWorker.
     * @param client the current client object.
     * @throws BluetoothStateException when getting the local device (computer) and bluetooth is not enabled. Also,
     * when starting the inquiry, and it fails due to other operations that are being performed by the device.
     * @author Hayden Brehm
     *//*

    private void startClientConnection(BTClient client) throws BluetoothStateException {
        // is this needed?
        BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");

        //display local device address and name
        vecDevices.clear();
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        MyLogger.log.debug("Address: {}",localDevice.getBluetoothAddress());
        MyLogger.log.debug("Name: {}",localDevice.getFriendlyName());

        try {
            RemoteDevice[] devices = getAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
            for (RemoteDevice rd : devices)
            {
                MyLogger.log.debug("Remote device found from retriveDevices(): {}", rd.getFriendlyName(false));
                vecDevices.add(rd);
            }

            // retrieveDevices() happens within the same frame of execution, no need for thread lock
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        MyLogger.log.debug("Device Inquiry Completed.");

        if(vecDevices.size() == 0){
            MyLogger.log.debug("No Devices Found.");
        }
        else{
            MyLogger.log.debug("Adding Devices To Device Controller.");
            DeviceController.AddToListTask listWorker = new DeviceController.AddToListTask(vecDevices);
            listWorker.execute();
        }
    }


    */
/**
     * Gets the current connection url set by getting the required service record in
     * {@link BTClient#servicesDiscovered(int, ServiceRecord[])}
     * @return the connection url
     * @author Hayden Brehm
     *//*

    public static String getConnectionURL() {
        return connectionURL;
    }

    */
/**
     * Gets the list of URLs to be used for connecting bt client to bt server.
     * @return the list of eligible URLs
     * @author Hayden Brehm
     *//*

    public static List<String> getUrls(){
        return urls;
    }

    */
/**
     * Connects the bt client to the bt server.
     * @return the StreamConnection to allow for read/write between client and server.
     * @author Hayden Brehm
     *//*

    public StreamConnection connect(){
        MyLogger.log.debug("Opening Connection with: {}", urls.get(0));

        try{
            if (streamConnection != null && connectRetry == 0)
                streamConnection.close();

            streamConnection = (StreamConnection) Connector.open(urls.get(0));
            MyLogger.log.debug("Connection successfully opened with: {}", urls.get(0));
            connectRetry = 0;
        }catch (IOException ex){
            MyLogger.log.debug("Connection ERROR! --> {}", ex.getMessage());
            */
/*if (connectRetry < 3){
                connectRetry++;
                return connect();
            }*//*

        }

        return streamConnection;
    }

    */
/**
     * Selects the specific device that we wish to find the bt service record matching
     * the name {@link BTClient#targetConnectionName} on.
     * @throws BluetoothStateException (getAgent().searchServices()) if the number of concurrent service search
     * transactions exceeds the limit specified by the bluetooth.sd.trans.max property obtained from the class
     * LocalDevice or the system is unable to start one due to current conditions
     * @author Hayden Brehm
     *//*

    public void selectDevice() throws BluetoothStateException {
        UUID[] uuidSet = { uuid };
        int[] attrIds = { btAttrServiceName };

        try {
            // add a colon after every 2 characters to format it in the way bluetooth address normally is
            // the proper bluetooth format is needed for the ADB constructor
            String btAddr = remoteDevice.getBluetoothAddress();
            new ADB(btAddr.replaceAll("..(?!$)", "$0:").trim()).enableBluetooth();

            Thread.sleep(10000);
            MyLogger.log.debug("Searching for service for device {} - {}",
                    remoteDevice.getFriendlyName(false),
                    remoteDevice.getBluetoothAddress()
            );

            int transactionID = getAgent().searchServices(attrIds, uuidSet, remoteDevice, this);
            MyLogger.log.debug("searchServices() returned transaction ID {}", transactionID);

            synchronized(lock){
                lock.wait();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This is for testing...will do away with hard-coding this later
    public void sendET(StreamConnection streamConnection) throws IOException, InterruptedException {
        //TODO: implement streamConnection tasks to do
        //send string
        OutputStream outStream=streamConnection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write("ET=100;VI=2;SI=1;TI=1;AT=3;MN=ACCESSORY-VER-1.0.0;FC=11;FR=8192;\r");
        pWriter.flush();


        //read response
        InputStream inStream=streamConnection.openInputStream();
        BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
        String lineRead=bReader2.readLine();
        System.out.println(lineRead);

        Thread.sleep(2000);

        pWriter.write("ET=101;VI=2;SI=1;TI=1;CT=0;UR=2812223333;\r");
        pWriter.flush();
        String lineRead2=bReader2.readLine();
        System.out.println(lineRead2);

        Thread.sleep(2000);

        pWriter.write("ET=102;VI=2;SI=1;TI=1;CT=0;\r");
        pWriter.flush();
        String lineRead3=bReader2.readLine();
        System.out.println(lineRead3);

        Thread.sleep(2000);

        pWriter.write("ET=103;VI=2;SI=1;TI=2;CT=0;\r");
        pWriter.flush();
        String lineRead4=bReader2.readLine();
        System.out.println(lineRead4);
    }

    */
/**
     * Gets the lock object.
     * @return the lock object.
     * @author Hayden Brehm
     *//*

    public Object getLock(){
        return lock;
    }

    */
/**
     * Gets the BTLock object
     * @return the BTLock object
     * @author Hayden Brehm
     *//*

    public Object getBTLock() {
        return BTLock;
    }

    */
/**
     * Gets the StreamConnection between client and server.
     * @return the streamConnection of the bt client connected to the bt server. If no connection return null.
     * @author Hayden Brehm
     *//*

    public StreamConnection getStreamConnection(){
        return streamConnection;
    }

    */
/**
     * Closes the StreamConnection between the client and server.
     * @author Hayden Brehm
     *//*

    public void closeConnection() {
        if (streamConnection == null)
        {
            MyLogger.log.debug("No stream connection!");
            return;
        }

        try{
            MyLogger.log.debug("Closing Output/Input to: {}", getStreamConnection());
            outStream.close();
            MyLogger.log.debug("Closing Connection to: {}", getStreamConnection());
            inStream.close();
            streamConnection.close();
            streamConnection = null;
        }catch (IOException e){
            MyLogger.log.debug("Closed Connection Failed! ----> {}", e.getMessage());
        }
    }

    */
/**
     * Sets the BT device (one we select).
     * Used in {@link DeviceController#initializeController(MainController)}.
     * @param remoteDevice the bt device we are interested in
     * @author Hayden Brehm
     *//*

    public void setDevice(RemoteDevice remoteDevice){
        this.remoteDevice = remoteDevice;
    }

    */
/**
     * @return boolean value of true if BTConnectionThread is running. False otherwise.
     * @author Hayden Brehm
     *//*

    boolean isRunning() {
        return running.get();
    }

    */
/**
     * @return boolean value of true if BTConnectionThread is stopped. False otherwise.
     * @author Hayden Brehm
     *//*

    boolean isStopped() {
        return stopped.get();
    }

    */
/**
     * Gets the Vector of devices found by the device inquiry
     * @return the Vector of devices.
     * @author Hayden Brehm
     *//*

    public static Vector<RemoteDevice> getVecDevices() {
        return vecDevices;
    }

    */
/**
     * Implemented from DiscoveryListener. For each unique device discovered, add to VecDevices.
     * @param btDevice device found during inquiry.
     * @param cod the service classes, major device class, and minor device class of the remote device. Not used.
     * @author Hayden Brehm
     *//*

    //methods of DiscoveryListener
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        MyLogger.log.debug("deviceDiscovered() called");
        //add the device to the vector
        try
        {
            if(!vecDevices.contains(btDevice)){
                MyLogger.log.debug("Adding {}", btDevice.getFriendlyName(false));
                vecDevices.addElement(btDevice);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    */
/**
     * Is called when services are found. Gets the service record containing
     * the name in {@link BTClient#targetConnectionName} and adds to the URL list.
     * @param transID  the transaction ID of the service search that is posting the result
     * @param servRecord a list of services found during the search request
     * @author Hayden Brehm
     *//*

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        MyLogger.log.debug("Calling servicesDiscovered() with transID: {}", transID);

        for (ServiceRecord serviceRecord : servRecord) {
            connectionURL = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            DataElement name = serviceRecord.getAttributeValue(btAttrServiceName); //DataElement is a Bluetooth service attribute value --> name
            MyLogger.log.debug("Service found: name: {}, url: {} ", name, connectionURL);

            if (connectionURL != null && name.getValue().equals(targetConnectionName)) {
                urls.add(connectionURL);
            }
        }
    }

    */
/**
     * When all available services are found, the lock notifies the lock.wait() that it can proceed.
     * @param transID the transaction ID identifying the request which initiated the service search
     * @param respCode the response code that indicates the status of the transaction
     * @author Hayden Brehm
     *//*

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        MyLogger.log.debug("serviceSearchCompleted() called with transID: {} | respCode: {}", transID, respCode);

        synchronized(lock){
            lock.notify();
        }
    }

    */
/**
     * When all available devices are found, the lock notifies the lock.wait() that it can proceed.
     * @param discType the type of request that was completed;
     * either INQUIRY_COMPLETED, INQUIRY_TERMINATED, or INQUIRY_ERROR
     * @author Hayden Brehm
     *//*

    @Override
    public void inquiryCompleted(int discType) {
        MyLogger.log.debug("inquiryCompleted() called with discType: {}", discType);
        synchronized(lock){
            lock.notify();
        }
    }

    */
/**
     * Run from Runnable. Creates a new thread that runs the start of the BT connection process and keeps it alive.
     * @author Hayden Brehm
     *//*

    @Override
    public void run() {
        MyLogger.log.debug("run() called");
        running.set(true);
        stopped.set(false);
        while (running.get()){
            try {
                Thread.currentThread().setName("BTConnectionThread");
                MyLogger.log.debug("Thread name is {}", Thread.currentThread().getName());
                try{
                    startClientConnection(this);
                }catch (IOException ex){
                    MyLogger.log.debug("IOEXCEPTION!! --> {}", ex.getMessage());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                running.set(false);
                MyLogger.log.debug("Interrupting Thread After InterruptedException");
                Thread.currentThread().interrupt();
                MyLogger.log.debug("Thread got interrupted! --> {}",e.getMessage());
            }

            try {
                synchronized(BTLock){
                    BTLock.wait();
                }
                MyLogger.log.debug("Running set to False");
                running.set(false);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MyLogger.log.debug("Thread {} has stopped!", Thread.currentThread().getName());
        stopped.set(true);
    }

    */
/**
     * Gets the agent that finds devices and records.
     * @return the DiscoveryAgent agent (local machine)
     *//*

    public DiscoveryAgent getAgent() throws javax.bluetooth.BluetoothStateException {
        if (agent == null)
            agent = LocalDevice.getLocalDevice().getDiscoveryAgent();

        return agent;
    }

    */
/**
     * Sets the agent (local machine) that will search for devices and records.
     * @param agent the agent to set.
     * @author Hayden Brehm
     *//*

    public void setAgent(DiscoveryAgent agent) {
        this.agent = agent;
    }

    */
/**
     * Sets the JList containing the list of devices to be used for the UI element.
     * @param jList the JList to be set
     * @author Hayden Brehm
     *//*

    public void setJList(JList<Object> jList){
        this.jList = jList;
    }

    */
/**
     * Gets the JList containing the list of devices to be used for the UI element.
     * @return the JList used for displaying to UI.
     * @author Hayden Brehm
     *//*

    public JList<Object> getJList(){
        return jList;
    }
}
*/
