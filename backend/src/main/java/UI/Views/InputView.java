package UI.Views;

import core.MyLogger;
import core.constants.AppInformation;
import core.constants.TestNGInfo;
import core.constants.TestNGInfo;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The InputView listens to PropertyChanges from InputModel and InputController.
 * The InputView controls UI changes and ensures these changes occur only on the EDT.
 * Utilizes fluent builder.
 * @author Hayden Brehm
 */
public class InputView implements PropertyChangeListener {
    private final JPanel TestInputs;
    private final JTextField deviceField;
    private final JComboBox testField;
    private final JLabel testFieldLabel;
    private final JLabel deviceFieldLabel;
    private final JRadioButton smartphoneBtn;
    private final JRadioButton featurephoneBtn;
    private final JButton runButton;
    private final JSpinner Iterations;
    private final JLabel IterationsLabel;
    private final JTextField mdnField;
    private final JLabel mdnFieldLabel;
    private final JRadioButton standardRadioButton;
    private final JRadioButton LMRRadioRadioButton;
    private final JLabel DeviceLabel;
    private final JLabel ClientLabel;
    private final JButton cancelButton;
    private final JSpinner delaySpinner;
    private final JLabel delaySpinnerLabel;
    private final JLabel errorLabel;
    private final JComboBox carrierSelect;
    private final JLabel carrierSelectLabel;
    private boolean mdnValidated;
    private boolean inputsValidated;
    private final SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * Constructor for InputView
     * @param inputViewBuilder the builder
     * @author Hayden Brehm
     */
    private InputView(InputViewBuilder inputViewBuilder){
        this.TestInputs = inputViewBuilder.TestInputs;
        this.deviceField = inputViewBuilder.deviceField;
        this.testField = inputViewBuilder.testField;
        this.testFieldLabel = inputViewBuilder.testFieldLabel;
        this.deviceFieldLabel = inputViewBuilder.deviceFieldLabel;
        this.smartphoneBtn = inputViewBuilder.smartphoneBtn;
        this.featurephoneBtn = inputViewBuilder.featurephoneBtn;
        this.runButton = inputViewBuilder.runButton;
        this.Iterations = inputViewBuilder.Iterations;
        this.IterationsLabel = inputViewBuilder.IterationsLabel;
        this.mdnField = inputViewBuilder.mdnField;
        this.mdnFieldLabel = inputViewBuilder.mdnFieldLabel;
        this.standardRadioButton = inputViewBuilder.standardRadioButton;
        this.LMRRadioRadioButton = inputViewBuilder.LMRRadioRadioButton;
        this.DeviceLabel = inputViewBuilder.DeviceLabel;
        this.ClientLabel = inputViewBuilder.ClientLabel;
        this.cancelButton = inputViewBuilder.cancelButton;
        this.delaySpinner = inputViewBuilder.delaySpinner;
        this.delaySpinnerLabel =inputViewBuilder.delaySpinnerLabel;
        this.errorLabel = inputViewBuilder.errorLabel;
        this.carrierSelect = inputViewBuilder.carrierSelect;
        this.carrierSelectLabel = inputViewBuilder.carrierSelectLabel;
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        initializeTestComboBox();
        initializeCarrierComboBox();
    }

    /**
     * Listens for PropertyChange events and acts upon them depending on the Property Name provided. InputView listens
     * to InputController and InputModel.
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     * @author Hayden Brehm
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();
        switch (propertyName){
            //TODO: THESE NEED TO CHANGE STATES AND ENABLE RUN----close to completion
            case "SetCancel":
                setCancelButton((Boolean) newValue);
                break;
            case "mdnStateChange":
                mdnState((Boolean) newValue);
                break;
            case "invalidMDN":
                if(newValue != null)
                    showErrorLabel((String) newValue, true);
                else showErrorLabel(null, false);
                break;
            case "Validated MDN":
                mdnValidated((Boolean) newValue);
                break;
            case "Inputs Validated":
                inputsValidated((Boolean) newValue);
                break;
            case "StandardSelected":
                standardSelected((Boolean) newValue);
                break;
            case "LmrRadioSelected":
                lmrRadioSelected((Boolean) newValue);
                break;
            case "SmartPhone Selected":
                smartPhoneSelected((Boolean) newValue);
                break;
            case "FeaturePhone Selected":
                featurePhoneSelected((Boolean) newValue);
                break;
        }
    }

    /**
     * Initializes Carrier JComboBox to add all available client packages from the appConfig.xml
     * @author Hayden Brehm
     */
    private void initializeCarrierComboBox(){
        if (SwingUtilities.isEventDispatchThread())
        {
            AppInformation.addAppsToComboBox(getCarrierSelect());
        } else SwingUtilities.invokeLater(this::initializeCarrierComboBox);
    }

