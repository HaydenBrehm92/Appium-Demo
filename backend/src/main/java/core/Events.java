package core;
import UI.Controllers.MainController;

import java.io.*;

/**
 * Fluent builder implementation of every possible EEI event that can be made to the BT KAP Server.
 * To implement, follow the following behavior in "code." Always use .build() to build the event you wish to send.
 * Documentation for PTT Accessory Specifications can be found
 * <a href="https://docs.google.com/document/d/17FClV5QmdTn0HOv55z-pajrd1p2TAkh0/edit?usp=sharing&ouid=101235156829639340209&rtpof=true&sd=true">here</a>
 * <pre>
 * {@code Events init = new Events.EventsBuilder("100","2","1","1").
 *                     setAccessoryType("3").setModelName("ACCESSORY-VER-1.0.0").
 *                     setFeatureCapabilities("34359732223").setFeaturesRequested("34225586174").build();
 * }</pre>
 * @author Hayden Brehm
 */
public class Events {
    private StringBuilder stringBuilder;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final String eventType;
    private final String versionIdentifier;
    private final String sessionIdentifier;
    private final String transactionIdentifier;
    private final String accessoryType;
    private final String modelName;
    private final String featureCapabilities;
    private final String featuresRequested;
    private final String index;
    private final String uriList;
    private final String clientVersion;
    private final String callType;
    private final String callSessionIdentifier;
    private final String multiFunctionEntry;
    private final String randomAccess;
    private final String keyIndex;
    private final String osmListCount;
    private final String osmStatusCode;
    private final String osmStatusMessageType;
    private final String osmShortDescription;
    private final String osmLongDescription;
    private final String ptxTextMessage;
    private final String timeStamp;
    private final String loginAndLogoutFlags;
    private final String incomingVideoPullType;
    private final String zoneIndex;
    private final String channelIndex;
    private final String scanMode;
    private final String externalEntity;
    private final String callCategory;
    private final String groupName;
    private final String emergencyCancelReason;
    private final String screenValue;

