package core.constants;

import org.openqa.selenium.WebElement;

/**
 * Use this class to get the proper size and coordinates for start and end positions for a given
 * element. This can also be expanded to provide more info.
 */
public class ElementCoordsInfo
{
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;


    public ElementCoordsInfo(WebElement element)
    {
        startX = element.getRect().x;
        startY = element.getRect().y + (element.getSize().height / 2);
        endX = element.getRect().x + element.getSize().width;
        endY = element.getRect().y + (element.getSize().height / 2);
    }

    /**
     * Gets the half-way height (y-coord) of the element that was initialized with this instance.
     * @return
     */
    public int HalfHeight()
    {
        return (startY + endY) / 2;
    }

    /**
     * Gets the half-way width (x-coord) of the element that was initialized with this instance.
     * @return
     */
    public int HalfWidth()
    {
        return (startX + endX) / 2;
    }
}
