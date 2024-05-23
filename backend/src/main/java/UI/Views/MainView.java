package UI.Views;

import UI.MainForm;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The Main View controls updating of all UI elements on the EDT.
 * This view focuses more on UI elements not specified by other MVC implementations.
 * The Main View also helps with bridging other MVC view updates. Implements PropertyChangeListener.
 * @author Hayden Brehm
 */
public class MainView implements PropertyChangeListener {
    MainForm mainForm;

    /**
     * Constructor for MainView
     * @param mainForm the mainForm that specifies mainly the formatting of the UI. Already has components laid out.
     * @author Hayden Brehm
     */
    public MainView(MainForm mainForm){
        this.mainForm = mainForm;
    }

    /**
     * Gets the Main Form which takes care of the formatting for UI Elements.
     * @return the Main Form
     * @author Hayden Brehm
     */
    public MainForm getMainForm() {return mainForm;}

    /**
     * Listens for PropertyChange events and acts upon them depending on the Property Name provided. MainView listens
     * to MainController and MainModel.
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     * @author Hayden Brehm
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();

        switch(propertyName){
            case "Running":
                disableDevicePanel((Boolean) newValue);
                break;
            case "Reset Progressbar":
                resetProgressBar();
                break;
            case "Log":
                log((String) newValue);
                break;
            case "Progressbar Visible":
                progressBarVisible((Boolean) newValue);
                break;
            case "Set Device Name":
                setDeviceName((String) newValue);
                break;
            case "Progressbar Increment":
                incrementProgressBar((int) newValue);
                break;
            case "Progressbar Setup":
                progressBarSetUp();
                break;
            case "Progressbar complete":
                progressComplete();
                break;
        }
    }

    /**
     * Disables or Enables components in the Devices JPanel. Ensured to run on the EDT.
     * @param disable the boolean value that determines if the Devices JPanel is enabled or disabled.
     * @author Hayden Brehm
     */
    private void disableDevicePanel(boolean disable){
        if(SwingUtilities.isEventDispatchThread()){
            for (Component component : mainForm.getDevices().getComponents()) {
                if (SwingUtilities.isEventDispatchThread())
                    component.setEnabled(!disable);
            }
        } else SwingUtilities.invokeLater(() -> disableDevicePanel(disable));
    }

    /**
     * Disables or Enables components in the TestInputs JPanel, excluding buttons. Ensured to run on the EDT.
     * @param disable the boolean value that determines if the Devices JPanel is enabled or disabled.
     * @author Hayden Brehm
     */
    private void disableInputPanel(boolean disable){
        if(SwingUtilities.isEventDispatchThread()){
            for (Component component : mainForm.getTestInputs().getComponents()){
                if (!(component instanceof JButton))
                    if (SwingUtilities.isEventDispatchThread())
                        component.setEnabled(!disable);
            }
        } else SwingUtilities.invokeLater(() -> disableInputPanel(disable));
    }

    /**
     * Sets the progress to 100 just to ensure the UI progress bar looks complete. In case of rounding errors.
     * Ensured to run on the EDT.
     * @author Hayden Brehm
     */
    private void progressComplete(){
        if (SwingUtilities.isEventDispatchThread())
            getProgressBar().setValue(100);
        else SwingUtilities.invokeLater(this::progressComplete);
    }

    /**
     * Gets the progress bar component from the MainForm.
     * @return the progress bar
     * @author Hayden Brehm
     */
    private JProgressBar getProgressBar(){return mainForm.getProgressBar();}

    /**
     * Gets the log text area component from MainForm.
     * @return the log text area
     * @author Hayden Brehm
     */
    private JTextArea getLog(){return mainForm.getLogText();}

    /**
     * Resets the Progress bar to 0. Ensured to run on the EDT.
     * @author Hayden Brehm
     */
    private void resetProgressBar(){
        if (SwingUtilities.isEventDispatchThread()){
            getProgressBar().setValue(0);
            getProgressBar().setStringPainted(false);
            getProgressBar().setIndeterminate(true);
        }else SwingUtilities.invokeLater(this::resetProgressBar);
    }

    /**
     * Updates the progress bar to increment its current value by the new value provided.
     * Ensured to run on the EDT.
     * @param value the amount to increment by
     * @author Hayden Brehm.
     */
    private void incrementProgressBar(int value){
        if(SwingUtilities.isEventDispatchThread())
            getProgressBar().setValue(value);
        else SwingUtilities.invokeLater(() -> incrementProgressBar(value));
    }

    /**
     * Logs a new String to the log window. Each addition is on its own newline. Ensured to run on the EDT.
     * @param str the String to append to the log window
     * @author Hayden Brehm
     */
    private void log(String str){
        if (SwingUtilities.isEventDispatchThread())
            getLog().append(str + "\n");
        else SwingUtilities.invokeLater(() -> log(str));
    }

    /**
     * Shows/Hides progress bar and enables/disables respectively.
     * Ensured to run on the EDT.
     * @param value the boolean value that determines if the progress bar will be visible/enabled. True shows the
     *              progress bar and enables it. False hides the progress bar and disables it.
     * @author Hayden Brehm
     */
    private void progressBarVisible(boolean value){
        if (SwingUtilities.isEventDispatchThread()){
            getProgressBar().setEnabled(value);
            getProgressBar().setVisible(value);
        }
        else SwingUtilities.invokeLater(() -> progressBarVisible(value));
    }

    /**
     * Sets up the progress bar to be ready for the Test that is running. Makes the numerical value show and progress
     * is represented by filling the progress bar. Ensured to run on the EDT.
     * @author Hayden Brehm
     */
    private void progressBarSetUp(){
        if (SwingUtilities.isEventDispatchThread()){
            getProgressBar().setStringPainted(true);
            getProgressBar().setIndeterminate(false);
        } else SwingUtilities.invokeLater(this::progressBarSetUp);
    }

    /**
     * Sets the device name that will be filled in the device field. Needed for bluetooth. Ensured to run on the EDT.
     * @param name the name to be set in the device field.
     * @author Hayden Brehm
     */
    private void setDeviceName(String name){
        if (SwingUtilities.isEventDispatchThread())
            getMainForm().getDeviceField().setText(name);
        else SwingUtilities.invokeLater(() -> setDeviceName(name));
    }

}
