package com.battleship.client;

import static org.fusesource.jansi.Ansi.*;

/**
 * The JansiHelper class provides utility methods for manipulating text colors
 * using the Jansi library.
 * 
 * Jansi is a Java library that allows for cross-platform colored console
 * output.
 */
public class JansiHelper {

    /**
     * Displays an alert message with the specified text in yellow color.
     *
     * @param text The text to be displayed as an alert.
     * @return A colorized string with the specified text in yellow color.
     */
    public static String alert(String text) {
        return colorize(text, "green");
    }

    /**
     * Colorizes the specified text using the provided color.
     *
     * @param text  The text to be colorized.
     * @param color The color to apply to the text.
     * @return A colorized string with the specified text and color.
     */
    public static String colorize(String text, String color) {
        return String.format("@|%s %s|@", color, text);
    }

    /**
     * Colorizes the background of the specified text using the provided color.
     * 
     * @param text  The text whose background should be colorized.
     * @param color The color to apply to the background.
     * @return A colorized string with the specified text and colorized background.
     */
    public static String colorizeBackground(String text, String color) {
        return String.format("@|bg_%s %s|@", color, text);
    }

    /**
     * 
     * Prints the specified text to the console with colorized output using the
     * Jansi library.
     * The text will be rendered according to the configured color settings.
     * 
     * @param text The text to be printed.
     */
    public static void print(String text) {
        System.out.println(ansi().render(text));
    }

}