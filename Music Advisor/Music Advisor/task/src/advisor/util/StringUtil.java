package advisor.util;

public class StringUtil {

    /**
     * Truncate a string to a specific length, appending "..." if truncated.
     * @param input The input string.
     * @param maxLength Maximum length for the string.
     * @return Truncated string.
     */
    public static String truncate(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength - 3) + "...";
    }

    /**
     * Converts a string to lowercase and removes leading/trailing whitespace.
     * @param input The input string.
     * @return A sanitized string.
     */
    public static String sanitize(String input) {
        return input == null ? "" : input.trim().toLowerCase();
    }
}
