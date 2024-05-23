package UI.Views;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The DeviceView listens to PropertyChanges from DeviceModel and DeviceController.
 * The DeviceView controls UI changes and ensures these changes occur only on the EDT.
 * @author Hayden Brehm
 */
public class DeviceView implements PropertyChangeListener {
    private final JButton acceptSelectedDeviceBtn;
    private final JList<Object> discoveredDevices;
    public static DefaultListModel<Object> deviceListModel = new DefaultListModel<>();
    private final JButton getDevicesBtn;

    /**
     * Constructor for DeviceView.
     * @param getDevicesBtn the "Get Devices" button component
     * @param acceptSelectedDeviceBtn the "Accept" button component
     * @param discoveredDevices the window that displays the list of devices discovered after pressing "Get Devices"
     * @author Hayden Brehm
     */
    public DeviceView(JButton getDevicesBtn, JButton acceptSelectedDeviceBtn, JList<Object> discoveredDevices){
        this.getDevicesBtn = getDevicesBtn;
        this.acceptSelectedDeviceBtn = acceptSelectedDeviceBtn;
        this.discoveredDevices = discoveredDevices;
    }

    /**
     * Listens for PropertyChange events and acts upon them depending on the Property Name provided. DeviceView listens
     * to DeviceController and DeviceModel.
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     * @author Hayden Brehm
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();

        switch(propertyName){
            case "Remove Devices":
                removeAllDevices();
                break;
        }
    }

    /**
     * Gets the acceptSelectedDeviceBtn component.
     * @return the JButton component of acceptSelectedDeviceBtn
     * @author Hayden Brehm
     */
    public JButton getAcceptSelectedDeviceBtn(){return acceptSelectedDeviceBtn;}

    /**
     * Gets the getDevicesBtn component.
     * @return the JButton component of getDevicesBtn
     * @author Hayden Brehm
     */
    public JButton getGetDevicesBtn(){return getDevicesBtn;}

    /**
     * Gets the discoveredDevices component.
     * @return the JList component of discoveredDevices
     * @author Hayden Brehm
     */
    public JList<Object> getDiscoveredDevices() {return discoveredDevices;}

    /**
     * Removes all devices in the JList component. Ensured to run on the EDT.
     * @author Hayden Brehm
     */
    private void removeAllDevices(){
        if(SwingUtilities.isEventDispatchThread()){
            deviceListModel.removeAllElements();
            getDiscoveredDevices().setModel(deviceListModel);
        } else SwingUtilities.invokeLater(this::removeAllDevices);
    }
}
