/*
package tests.BasicPTT;

import core.Events;
import org.testng.annotations.Test;
import tests.BaseTest;
import tests.Data.DataProviders;

*/
/**
 * @deprecated EEI no longer in use
 *//*

@Deprecated
public class EEIEventsTest extends BaseTest {

    @Test(dataProvider = "EEIEventsIterations", dataProviderClass = DataProviders.class)
    public void Events(int iteration){
        setDataProviderIterationVal(iteration); // These iterations are not really needed. This is an internal test
        Events events;

       // Log Copy
        events = new Events.EventsBuilder("301","1","1","1")
                .setCallType("6").setURIList("1").build();
        sendAndRead(events);
        waitSimulation(10000);

        // KPI Enable
        events = new Events.EventsBuilder("301","1","1","1")
                .setCallType("7").setURIList("1").build();
        sendAndRead(events);
        waitSimulation(10000);

        // KPI Disable
        events = new Events.EventsBuilder("301","1","1","1")
                .setCallType("7").setURIList("0").build();
        sendAndRead(events);
        waitSimulation(10000);

        // CATO Enabled
        events = new Events.EventsBuilder("301","1","1","1")
                .setCallType("8").setURIList("1").build();
        sendAndRead(events);
        waitSimulation(10000);

        // CATO Disabled
        events = new Events.EventsBuilder("301","1","1","1")
                .setCallType("8").setURIList("0").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Console Logs Enabled
        events = new Events.EventsBuilder("312","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Console Logs Disabled
        events = new Events.EventsBuilder("313","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Web Content Debug Enabled
        events = new Events.EventsBuilder("314","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Web Content Debug Disabled
        events = new Events.EventsBuilder("315","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to PTT Settings
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("0").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to History
        // This is causing app to go to background
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("1").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Favorites (Contact)
        // This is causing app to go to background
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("2").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Favorites (Groups)
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("3").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Contacts
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("4").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Groups
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("5").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Map
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("6").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Hamburger Menu (opened on default screen)
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("7").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Navigate to Profile Change
        events = new Events.EventsBuilder("302","1","1","1")
                .setScreenValue("8").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Profile Select (selecting first non-default profile)
        events = new Events.EventsBuilder("311","1","1","1")
                .setPtxTextMessage("1").build();
        sendAndRead(events);
        waitSimulation(10000);

        // Hide Offline
        events = new Events.EventsBuilder("303","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Show Offline
        events = new Events.EventsBuilder("304","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Background Mode Enabled
        events = new Events.EventsBuilder("305","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Background Mode Disabled
        events = new Events.EventsBuilder("306","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Enable Use Wi-Fi Setting
        events = new Events.EventsBuilder("307","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Disable Use Wi-Fi Setting
        events = new Events.EventsBuilder("308","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Enable Tones Setting
        events = new Events.EventsBuilder("309","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);

        // Disable Tones Setting
        events = new Events.EventsBuilder("310","1","1","1")
                .build();
        sendAndRead(events);
        waitSimulation(10000);
    }

    @Override
    public void resetToDefaultLocation() {

    }

    @Override
    public void recover() {

    }

    @Override
    public void testLoop() {

    }
}
*/