    /**
     * Initializes Test JComboBox to add all available tests from the testng.xml
     * @author Hayden Brehm
     */
    private void initializeTestComboBox(){
        if (SwingUtilities.isEventDispatchThread())
        {
            TestNGInfo.addTestsToJComboBox(getTestField());
        } else SwingUtilities.invokeLater(this::initializeTestComboBox);
    }

    /**
     * Sets whether the cancel button is enabled or not. Ensured to run on the EDT.
     * @param value the boolean value that determines if the cancel button is enabled or not. Enabled if True.
     *              Otherwise, disabled.
     * @author Hayden Brehm
     */
    private void setCancelButton(boolean value){
        if(SwingUtilities.isEventDispatchThread())
            getCancelButton().setEnabled(value);
        else SwingUtilities.invokeLater(() -> setCancelButton(value));
    }

    /**
     * Shows an Error Message related to the MDN field. Determines which error message will be displayed and if it will
     * be displayed. If error message is {@code null} then the error label will be invisible. Ensured to run on the EDT.
     * @param errorMessage the error message to be displayed
     * @param value whether the message will be visible or not
     * @author Hayden Brehm
     */
    private void showErrorLabel(String errorMessage, boolean value){
        MyLogger.log.debug("Error msg is: {}", errorMessage);
        if(SwingUtilities.isEventDispatchThread()) {
            getErrorLabel().setText(errorMessage);
            getErrorLabel().setVisible(value);
        } else SwingUtilities.invokeLater(() -> showErrorLabel(errorMessage, value));
    }

    /**
     * Sets whether the MDN field is enabled or not. Ensured to run on the EDT.
     * @param value the boolean value that determines if the mdn field is enabled or not. Enabled if True.
     *              Otherwise, disabled.
     * @author Hayden Brehm
     */
    private void mdnState(boolean value){
        if(SwingUtilities.isEventDispatchThread())
            getMdnField().setEnabled(value);
        else SwingUtilities.invokeLater(() -> mdnState(value));
    }

    /**
     * Selects/Deselects the Standard button option and
     * Deselects/Selects the LMR Radio button option respectively. Ensured to run on the EDT.
     * @param value the boolean value that determines if the Standard button is selected/deselected
     * @author Hayden Brehm
     */
    private void standardSelected(boolean value){
        if(SwingUtilities.isEventDispatchThread()) {
            getStandardRadioButton().setSelected(value);
            if (value)
                getLMRRadioRadioButton().setSelected(false);
        }else SwingUtilities.invokeLater(() -> standardSelected(value));
    }

    /**
     * Selects/Deselects the LMR Radio button option and
     * Deselects/Selects the Standard button option respectively. Ensured to run on the EDT.
     * @param value the boolean value that determines if the LMR Radio button is selected/deselected
     * @author Hayden Brehm
     */
    private void lmrRadioSelected(boolean value){
        if(SwingUtilities.isEventDispatchThread()) {
            getLMRRadioRadioButton().setSelected(value);
            if (value)
                getStandardRadioButton().setSelected(false);
        }else SwingUtilities.invokeLater(() -> lmrRadioSelected(value));
    }

