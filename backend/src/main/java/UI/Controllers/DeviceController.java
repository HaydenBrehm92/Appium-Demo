package UI.Controllers;

import UI.Models.DeviceModel;
import UI.Views.DeviceView;
import api.Android;
import com.beust.ah.A;
import core.ADB;
import core.Events;
import core.MyLogger;

import core.managers.DriverManager;


import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * The Device Controller controls all aspects related to retrieving bluetooth devices and selecting them for testing.
 * @author Hayden Brehm
 */
public class DeviceController {

    private int attempts;
    private final SwingPropertyChangeSupport propertyChangeSupport;
    private final DeviceView deviceView;
    private static DeviceModel deviceModel;
    private static JList<Object> jList;

    /**
     * Device Controller constructor
     * @param deviceView the device view
     * @param deviceModel the device model
     * @param mainController the {@link MainController} to pass to the initializer
     * @author Hayden Brehm
     */
    public DeviceController(DeviceView deviceView, DeviceModel deviceModel,
                            MainController mainController){
        this.deviceView = deviceView;
        DeviceController.deviceModel = deviceModel;
        jList = deviceView.getDiscoveredDevices();
        propertyChangeSupport = new SwingPropertyChangeSupport(true, true);
        initializeController(mainController);
    }

    /**
     * Initializes the settings for the Device Controller. This includes lambda expressions for ActionListener actions.
     * Ensures that the bluetooth thread is properly maintained.
     * @param mainController the Main Controller set as a PropertyChangeListener for Device Controller.
     * @author Hayden Brehm
     */
    public void initializeController(MainController mainController){
        deviceModel.addPropertyChangeListener(deviceView);
        deviceModel.addPropertyChangeListener(mainController); // Updating Log window
        propertyChangeSupport.addPropertyChangeListener(deviceView);
        propertyChangeSupport.addPropertyChangeListener(mainController);

        /*deviceView.getGetDevicesBtn().addActionListener((ActionEvent e) -> {
            deviceModel.devicesRunning(true);
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            int threadCount = threadGroup.activeCount();
            Thread[] threadList = new Thread[threadCount];
            threadGroup.enumerate(threadList);
            for (int i = 0; i < threadCount; i++) {
                if (threadList[i].getName().equals("BTConnectionThread")) {
                    MyLogger.log.debug("Thread #{} {}", i, threadList[i].getName());
                    //client.setDevice(null);
                    //propertyChangeSupport.firePropertyChange("Remove Devices", null, null);
                    synchronized (client.getBTLock()) {
                        client.getBTLock().notifyAll();
                    }
                    break;
                }
            }

            Thread thread = new Thread(client);
            thread.start();
            thread.interrupt();

            MyLogger.log.debug("Invoking \"Remove Device\" property");
            propertyChangeSupport.firePropertyChange("Remove Devices", null, null);
            client.setDevice(null);
        });*/

        /*deviceView.getAcceptSelectedDeviceBtn().addActionListener((ActionEvent e) -> {
            if (BTClient.getUrls().size() > 0) {
                BTClient.getUrls().removeAll(BTClient.getUrls());   //Needed for new device selected if one has already been selected
                attempts = 0;
            }

            String name = (String) deviceView.getDiscoveredDevices().getSelectedValue();
            for (Object remoteDev : BTClient.getVecDevices()) {
                RemoteDevice device = (RemoteDevice) remoteDev;
                try {
                    if (device.getFriendlyName(false).equals(name)) {
                        client.setDevice(device);
                    }
                } catch (IOException ex) {
                    MyLogger.log.debug("Could not get Friendly Name! ----> {}", ex.getMessage());
                    ex.printStackTrace();
                }
            }

            propertyChangeSupport.firePropertyChange("Set Device Name", null, name);    //when not doing thread
            //new Thread(() -> selectDevice(client, name)).start();
        });

        MyLogger.log.debug("Device Controller Initialized!");
    }*/

    /*public void selectDevice(BTClient client, String name){
        try {
            client.selectDevice();
        } catch (IOException ex) {
            MyLogger.log.debug("IOException! Could not select device! ----> {}", ex.getMessage());
            throw new RuntimeException(ex);
        }

        try {
            if (BTClient.getUrls().isEmpty())
            {
                deviceModel.serviceRecord(false);
                throw new Exception();
            }
            else {
                deviceModel.serviceRecord(true);
                propertyChangeSupport.firePropertyChange("Service Record Found", null, name);

                *//*
                 * BELOW MOVED TO BASETEST SETUP
                 *//*
                *//*propertyChangeSupport.firePropertyChange
                        ("Log", null, "Trying To Open Connection!");
                // Connection + opening streams
                client.connect();
                Thread.sleep(1000);
                try{
                    BTClient.outStream = BTClient.streamConnection.openOutputStream();
                    BTClient.inStream = BTClient.streamConnection.openInputStream();
                }catch (IOException ex){
                    MyLogger.log.debug("IOException occurred! ----> {}", ex.getMessage());
                    ex.printStackTrace();
                }
                Thread.sleep(1000);
                *//**//*ArrayList<String> arrDev = DriverManager.getDevices();
                for (String dev : arrDev) {
                    new ADB(dev).homeKey();
                }*//**//*

                Events initialize;
                initialize = new Events.EventsBuilder("100","2","1","1").
                        setAccessoryType("3").setModelName("EEI-Tool").
                        setFeatureCapabilities("34359732223").setFeaturesRequested("34225586174").build();

                initialize.sendEvent(BTClient.outStream);
                Thread.sleep(100);

                if (initialize.readEvent(BTClient.inStream).equals(""))
                    throw new Exception("Initialization ACK not received. Retrying.");

                //MyLogger.log.debug("Foreground Activity: {}", Android.adb.getForegroundActivity());

                //checking if properly connected
                if(BTClient.streamConnection != null)
                    propertyChangeSupport.firePropertyChange("Connected", null, true);
                else propertyChangeSupport.firePropertyChange("Connected", null, false);*//*
            }
        } catch (Exception ex) {
            client.closeConnection();
            if (attempts < 2) {
                attempts++;
                selectDevice(client, name);
            } else {
                attempts = 0;
                propertyChangeSupport.firePropertyChange("Log", null,"Could not find service!");
                MyLogger.log.debug("URL IS EMPTY!");
            }
        }
    }*/

    /**
     * Extends the SwingWorker class to update JList with up-to-date bluetooth device information on the EDT.
     * @author Hayden Brehm
     */
    /*public static class AddToListTask extends SwingWorker<Void, RemoteDevice> {
        private Vector vecDevices;

        *//**
         * Constructor for AddToListTask
         * @param vecDevices the Vector containing all devices
         *//*
        public AddToListTask(Vector vecDevices){
            this.vecDevices = vecDevices;
        }

        *//**
         * On the EDT, this process runs in the background.
         * Send chunks of information to the process() method.
         * @return null
         * @author Hayden Brehm.
         *//*
        @Override
        protected Void doInBackground() {
            MyLogger.log.debug("Bluetooth Devices: ");
            for (int i = 0; i < getVecDevices().size(); i++){
                RemoteDevice device = (RemoteDevice) getVecDevices().get(i);
                publish(device);
            }
            return null;
        }

        *//**
         * Adds bluetooth devices to JList.
         * @param chunks intermediate results to process
         * @author Hayden
         *//*
        @Override
        protected void process(List<RemoteDevice> chunks){
            for (RemoteDevice dev : chunks)
            {
                try{
                    String name = dev.getFriendlyName(false);
                    String address = dev.getBluetoothAddress();
                    getListModel().addElement(name);
                    MyLogger.log.debug("Name: {} Address: {}", name, address);
                }catch (IOException ex){
                    MyLogger.log.debug("IOException! Could not get Friendly name! ----> {}", ex.getMessage());
                }
            }
        }

        *//**
         * Executes after doInBackground has completed.
         * Sets to model for JList and fires a PropertyChange to device view.
         * @author Hayden Brehm
         *//*
        @Override
        protected void done(){
            MyLogger.log.debug("Finished fetching devices!");
            jList.setModel(getListModel());
            deviceModel.devicesRunning(false);
        }

        *//**
         * Gets the DeviceListModel.
         * @return Device list model
         * @author Hayden Brehm
         *//*
        private DefaultListModel<Object> getListModel() {
            return DeviceView.deviceListModel;
        }

        *//**
         * Gets the Vector of devices.
         * @return the vector of devices.
         * @author Hayden Brehm
         *//*
        private Vector getVecDevices() {
            return vecDevices;
        }*/
    }
}