    /**
     * Events Constructor that assigns the corresponding fields set by the EventsBuilder
     * @param eventsBuilder the EventsBuilder instance
     * @author Hayden Brehm
     */
    private Events(EventsBuilder eventsBuilder){
        this.eventType = eventsBuilder.eventType;
        this.versionIdentifier = eventsBuilder.versionIdentifier;
        this.sessionIdentifier = eventsBuilder.sessionIdentifier;
        this.transactionIdentifier = eventsBuilder.transactionIdentifier;
        this.accessoryType = eventsBuilder.accessoryType;
        this.modelName = eventsBuilder.modelName;
        this.featureCapabilities = eventsBuilder.featureCapabilities;
        this.featuresRequested = eventsBuilder.featuresRequested;
        this.index = eventsBuilder.index;
        this.uriList = eventsBuilder.uriList;
        this.clientVersion = eventsBuilder.clientVersion;
        this.callType = eventsBuilder.callType;
        this.callSessionIdentifier = eventsBuilder.callSessionIdentifier;
        this.multiFunctionEntry = eventsBuilder.multiFunctionEntry;
        this.randomAccess = eventsBuilder.randomAccess;
        this.keyIndex = eventsBuilder.keyIndex;
        this.osmListCount = eventsBuilder.osmListCount;
        this.osmStatusCode = eventsBuilder.osmStatusCode;
        this.osmStatusMessageType = eventsBuilder.osmStatusMessageType;
        this.osmShortDescription = eventsBuilder.osmShortDescription;
        this.osmLongDescription = eventsBuilder.osmLongDescription;
        this.ptxTextMessage = eventsBuilder.ptxTextMessage;
        this.timeStamp = eventsBuilder.timeStamp;
        this.loginAndLogoutFlags = eventsBuilder.loginAndLogoutFlags;
        this.incomingVideoPullType = eventsBuilder.incomingVideoPullType;
        this.zoneIndex = eventsBuilder.zoneIndex;
        this.channelIndex = eventsBuilder.channelIndex;
        this.scanMode = eventsBuilder.scanMode;
        this.externalEntity = eventsBuilder.externalEntity;
        this.callCategory = eventsBuilder.callCategory;
        this.groupName = eventsBuilder.groupName;
        this.emergencyCancelReason = eventsBuilder.emergencyCancelReason;
        this.screenValue = eventsBuilder.screenValue;
    }
    public String getEventType() {
        return eventType;
    }
    public String getVersionIdentifier() {
        return versionIdentifier;
    }
    public String getSessionIdentifier() {
        return sessionIdentifier;
    }
    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }
    public String getAccessoryType() {
        return accessoryType;
    }
    public String getModelName() {
        return modelName;
    }
    public String getFeatureCapabilities() {
        return featureCapabilities;
    }
    public String getFeaturesRequested() {
        return featuresRequested;
    }
    public String getIndex() {
        return index;
    }
    public String getUriList() {
        return uriList;
    }
    public String getClientVersion() {
        return clientVersion;
    }
    public String getCallType() {
        return callType;
    }
    public String getCallSessionIdentifier() {
        return callSessionIdentifier;
    }
    public String getMultiFunctionEntry() {
        return multiFunctionEntry;
    }
    public String getRandomAccess() {
        return randomAccess;
    }
    public String getKeyIndex() {
        return keyIndex;
    }
    public String getOsmListCount() {
        return osmListCount;
    }
    public String getOsmStatusCode() {
        return osmStatusCode;
    }
    public String getOsmStatusMessageType() {
        return osmStatusMessageType;
    }
    public String getOsmShortDescription() {
        return osmShortDescription;
    }
    public String getOsmLongDescription() {
        return osmLongDescription;
    }
    public String getPtxTextMessage() {
        return ptxTextMessage;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public String getLoginAndLogoutFlags() {
        return loginAndLogoutFlags;
    }
    public String getIncomingVideoPullType() {
        return incomingVideoPullType;
    }
    public String getZoneIndex() {
        return zoneIndex;
    }
    public String getChannelIndex() {
        return channelIndex;
    }
    public String getScanMode() {
        return scanMode;
    }
    public String getExternalEntity() {
        return externalEntity;
    }
    public String getCallCategory() {
        return callCategory;
    }
    public String getGroupName() {
        return groupName;
    }
    public String getEmergencyCancelReason() {
        return emergencyCancelReason;
    }
    public String getScreenValue(){
        return screenValue;
    }

    /**
     * Sends the built event to the BT Server to be read. Must send INIT Event before any other event can be sent.
     * @param outputStream the stream used by the BufferedWriter that writes to the BT KAP Server.
     * @author Hayden Brehm
     */
    public void sendEvent(OutputStream outputStream) {
        StringBuilder result = new StringBuilder();
        String[] values = {getEventType(), getVersionIdentifier(), getSessionIdentifier(), getTransactionIdentifier(),
            getAccessoryType(), getModelName(), getFeatureCapabilities(), getFeaturesRequested(), getIndex(), getUriList(),
            getClientVersion(), getCallType(), getCallSessionIdentifier(), getMultiFunctionEntry(), getRandomAccess(),
            getKeyIndex(), getOsmListCount(), getOsmStatusCode(), getOsmStatusMessageType(), getOsmShortDescription(),
            getOsmLongDescription(), getPtxTextMessage(), getTimeStamp(), getLoginAndLogoutFlags(), getIncomingVideoPullType(),
            getZoneIndex(), getChannelIndex(), getScanMode(), getExternalEntity(), getCallCategory(), getGroupName(),
            getEmergencyCancelReason(), getScreenValue()};

        for (String value : values) {
            result.append(value == null ? "" : value);
        }

        String message = result + "\r";
        MyLogger.log.debug("Sending: {}", message);

        // Percent Encoding for spaces
        /*try{
            message = URLEncoder.encode(message, "UTF-8").replace(" ", "%20");
        }catch (UnsupportedEncodingException e){
            MyLogger.log.debug("Encoding Exception Occured! ----> {}", e.getMessage());
            e.printStackTrace();
        }*/

        try{
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(message);
            bufferedWriter.flush();
        }catch (IOException e){
            MyLogger.log.debug("Write exception! ----> {}", e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("10053")){
                MainController.testManager.getMainController().log("Connection went bad! Canceling Test");
                MainController.testManager.getMainController().cancelTest();    //Maybe logic to retry connection
            }
        }
    }

    /**
     * Reads the incoming events from the BT KAP Server. They come in as EA events.
     * @param inputStream the stream used by the BufferedReader that reads event acks from the BT KAP Server.
     * @author Hayden Brehm
     */
    public String readEvent(InputStream inputStream) {
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Thread.sleep(100);
            stringBuilder = new StringBuilder();
            String line;
            while (bufferedReader.ready()){
                line = bufferedReader.readLine() + "\n";
                stringBuilder.append(line);
            }
            // NEED TO READ ALL LINES
            MyLogger.log.debug("Response: {}", stringBuilder.toString());
        }catch (Exception e){
            MyLogger.log.debug("Read exception! ----> {}", e.getMessage());
        }
        return stringBuilder.toString();
    }

    /**
     * Closes both BufferedReader and BufferedWriter. Exception occurs if it fails.
     * @author Hayden Brehm
     */
    public void closeReadandWrite(){
        try{
            bufferedReader.close();
            bufferedWriter.close();
        }catch (IOException ex){
            MyLogger.log.debug("Failed to close read/write! --> {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    /**
     * Static class that sets and builds the Event to be used. Uses the builder design.
     * @author Hayden Brehm
     */
    public static class EventsBuilder{
        private final String eventType;
        private final String versionIdentifier;
        private final String sessionIdentifier;
        private final String transactionIdentifier;
        private String accessoryType;
        private String modelName;
        private String featureCapabilities;
        private String featuresRequested;
        private String index;
        private String uriList;
        private String clientVersion;
        private String callType;
        private String callSessionIdentifier;
        private String multiFunctionEntry;
        private String randomAccess;
        private String keyIndex;
        private String osmListCount;
        private String osmStatusCode;
        private String osmStatusMessageType;
        private String osmShortDescription;
        private String osmLongDescription;
        private String ptxTextMessage;
        private String timeStamp;
        private String loginAndLogoutFlags;
        private String incomingVideoPullType;
        private String zoneIndex;
        private String channelIndex;
        private String scanMode;
        private String externalEntity;
        private String callCategory;
        private String groupName;
        private String emergencyCancelReason;
        private String screenValue;

        /**
         * EventsBuilder Constructor that sets the mandatory parameters excluding Transaction Identifier.
         * @param eventType The Event Type (e.g. 100, 101, etc.)
         * @param versionIdentifier The Version Identifier.
         * @param sessionIdentifier The Session Identifier.
         * @param transactionIdentifier The Transaction Identifier.
         * @author Hayden Brehm
         */
        public EventsBuilder(String eventType, String versionIdentifier, String sessionIdentifier, String transactionIdentifier){
            this.eventType = "ET=" + eventType + ";";
            this.versionIdentifier = "VI=" + versionIdentifier + ";";
            this.sessionIdentifier = "SI=" + sessionIdentifier + ";";
            this.transactionIdentifier = "TI=" + transactionIdentifier + ";";
        }

        /**
         * Sets the Accessory Type.
         * @param accessoryType the type of accessory.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setAccessoryType(String accessoryType){
            this.accessoryType = "AT=" + accessoryType + ";";
            return this;
        }

        /**
         * Sets the Model Name.
         * @param modelName the name of the Model.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setModelName(String modelName){
            this.modelName = "MN=" + modelName + ";";
            return this;
        }

        /**
         * Sets the Feature Capabilities to be used by the bluetooth device.
         * This payload indicates the set of capabilities the entity has for
         * various functionalities.
         * @param featureCapabilities the feature capabilities to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setFeatureCapabilities(String featureCapabilities){
            this.featureCapabilities = "FC=" + featureCapabilities + ";";
            return this;
        }

        /**
         * Sets the Features Requested by the bluetooth device.
         * This payload indicates the set of features (events) the entity
         * (accessory and 3rd party app) is interested in from the Kodiak PTT app.
         * @param featuresRequested the features requested to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setFeaturesRequested(String featuresRequested){
            this.featuresRequested = "FR=" + featuresRequested + ";";
            return this;
        }

        /**
         * Sets the index value to be used for the ET.
         * This payload is used in various requests
         * (CALL SETUP, SEND IPA, VOLUME UP/DOWN etc.) messages.
         * For CALL and IPA events, this payload identifies the index (position)
         * of contact or group on the device.
         * For VOLUME events, this payload values are restricted between
         * 0 to 9 (0, 1, 2, ...9), and it conveys the level of volume set.
         * @param index the index to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setIndex(String index){
            this.index = "IN=" + index + ";";
            return this;
        }

        /**
         * This payload is used in various events from PTT App to accessory/3rd Party App
         * and vice versa (for e.g. CALL SETUP, INCOMING CALL, INCOMING IPA, SEND IPA,
         * SEND PTX MESSAGE, SEND OSM, SEND OSM LIST, CHANGE SELECTED TALKGROUP etc.).
         * <br>Note: Current release does not support 1-Many call type
         * @param uriList the URI to be set. This field can contain either MDN(s) or group URI.
         * <br><br>
         * Examples:<br>Group URI: UR=tel:9835681_corp-123 <br>MDN: UR=9254682137
         * <br>MDN: UR=+919254682137 <br>MDN: UR=tel:+91925468213
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setURIList(String uriList){
            this.uriList = "UR=" + uriList + ";";
            return this;
        }

        /**
         * This payload is used in response to the INIT message.
         * This identifies the version details of Kodiak PTT application (device).
         * Not used for ET events sent to server currently.
         * @param clientVersion the client version to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setClientVersion(String clientVersion){
            this.clientVersion = "CV=" + clientVersion + ";";
            return this;
        }

        /**
         * This payload is used in a request (call/IPA) message, from entity and
         * Kodiak PTT app both. This identifies the type of call and PTX message.
         * @param callType the call type to be set.
         * <br><br> For Example: <br> 0 = 1-1 PTT <br> 1 = Group PTT <br> 2 = Ad-Hoc
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setCallType(String callType){
            this.callType = "CT=" + callType + ";";
            return this;
        }

        /**
         *This payload is used by Kodiak PTT to an accessory or a 3rd party app to send
         * the status (success/failure) of outgoing call requests (PTT, Video) made from
         * accessory/3rd party Apps. This field is to be percent-encoded.
         * @param callSessionIdentifier the session identifier to be set.
         * <br>Examples (CS):<br>ET=214;VI=2;SI=1;TI=591;CS=tel:9835681_corp-123;\r
         * <br>ET=235;VI=2;SI=1;TI=591;CS=recipient not available;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setCallSessionIdentifier(String callSessionIdentifier){
            this.callSessionIdentifier = "CS=" + callSessionIdentifier + ";";
            return this;
        }

        /**
         * This payload is used in a request (PTT KEY HOT FUNCTION ASSIGNMENT KEY) message.
         * This payload is mandatory.
         * @param multiFunctionEntry multi-function entry to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setMultiFunctionEntry(String multiFunctionEntry){
            this.multiFunctionEntry = "EN=" + multiFunctionEntry + ";";
            return this;
        }

        /**
         * This payload is used in a request (INIT) message.
         * This variable indicates the maximum number of random access keys supported by RSM.
         * This payload is optional.
         * @param randomAccess the random access keys to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setRandomAccess(String randomAccess){
            this.randomAccess = "RA=" + randomAccess + ";";
            return this;
        }

        /**
         * This payload is sent with programmable key events (120, 121).
         * This data indicates the particular key index/Id on accessory and shall
         * be used by PTT app to process accordingly, for e.g. send Status Message
         * from list based on Key Index received.
         * @param keyIndex the key index to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setKeyIndex(String keyIndex){
            this.keyIndex = "KI=" + keyIndex + ";";
            return this;
        }

        /**
         * This payload is sent with the ‘Count Of OSM’ event. It's sent from the PTT
         * app to the accessory/3rd party app.
         * @param osmListCount the OSM list count to be set.
         * <br> Example (LC): <br> ET=233;VI=2;SI=1;TI=1;UR=tel:9835681_corp-123;LC=5;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setOsmListCount(String osmListCount) {
            this.osmListCount = "LC=" + osmListCount + ";";
            return this;
        }

        /**
         * This payload is sent with ‘List Of OSM’ and ‘Send OSM’ events/messages.
         * It's sent from the PTT app to the accessory/3rd party app and vice versa.
         * @param osmStatusCode the OSM status code to be set.
         * <br> Example (OC): <br>ET=142;VI=2;SI=1;TI=1;UR=tel:9835681_corp-123;OC=123;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setOsmStatusCode(String osmStatusCode) {
            this.osmStatusCode = "OC=" + osmStatusCode + ";";
            return this;
        }

        /**
         * This payload is sent with a ‘List Of OSM’ event/message.
         * It's sent from the PTT app to the accessory/3rd party app.
         * @param osmStatusMessageType the OSM status message type to be set.
         * Editable (1) or non-editable (0).
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setOsmStatusMessageType(String osmStatusMessageType) {
            this.osmStatusMessageType = "OT=" + osmStatusMessageType + ";";
            return this;
        }

        /**
         * This payload is sent with the ‘List Of OSM’ event. It's sent from the PTT app
         * to the accessory/3rd party app. This is a String field. This field is to be
         * percent-encoded.
         * @param osmShortDescription the OSM short description to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setOsmShortDescription(String osmShortDescription) {
            this.osmShortDescription = "SD=" + osmShortDescription + ";";
            return this;
        }

        /**
         * This payload is sent with the ‘List Of OSM’ event. It's sent from the PTT app
         * to the accessory/3rd party app. This field is to be
         * percent-encoded.
         * @param osmLongDescription the OSM long description to be set.
         * <br> Example (LD): <br>ET=234;VI=2;SI=1;TI=1;UR=tel:9835681_corp-123;OC=123,OT=0;SD=asap;LD=are you
         * coming;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setOsmLongDescription(String osmLongDescription) {
            this.osmLongDescription = "LD=" + osmLongDescription + ";";
            return this;
        }

        /**
         * This payload is sent with the "Send/Notify PTX Text Message" event. It's
         * sent from the PTT app to the accessory/3rd party app and vice versa.
         * 3rd Party Application MUST make sure to follow rules for PTX text message
         * length and format before sending. This field is to be percent-encoded. <br>
         * Note: The current release supports 2000 UTF-8 characters as PTX text messages. Pls check PTT
         * application spec for latest details
         * @param ptxTextMessage the PTX message to be set.
         * <br> Example (PM): ET=139;VI=2;SI=1;TI=1;IN=1;UR=tel:9835681_corp-123;PM=Hello;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setPtxTextMessage(String ptxTextMessage) {
            this.ptxTextMessage = "PM=" + ptxTextMessage + ";";
            return this;
        }

        /**
         * This payload format is UTC.
         * This particular payload usage is not yet decided and hence not supported at present and this needs to be
         * defined at a later date.
         * @param timeStamp timestamp to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setTimeStamp(String timeStamp) {
            this.timeStamp = "TS=" + timeStamp + ";";
            return this;
        }

        /**
         * This payload is sent with Login and Logout events from 3rd Party Applications to Kodiak PTT.
         * This particular payload is not yet supported and hence the format needs to be defined at later date.
         * @param loginAndLogoutFlags the login and logout flags to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setLoginAndLogoutFlags(String loginAndLogoutFlags) {
            this.loginAndLogoutFlags = "LF=" + loginAndLogoutFlags + ";";
            return this;
        }

        /**
         * This payload is sent with incoming video pull request notification events to 3rd Party
         * Applications by Kodiak PTT.
         * @param incomingVideoPullType the incoming video pull type to be set.
         * <br> 0: Unconfirmed Pull <br> 1: Confirmed Pull
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setIncomingVideoPullType(String incomingVideoPullType) {
            this.incomingVideoPullType = "PT=" + incomingVideoPullType + ";";
            return this;
        }

        /**
         *This payload is sent with the change selected talkgroup request event from Accessory or 3rd Party
         * Applications to Kodiak PTT.
         * It contains index number of zone.
         * @param zoneIndex the zone index to be set.
         * <br>Example (ZI)<br>ET=151;VI=2;SI=1;TI=1;ZI=1;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setZoneIndex(String zoneIndex) {
            this.zoneIndex = "ZI=" + zoneIndex + ";";
            return this;
        }

        /**
         * This payload is sent with the change selected talkgroup request event from Accessory or 3rd Party
         * Applications to Kodiak PTT.
         * It contains index number of channel.
         * @param channelIndex the channel index to be set.
         * <br>Example (CI)<br>ET=151;VI=2;SI=1;TI=1;ZI=1;CI=3;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setChannelIndex(String channelIndex) {
            this.channelIndex = "CI=" + channelIndex + ";";
            return this;
        }

        /**
         * This payload is sent with the change scan mode request event from Accessory or 3rd Party Applications to
         * Kodiak PTT app. It contains the value of scan mode to be changed.
         * At present, it supports binary values only, 1 means scan mode On and 0 means scan mode Off.
         * @param scanMode the scane mode to be set.
         * <br>Example (SM)<br>ET=152;VI=2;SI=1;TI=1;SM=1;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setScanMode(String scanMode) {
            this.scanMode = "SM=" + scanMode + ";";
            return this;
        }

        /**
         * This payload is sent with every event sent from Kodiak PTT app to Accessory or 3rd Party Applications.
         * It contains the type of external entity that Kodiak PTT app is communicating with.
         * This payload is just used for identification of external entities and does not contain any data,
         * and hence it's suggested that external entities receiving this payload should just ignore this.
         * <br> Its enumerated integer value as following:<br> BT = 0; USB-C = 1, APP2APP = 2
         * @param externalEntity the external entity to be set.
         * <br>Example (EE)<br>ET=152;VI=2;SI=1;TI=1;EE=0;\r
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setExternalEntity(String externalEntity) {
            this.externalEntity = "EE=" + externalEntity + ";";
            return this;
        }

        /**
         * This payload is used in a request (Call) message from the entity to Kodiak PTT app and similar notification
         * events (Call) from Kodiak PTT app to entity.
         * This identifies the type of call (PTT, Video, SFD etc.). This payload is supported from PTT release 11.x
         * @param callCategory the call category to be set.
         * <br>Example<br>0 = PTT Call<br>1 = SFD Call<br>2 = Video Call<br>3 = Video Pull<br>4 = Ambient Call-TU<br>
         * 5 = Ambient Call-AU<br>6 = Discreet Call-AU
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setCallCategory(String callCategory) {
            this.callCategory = "CC=" + callCategory + ";";
            return this;
        }

        /**
         * This payload is used in the ‘SELECTED TALKGROUP NOTIFICATION’ event from the Kodiak PTT app.
         * This field is a string type field and to be percent-encoded. This field contains a group name.
         * @param groupName the group name to be set.
         * <br>Example (GN):<br> GN=maintenance group
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setGroupName(String groupName) {
            this.groupName = "GN=" + groupName + ";";
            return this;
        }

        /**
         * This payload is used in the ‘CANCEL EMERGENCY’ event (157) from the 3rd party app to Kodiak PTT
         * app. This field contains a group name.<br>Its enumerated integer values are as following:<br>
         * Valid = 0; False = 1
         * @param emergencyCancelReason the emergency cancel reason to be set.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setEmergencyCancelReason(String emergencyCancelReason) {
            this.emergencyCancelReason = "ER=" + emergencyCancelReason + ";";
            return this;
        }

        /**
         * This payload is used to send the screen value we wish to navigate to. It uses integer values to determine
         * the screen.
         * @param screenValue the value corresponding to the screen we wish to move to.
         * @return the current object instance.
         * @author Hayden Brehm
         */
        public EventsBuilder setScreenValue(String screenValue){
            this.screenValue = "SV=" + screenValue + ";";
            return this;
        }

        /**
         * Calls the Events constructor by passing the current EventsBuilder instance. The events constructor
         * unpacks the fields set by EventsBuilder and assigns them to their corresponding fields in Events.
         * @return a new Events object
         * @author Hayden Brehm
         */
        public Events build(){return new Events(this);}
    }
}
