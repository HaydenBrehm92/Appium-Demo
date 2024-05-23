package tests.Data;
import core.MyLogger;
import core.managers.TestManager;
import org.testng.annotations.DataProvider;

public class DataProviders {
    @DataProvider(name = "RecentCallIterations")
    public static Object[][] Calls(){
        String s = TestManager.customTestIterations.get("RecentCallTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][]{{Integer.parseInt(s)}};
    }

    @DataProvider(name = "PtxMessagesIterations")
    public static Object[][] Messages(){
        String s = TestManager.customTestIterations.get("PtxMessagesTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "EmergencyIterations")
    public static Object[][] Emergency(){
        String s = TestManager.customTestIterations.get("EmergencyTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "LoginIterations")
    public static Object[][] Login(){
        String s = TestManager.customTestIterations.get("LoginTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "HistoryLoadIterations")
    public static Object[][] HistoryLoad(){
        String s = TestManager.customTestIterations.get("HistoryLoadTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "AddContactIterations")
    public static Object[][] AddContact(){
        String s = TestManager.customTestIterations.get("AddContactTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "EEIEventsIterations")
    public static Object[][] EEIEvents(){
        String s = TestManager.customTestIterations.get("EEIEventsTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }

    @DataProvider(name = "TemplateIterations")
    public static Object[][] Template(){
        String s = TestManager.customTestIterations.get("TemplateTest");
        if (s == null)
            s = "1";
        MyLogger.log.debug("CustomTestIteration found to be {}", s);
        return new Object[][] {{Integer.parseInt(s)}};
    }
}
