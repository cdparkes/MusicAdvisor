package advisor.util;

import com.google.gson.JsonObject;

public class ErrorUtil {

    /**
     * Extract and return the error message from a JSON response.
     * @param json JsonObject containing the error block.
     * @return The error message, or a default message if not found.
     */
    public static String extractErrorMessage(JsonObject json) {
        if (json == null || !json.has("error")) {
            return "Unknown error occurred.";
        }

        JsonObject error = json.getAsJsonObject("error");
        return error.has("message") ? error.get("message").getAsString() : "Unknown error.";
    }
}
