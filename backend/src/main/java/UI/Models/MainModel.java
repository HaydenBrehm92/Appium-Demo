package UI.Models;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * The Main Model tracks the state of the Progress bar and Log window.
 * It can also access other models.
 * @author Hayden Brehm
 */
public class MainModel {
    private double progressPerTask = 0d;
    private double currentProgress = 0d;
    private final InputModel inputModel;
    private final DeviceModel deviceModel;
    private boolean progressBarVisible;
    private final SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * The Constructor for MainModel.
     * @param inputModel the model for Input
     * @param deviceModel the model for Device
     * @author Hayden Brehm
     */
    public MainModel(InputModel inputModel, DeviceModel deviceModel){
        this.inputModel = inputModel;
        this.deviceModel = deviceModel;
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    }

    /**
     * Adds a PropertyChangeListener to this class object.
     * This is used when initializing in the Main Controller to allow the Main View to listen for property changes from
     * the Main Model.
     * @param propertyChangeListener the PropertyChangeListener that will listen on PropertyChanges from MainModel
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * Gets the Input Model
     * @return the Input Model
     * @author Hayden Brehm
     */
    public InputModel getInputModel(){return inputModel;}

    /**
     * Fires a PropertyChange whether the progress bar is visible or not.
     * Updates the state of the progress bar as a boolean value.
     * @param progressBarVisible the boolean value passed to the PropertyChange
     * @author Hayden Brehm
     */
    public void setProgressBarVisible(boolean progressBarVisible) {
        this.progressBarVisible = progressBarVisible;
        propertyChangeSupport.firePropertyChange("Progressbar Visible", null, progressBarVisible);
    }

    /**
     * Checks if the progress bar is visible or not.
     * @return the boolean value of the progress bar. True if visible. False otherwise.
     */
    public boolean isProgressBarVisible(){return progressBarVisible;}

    /**
     * Sets how much the progress bar will fill when completing a task.
     * @param progressPerTask the amount the progress bar will fill.
     * @author Hayden Brehm
     */
    public void setProgressPerTask(double progressPerTask) {
        this.progressPerTask = progressPerTask;
    }

    /**
     * Gets the amount the progress bar will fill when completing a task.
     * @return a double of how much the progress bar will fill
     * @author Hayden Brehm
     */
    public double getProgressPerTask() {
        return progressPerTask;
    }

    /**
     * Sets the current progress made on the progress bar.
     * @param currentProgress the current progress amount
     * @author Hayden Brehm
     */
    public void setCurrentProgress(double currentProgress) {
        this.currentProgress = currentProgress;
    }

    /**
     * Gets the current progress made on the progress bar.
     * @return the current progress made
     * @author Hayden Brehm
     */
    public double getCurrentProgress() {
        return currentProgress;
    }
}
