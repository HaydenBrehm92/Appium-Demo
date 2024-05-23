package UI;

import core.MyLogger;
import core.managers.FileManager;
import core.managers.TestManager;
import org.testng.annotations.Test;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * This class reads/writes to the inputs.txt file and updates it as needed.
 * @author Hayden Brehm
 */
public class InputFileReaderAndWriter {
    String path;
    private String contents;
    private HashMap<String, String> hashMap;
    public String getContents() {
        return contents;
    }
    public static String customIterations;

    public enum inputs {
        Device,
        Carrier,
        Test,
        MDN,
        Iterations,
        Delay,
        Smartphone,
        Featurephone,
        Standard,
        Radio,
        PTXMessage,
        BuildMessage,
        BuildIPA,
        BuildLocation,
        BuildVoiceRecord,
        BuildPicture,
        BuildFile,
        LegacyExecution,
        CustomTests
    }

    /**
     * Constructor
     */
    public InputFileReaderAndWriter(){
        hashMap = new HashMap<>();
        try{
            path = FileManager.getFormattedParentPath(InputFileReaderAndWriter.class.getProtectionDomain().
                    getCodeSource().getLocation().getPath()) + "/" + FileManager.inputConfigFile;
            MyLogger.log.debug("Path: {}", path);

            if (!FileManager.checkIfExists(path))
            {
                initializeDefaults();
                //readFile();
                MyLogger.log.info("File Created! ----> " + FileManager.inputConfigFile);
            }
            else
            {
                MyLogger.log.info("File exists! Checking if file upgrade is needed...");
                checkAndUpgradeXMLFile();
            }

            readFile();
        }catch (Exception ex){
            MyLogger.log.debug("Exception! ----> {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void initializeDefaults()
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("config");
            doc.appendChild(rootElement);

            for (inputs inputField : inputs.values())
            {
                AddNewElement(doc, rootElement, inputField.toString());
            }

            // write the content into xml file
            WriteToXMLFile(doc);

            // Output to console for testing
            //StreamResult consoleResult = new StreamResult(System.out);
            //transformer.transform(source, consoleResult);
        }
        catch (Exception e)
        {
            MyLogger.log.debug("Exception Occurred With XML Generation! ----> {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the inputConfig.xml has all of the required fields for execution.
     * Since this file is user-editable, the entire file cannot be overwritten. This function will instead
     * check for any headers that are missing and add defaults values into the file.
     * @author Victor Dang
     */
    public void checkAndUpgradeXMLFile()
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(path);

            boolean fileUpdated = false; // flag to notify if the XML file was updated
            inputs[] inputsEnum = inputs.values();

            Element root = document.getDocumentElement();
            NodeList list = root.getChildNodes();

            // outer for loop looping through all the enum headers
            for (int i = 0; i < inputsEnum.length; ++i)
            {
                boolean inputEnumExist = false;
                MyLogger.log.debug("Checking if input enum " + inputsEnum[i].toString() + " exists...");

                // inner for loop looping through the headers that already exist in the XML file, comparing
                // against the inputs enum values.
                // brute force method of implementing this, may have to find a better solution down the line
                for (int j = 0; j < list.getLength(); ++j)
                {
                    // only checking for node element, using guard-clause syntax to make code neater
                    Node node = list.item(j);
                    if (node.getNodeType() != Element.ELEMENT_NODE)
                        continue;

                    Element element = (Element) node;
                    String type = element.getAttribute("Type");
                    //MyLogger.log.debug("Checking " + type);

                    // current input enum does exist so break out of the inner for loop
                    if (type.trim().equals(inputsEnum[i].toString()))
                    {
                        MyLogger.log.debug(inputsEnum[i].toString() + " exists");
                        inputEnumExist = true;
                        break;
                    }
                }

                // if the input enum header doesn't exist, add it into the XML file.
                if (!inputEnumExist)
                {
                    MyLogger.log.debug(inputsEnum[i].toString() + " does not exist, adding new to XML file...");
                    AddNewElement(document, root, inputsEnum[i].toString());
                    fileUpdated = true;
                }
            }

            if (fileUpdated)
                WriteToXMLFile(document);
        }
        catch(Exception ex)
        {
            MyLogger.log.debug("IOException! ----> {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void readFile()
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(path);

            NodeList list = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < list.getLength(); ++i)
            {
                Node node = list.item(i);
                if (node.getNodeType() != Element.ELEMENT_NODE)
                    continue;

                Element element = (Element) node;
                String type = element.getAttribute("Type");
                String value = element.getElementsByTagName("Value").item(0).getTextContent();
                hashMap.put(type, value);

                if(type.equals(inputs.CustomTests.toString()))
                    customIterations = element.getElementsByTagName("Iterations").item(0).getTextContent();  //Finding Iterations

            }
        }
        catch(Exception ex)
        {
            MyLogger.log.debug("IOException! ----> {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void updateInputToXMLFile(String type, String value)
    {
        if (value.isEmpty())
            value = " ";

        try
        {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new File(path));

            NodeList list = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                if (node.getNodeType() != Element.ELEMENT_NODE)
                    continue;

                Element element = (Element) node;
                if (element.getAttribute("Type").equals(type))
                    element.getElementsByTagName("Value").item(0).setTextContent(value);

                hashMap.put(type, value);
            }

            WriteToXMLFile(doc);
        }
        catch (Exception e)
        {
            MyLogger.log.debug("Exception Occurred! ----> {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getHashMap() { return hashMap; }

    /**
     * Adds a new element to the provided XML document file.
     * @param document the XML file to update
     * @param root the root of the XML file
     * @param attribute the new attribute/value to add to the XML file
     * @author Victor Dang
     */
    void AddNewElement(Document document, Element root, String attribute)
    {
        // input element
        Element input = document.createElement("input");
        root.appendChild(input);

        // setting attribute to element
        Attr attr = document.createAttribute("Type");
        attr.setValue(attribute);
        input.setAttributeNode(attr);

        // value element
        Element value = document.createElement("Value");
        value.appendChild(document.createTextNode("0"));
        input.appendChild(value);

        if(attribute.equals(inputs.CustomTests.toString())){
            Element iteration = document.createElement("Iterations");
            iteration.appendChild(document.createTextNode("0"));
            input.appendChild(iteration);
        }

        MyLogger.log.debug("Element " + attribute + " added!");
    }

    /**
     * Saves the XML document to the hard drive as an .xml file.
     * @param document the document to save as an XML file
     * @author Victor Dang
     */
    void WriteToXMLFile(Document document)
    {
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);

            MyLogger.log.debug("Successfully written to XML file!");
        }
        catch (Exception ex)
        {
            MyLogger.log.debug("Issue with writing to XML file!");
            ex.printStackTrace();
        }
    }

    @Test
    public void test()
    {
        //String str = this.readFile();
        //MyLogger.log.info("String reads: {}", str);
        //this.updateInputs(str, "Iterations", "15");
        this.initializeDefaults();
    }
}
