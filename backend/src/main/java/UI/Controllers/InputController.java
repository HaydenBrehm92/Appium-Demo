package UI.Controllers;

import UI.Models.InputModel;
import UI.Views.InputView;
import UI.InputFileReaderAndWriter;
import core.MyLogger;
import core.constants.AppInformation;
import core.constants.TestConstants;
import core.managers.TestManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Objects;

/**
 * The Input Controller controls all user inputs for setting parameters for the test to be run. It also verifies that
 * conditions are met that satisfy the prerequisites needed to begin a test.
 * @author Hayden Brehm
 */
public class InputController {
    private final SwingPropertyChangeSupport propertyChangeSupport;
    private final InputView inputView;
    private final InputModel inputModel;
    private TestManager testManager;
    private final InputFileReaderAndWriter inputFileReaderAndWriter = new InputFileReaderAndWriter();

    /**
     * Constructor for Input Controller
     * @param inputView the view
     * @param inputModel the model
     * @param mainController the {@link MainController} to pass to the initializer
     */
    public InputController(InputView inputView, InputModel inputModel, MainController mainController){
        propertyChangeSupport = new SwingPropertyChangeSupport(true, true);
        this.inputView = inputView;
        this.inputModel = inputModel;
        initializeController(mainController);
    }

    /**
     * Initializes the settings for the Input Controller. This includes lambda expressions for ActionListener actions.
     * Initializer also validates input from the user in these ActionListeners.
     * Allows InputView to listen to InputController and InputModel.
     * @param mainController the Main Controller set as a PropertyChangeListener for Input Controller.
     * @author Hayden Brehm
     */
    private void initializeController(MainController mainController){
        inputView.getMdnField().setEnabled(false);
        inputView.getErrorLabel().setVisible(false);
        inputView.getErrorLabel().setOpaque(true);
        // Adding to both model and controller
        inputModel.addPropertyChangeListener(inputView);
        propertyChangeSupport.addPropertyChangeListener(inputView);
        propertyChangeSupport.addPropertyChangeListener(mainController);

        //Model to JSpinner for Iterations
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, 20000, 1);
        inputView.getIterations().setModel(spinnerNumberModel);

        //Model to JSpinner for Delays
        SpinnerNumberModel spinnerNumberModel2 = new SpinnerNumberModel(0, 0, 3600000, 1000);
        inputView.getDelaySpinner().setModel(spinnerNumberModel2);

        /*
         * Validate all fields before run...
         * (Device field, Test field, MDN, Delay, Device Types, Client Types)
         */

        inputView.getDeviceField().addActionListener((ActionEvent e) -> {
            validate();
        });

        inputView.getTestField().addActionListener((ActionEvent e) -> {
            validate();
        });

        inputView.getMdnField().addActionListener((ActionEvent e) -> {
            validate();
        });

        inputView.getMdnField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateMDN(inputView.getMdnField().getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateMDN(inputView.getMdnField().getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {/*Textfield doesn't do anything here*/}
        });

        inputView.getSmartphoneBtn().addItemListener((ItemEvent ie) -> {
            selectSmartPhone();
            validate();
        });

        inputView.getFeaturephoneBtn().addItemListener((ItemEvent ie) -> {
            selectFeaturePhone();
            validate();
        });

        inputView.getStandardRadioButton().addItemListener((ItemEvent ie) -> {
            selectStandard();
            validate();
        });

        inputView.getLMRRadioRadioButton().addItemListener((ItemEvent ie) -> {
            selectLmrRadio();
            validate();
        });

