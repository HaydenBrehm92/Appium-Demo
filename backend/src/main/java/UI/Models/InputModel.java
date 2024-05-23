package UI.Models;

import UI.Controllers.MainController;
import core.MyLogger;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * The InputModel tracks the state of all inputs the user can use.
 * @author Hayden Brehm
 */
public class InputModel {
    private boolean runEnabled;
    private boolean errorShown;
    private boolean mdnEnabled;
    private boolean errorEnabled;
    private boolean cancelEnabled;
    private boolean standardSelected;
    private boolean lmrRadioSelected;
    private boolean smartPhoneSelected;
    private boolean featurePhoneSelected;
    private boolean featurephoneValidated;
    private boolean mdnValidated;
    private boolean smartphoneValidated;
    private boolean inputsValidated;
    private final SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * InputModel constructor
     */
    public InputModel(){
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    }

    /**
     * Adds a PropertyChangeListener to this model. Used to allow InputView to listen to InputModel in
     * {@link UI.Controllers.InputController#initializeController(MainController)}.
     * @param propertyChangeListener the PropertyChangeListener that will listen on PropertyChanges from InputModel
     * @author Hayden Brehm
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * Sets the state that the "Run" button is in. True = Enabled. False = Disabled.
     * @param value the boolean value to set the "Run" button state
     * @author Hayden Brehm
     */
    public void changeRunState(boolean value){
        runEnabled = value;
    }

    /**
     * Sets the validation state of inputs. True = all fields satisfied. False = all requirements not met.
     * Fires a PropertyChange "Inputs Validated" to the InputView.
     * @param value the boolean value to set the validation state
     * @author Hayden Brehm
     */
    public void inputsValidatedState(boolean value){
        this.inputsValidated = value;
        propertyChangeSupport.firePropertyChange("Inputs Validated", null, value);
    }

    /**
     * Sets the state that the mdn field is in. True = enabled. False = disabled.
     * Fires a PropertyChange "mdnStateChange" to the InputView.
     * @param value the boolean value to set the mdn field state
     * @author Hayden Brehm
     */
    public void mdnState(boolean value){
        MyLogger.log.debug("MDN Field set to: {}", value);
        mdnEnabled = value;
        propertyChangeSupport.firePropertyChange("mdnStateChange", null, value);
    }

    /**
     * Sets the state that the "Cancel" button is in. True = enabled. False = disabled.
     * Fires a PropertyChange "SetCancel" to the InputView.
     * @param value the boolean value to set the cancel button state
     * @author Hayden Brehm
     */
    public void cancelState(boolean value){
        MyLogger.log.debug("Cancel Button set to: {}", value);
        cancelEnabled = value;
        propertyChangeSupport.firePropertyChange("SetCancel", null, value);
    }

    /**
     * Sets the state that the error label is in. True = Enabled. False = Disabled.
     * Fires PropertyChanges "invalidMDN", "Validated MDN", and "Inputs Validated" to the InputView.
     * @param error the String error message to display
     * @param value the boolean value to set the error state
     * @author Hayden Brehm
     */
    public void errorState(String error, boolean value){
        MyLogger.log.debug("MDN error state set to: {}", value);
        errorEnabled = value;
        if (error != null) {
            MyLogger.log.debug("MDN Validated set to: {}", value);
            this.mdnValidated = false;
            propertyChangeSupport.firePropertyChange("invalidMDN", null, error);
            propertyChangeSupport.firePropertyChange("Validated MDN", null, false);
            propertyChangeSupport.firePropertyChange("Inputs Validated", null, false);
        }
        else {
            MyLogger.log.debug("MDN Validated set to: {}", value);
            this.mdnValidated = true;
            propertyChangeSupport.firePropertyChange("Validated MDN", null, true);
            propertyChangeSupport.firePropertyChange("invalidMDN", null, null);
        }
    }

    /**
     * Sets the state that the smartPhone button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "SmartPhone Selected" to the InputView.
     * @param value the boolean value to set the smartPhone button state
     * @author Hayden Brehm
     */
    public void smartPhoneSelected(boolean value){
        MyLogger.log.debug("Smartphone set to: {}", value);
        this.smartPhoneSelected = value;
        if (isFeaturePhoneSelected() && value) {
            this.featurePhoneSelected = false;
            MyLogger.log.debug("Featurephone set to: {}", false);
        }
        propertyChangeSupport.firePropertyChange("SmartPhone Selected", null, value);
    }

