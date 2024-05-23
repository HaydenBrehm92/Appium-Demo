package core.JsonHelpers;

/**
 * This class is used to deserialize TestInput data from UI into a Java object.
 * @author Hayden Brehm
 */
public class JsonToTestData {
    private String deviceName;
    private String branding;
    private String testcaseId;
    private String iterationCombination;
    private String delayInmilliSec;
    private String getdeviceType;
    private String getclientType;

    /**
     * Gets the delay to be used.
     * @return delay.
     * @author Hayden Brehm
     */
    public String getDelayInmilliSec() {
        return (delayInmilliSec != null) ? delayInmilliSec : "";
    }

    /**
     * Gets the test case to be used.
     * @return test case.
     * @author Hayden Brehm
     */
    public String getTestcaseId() {
        return (testcaseId != null) ? testcaseId : "";
    }

    /**
     * Gets the client type to be used.
     * @return client type. Radio or Standard.
     * @author Hayden Brehm
     */
    public String getGetclientType() {
        return (getclientType != null) ? getclientType : "";
    }

    /**
     * Gets the iteration combination to be used.
     * @return iteration combination.
     * @author Hayden Brehm
     */
    public String getIterationCombination() {
        return (iterationCombination != null) ? iterationCombination : "";
    }

    /**
     * Gets the device's name to be used.
     * @return the device's name.
     * @author Hayden Brehm
     */
    public String getDeviceName() {
        return (deviceName != null) ? deviceName : "";
    }

    /**
     * Gets the branding to be used.
     * @return the branding.
     * @author Hayden Brehm
     */
    public String getBranding() {
        return (branding != null) ? branding : "";
    }

    /**
     * Gets the device type to be used.
     * @return device type.
     * @author Hayden Brehm
     */
    public String getGetdeviceType() {
        return (getdeviceType != null) ? getdeviceType : "";
    }
}
