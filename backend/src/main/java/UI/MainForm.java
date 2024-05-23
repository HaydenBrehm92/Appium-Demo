package UI;
import UI.Controllers.DeviceController;
import UI.Controllers.InputController;
import UI.Controllers.MainController;
import UI.Models.DeviceModel;
import UI.Models.InputModel;
import UI.Models.MainModel;
import UI.Views.DeviceView;
import UI.Views.InputView;
import UI.Views.MainView;
import api.Android;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import core.ADB;
import core.MemInfoExecution;
import core.MyLogger;
import core.appium.AppiumServer;
import core.constants.AppInformation;
import core.constants.TestConstants;
import core.managers.DriverManager;
import core.managers.FileManager;

import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class MainForm {
    private ExecutorService executorService;
    private JPanel mainPanel;
    private JList<Object> discoveredDevices;
    private String selectedDevice;
    private JButton AcceptSelectedDeviceButton;
    private JButton getdevicesbtn;
    private JPanel Devices;
    private JPanel TestInputs;
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
    private JProgressBar progressBar;
    private JPanel Progress;
    private JScrollPane LogPane;
    private JPanel LogPaneJpanel;
    private JTextArea logText;
    private JSpinner delaySpinner;
    private JLabel delaySpinnerLabel;
    private JLabel errorLabel;
    private JComboBox carrierSelect;
    private JLabel carrierSelectLabel;
    private JMenuBar jMenuBar;
    private JMenu menu;
    private URI feedbackURL = null;
    private URI userGuideURL = null;


    public MainForm() {
        // create logs folder if needed
        FileManager.checkAndCreateDirectory(FileManager.logsFolder);

        initializeMVC();
        initializeHelp();
    }


    /**
     * Initializes the Help menu item. This includes the actions, settings, and shortcuts assigned to selections.
     *
     * @author Hayden Brehm
     */
    private void initializeHelp() {
        try {
            userGuideURL = new URI
                    ("https://docs.google.com/document/d/1G9D2GaLgBewHgr11fQhr3rkuWoI9cyXWK3yGQGBt9gY/edit?usp=sharing");
            feedbackURL = new URI
                    ("https://docs.google.com/forms/d/e/1FAIpQLSduBrLeMgoGEKCSvdKziCSCGY7CbJzYxi6OWo0FilpZb0C2IA/viewform");
        } catch (URISyntaxException ex) {
            MyLogger.log.debug("URISyntaxException Occurred! ----> {}", ex.getMessage());
        }

        // JMenuBar setting
        jMenuBar = new JMenuBar();

        //Menu added to toolbar
        menu = new JMenu();
        menu.setText("Help");
        menu.setMnemonic(KeyEvent.VK_A);

        //Menu items
        JMenuItem menuItem = new JMenuItem("Feedback", KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menu.add(menuItem);

        JMenuItem menuItem2 = new JMenuItem("User Guide", KeyEvent.VK_T);
        menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menu.add(menuItem2);

        // Add to Jmenubar
        jMenuBar.add(menu);

        menuItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(feedbackURL);
            } catch (Exception ex) {
                MyLogger.log.debug("EXCEPTION occurred! ----> {}", ex.getMessage());
            }
        });

        menuItem2.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(userGuideURL);
            } catch (Exception ex) {
                MyLogger.log.debug("EXCEPTION occurred! ----> {}", ex.getMessage());
            }
        });
    }

    /**
     * Initializes MVC implementation for UI components
     *
     * @author Hayden Brehm
     */
    private void initializeMVC() {


        //Device Model and Device View
        DeviceView deviceView = new DeviceView(getdevicesbtn, AcceptSelectedDeviceButton, discoveredDevices);
        DeviceModel deviceModel = new DeviceModel();

        //Input Model and Input View
        InputView inputView = new InputView.InputViewBuilder(mainPanel).
                setDeviceField(deviceField).
                setDeviceFieldLabel(deviceFieldLabel).
                setIterations(Iterations).
                setIterationsLabel(IterationsLabel).
                setDelaySpinner(delaySpinner).
                setDelaySpinnerLabel(delaySpinnerLabel).
                setDeviceLabel(DeviceLabel).
                setSmartphoneBtn(smartphoneBtn).
                setFeaturephoneBtn(featurephoneBtn).
                setMdnField(mdnField).
                setmdnFieldLabel(mdnFieldLabel).
                setStandardRadioButton(standardRadioButton).
                setLMRRadioRadioButton(LMRRadioRadioButton).
                setTestField(testField).
                setTestFieldLabel(testFieldLabel).
                setClientLabel(ClientLabel).
                setCancelButton(cancelButton).
                setRunButton(runButton).
                setErrorLabel(errorLabel).
                setCarrierSelect(carrierSelect).
                setCarrierSelectLabel(carrierSelectLabel).
                build();
        InputModel inputModel = new InputModel();

        //Main Model and Main View
        MainModel mainModel = new MainModel(inputModel, deviceModel);
        MainView mainView = new MainView(this);

        //Controllers
        MainController mainController = new MainController(mainView, mainModel, this);
        new DeviceController(deviceView, deviceModel, mainController);
        new InputController(inputView, inputModel, mainController);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JList<Object> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public JPanel getTestInputs() {
        return TestInputs;
    }

    public JTextField getDeviceField() {
        return deviceField;
    }

    public JSpinner getIterations() {
        return Iterations;
    }

    public JTextField getMdnField() {
        return mdnField;
    }

    public JComboBox getTestField() {
        return testField;
    }

    public String getSelectedDevices() {
        return selectedDevice;
    }

    public JButton getAcceptSelectedDeviceButton() {
        return AcceptSelectedDeviceButton;
    }

    public JRadioButton getSmartphoneBtn() {
        return smartphoneBtn;
    }

    public JRadioButton getFeaturephoneBtn() {
        return featurephoneBtn;
    }

    public JRadioButton getStandardRadioButton() {
        return standardRadioButton;
    }

    public JRadioButton getLMRRadioRadioButton() {
        return LMRRadioRadioButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JPanel getProgress() {
        return Progress;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JScrollPane getLogPane() {
        return LogPane;
    }

    public JTextArea getLogText() {
        return logText;
    }

    public String getSelectedDevice() {
        return selectedDevice;
    }

    public JMenuBar getMenuBar() {
        return jMenuBar;
    }

    public JPanel getDevices() {
        return Devices;
    }

    public JComboBox getCarrierSelect() {
        return carrierSelect;
    }

    public JLabel getCarrierSelectLabel() {
        return carrierSelectLabel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        Devices = new JPanel();
        Devices.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(Devices, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(250, 254), null, 0, true));
        Devices.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Discovered Devices", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        discoveredDevices = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        discoveredDevices.setModel(defaultListModel1);
        Devices.add(discoveredDevices, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 150), null, 0, false));
        getdevicesbtn = new JButton();
        getdevicesbtn.setText("Get Devices");
        Devices.add(getdevicesbtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(96, 36), null, 0, false));
        AcceptSelectedDeviceButton = new JButton();
        AcceptSelectedDeviceButton.setText("Accept");
        Devices.add(AcceptSelectedDeviceButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TestInputs = new JPanel();
        TestInputs.setLayout(new GridLayoutManager(9, 4, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(TestInputs, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, true));
        TestInputs.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Inputs", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        deviceField = new JTextField();
        deviceField.setEditable(false);
        deviceField.setText("");
        TestInputs.add(deviceField, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        testField = new JComboBox();
        testField.setEditable(false);
        testField.setEnabled(true);
        TestInputs.add(testField, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        testFieldLabel = new JLabel();
        testFieldLabel.setText("Test");
        testFieldLabel.setVerticalAlignment(0);
        TestInputs.add(testFieldLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deviceFieldLabel = new JLabel();
        deviceFieldLabel.setHorizontalAlignment(0);
        deviceFieldLabel.setText("Device");
        TestInputs.add(deviceFieldLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        smartphoneBtn = new JRadioButton();
        smartphoneBtn.setText("Smartphone");
        TestInputs.add(smartphoneBtn, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 21), null, 0, false));
        featurephoneBtn = new JRadioButton();
        featurephoneBtn.setText("Featurephone");
        TestInputs.add(featurephoneBtn, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Iterations = new JSpinner();
        TestInputs.add(Iterations, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 20), null, 0, false));
        IterationsLabel = new JLabel();
        IterationsLabel.setText("Iterations");
        TestInputs.add(IterationsLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mdnField = new JTextField();
        mdnField.setEnabled(true);
        mdnField.setText("0000000000");
        TestInputs.add(mdnField, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mdnFieldLabel = new JLabel();
        mdnFieldLabel.setText("MDN");
        TestInputs.add(mdnFieldLabel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        standardRadioButton = new JRadioButton();
        standardRadioButton.setText("Standard");
        TestInputs.add(standardRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 5), null, 0, false));
        LMRRadioRadioButton = new JRadioButton();
        LMRRadioRadioButton.setText("LMR Radio");
        TestInputs.add(LMRRadioRadioButton, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        DeviceLabel = new JLabel();
        DeviceLabel.setText("Device Type");
        TestInputs.add(DeviceLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ClientLabel = new JLabel();
        ClientLabel.setText("Client Type");
        TestInputs.add(ClientLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        runButton = new JButton();
        runButton.setEnabled(false);
        runButton.setText("Run");
        TestInputs.add(runButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(125, 30), new Dimension(125, 30), null, 0, false));
        cancelButton = new JButton();
        cancelButton.setEnabled(false);
        cancelButton.setText("Cancel");
        TestInputs.add(cancelButton, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delaySpinner = new JSpinner();
        TestInputs.add(delaySpinner, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delaySpinnerLabel = new JLabel();
        delaySpinnerLabel.setText("Delay(ms)");
        TestInputs.add(delaySpinnerLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorLabel = new JLabel();
        errorLabel.setEnabled(true);
        Font errorLabelFont = this.$$$getFont$$$("Arial", -1, 14, errorLabel.getFont());
        if (errorLabelFont != null) errorLabel.setFont(errorLabelFont);
        errorLabel.setForeground(new Color(-61428));
        errorLabel.setHorizontalAlignment(0);
        errorLabel.setText("");
        TestInputs.add(errorLabel, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        carrierSelect = new JComboBox();
        TestInputs.add(carrierSelect, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        carrierSelectLabel = new JLabel();
        carrierSelectLabel.setText("Carrier");
        TestInputs.add(carrierSelectLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Progress = new JPanel();
        Progress.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(Progress, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), null, new Dimension(-1, 50), 0, false));
        Progress.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Outcome", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        progressBar = new JProgressBar();
        progressBar.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        Progress.add(progressBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        LogPaneJpanel = new JPanel();
        LogPaneJpanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(LogPaneJpanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 200), null, new Dimension(-1, 200), 0, true));
        LogPaneJpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Log", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, LogPaneJpanel.getFont()), null));
        LogPane = new JScrollPane();
        LogPane.putClientProperty("html.disable", Boolean.FALSE);
        LogPaneJpanel.add(LogPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 165), null, new Dimension(-1, 165), 0, false));
        LogPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        logText = new JTextArea();
        logText.setEditable(true);
        logText.setLineWrap(true);
        logText.setWrapStyleWord(true);
        LogPane.setViewportView(logText);
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        testFieldLabel.setLabelFor(testField);
        deviceFieldLabel.setLabelFor(deviceField);
        IterationsLabel.setLabelFor(Iterations);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}


