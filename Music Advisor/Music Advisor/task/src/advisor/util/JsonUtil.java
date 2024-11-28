package advisor.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {

    /**
     * Safely extract a value from a JSON object as a String.
     * @param obj JsonObject to extract from.
     * @param key The key to look for.
     * @return The value as a String or an empty String if the key doesn't exist.
     */
    public static String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    /**
     * Safely extract a nested JsonObject.
     * @param obj JsonObject to extract from.
     * @param key The key to look for.
     * @return The nested JsonObject or null if it doesn't exist.
     */
    public static JsonObject getJsonObject(JsonObject obj, String key) {
        return obj.has(key) && obj.get(key).isJsonObject() ? obj.getAsJsonObject(key) : null;
    }

    /**
     * Safely extract a nested JsonArray.
     * @param obj JsonObject to extract from.
     * @param key The key to look for.
     * @return The JsonArray or null if it doesn't exist.
     */
    public static JsonElement getJsonArray(JsonObject obj, String key) {
        return obj.has(key) && obj.get(key).isJsonArray() ? obj.get(key) : null;
    }
}
