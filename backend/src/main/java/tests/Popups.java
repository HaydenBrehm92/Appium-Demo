package tests;

import UI.Controllers.MainController;
import core.MyLogger;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import page_libraries.Alerts;
import page_libraries.Common;
import page_libraries.History;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Popups {
    private enum PopupType{
        IPA,
        MCA,
        Confirm,
        Emergency,
        EmergencyTransition,
        Reconnecting,
        NoConnection,
        Information
    }
    private PopupType popupType;
    private boolean recoverFromNetworkDown;
    private final AndroidDriver driver;

    public Popups(AndroidDriver driver) { this.driver = driver; }

    public Enum<PopupType> getPopupType() {
        return popupType;
    }

    public boolean isRecoverFromNetworkDown() {
        return recoverFromNetworkDown;
    }

    public boolean isPopUpDisplayed()
    {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(100));
        boolean displayed = (isNoConnection() || isReconnecting() || isEmergencyTransition()
                || isMCA() || isIPA() || isInformation() || isConfirm() || isEmergency());
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(1000));

        return displayed;
    }

    private boolean isIPA(){
        try{
            if(driver.findElement
                    (By.xpath(Alerts.IPA))
                    .isDisplayed()) {
                MyLogger.log.debug("PopUp is: {}", PopupType.IPA.toString());
                popupType = PopupType.IPA;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isMCA(){
        try{
            if (driver.findElement(By.xpath(Alerts.MCA)).isDisplayed()) {
                MyLogger.log.debug("PopUp is: {}", PopupType.MCA.toString());
                popupType = PopupType.MCA;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isEmergency(){
        try{
            if (driver.findElement(By.xpath(Alerts.EmergencyAlert)).isDisplayed()
                && driver.findElement(By.xpath(Common.Dismiss)).isDisplayed()){
                MyLogger.log.debug("PopUp is: {}", PopupType.Emergency.toString());
                popupType = PopupType.Emergency;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isEmergencyTransition(){
        try{
            if (driver.findElement(By.xpath(Common.PleaseWait)).isDisplayed()){
                MyLogger.log.debug("PopUp is: {}", PopupType.EmergencyTransition.toString());
                popupType = PopupType.EmergencyTransition;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isReconnecting(){
        try{
            if (driver.findElement(By.xpath(Alerts.Reconnecting)).isDisplayed()){
                recoverFromNetworkDown = true;
                MyLogger.log.debug("PopUp is: {}", PopupType.Reconnecting.toString());
                popupType = PopupType.Reconnecting;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isNoConnection(){
        try{
            if (driver.findElement
                    (By.xpath(Alerts.ConnectionUnavailable)).isDisplayed()
                    || driver.findElement(By.xpath(Alerts.NoConnection)).isDisplayed()) {
                recoverFromNetworkDown = true;
                MyLogger.log.debug("PopUp is: {}", PopupType.NoConnection.toString());
                popupType = PopupType.NoConnection;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isInformation(){
        try{
            if (driver.findElement(By.xpath(Common.Information)).isDisplayed()){
                MyLogger.log.debug("PopUp is: {}", PopupType.Information.toString());
                popupType = PopupType.Information;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    private boolean isConfirm(){
        try{
            if (driver.findElement(By.xpath(Common.Confirm)).isDisplayed()){
                MyLogger.log.debug("PopUp is: {}", PopupType.Confirm.toString());
                popupType = PopupType.Confirm;
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }
        return false;
    }

    public void handlePopup(){
        if (popupType == null)
            throw new RuntimeException("Popup was not discovered yet! Cannot handle before discovering!");

        switch (popupType){
            case IPA:
            case MCA:
                driver.findElement(By.xpath(Common.NotNow)).click();
                break;
            case Emergency:
                driver.findElement(By.xpath(Common.Dismiss)).click();
                break;
            case EmergencyTransition:
                //wait here or restart application
                break;
            case Reconnecting:
            case NoConnection:
                handleNetworkDown();
                break;
            case Information:
                driver.findElement(By.xpath(Common.OK)).click();
                break;
            case Confirm:
                driver.findElement(By.xpath(Common.Yes)).click();
                break;
        }
    }

    private void handleNetworkDown(){
        AtomicBoolean networkDown = new AtomicBoolean(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            MyLogger.log.debug("ExecutorService starting thread with name: {}", Thread.currentThread().getName());
            MainController.testManager.getMainController().log("Network is down! Waiting...");
            //SetImplicitWait(Duration.ofSeconds(1));

            while (true){
                Thread.sleep(4000);

                try{
                    if (MainController.testManager.isRadio())
                        driver.findElement(By.xpath(History.RadioMostRecent)).click();
                    else
                        driver.findElement(By.xpath(History.StandardMostRecent)).click();
                }catch (NoSuchElementException e){
                    MyLogger.log.debug("Couldn't find history entry to tap");
                }

                if (!isNoConnection() && !isReconnecting()){
                    MainController.testManager.getMainController().log("Network may have recovered! Retrying...");
                    networkDown.set(false);
                    break;
                }
            }
            throw new TimeoutException();
        });


        // Logic to quit after certain time
        try {
            future.get(2, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MyLogger.log.debug("Shutting down executor service");
            executor.shutdownNow();
            if (networkDown.get()) {
                MainController.testManager.getMainController().log("Network never recovered! Canceling Test.");
                MyLogger.log.debug("Recovery failed. Interrupting thread {}", Thread.currentThread().getName());
                Thread.currentThread().interrupt();
                MainController.testManager.getMainController().cancelTest();
            }
        }
    }
}