        inputView.getRunButton().addActionListener((ActionEvent e) -> {
            /*TestManager.DeviceType deviceType;
            TestManager.Type type;*/
            updateAllInputsToFile();
            AppInformation.setAppToUse((String) inputView.getCarrierSelect().getSelectedItem());
            inputModel.changeRunState(true);
            inputModel.cancelState(true);



            /*if (inputModel.isSmartPhoneSelected())
                deviceType = TestManager.DeviceType.SmartPhone;
            else deviceType = TestManager.DeviceType.FeaturePhone;

            if (inputModel.isStandardSelected())
                type = TestManager.Type.Handset;
            else type = TestManager.Type.Radio;*/

            /*
            String testSelection = Objects.requireNonNull(inputView.getTestField().getSelectedItem()).toString();
            switch (testSelection) {
                case TestConstants.BatchOne:
                    testManager = new TestManager.TestManagerBuilder((Integer) inputView.getIterations().getValue(),
                            type, deviceType)
                            .setPhoneNumber(Long.parseLong(inputView.getMdnField().getText()))
                            .setDelay((Integer) inputView.getDelaySpinner().getValue())
                            .setMainController(mainController)
                            .setClient(client)
                            .build();
                    break;
                case TestConstants.RecentCallsTest:
                    testManager = new TestManager.TestManagerBuilder((Integer) inputView.getIterations().getValue(),
                            type, deviceType)
                            .setDelay((Integer) inputView.getDelaySpinner().getValue())
                            .setMainController(mainController)
                            .setClient(client)
                            .build();
                    break;
                case TestConstants.PtxMessagesTest:
                case TestConstants.AddContactTest:
                    testManager = new TestManager.TestManagerBuilder((Integer) inputView.getIterations().getValue(),
                            type, deviceType)
                            .setDelay((Integer) inputView.getDelaySpinner().getValue())
                            .setPhoneNumber(Long.parseLong(inputView.getMdnField().getText()))
                            .setMainController(mainController)
                            .setClient(client)
                            .build();
                    break;
            }*/
            //String testName = Objects.requireNonNull(inputView.getTestField().getSelectedItem()).toString();
            propertyChangeSupport.firePropertyChange("Ready", null, null);
            //propertyChangeSupport.firePropertyChange("Set Config", null, getTestManager());
        });

        inputView.getCancelButton().addActionListener((ActionEvent e) -> {
            inputModel.cancelState(false);
            propertyChangeSupport.firePropertyChange("Cancel", null, null);
        });

