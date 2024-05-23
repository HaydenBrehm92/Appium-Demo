package core.managers;

import UI.Controllers.MainController;
import core.JsonHelpers.JsonToTestData;

import java.util.HashMap;


public class TestManager {
    // TODO: Need to change TestManager around JsonToTestData
    public static JsonToTestData jsonToTestData;
    private final MainController mainController;
    private final String testName;
    private final String deviceName;
    private final int iterations;
    private final long phoneNum;
    private final int delay;
    private final Type clientType;
    private final DeviceType deviceType;
    private final String PTXMessage;
    private final String BuildMessage;
    private final String BuildIPA;
    private final String BuildLocation;
    private final String BuildVoiceRecord;
    private final String BuildPicture;
    private final String BuildFile;
    private final String LegacyExecution;
    private final String CustomTests;
    private int currentIteration;
    private boolean bluetoothInitialized;
    public static final HashMap<String,String> customTestIterations = new HashMap<>();

    public boolean isBluetoothInitialized() {
        return bluetoothInitialized;
    }

    public void setBluetoothInitialized(boolean bluetoothInitialized) {
        this.bluetoothInitialized = bluetoothInitialized;
    }

    public enum DeviceType{
        SmartPhone,
        FeaturePhone,
    }
    public enum Type{
        Handset,
        Radio,
    }

    private TestManager(TestManagerBuilder testManagerBuilder){
        this.iterations = testManagerBuilder.iterations;
        this.mainController = testManagerBuilder.mainController;
        this.clientType = testManagerBuilder.clientType;
        this.deviceType = testManagerBuilder.deviceType;
        this.delay = testManagerBuilder.delay;
        this.phoneNum = testManagerBuilder.phoneNum;
        this.testName = testManagerBuilder.testName;
        this.deviceName = testManagerBuilder.deviceName;
        this.PTXMessage = testManagerBuilder.PTXMessage;
        this.BuildMessage = testManagerBuilder.BuildMessage;
        this.BuildIPA = testManagerBuilder.BuildIPA;
        this.BuildLocation = testManagerBuilder.BuildLocation;
        this.BuildVoiceRecord = testManagerBuilder.BuildVoiceRecord;
        this.BuildPicture = testManagerBuilder.BuildPicture;
        this.BuildFile = testManagerBuilder.BuildFile;
        this.LegacyExecution = testManagerBuilder.LegacyExecution;
        this.CustomTests = testManagerBuilder.CustomTests;
        this.currentIteration = 0;
    }

    public boolean isSmartPhone(){
        return this.deviceType.equals(DeviceType.SmartPhone);
    }

    public boolean isFeaturePhone(){
        return this.deviceType.equals(DeviceType.FeaturePhone);
    }

    public boolean isHandset(){
        return this.clientType.equals(Type.Handset);
    }

    public boolean isRadio(){
        return this.clientType.equals(Type.Radio);
    }

    public boolean isLegacyExecution(){ return this.LegacyExecution.equals("1");}

    public boolean isBuildMessageEnabled(){
        return MainController.testManager.getBuildMessage().equals("1");
    }
    public boolean isBuildIPAEnabled(){
        return MainController.testManager.getBuildIPA().equals("1");
    }
    public boolean isBuildLocationEnabled(){
        return MainController.testManager.getBuildLocation().equals("1");
    }
    public boolean isBuildVoiceRecordEnabled(){
        return MainController.testManager.getBuildVoiceRecord().equals("1");
    }
    public boolean isBuildPictureEnabled(){
        return MainController.testManager.getBuildPicture().equals("1");
    }
    public boolean isBuildFileEnabled(){
        return MainController.testManager.getBuildFile().equals("1");
    }

    public MainController getMainController() {
        return mainController;
    }

    public int getIterations() {
        return iterations;
    }

    public long getPhoneNum() {
        return phoneNum;
    }

    public int getDelay() {
        return delay;
    }

    public Type getClientType() {
        return clientType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getTestName() {return testName;}

    public String getDeviceName() {return deviceName;}

    public void updateCurrentIteration(){
        currentIteration++;
    }

    public int getCurrentIteration(){return currentIteration;}

    public void resetCurrentIteration(){currentIteration = 0;}

    public String getPTXMessage() {return PTXMessage;}
    public String getBuildMessage() {return BuildMessage;}

    public String getBuildIPA() {return BuildIPA;}

    public String getBuildLocation() {return BuildLocation;}

    public String getBuildVoiceRecord() {return BuildVoiceRecord;}

    public String getBuildPicture() {return BuildPicture;}

    public String getBuildFile() {return BuildFile;}

    public String getLegacyExecution() {return LegacyExecution;}

    public String getCustomTests() {return CustomTests;}

    public static class TestManagerBuilder {
        public MainController mainController;
        private String testName;
        private String deviceName;
        public int iterations;
        public long phoneNum;
        public int delay;
        public Type clientType;
        public DeviceType deviceType;
        public String PTXMessage;
        public String BuildMessage;
        public String BuildIPA;
        public String BuildLocation;
        public String BuildVoiceRecord;
        public String BuildPicture;
        public String BuildFile;
        public String LegacyExecution;
        public String CustomTests;

        public TestManagerBuilder(int iterations, Type clientType, DeviceType deviceType){
            this.iterations = iterations;
            this.clientType = clientType;
            this.deviceType = deviceType;
        }

        public TestManagerBuilder setMainController(MainController mainController){
            this.mainController = mainController;
            return this;
        }

        public TestManagerBuilder setPhoneNumber(long phoneNum){
            this.phoneNum = phoneNum;
            return this;
        }

        public TestManagerBuilder setDelay(int delay){
            this.delay = delay;
            return this;
        }

        public TestManagerBuilder setTestName(String testName){
            this.testName = testName;
            return this;
        }

        public TestManagerBuilder setDeviceName(String deviceName){
            this.deviceName = deviceName;
            return this;
        }

        public TestManagerBuilder setPTXMessage(String PTXMessage){
            this.PTXMessage = PTXMessage;
            return this;
        }

        public TestManagerBuilder setBuildMessage(String BuildMessage){
            this.BuildMessage = BuildMessage;
            return this;
        }

        public TestManagerBuilder setBuildIPA(String BuildIPA){
            this.BuildIPA = BuildIPA;
            return this;
        }

        public TestManagerBuilder setBuildLocation(String BuildLocation){
            this.BuildLocation = BuildLocation;
            return this;
        }

        public TestManagerBuilder setBuildVoiceRecord(String BuildVoiceRecord){
            this.BuildVoiceRecord = BuildVoiceRecord;
            return this;
        }

        public TestManagerBuilder setBuildPicture(String BuildPicture){
            this.BuildPicture = BuildPicture;
            return this;
        }

        public TestManagerBuilder setBuildFile(String BuildFile){
            this.BuildFile = BuildFile;
            return this;
        }

        public TestManagerBuilder setLegacyExecution(String LegacyExecution){
            this.LegacyExecution = LegacyExecution;
            return this;
        }

        public TestManagerBuilder setCustomTests(String CustomTests){
            this.CustomTests = CustomTests;
            return this;
        }

        public TestManager build(){return new TestManager(this);}
    }

    public static class ptxManager {
        private boolean IPA = true;

        public ptxManager(){}

        public void setIPA(boolean IPA) {
            this.IPA = IPA;
        }

        public boolean getIPA(){return IPA;}
    }
}