    /**
     * Sets the state that the featurePhone button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "FeaturePhone Selected" to the InputView.
     * @param value the boolean value to set the featurePhone button state
     * @author Hayden Brehm
     */
    public void featurePhoneSelected(boolean value){
        MyLogger.log.debug("Featurephone set to: {}", value);
        this.featurePhoneSelected = value;
        if (isSmartPhoneSelected() && value) {
            this.smartPhoneSelected = false;
            MyLogger.log.debug("Smartphone set to: {}", false);
        }
        propertyChangeSupport.firePropertyChange("FeaturePhone Selected", null, value);
    }

    /**
     * Sets the state that the LMR Radio button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "LmrRadioSelected" to the InputView.
     * @param value the boolean value to set the LMR Radio button state
     * @author Hayden Brehm
     */
    public void lmrRadioSelected(boolean value){
        MyLogger.log.debug("LMR Radio set to: {}", value);
        this.lmrRadioSelected = value;
        if (isStandardSelected() && value){
            this.standardSelected = false;
            MyLogger.log.debug("Standard set to: {}", false);
        }
        propertyChangeSupport.firePropertyChange("LmrRadioSelected", null, value);
    }

    /**
     * Sets the state that the Standard button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "StandardSelected" to the InputView.
     * @param value the boolean value to set the Standard button state
     * @author Hayden Brehm
     */
    public void standardSelected(boolean value){
        MyLogger.log.debug("Standard set to: {}", value);
        this.standardSelected = value;
        if (isLmrRadioSelected() && value) {
            this.lmrRadioSelected = false;
            MyLogger.log.debug("LMR Radio set to: {}", false);
        }
        propertyChangeSupport.firePropertyChange("StandardSelected", null, value);
    }

    /**
     * Sets the state that the Featurephone button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "FeaturePhone Validated" to the InputView. Currently Not Implemented In InputView.
     * @param value the boolean value to set the Featurephone validation state
     * @author Hayden Brehm
     */
    public void featurephoneValidated(boolean value){
        MyLogger.log.debug("Featurephone set to: {}", value);
        featurephoneValidated = value;
        propertyChangeSupport.firePropertyChange("FeaturePhone Validated", null, value);
    }

    /**
     * Sets the state the Smartphone button is in. True = Selected. False = Deselected.
     * Fires PropertyChange "FeaturePhone Validated" to the InputView. Currently Not Implemented In InputView.
     * @param value the boolean value to set the SmartPhone validation state
     * @author Hayden Brehm
     */
    public void smartphoneValidated(boolean value){
        MyLogger.log.debug("Smartphone Validated: {}", value);
        smartphoneValidated = value;
        propertyChangeSupport.firePropertyChange("SmartPhone Validated", null, value);
    }

    /**
     * Gets the boolean value related to Smartphone validation.
     * @return the boolean value of Smartphone validation
     * @author Hayden Brehm
     */
    public boolean isSmartphoneValidated(){return smartphoneValidated;}

    /**
     * Gets the boolean value related to Featurephone validation.
     * @return the boolean value of Featurephone validation
     * @author Hayden Brehm
     */
    public  boolean isFeaturephoneValidated(){return featurephoneValidated;}

    /**
     * Gets the boolean value related to Smartphone selection.
     * @return the boolean value of Smartphone selection
     * @author Hayden Brehm
     */
    public boolean isStandardSelected(){return standardSelected;}

    /**
     * Gets the boolean value related to LMR Radio selection.
     * @return the boolean value of LMR Radio selection
     * @author Hayden Brehm
     */
    public boolean isLmrRadioSelected(){return lmrRadioSelected;}

    /**
     * Gets the boolean value related to the Error visibility.
     * @return the boolean value of Error Label visibility.
     * @author Hayden Brehm
     */
    public boolean isErrorShown(){return errorShown;}

    /**
     * Gets the boolean value related to the Run button state.
     * @return the boolean value of the Run button state
     * @author Hayden Brehm
     */
    public boolean isRunEnabled(){return runEnabled;}

    /**
     * Gets the boolean value related to the mdn field state.
     * @return the boolean value of the mdn field state
     * @author Hayden Brehm
     */
    public boolean isMdnEnabled(){return mdnEnabled;}

    /**
     * Gets the boolean value related to the validation state of the mdn field.
     * @return the boolean value of the validation state of the mdn field
     * @author Hayden Brehm
     */
    public boolean isMdnValidated(){return mdnValidated;}

    /**
     * Gets the boolean value related to Smartphone selection.
     * @return the boolean value of Smartphone selection
     * @author Hayden Brehm
     */
    public boolean isSmartPhoneSelected(){return smartPhoneSelected;}

    /**
     * Gets the boolean value related to Featurephone selection.
     * @return the boolean value of Featurephone selection
     * @author Hayden Brehm
     */
    public boolean isFeaturePhoneSelected(){return featurePhoneSelected;}
}