        MyLogger.log.debug("Input Controller Initialized!");
    }


    public TestManager getTestManager() {
        return testManager;
    }

    /**
     * Selects the Standard Client Type and updates the Input Model with the boolean value. True if selected. False
     * otherwise.
     * @author Hayden Brehm
     */
    private void selectStandard(){
        inputModel.standardSelected(inputView.getStandardRadioButton().isSelected());
    }

    /**
     * Selects the LMR Radio Client Type and updates the Input Model with the boolean value. True if selected. False
     * otherwise.
     * @author Hayden Brehm
     */
    private void selectLmrRadio(){
        inputModel.lmrRadioSelected(inputView.getLMRRadioRadioButton().isSelected());
    }

    /**
     * Selects the Featurephone Device Type and updates the Input Model with the boolean value. True if selected. False
     * otherwise.
     * @author Hayden Brehm
     */
    private void selectFeaturePhone(){
        inputModel.featurePhoneSelected(inputView.getFeaturephoneBtn().isSelected());
    }

    /**
     * Selects the Smartphone Device Type and updates the Input Model with the boolean value. True if selected. False
     * otherwise.
     * @author Hayden Brehm
     */
    private void selectSmartPhone(){
        inputModel.smartPhoneSelected(inputView.getSmartphoneBtn().isSelected());
    }

    /**
     * Updates the mdn field to enabled or disabled based on user input.
     * <br>Enabled:<br>Test = Batchone<br>Featurephone + PtxMessagesTest are selected<br>Test = AddContactsTest<br><br>
     * Disabled otherwise.
     * @author Hayden Brehm
     */
    private void mdnFieldState(){
        //TODO: MAKE THIS ONLY APPEAR FOR FEATUREPHONE(ptx) AND ADDCONTACTS(both)
        inputModel.mdnState(Objects.equals(inputView.getTestField().getSelectedItem(), TestConstants.BatchOne) ||
                Objects.equals(inputView.getTestField().getSelectedItem(), TestConstants.AddContactTest) ||
                (Objects.equals(inputView.getTestField().getSelectedItem(), TestConstants.PtxMessagesTest) &&
                inputModel.isFeaturePhoneSelected()));
    }

    /**
     * Validates if all criteria are met to enable the "Run" button.
     * @author Hayden Brehm
     */
    private void validate(){
        mdnFieldState();    //Checks if mdn Field should be enabled/disabled
        if (inputModel.isSmartPhoneSelected()){ // ISSUE OCCURS HERE. IF NOT SELECTED IT SHOULD VALIDATE TO FALSE
            validateSmartPhone();
        } else if (inputModel.isFeaturePhoneSelected())
            validateFeaturePhone();
        else {
            validateSmartPhone();   //will be false. Needed for deselected smartphone.
            validateFeaturePhone(); //will be false. Needed for deselected featurephone.
        }

        if(inputModel.isMdnEnabled()) {
            if (inputModel.isMdnValidated() && inputModel.isSmartphoneValidated())
                inputModel.inputsValidatedState(true);
            else inputModel.inputsValidatedState(inputModel.isMdnValidated() && inputModel.isFeaturephoneValidated());
        }
        else
        {
            if (inputModel.isSmartphoneValidated())
                inputModel.inputsValidatedState(true);
            else inputModel.inputsValidatedState(inputModel.isFeaturephoneValidated());
        }
    }

    /**
     * Validates the criteria for Smartphones
     * @author Hayden Brehm
     */
    private void validateSmartPhone(){
        if(inputModel.isSmartPhoneSelected()){
            if(inputView.getTestField() != null && (inputModel.isLmrRadioSelected() || inputModel.isStandardSelected()))
            {
                if (inputModel.isMdnEnabled())
                    validateMDN(inputView.getMdnField().getText());
                inputModel.smartphoneValidated(true);
            } else inputModel.smartphoneValidated(false);
        } else inputModel.smartphoneValidated(false);

        /*if (inputView.getTestField() != null &&
                inputModel.isSmartPhoneSelected() &&
                (inputModel.isLmrRadioSelected() || inputModel.isStandardSelected()))
        {
            if (inputModel.isMdnEnabled()){
                validateMDN(inputView.getMdnField().getText());
            }

            inputModel.smartphoneValidated(true);
        }
        else inputModel.smartphoneValidated(false); //TODO: FIX THIS, IT IS NEVER FIRED*/
    }

    /**
     * Validates the criteria for Featurephones
     * @author Hayden Brehm
     */
    private void validateFeaturePhone() {
        if(inputView.getTestField() != null &&
                inputView.getFeaturephoneBtn().isSelected() &&
                !inputView.getDeviceField().getText().equals("") &&
                (inputView.getLMRRadioRadioButton().isSelected() || inputView.getStandardRadioButton().isSelected()))
        {
            if (inputModel.isMdnEnabled()){
                validateMDN(inputView.getMdnField().getText());
            }

            inputModel.featurephoneValidated(true);
        }
        else inputModel.featurephoneValidated(false);
    }

    /**
     * Validates the criteria for MDN. An error message is displayed if the MDN's criteria is not met.
     * @param mdnText the String text given to the mdn field.
     * @author Hayden Brehm
     */
    private void validateMDN(String mdnText){
        if(mdnText.isEmpty()) {
            MyLogger.log.debug("MDN EMPTY!");
            inputModel.errorState("MDN must not be empty!", true);
        }
        else if (!mdnText.matches("[0-9]+")) {
            MyLogger.log.debug("NON-DIGIT DETECTED!");
            inputModel.errorState("MDN must be only digits", true);
        }
        else {MyLogger.log.debug("MDN VALIDATED!"); inputModel.errorState(null, false);}
    }

    private void updateAllInputsToFile(){
        //Device Name
        inputFileReaderAndWriter.updateInputToXMLFile
                (InputFileReaderAndWriter.inputs.Device.toString(), inputView.getDeviceField().getText());

        //Carrier
        inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Carrier.toString(),
                (String) inputView.getCarrierSelect().getSelectedItem());

        //Test
        inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Test.toString(),
                (String) inputView.getTestField().getSelectedItem());

        //MDN
        inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.MDN.toString(),
                inputView.getMdnField().getText());

        //Iterations
        inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Iterations.toString(),
                (inputView.getIterations().getValue()).toString());

        //Delay
        inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Delay.toString(),
                (inputView.getDelaySpinner().getValue()).toString());

        //Smartphone / Featurephone
        if (inputModel.isSmartPhoneSelected()) {
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Smartphone.toString(), "1");
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Featurephone.toString(), "0");
        }
        else {
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Smartphone.toString(), "0");
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Featurephone.toString(), "1");
        }

        //Standard / LMR
        if (inputModel.isStandardSelected())
        {
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Standard.toString(), "1");
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Radio.toString(), "0");
        }
        else {
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Standard.toString(), "0");
            inputFileReaderAndWriter.updateInputToXMLFile(InputFileReaderAndWriter.inputs.Radio.toString(), "1");
        }
    }
}
