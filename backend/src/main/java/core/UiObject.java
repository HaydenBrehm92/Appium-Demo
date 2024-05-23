package core;

import core.constants.ElementCoordsInfo;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import java.time.Duration;
import java.util.Arrays;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * UIObject corresponds to each element found on the smartphone and the actions it can take.
 */
public class UiObject {
    private final String locator;
    private final AndroidDriver driver;


    /**
     * UIObject Constructor
     * @param locator the path passed to find the location of the UI object.
     * @author Hayden Brehm, Victor Dang
     */
    public UiObject(String locator, AndroidDriver driver){
        this.driver = driver;
        this.locator = locator;
        MyLogger.log.debug("Xpath locator: {}", locator);
    }

    /**
     * Gets the WebElement using the locator held by this object.
     * @return The WebElement object for the locator stored in this UIObject. May return null if the locator
     * is not a valid XPath.
     * @author Hayden Brehm, Victor Dang
     */
    public WebElement getElement()
    {
        return (isXpath()) ? driver.findElement(By.xpath(locator)) : null;
    }

    /**
     * Boolean that finds if the locator field is a xpath.
     * @return True if the locator is a xpath. False otherwise.
     * @author Hayden Brehm
     */
    private boolean isXpath(){
        return locator.contains("//*[") || locator.contains("/hierarchy") || locator.contains("//span");
    }

    /**
     * Finds if an element exists at the object's xpath.
     * @return True if the element exists, false otherwise.
     * @author Victor Dang
     */
    public boolean elementExists()
    {
        try
        {
            return getElement().isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            //e.printStackTrace();
            MyLogger.log.warn(locator + " could not be found, returning false!");
            return false;
        }
    }

    /**
     * Click event on the UIObject's location based on xpath. If a stale element is found when trying to click
     * this element, then a retry will occur. As of now, this will retry indefinitely until a click happens.
     * @author Hayden Brehm, Victor Dang
     */
    public void tap()
    {
        boolean isStaleElement;

        // using a do-while loop to avoid recursion
        do
        {
            try
            {
                getElement().click();
                isStaleElement = false;
            }
            catch (StaleElementReferenceException se)
            {
                //se.printStackTrace();
                MyLogger.log.debug("Stale element reference encountered on tap, retrying...");
                isStaleElement = true;
            }
        } while (isStaleElement);
    }

    public String getText()
    {
        return getElement().getText();
    }

