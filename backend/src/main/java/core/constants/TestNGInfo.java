package core.constants;

import core.MyLogger;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;

public class TestNGInfo {
    public static final ArrayList<String> testList = new ArrayList<>();

    static
    {
        try
        {
            InputStream xmlPath;
            xmlPath = AppInformation.class.getClassLoader().getResourceAsStream("testng.xml");    //JAR resource loading
            if (xmlPath == null)
                xmlPath = AppInformation.class.getResourceAsStream("/testng.xml");    //IDE debugging loading

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
                    String testName = node.getAttributes().getNamedItem("name").getNodeValue();
                    testList.add(testName);
                    MyLogger.log.debug("Test found from XML: {}", testName);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addTestsToJComboBox(JComboBox jComboBox){
        for (String test : testList)
            jComboBox.addItem(test);
        jComboBox.setSelectedIndex(0);
    }

    @Test
    public void test(){
        //Testing that static correctly gathers testnames
        TestNGInfo testInfo = new TestNGInfo();
    }
}


