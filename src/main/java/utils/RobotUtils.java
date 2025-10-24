package utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

/**
 * Utility class to handle OS-level keyboard and mouse automation
 * (e.g. Basic Auth popups, file uploads, native dialogs)
 *
 * ✅ Singleton Robot instance for reuse
 * ✅ Thread-safe for multi-scenario runs
 * ✅ Common reusable actions (type, pressTab, pressEnter, pasteText, etc.)
 *
 * ⚠️ Note: Works only in non-headless browsers and on local desktop runs.
 */
public class RobotUtils {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
            robot.setAutoDelay(200); // small delay between actions
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Robot instance: " + e.getMessage());
        }
    }

    /** Wait for specified milliseconds */
    public static void delay(int ms) {
        robot.delay(ms);
    }

    /** Press and release a single key */
    public static void pressKey(int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }

    /** Press ENTER key */
    public static void pressEnter() {
        pressKey(KeyEvent.VK_ENTER);
    }

    /** Press TAB key */
    public static void pressTab() {
        pressKey(KeyEvent.VK_TAB);
    }

    /** Press ESC key */
    public static void pressEsc() {
        pressKey(KeyEvent.VK_ESCAPE);
    }

    /** Type a string character by character */
    public static void type(String text) {
        for (char c : text.toCharArray()) {
            typeChar(c);
        }
    }

    /** Type a single character */
    private static void typeChar(char c) {
        try {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character '" + c + "'");
            }
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            System.err.println("⚠️ Unable to type character: " + c + " → " + e.getMessage());
        }
    }

    /** Paste text using clipboard (useful for file uploads, input dialogs, etc.) */
    public static void pasteText(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        // Ctrl + V
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /** Press Ctrl + A (Select All) */
    public static void selectAll() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /** Press Ctrl + C (Copy) */
    public static void copy() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /** Press Ctrl + V (Paste) */
    public static void paste() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /** Press Alt + F4 (Close window) */
    public static void closeWindow() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
    }
}