    /**
     * Long Press event (based on a provided time in seconds) on the UIObject's location based on xpath.
     * There will be a delay equal to the amount of seconds passed to this method.
     * @author Hayden Brehm, Victor Dang
     */
    public void LongPress(int seconds)
    {
        MyLogger.log.debug("Performing long press for " + seconds + " seconds...");

        // W3C Selenium update, everything is unnecessarily complicated :(
        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence touch = new Sequence(input, 1);
        ElementCoordsInfo coords = new ElementCoordsInfo(getElement());

        touch.addAction(input.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), coords.HalfWidth(), coords.HalfHeight()));

        touch.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // this is only meant for long pressing because there doesn't seem to be any way to actually
        // add a delay between actions using this method, the pointer itself should not move anywhere
        // on the screen
        touch.addAction(input.createPointerMove(Duration.ofSeconds(seconds),
                PointerInput.Origin.viewport(), coords.HalfWidth(), coords.HalfHeight()));

        touch.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(touch));
    }

    /**
     * Drag Right Gesture event, on the UIObject's location based on xpath. The drag duration for fully
     * dragging an element across the screen will be at least 500ms. Take this time into account when calling
     * this method.
     *
     * @author Edgar Bermudez, Victor Dang
     */
    public void dragRightGesture()
    {
        MyLogger.log.debug("Performing drag right gesture...");
        ElementCoordsInfo coords = new ElementCoordsInfo(getElement());
        drag(coords.startX, coords.startY, coords.endX, coords.endY, 500, driver);
    }

    /**
     * Used to drag a UI from a specified start and end point. The drag action will drag the UI
     * element for the specified duration in milliseconds.
     * @param startX starting width value
     * @param startY starting height value
     * @param endX ending width value
     * @param endY ending height value
     * @param durationMilliSeconds The duration of the drag.
     */
    public static void drag(int startX, int startY, int endX, int endY, long durationMilliSeconds, AndroidDriver driver)
    {
        MyLogger.log.debug("Performing drag gesture...");

        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(input, 1);

        // move pointer to correct position
        drag.addAction(input.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), startX, startY));

        // input down to indicate finger touch
        drag.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // drag for the specified amount of time
        drag.addAction(input.createPointerMove(Duration.ofMillis(durationMilliSeconds),
                PointerInput.Origin.viewport(), endX, endY));

        // input up to indicate finger release
        drag.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // perform the actions
        driver.perform(Arrays.asList(drag));
    }

    /**
     * Taps at the specified location.
     * @param x X position to tap at
     * @param y Y position to tap at
     * @author Victor Dang
     */
    public static void tapAt(int x, int y, AndroidDriver driver)
    {
        drag(x, y, x, y, 0, driver);
    }

    /**
     * Scrolls down on the screen to find a specific string value. It will assume that the screen starts at the top
     * and will scroll down.
     * @author Hayden Brehm, Victor Dang
     */
    public void swipeFind()
    {
        // this is a fun ways of doing things
        Dimension windowSize = driver.manage().window().getSize();

        int width = windowSize.getWidth() / 2;
        int height = (int) (windowSize.getHeight() / 0.75f);

        while (!elementExists())
        {
            try
            {
                MyLogger.log.debug("Swiping down to find element");
                drag(width, width, height, 0, 250, driver);
            }
            catch (Exception e)
            {
                MyLogger.log.debug("Can't find element, ignoring exception");
            }
        }
    }

    /**
     * Inputs a String value into some field specified by the UIObject's location based on xpath.
     * @param keys the String value to be input.
     * @author Hayden Brehm
     */
    public void inputKeys(String keys){
        getElement().sendKeys(keys);
    }

    /**
     * Checks to see if a checkbox is already checked.
     * Used specifically in the engineering menu.
     * @return True if the checkbox is already checked. False otherwise.
     * @author Hayden Brehm
     */
    public boolean isCheckboxChecked()
    {
        // the "checked" attribute will always return false so the only attribute that changes when checked or
        // unchecked is the text attribute.
        // we'll need to set the string as all lower case since there seems to be a difference between ATT and
        // VZW when setting the checked text.
        // ATT 12.3 = "Checkbox Checked"
        // VZW 11.3 = "Checkbox, checked"
        // this "checked" part of the string will not be there when the checkbox is unchecked.
        // hopefully this solution is enough to cover all clients.

        //MyLogger.log.debug("Text attribute: {}", getText().toLowerCase());
        return getText().toLowerCase().contains("checked");
    }

    /**
     * Wait until this element is visible on the screen but also having with a timeout in case
     * it takes too long for the element to appear. If a timeout happens, the code will continue, but
     * may crash later due to no element exception.
     * @param millisTimeout the amount of time before timing out.
     * @return the current object instance.
     * @author Victor Dang
     */
    public UiObject waitUntilVisible(long millisTimeout)
    {
        return explicitWait(millisTimeout, ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
    }

    /**
     * Wait until this element is visible on the screen but also having with a timeout in case
     * it takes too long for the element to appear. If a timeout happens, the code will continue, but
     * may crash later due to no element exception.
     * @param millisTimeout the amount of time before timing out.
     * @return the current object instance.
     * @author Victor Dang
     */
    public UiObject waitUntilHidden(long millisTimeout)
    {
        return explicitWait(millisTimeout, ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
    }

    /**
     * Wait until this element is clickable on the screen but also having with a timeout in case
     * it takes too long for the element to appear. If a timeout happens, the code will continue, but
     * may crash later due to no element exception.
     * @param millisTimeout the amount of time before timing out.
     * @return the current object instance.
     * @author Victor Dang
     */
    public UiObject waitUntilClickable(long millisTimeout)
    {
        return explicitWait(millisTimeout, ExpectedConditions.elementToBeClickable(By.xpath(locator)));
    }

    /**
     * Generic explicit wait to simplify explicit wait calls and development. Internal use only.
     * @param millisTimeout the amount of time before timing out.
     * @param condition the condition to check for during the allotted timeout period.
     * @return the current UI Object instance.
     * @param <T> a generic type for ExpectedCondition interface.
     */
    private <T> UiObject explicitWait(long millisTimeout, ExpectedCondition<T> condition)
    {
        try
        {
            new WebDriverWait(driver, Duration.ofMillis(millisTimeout))
                    //.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.refreshed(condition));
        }
        catch (TimeoutException te)
        {
            //te.printStackTrace();
            MyLogger.log.warn("Timeout occurred!");
        }
        catch (StaleElementReferenceException se)
        {
            //se.printStackTrace();
            MyLogger.log.warn("Stale element occurred!");
        }

        return this;
    }
}
