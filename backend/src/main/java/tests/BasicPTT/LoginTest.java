package tests.BasicPTT;

import UI.Controllers.MainController;
import core.Events;
import core.MyLogger;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.Activation;
import page_libraries.Common;
import page_libraries.Hamburger;
import page_libraries.Settings;
import tests.BaseTest;
import tests.Data.DataProviders;


public class LoginTest extends BaseTest {

    @Test(dataProvider = "LoginIterations", dataProviderClass = DataProviders.class)
    public void Login(int iteration){
        setDataProviderIterationVal(iteration);
        testLoop();
    }
    @Override
    public void resetToDefaultLocation() {

    }

    @Override
    public void recover() {

    }

    @Override
    public void testLoop() {
        for(int i = MainController.testManager.getCurrentIteration();
            i < getIterations(); i++){

            testExecution();

            //driverManager.activateApp(DeviceInformation.getUnlockPackage());
            MainController.testManager.getMainController().
                    log("Login");

            try{
                if (getUIObject(Common.Yes).elementExists())
                    getUIObject(Common.Yes).tap();
                //if(driver.findElement(By.xpath("//*[contains(@text, \"Yes\")]")).isDisplayed())
                //    driver.findElement(By.xpath("//*[contains(@text, \"Yes\")]")).click();
            }catch (NoSuchElementException e){
                MyLogger.log.debug("Login Prompt not found. Cancelling Test...");
                MainController.testManager.getMainController().cancelTest();
                return;
            }

            try{
                //if(driver.findElement(By.xpath("//*[contains(@text, \"AGREE\")]")).isDisplayed())
                //    driver.findElement(By.xpath("//*[contains(@text, \"AGREE\")]")).click();
                if (getUIObject(Activation.Agree).elementExists())
                    getUIObject(Activation.Agree).tap();
            }catch (NoSuchElementException e){
                MyLogger.log.debug("Monitoring Conversation Popup Not Found. Continuing...");
            }

            //If we see hamburger menu then it has successfully logged in
            if (getUIObject(Hamburger.Menu).elementExists())
                MainController.testManager.updateCurrentIteration();

            MainController.testManager.getMainController().
                    log("Completed Logout --> Login #" + (i + 1));

            /*if(!MainController.testManager.isLegacyExecution() &&
                    i != (MainController.testManager.getIterations() - 1)) {
                MainController.testManager.getMainController().
                        log("Reestablishing Bluetooth Connection & Initialization");
                MainController.testManager.getMainController().connectToDeviceAndInit();
            }*/

        }
        MainController.testManager.resetCurrentIteration();  // reset for CustomTest. Does nothing meaningful else wise
        Assert.assertEquals
                (MainController.testManager.getCurrentIteration(), MainController.testManager.getIterations());
    }

    private void testExecution(){
        getUIObject(Hamburger.Menu).tap();
        getUIObject(Hamburger.Settings).waitUntilClickable(5000).tap();
        getUIObject(Settings.Logout).swipeFind();
        getUIObject(Settings.Logout).tap();
        MainController.testManager.getMainController().log("Logging Out");
        waitSimulation(1000);
    }
}
