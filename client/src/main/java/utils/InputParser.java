package utils;

/**
 * Utility class for parsing input to identify its type (ID, thread number, or
 * file path).
 */
public class InputParser {

    /**
     * Checks if the input is a valid ID (only digits).
     *
     * @param input The input string.
     * @return True if the input represents an ID, false otherwise.
     */
    public static boolean isId(String input) {
        return input.matches("\\d+"); // Only digits
    }

    /**
     * Checks if the input is a thread number (starts with 'N' followed by digits).
     *
     * @param input The input string.
     * @return True if the input represents a thread number, false otherwise.
     */
    public static boolean isThreadNumber(String input) {
        return input.matches("N\\d+"); // Starts with 'N' followed by digits
    }

    /**
     * Checks if the input is a valid file path.
     *
     * @param input The input string.
     * @return True if the input ends with ".txt", false otherwise.
     */
    public static boolean isFilePath(String input) {
        return input.endsWith(".txt");
    }
}