    /**
     * Selects/Deselects the Smartphone button option and
     * Deselects/Selects the Featurephone button option respectively. Ensured to run on the EDT.
     * @param value the boolean value that determines if the Smartphone button is selected/deselected
     * @author Hayden Brehm
     */
    private void smartPhoneSelected(boolean value){
        if(SwingUtilities.isEventDispatchThread()) {
            getSmartphoneBtn().setSelected(value);
            if (value)
                getFeaturephoneBtn().setSelected(false);
        }else SwingUtilities.invokeLater(() -> smartPhoneSelected(value));
    }

    /**
     * Selects/Deselects the Featurephone button option and
     * Deselects/Selects the Smartphone button option respectively. Ensured to run on the EDT.
     * @param value the boolean value that determines if the Featurephone button is selected/deselected
     * @author Hayden Brehm
     */
    private void featurePhoneSelected(boolean value){
        if(SwingUtilities.isEventDispatchThread()) {
            getFeaturephoneBtn().setSelected(value);
            if (value)
                getSmartphoneBtn().setSelected(false);
        }else SwingUtilities.invokeLater(() -> featurePhoneSelected(value));
    }

    /**
     * Sets the inputsValidated field to True if validated. False otherwise. Also, sets whether the run button
     * is enabled/disabled.
     * @param value sets whether the run button is enabled or not. Enabled if True. Disabled otherwise.
     * @author Hayden Brehm
     */
    private void inputsValidated(boolean value){
        this.inputsValidated = value;
        if(SwingUtilities.isEventDispatchThread())
            getRunButton().setEnabled(value);
        else SwingUtilities.invokeLater(() -> inputsValidated(value));
    }

    /**
     * Sets whether the mdn field is validated or not.
     * @param value the boolean value that determines if the mdn field is validated.
     * @author Hayden Brehm
     */
    private void mdnValidated(boolean value){this.mdnValidated = value;}

    /**
     * Gets the runButton JButton.
     * @return runButton JButton component.
     * @author Hayden Brehm
     */
    public JButton getRunButton(){return runButton;}

    /**
     * Gets the TestInputs JPanel.
     * @return TestInputs JPanel component.
     * @author Hayden Brehm
     */
    public JPanel getTestInputs() {return TestInputs;}

    /**
     * Gets the DeviceField JTextField.
     * @return DeviceField JTextField component.
     * @author Hayden Brehm
     */
    public JTextField getDeviceField() {return deviceField;}

    /**
     * Gets the testField JComboBox.
     * @return testField JComboBox component.
     * @author Hayden Brehm
     */
    public JComboBox getTestField() {return testField;}

    /**
     * Gets the testFieldLabel JLabel.
     * @return testFieldLabel JLabel component
     * @author Hayden Brehm
     */
    public JLabel getTestFieldLabel() {return testFieldLabel;}

    /**
     * Gets the deviceFieldLabel JLabel.
     * @return deviceFieldLabel JLabel component
     * @author Hayden Brehm
     */
    public JLabel getDeviceFieldLabel() {return deviceFieldLabel;}

    /**
     * Gets the smartphoneBtn JRadioButton.
     * @return smartphoneBtn JRadioButton component
     * @author Hayden Brehm
     */
    public JRadioButton getSmartphoneBtn() {return smartphoneBtn;}

    /**
     * Gets the featurephoneBtn JRadioButton.
     * @return featurephoneBtn JRadioButton component
     * @author Hayden Brehm
     */
    public JRadioButton getFeaturephoneBtn() {
        return featurephoneBtn;
    }

    /**
     * Gets the Iterations JSpinner.
     * @return Iterations JSpinner component
     * @author Hayden Brehm
     */
    public JSpinner getIterations() {
        return Iterations;
    }

    /**
     * Gets the IterationsLabel JLabel.
     * @return IterationsLabel JLabel component.
     * @author Hayden Brehm
     */
    public JLabel getIterationsLabel() {
        return IterationsLabel;
    }

    /**
     * Gets the mdnField JTextField.
     * @return mdnField JTextField component.
     * @author Hayden Brehm
     */
    public JTextField getMdnField() {
        return mdnField;
    }

