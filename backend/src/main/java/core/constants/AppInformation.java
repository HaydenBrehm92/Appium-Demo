package core.constants;

import core.MyLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AppInformation
{
    // Contains the info about the package to use.
    private static PackageInfo packageToUse;

    // contains all the supported apps
    private static final HashMap<String, PackageInfo> apps = new HashMap<>();


    /**
     * Static initializer for this class. Reads in the XML file and populates the
     * map accordingly. If there are any updates to the XML file, then this program
     * will need to be restarted in order to reflect the changes.
     *
     * If we are making this a non-static class, then this can become the constructor.
     * @author Victor Dang
     */
    static
    {
        try
        {
            InputStream xmlPath;
            xmlPath = AppInformation.class.getClassLoader().getResourceAsStream("appConfig.xml");    //JAR resource loading
            if (xmlPath == null)
                xmlPath = AppInformation.class.getResourceAsStream("/appConfig.xml");    //IDE debugging loading

            apps.clear(); // just in case...
            MyLogger.log.debug("Looking for XML file in {}", xmlPath);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(xmlPath);  // parsing the inputstream of the xml

            NodeList list = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < list.getLength(); ++i)
            {
                Node node = list.item(i);

                if (node.getNodeType() == Element.ELEMENT_NODE)
                {
                    Element element = (Element) node;

                    String id = node.getAttributes().getNamedItem("ID").getNodeValue();
                    String appName = getElementByTag(element, "appName");
                    String packageName = getElementByTag(element, "packageName");
                    String engCode = getElementByTag(element, "engCode");
                    String copyLogs = getElementByTag(element, "logCopy");

                    // making the key the same string as the string that will be added to the combo box so that
                    // it can be easily found whenever the user makes a selection from the combo box
                    apps.put(appName, new PackageInfo(id, appName, packageName, engCode, copyLogs));
                    MyLogger.log.debug("App found from XML: {}", id);
                    //MyLogger.log.debug("XML Values: id = {} | appName = {} | packageName = {} | engCode = {}", id, appName, packageName, engCode);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets all the supported apps.
     * @return Returns a Collection of supported apps.
     */
    public static Collection<PackageInfo> getSupportedPackages() { return apps.values(); }

    /**
     * Gets all the supported carriers.
     * @return Returns a Collection of supported carriers.
     * @author Victor Dang
     */
    public static Collection<String> getSupportedCarriers() { return apps.keySet(); }

    /**
     * Gets the selected package name.
     * @return the String unlock package name
     * Returns the selected package name.
     * @author Victor Dang
     */
    public static String getUnlockPackage()
    {
        return (packageToUse == null) ? null : packageToUse.packageName;
    }

    /**
     * Gets the engineering code for the selected package.
     * @return the String engineering code
     * Returns the engineering code for the selected package.
     * @author Victor Dang
     */
    public static String getEngineeringCode()
    {
        return (packageToUse == null) ? null : packageToUse.engCode;
    }

    public static String getCopyLogCode(){ return (packageToUse == null) ? null : packageToUse.copyLogs;}

    /**
     * Is the currently selected app a Verizon app?
     * @return true if the selected app is VZW false otherwise. Note: null packages will return false as well.
     */
    public static boolean isVerizonPackage() { return packageToUse.equals(apps.get("Verizon")); }

    /**
     * Is the currently selected app an ATT app?
     * @return true if the selected app is ATT false otherwise. Note: null packages will return false as well.
     */
    public static boolean isATTPackage()
    {
        return packageToUse.equals(apps.get("ATT"));
    }

    /**
     * Is the currently selected app a Wave app?
     * @return true if the selected app is Wave false otherwise. Note: null packages will return false as well.
     */
    public static boolean isWavePackage()
    {
        return packageToUse.equals(apps.get("Wave"));
    }

    /**
     * Is the currently selected app a T-Mobile app?
     * @return true if the selected app is T-Mobile false otherwise. Note: null packages will return false as well.
     */
    public static boolean isSprintPackage() { return packageToUse.equals(apps.get("T-Mobile")); }

    /**
     * Is the currently selected app a Bell app?
     * @return true if the selected app is Bell false otherwise. Note: null packages will return false as well.
     */
    public static boolean isBellPackage()
    {
        return packageToUse.equals(apps.get("Bell"));
    }


    /**
     * Sets the app to use, this should be called from that combo box that has the app name choices.
     * Don't pass the package name to this method! This will be the method to use once the updated
     * UI design has been implemented.
     * @param appName i.e. "AT&T", see appConfig.xml for the different app names.
     * @return Returns true if the default app was set successfully, false if no supported app with the
     * specified appName was found.
     * @author Victor Dang
     */
    public static boolean setAppToUse(String appName)
    {
        if (apps.containsKey(appName))
        {
            packageToUse = apps.get(appName);
            MyLogger.log.debug("Setting PTT app to use: {}", packageToUse.id);
            //MyLogger.log.debug("Setting PTT app to use: {}, {}, {}", appName, unlockPackage, engineeringCode);

            return true;
        }

        MyLogger.log.debug("Invalid app name: {}!", appName);
        return false;
    }

    /**
     * Adds the app names from the HashMap to the specified combo box.
     * @param box The JComboBox where the user will select the desired app from.
     * @deprecated Deprecated in favor of the new UI
     * @author Victor Dang
     */
    public static void addAppsToComboBox(JComboBox box) // TEMPORARY, Remove after new UI implementation
    {
        for (PackageInfo p : apps.values())
        {
            box.addItem(p.appName);
        }
        // set the app at index 0 to be the default app on the combo box
        box.setSelectedIndex(0);
        setAppToUse(box.getItemAt(0).toString());
    }

    /**
     * Checks all the packages available on the device to see if there are any that matches the
     * package chosen by the user. If the unlockPackage was not set beforehand, this method will
     * return false.
     * @param packageNames the list of packages in the device, obtained through ADB
     * @return Returns true if any of the supported PTT apps is installed in the device.
     * @author Victor Dang
     */
    public static boolean checkForSelectedPackage(ArrayList<String> packageNames)
    {
        if (packageToUse != null && packageToUse.isValid())
        {
            boolean result = packageNames.contains(packageToUse.packageName);
            MyLogger.log.debug("Does list contains {}? {}", packageToUse.packageName, Boolean.toString(result));
            return result;
        }
        else
        {
            MyLogger.log.debug("unlockPackage has not yet been set!");
            return false;
        }
    }

    /**
     * Used internally to simplify grabbing XML elements, because try typing out that line of code over and over.
     * @param element
     * @param tag
     * @return Returns the content of the element with the specified tag.
     * @author Victor Dang
     */
    private static String getElementByTag(Element element, String tag)
    {
        return element.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();
    }
}
