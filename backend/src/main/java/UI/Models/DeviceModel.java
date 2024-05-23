package UI.Models;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class DeviceModel {
    private final SwingPropertyChangeSupport propertyChangeSupport;
    private boolean devicesRunning;
    private boolean serviceRecord;
    private boolean connected;

    public DeviceModel(){
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void devicesRunning(boolean devicesRunning){
        this.devicesRunning = devicesRunning;
        String result = (devicesRunning) ? "Getting Devices..." : "Fetched All Devices...";
        propertyChangeSupport.firePropertyChange("Log", null, result);
    }

    public void serviceRecord(boolean serviceRecord){
        this.serviceRecord = serviceRecord;
    }

    public void connected(boolean connected){this.connected = connected;}

    public boolean isDevicesRunning(){return devicesRunning;}
    public boolean isServiceRecordFound(){return serviceRecord;}
}
