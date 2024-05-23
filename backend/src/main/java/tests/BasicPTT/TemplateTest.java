package tests.BasicPTT;

import org.testng.annotations.Test;
import tests.BaseTest;
import tests.Data.DataProviders;

public class TemplateTest extends BaseTest {
    /*
     * Use page_libraries to use objects to interact with elements on the screen. For example,
     * Callscreen call = new Callscreen();
     * call.endcall().tap();
     *
     * This will tap the endcall element if it is currently displayed.
     * Use waitSimulation(insert num here) to usually allow the screen to load / emulate a person pressing items on the
     * screen.
     */

    @Test(dataProvider = "TemplateIterations", dataProviderClass = DataProviders.class)
    public void Test(int iteration){
        setDataProviderIterationVal(iteration);
        // Insert steps from default location (Contact Screen for std and Call Screen for radio) until you reach the
        // main loop of your test.
    }

    @Override
    public void resetToDefaultLocation() {
        // Logic to reset to the default location. Used mainly to recover the test from some unforeseen blockage.
    }

    @Override
    public void recover() {
        // Logic for recovery from popups/network down events
    }

    @Override
    public void testLoop() {
        // The main loop of your test to iterate over.
    }
}
