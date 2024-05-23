package UI.Controllers;

import UI.InputFileReaderAndWriter;
import UI.MainForm;
import UI.Models.MainModel;
import UI.Views.MainView;
import api.Android;
import core.*;
import core.constants.TestNGInfo;
import core.managers.*;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * The Main Controller controls the overarching processes that enable tests to run as well as sharing information to
 * the user.
 * @author Hayden Brehm
 */
public class MainController implements PropertyChangeListener {
    public static boolean DEBUG = false;   // all lines of code marked as temp will execute if this boolean is true.
    private final SwingPropertyChangeSupport propertyChangeSupport;
    private final MainView mainView;
    private final MainModel mainModel;

    public static TestManager testManager;
    private final MainForm mainForm;
    private boolean isCustomTest;
    private TestNG testNG;
    private Worker worker;
    Object testThread;
    private static int attempts = 0;

    /**
     * Constructor for MainController
     * @param mainView the view
     * @param mainModel the model
     * @param mainForm the mainForm that contains the formatting of the UI elements
     * @author Hayden Brehm
     */
    public MainController(MainView mainView, MainModel mainModel, MainForm mainForm){
        this.mainView = mainView;
        this.mainModel = mainModel;
        this.mainForm = mainForm;
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        initializeController();

        // the lines of code below is to enable or disable DEBUG mode depending on whether or not
        // this is running as a standalone jar file or from Intellij
        String urlStr = MainController.class.getResource("MainController.class").toString();
        DEBUG = !urlStr.startsWith("jar");
        MyLogger.log.debug("urlStr: " + urlStr + " | DEBUG = " + Boolean.toString(DEBUG));


        // start up servers and set shutdown hook
        DriverManager.checkForAppiumProcesses();
        DriverManager.checkForMemInfoProcesses();
        DriverManager.checkForScrcpyProcesses();
        DriverManager.startGetDevices();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MyLogger.log.debug("Shutting down all devices...");
            DriverManager.shutdownAllDevices();
        }));
    }

    /**
     * Initializes the controller by setting up some initial settings.
     * PropertyChangeListeners are added for the model as well as controller.
     * ProgressBar is also defaulted to invisible.
     */
    private void initializeController(){
        // Adding to both model and controller
        mainModel.addPropertyChangeListener(mainView);
        propertyChangeSupport.addPropertyChangeListener(mainView);
        mainModel.setProgressBarVisible(false);

        MyLogger.log.debug("Main Controller Initialized!");
    }

    /**
     * Listens for property changes from other Controllers and acts based on the event sent.
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     * @author Hayden Brehm
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();
        switch (propertyName){
            case "Ready":
                readInputConfig();
                beginTest();
                break;
            case "Cancel":
                cancelTest();
                break;
            case "Set Config":
                setConfig((TestManager) newValue);
                break;
            case "Finished":
                resetProgressBar();
                break;
            case "Log":
                log((String) newValue);
                break;
            case "Service Record Found":
                serviceRecord((String) newValue);
                log("Service Record Found!");
                break;
            case "Connected":
                if((Boolean) newValue)
                    log("Device Connected!");
                else log("Device could not connect! Try again.");
                break;
            case "Set Device Name":
                propertyChangeSupport.firePropertyChange("Set Device Name", null, newValue);
                break;
        }
    }

    private void readInputConfig(){
        TestManager.DeviceType deviceType;
        TestManager.Type type;
        InputFileReaderAndWriter inputFileReaderAndWriter = new InputFileReaderAndWriter();
        HashMap<String, String> hashMap = inputFileReaderAndWriter.getHashMap();

        try {
            if (Integer.parseInt(hashMap.get(InputFileReaderAndWriter.inputs.Smartphone.toString())) > 0) //number format exception
                deviceType = TestManager.DeviceType.SmartPhone;
            else deviceType = TestManager.DeviceType.FeaturePhone;

            if (Integer.parseInt(hashMap.get(InputFileReaderAndWriter.inputs.Standard.toString())) > 0)
                type = TestManager.Type.Handset;
            else type = TestManager.Type.Radio;

            if (hashMap.get(InputFileReaderAndWriter.inputs.MDN.toString()).equals("0000000000")){
                hashMap.put(InputFileReaderAndWriter.inputs.MDN.toString(), "0");
            }

            TestManager testManager = new TestManager.TestManagerBuilder
                    (Integer.parseInt(hashMap.get(InputFileReaderAndWriter.inputs.Iterations.toString())),
                            type, deviceType)
                    .setPhoneNumber(Long.parseLong(hashMap.get(InputFileReaderAndWriter.inputs.MDN.toString())))
                    .setDelay(Integer.parseInt(hashMap.get(InputFileReaderAndWriter.inputs.Delay.toString())))
                    .setMainController(this)
                    .setTestName(hashMap.get(InputFileReaderAndWriter.inputs.Test.toString()))
                    .setDeviceName(hashMap.get(InputFileReaderAndWriter.inputs.Device.toString()))
                    .setPTXMessage(hashMap.get(InputFileReaderAndWriter.inputs.PTXMessage.toString()))
                    .setBuildMessage(hashMap.get(InputFileReaderAndWriter.inputs.BuildMessage.toString()))
                    .setBuildIPA(hashMap.get(InputFileReaderAndWriter.inputs.BuildIPA.toString()))
                    .setBuildLocation(hashMap.get(InputFileReaderAndWriter.inputs.BuildLocation.toString()))
                    .setBuildVoiceRecord(hashMap.get(InputFileReaderAndWriter.inputs.BuildVoiceRecord.toString()))
                    .setBuildPicture(hashMap.get(InputFileReaderAndWriter.inputs.BuildPicture.toString()))
                    .setBuildFile(hashMap.get(InputFileReaderAndWriter.inputs.BuildFile.toString()))
                    .setLegacyExecution(hashMap.get(InputFileReaderAndWriter.inputs.LegacyExecution.toString()))
                    .setCustomTests(hashMap.get(InputFileReaderAndWriter.inputs.CustomTests.toString()))
                    .build();

            setConfig(testManager);
        }catch (NumberFormatException ex){
            MyLogger.log.debug("NumberFormatException! ----> {}", ex.getMessage());
            ex.printStackTrace();
        }catch (Exception e){
            MyLogger.log.debug("Exception! ----> {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up the remaining required settings to run a test, and executes the test on a new SwingWorker thread to
     * ensure that since we will be updating UI elements (e.g. the log window and progress bar) on the EDT
     * (Event Dispatch Thread) to comply with Swing documentation.
     * @author Hayden Brehm
     */
    private void beginTest(){
        log("Setting up test");
        TestListenerAdapter tla = new TestListenerAdapter();
        List<String> testName = Lists.newArrayList();
        worker = new Worker();

        DriverManager.checkForMemInfoProcesses();
        DriverManager.checkForScrcpyProcesses();

        /*
         * If using only the dropdown test selection instead of specifying the tests to run
         */
        if(MainController.testManager.getCustomTests().equals("0")){
            isCustomTest = false;
            testName.add(testManager.getTestName());
            //temp
            List<String> suites = Lists.newArrayList();
            if (DEBUG) {
                File file = new File("src/Main/resources/testng.xml");
                suites.add(file.toString());
            }
            testNG = new TestNG();
            testNG.addListener(tla);

            //keep
            if (!DEBUG) {
                testNG.setTestJar("AppiumAutomatioUtility-1.0-SNAPSHOT.jar");
                testNG.setXmlPathInJar("testng.xml");
            }
            //temp
            if (DEBUG) {
                testNG.setTestSuites(suites);
            }
            testNG.setTestNames(testName);
            TestManager.customTestIterations.put(testManager.getTestName(), "1"); //for dataprovider (makes customtest easier)
        }else{
            isCustomTest = true;
            String str = MainController.testManager.getCustomTests();
            ArrayList<String> arrayList = new ArrayList<>();

            /*
             * Checking that the string input in inputConfig.xml is a real input
             */
            for (String s : str.split(";")){
                for (int i = 0; i < TestNGInfo.testList.size(); i++){
                    if (s.contains(TestNGInfo.testList.get(i))){
                        MyLogger.log.debug("String -> {} Added ", s);
                        arrayList.add(s);
                    }
                }

                if(arrayList.isEmpty()) {
                    //MainController.testManager.getMainController()
                    //        .log("Incorrect test name was input into inputConfig.xml");
                    log("Incorrect test name was input into inputConfig.xml");
                    throw new RuntimeException("Incorrect Test Name Added");
                }
            }

            /*
             * Making Virtual XML file to include the tests.
             */
            XmlSuite suite = new XmlSuite();
            suite.setName("CustomSuite");

            /*
             * Iterates over the range of entries in arrayList and duplicates if Iteration is > 1. This is a roundabout way
             * to run the tests the user sets multiple times.
             */
            for (int i = 0; i < MainController.testManager.getIterations(); i++) {
                for (String s : arrayList) {
                    List<XmlClass> classes = new ArrayList<XmlClass>(); //new empty class for the new test
                    XmlTest test = new XmlTest(suite); // new test
                    test.setName(s + "-" + i);    // new test gets a name
                    classes.add(new XmlClass("tests.BasicPTT." + s));   // adding the test to execute into the class arraylist
                    test.setXmlClasses(classes); // setting the tests that can be executed under this test
                }
            }

            MyLogger.log.debug("Iteration List: {}", InputFileReaderAndWriter.customIterations);
            ArrayList<String> iterList
                    = new ArrayList<>(Arrays.asList(InputFileReaderAndWriter.customIterations.split(";"))); //List of iterations

            /*
             * Hashmap "put" for each iteration associated with each custom test
             */
            for (int i = 0; i < iterList.size(); i++){
                if (iterList.get(i) != null) {
                    if(Integer.parseInt(iterList.get(i)) >= 1) {
                        MyLogger.log.debug("Adding hashmap entry [{}, {}]", arrayList.get(i), iterList.get(i));
                        TestManager.customTestIterations.put(arrayList.get(i), iterList.get(i));
                    } else TestManager.customTestIterations.put(arrayList.get(i), "1");
                } else TestManager.customTestIterations.put(arrayList.get(i), "1");
            }

            //Below needs to be tested
            testNG = new TestNG();
            testNG.addListener(tla);

            if (!DEBUG) {
                testNG.setTestJar("AppiumAutomatioUtility-1.0-SNAPSHOT.jar");
            }

            List<XmlSuite> suites = new ArrayList<>();
            suites.add(suite);
            testNG.setXmlSuites(suites);
        }

        mainModel.setProgressBarVisible(true);
        worker.execute();
    }

    private void setConfig(TestManager testManager){
        MainController.testManager = testManager;
    }

    /**
     * Cancels the currently running test, and it closes any BT connections established while the test was running.
     * @author Hayden Brehm
     */
    public void cancelTest(){
        /*if (client.getStreamConnection() != null)
            client.closeConnection();*/
        getWorker().cancel(true);
        log("Test canceled!");
        propertyChangeSupport.firePropertyChange("Running", null, false);
    }

    /*public void connectToDeviceAndInit() {
        BTClient.getUrls().clear();

        if (BTClient.streamConnection != null)
            client.closeConnection();

        try {
            client.selectDevice();
        } catch (IOException ex) {
            MyLogger.log.debug("IOException! Could not select device! ----> {}", ex.getMessage());
            throw new RuntimeException(ex);
        }

        try {
            if (BTClient.getUrls().isEmpty()) {
                throw new Exception();
            } else {
                //propertyChangeSupport.firePropertyChange("Log", null, "Service Record Found!");
                log("Service Record Found!");

                //propertyChangeSupport.firePropertyChange
                //        ("Log", null, "Trying To Open Connection!");
                log("Trying To Open Connection!");

                // Connection + opening streams
                client.connect();
                Thread.sleep(1000);
                try {
                    BTClient.outStream = BTClient.streamConnection.openOutputStream();
                    BTClient.inStream = BTClient.streamConnection.openInputStream();
                } catch (IOException ex) {
                    MyLogger.log.debug("IOException occurred! ----> {}", ex.getMessage());
                    ex.printStackTrace();
                }
                Thread.sleep(1000);

                Events initialize;
                initialize = new Events.EventsBuilder("100", "2", "1", "1").
                        setAccessoryType("3").setModelName("EEI-Tool").
                        setFeatureCapabilities("34359732223").setFeaturesRequested("34225586174").build();

                initialize.sendEvent(BTClient.outStream);
                Thread.sleep(100);

                if (initialize.readEvent(BTClient.inStream).equals(""))
                    throw new Exception("Initialization ACK not received. Retrying.");

                //checking if properly connected
                if (BTClient.streamConnection != null){
                    log("Device Successfully Connected & Initialized!");
                }
                else log("Device was unable to properly connect!"); //will probably never be called
            }
        }catch (Exception ex) {
            client.closeConnection();
            if (attempts < 2) {
                attempts++;
                connectToDeviceAndInit();
            } else {
                attempts = 0;
                //propertyChangeSupport.firePropertyChange("Log", null,"Could not find service!");
                log("Could not find service!");
                MyLogger.log.debug("URL IS EMPTY!");
                cancelTest();
            }
        }
    }*/

    /*public void initializeBluetooth(){
        Events initialize;
        initialize = new Events.EventsBuilder("100", "2", "1", "1").
                setAccessoryType("3").setModelName("EEI-Tool").
                setFeatureCapabilities("34359732223").setFeaturesRequested("34225586174").build();

        try{
            initialize.sendEvent(BTClient.outStream);
            Thread.sleep(100);
            initialize.readEvent(BTClient.inStream);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }*/

    /**
     * Fires the PropertyChange "Progressbar complete" to MainView
     * @author Hayden Brehm
     */
    public void completeProgressBar(){
        propertyChangeSupport.firePropertyChange("Progressbar complete", null, null);
    }

    /**
     * Fires the PropertyChange "Reset Progressbar" to MainView
     * @author Hayden Brehm
     */
    public void resetProgressBar(){
        propertyChangeSupport.firePropertyChange("Reset Progressbar", null, null);
    }

    /**
     * Fires the PropertyChange "Set Device Name" to MainView
     * @param name the name to be set by the MainView
     * @author Hayden Brehm
     */
    private void serviceRecord(String name){
        propertyChangeSupport.firePropertyChange("Set Device Name", null, name);
    }

    /**
     * Gets the current worker.
     * @return the current worker
     * @author Hayden Brehm
     */
    public Worker getWorker(){
        return worker;
    }

    public boolean getIsCustomTest(){return isCustomTest;}

    /**
     * Fires the PropertyChange "Progressbar Setup" to MainView
     * @author Hayden Brehm
     */
    public void progressBarSetUp(){
        propertyChangeSupport.firePropertyChange("Progressbar Setup", null, null);
    }

    /**
     * Finds if progressPerTask has been set.
     * @return True if the value is not the default 0d. False otherwise.
     * @author Hayden Brehm
     */
    private boolean isProgressPerTaskSet() {
        return mainModel.getProgressPerTask() != 0d;
    }

    /**
     * Sets the amount of progress each task with increase the progress bar.
     * @param iterations how many iterations the test will run
     * @param totalTasks how many tasks there are in each iteration
     * @author Hayden Brehm
     */
    public void setProgressPerTask(int iterations, int totalTasks) {
        double result = (1d / (totalTasks * (double) iterations)) * 100d;
        mainModel.setProgressPerTask(result);
    }

    /**
     * Increases the Progress bar. Fires the PropertyChange "Progressbar Increment" to MainView with the new amount.
     * Since the progress bar must increase by an integer we only update if the new amount (cast to int) is greater
     * than the old amount.
     * @throws RuntimeException if {@link MainController#setProgressPerTask(int, int)} was not set previously.
     * @author Hayden Brehm
     */
    public void taskComplete() {
        int result;
        try{
            if (isProgressPerTaskSet()) {
                result = (int) (mainModel.getCurrentProgress() + mainModel.getProgressPerTask());
                mainModel.setCurrentProgress(mainModel.getCurrentProgress() + mainModel.getProgressPerTask());
                MyLogger.log.debug("Current Progress is: {}%", mainModel.getCurrentProgress());
                result = Math.max(result, mainView.getMainForm().getProgressBar().getValue());
                propertyChangeSupport.firePropertyChange("Progressbar Increment", null, result);
            } else throw new RuntimeException("No Progress per task was set!");
        }catch (RuntimeException ex){
            MyLogger.log.debug("RuntimeException! ----> {}", ex.getMessage());
        }
    }

    /**
     * Fires the PropertyChange "Log" to MainView along with the timestamp of the log. This will also output to
     * MyLogger.
     * @param str the String to add to the log window
     * @author Hayden Brehm, Victor Dang
     */
    public void log(String str)
    {
        String timestamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        propertyChangeSupport.firePropertyChange("Log", null, "[" + timestamp + "] " + str);
        MyLogger.log.debug(str);
    }


    /**
     * Worker extends SwingWorker to execute the Test on the EDT
     * @author Hayden Brehm
     */
    public class Worker extends SwingWorker<Void,Void>{
        /**
         * Runs the Test in the background on the EDT
         * @return null
         * @throws Exception if the Test fails.
         * @author Hayden Brehm
         */
        @Override
        protected Void doInBackground() throws Exception {
            try{
                Thread.currentThread().setName("TestWorkerThread-" + Thread.currentThread().getId());
                testThread = Thread.currentThread().getName();
                MyLogger.log.debug("MainController starting main test thread named: {}",
                        Thread.currentThread().getName());
                propertyChangeSupport.firePropertyChange("Running", null, true);

                DriverManager.stopGetDevices();
                DriverManager.updateAppiumServers(true); // called here to shut down servers that are not in use
                DriverManager.createDrivers(); // create drivers after server are completely initialized
                DriverManager.checkForMemInfoProcesses(); // end all lingering memInfo processes
                PythonScriptExecution.startMemInfoTimer();

                MyLogger.log.debug("Running TestNG-{}", testNG.getDefaultSuiteName());
                testNG.run();
            }catch (Exception e){
                MyLogger.log.debug("Test failed!");
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Executes after doInBackground() has completed.
         * Fires PropertyChange "Running" to false and "Finished" to true in MainView
         * @author Hayden Brehm
         */
        @Override
        protected void done() {
            mainModel.getInputModel().cancelState(false);

            if(!worker.isCancelled())
                log("Completed Test!");
            else
                log("Test could not be completed!");

            PythonScriptExecution.endMemInfoTimer();

            for (Android device : DriverManager.getAllAndroidDevices()) {
                String deviceID = device.getDeviceID();
                PythonScriptExecution.dataBreakdownScript(deviceID);
            }
            //PythonScriptExecution.dataBreakdownScript();

            DriverManager.killAllDrivers();
            DriverManager.startGetDevices();

            propertyChangeSupport.firePropertyChange("Running", null, false);
            propertyChangeSupport.firePropertyChange("Finished", null, true);
            setProgressPerTask(0,0);
            mainModel.setCurrentProgress(0);
            mainModel.setProgressBarVisible(false);
            resetProgressBar();
        }
    }
}
