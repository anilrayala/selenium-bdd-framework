package pages.components;

import org.openqa.selenium.WebDriver;

/**
 * BaseComponent - Abstract class for reusable UI components.
 * Extend this for headers, footers, modals, etc.
 */
public abstract class BaseComponent {
    protected WebDriver driver;

    public BaseComponent(WebDriver driver) {
        this.driver = driver;
    }

    public abstract boolean isDisplayed();
}