    /**
     * Gets the mdnFieldLabel JLabel.
     * @return mdnFieldLabel JLabel component.
     * @author Hayden Brehm
     */
    public JLabel getMdnFieldLabel() {
        return mdnFieldLabel;
    }

    /**
     * Gets the standardRadioButton JRadioButton.
     * @return standardRadioButton JRadioButton component
     * @author Hayden Brehm
     */
    public JRadioButton getStandardRadioButton() {
        return standardRadioButton;
    }

    /**
     * Gets the LMRRadioRadioButton JRadioButton.
     * @return LMRRadioRadioButton JRadioButton component
     * @author Hayden Brehm
     */
    public JRadioButton getLMRRadioRadioButton() {
        return LMRRadioRadioButton;
    }

    /**
     * Gets the DeviceLabel JLabel.
     * @return DeviceLabel JLabel component
     * @author Hayden Brehm
     */
    public JLabel getDeviceLabel() {
        return DeviceLabel;
    }

    /**
     * Gets the ClientLabel JLabel.
     * @return ClientLabel JLabel component
     * @author Hayden Brehm
     */
    public JLabel getClientLabel() {
        return ClientLabel;
    }

    /**
     * Gets the cancelButton JButton.
     * @return cancelButton JButton component
     * @author Hayden Brehm
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Gets the delaySpinner JSpinner.
     * @return delaySpinner JSpinner component.
     * @author Hayden Brehm
     */
    public JSpinner getDelaySpinner() {
        return delaySpinner;
    }

    /**
     * Gets the delaySpinnerLabel JLabel.
     * @return delaySpinnerLabel JLabel component
     * @author Hayden Brehm
     */
    public JLabel getDelaySpinnerLabel() {
        return delaySpinnerLabel;
    }

    /**
     * Gets the errorLabel JLabel.
     * @return errorLabel JLabel component.
     * @author Hayden Brehm
     */
    public JLabel getErrorLabel(){return errorLabel;}

    public JComboBox getCarrierSelect() {
        return carrierSelect;
    }

    public JLabel getCarrierSelectLabel() {
        return carrierSelectLabel;
    }

    public static class InputViewBuilder{
        private final JPanel TestInputs;
        private JTextField deviceField;
        private JComboBox testField;
        private JLabel testFieldLabel;
        private JLabel deviceFieldLabel;
        private JRadioButton smartphoneBtn;
        private JRadioButton featurephoneBtn;
        private JButton runButton;
        private JSpinner Iterations;
        private JLabel IterationsLabel;
        private JTextField mdnField;
        private JLabel mdnFieldLabel;
        private JRadioButton standardRadioButton;
        private JRadioButton LMRRadioRadioButton;
        private JLabel DeviceLabel;
        private JLabel ClientLabel;
        private JButton cancelButton;
        private JSpinner delaySpinner;
        private JLabel delaySpinnerLabel;
        private JLabel errorLabel;
        private JComboBox carrierSelect;
        private JLabel carrierSelectLabel;

        /**
         * InputViewBuilder Constructor
         * @param TestInputs JPanel component related to Inputs
         */
        public InputViewBuilder(JPanel TestInputs){
            this.TestInputs = TestInputs;
        }

        /**
         * Sets the deviceField for the View.
         * @param deviceField JTextField component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setDeviceField(JTextField deviceField){
            this.deviceField = deviceField;
            return this;
        }

        /**
         * Sets the deviceFieldLabel for the View.
         * @param deviceFieldLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setDeviceFieldLabel(JLabel deviceFieldLabel){
            this.deviceFieldLabel = deviceFieldLabel;
            return this;
        }

        /**
         * Sets the testField for the View.
         * @param testField JComboBox component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setTestField(JComboBox testField){
            this.testField = testField;
            return this;
        }

        /**
         * Sets the testFieldLabel for the View.
         * @param testFieldLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setTestFieldLabel(JLabel testFieldLabel){
            this.testFieldLabel = testFieldLabel;
            return this;
        }

        /**
         * Sets the smartphoneBtn for the View.
         * @param smartphoneBtn JRadioButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setSmartphoneBtn(JRadioButton smartphoneBtn){
            this.smartphoneBtn = smartphoneBtn;
            return this;
        }

        /**
         * Sets the featurephoneBtn for the View.
         * @param featurephoneBtn JRadioButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setFeaturephoneBtn(JRadioButton featurephoneBtn){
            this.featurephoneBtn = featurephoneBtn;
            return this;
        }

        /**
         * Sets the runButton for the View.
         * @param runButton JButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setRunButton(JButton runButton){
            this.runButton = runButton;
            return this;
        }

        /**
         * Sets the Iterations for the View.
         * @param Iterations JSpinner component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setIterations(JSpinner Iterations){
            this.Iterations = Iterations;
            return this;
        }

        /**
         * Sets the IterationsLabel for the View.
         * @param IterationsLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setIterationsLabel(JLabel IterationsLabel){
            this.IterationsLabel = IterationsLabel;
            return this;
        }

        /**
         * Sets the mdnField for the View.
         * @param mdnField JTextField component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setMdnField(JTextField mdnField){
            this.mdnField = mdnField;
            return this;
        }

        /**
         * Sets the mdnFieldLabel for the View.
         * @param mdnFieldLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setmdnFieldLabel(JLabel mdnFieldLabel){
            this.mdnFieldLabel = mdnFieldLabel;
            return this;
        }

        /**
         * Sets the standardRadioButton for the View.
         * @param standardRadioButton JRadioButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setStandardRadioButton(JRadioButton standardRadioButton){
            this.standardRadioButton = standardRadioButton;
            return this;
        }

        /**
         * Sets the LMRRadioRadioButton for the View.
         * @param LMRRadioRadioButton JRadioButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setLMRRadioRadioButton(JRadioButton LMRRadioRadioButton){
            this.LMRRadioRadioButton = LMRRadioRadioButton;
            return this;
        }

        /**
         * Sets the DeviceLabel for the View.
         * @param DeviceLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setDeviceLabel(JLabel DeviceLabel){
            this.DeviceLabel = DeviceLabel;
            return this;
        }

        /**
         * Sets the ClientLabel for the View.
         * @param ClientLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setClientLabel(JLabel ClientLabel){
            this.ClientLabel = ClientLabel;
            return this;
        }

        /**
         * Sets the cancelButton for the View.
         * @param cancelButton JButton component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setCancelButton(JButton cancelButton){
            this.cancelButton = cancelButton;
            return this;
        }

        /**
         * Sets the delaySpinner for the View.
         * @param delaySpinner JSpinner component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setDelaySpinner(JSpinner delaySpinner){
            this.delaySpinner = delaySpinner;
            return this;
        }

        /**
         * Sets the delaySpinnerLabel for the View.
         * @param delaySpinnerLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setDelaySpinnerLabel(JLabel delaySpinnerLabel){
            this.delaySpinnerLabel = delaySpinnerLabel;
            return this;
        }

        /**
         * Sets the errorLabel for the View.
         * @param errorLabel JLabel component
         * @return the current object instance
         * @author Hayden Brehm
         */
        public InputViewBuilder setErrorLabel(JLabel errorLabel){
            this.errorLabel = errorLabel;
            return this;
        }

        public InputViewBuilder setCarrierSelect(JComboBox carrierSelect){
            this.carrierSelect = carrierSelect;
            return this;
        }

        public InputViewBuilder setCarrierSelectLabel(JLabel carrierSelectLabel){
            this.carrierSelectLabel = carrierSelectLabel;
            return this;
        }

        /**
         * Calls the InputView constructor by passing the current InputViewBuilder instance. The InputView constructor
         * unpacks the fields set by InputViewBuilder and assigns them to their corresponding fields in InputView.
         * @return a new InputView object
         * @author Hayden Brehm
         */
        public InputView build(){
            return new InputView(this);
        }
    }
}
