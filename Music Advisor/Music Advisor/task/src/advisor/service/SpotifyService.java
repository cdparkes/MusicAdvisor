package advisor.service;

import advisor.Authentication;
import advisor.models.Category;
import advisor.models.Playlist;
import advisor.models.Release;
import advisor.util.ErrorUtil;
import advisor.util.JsonUtil;
import advisor.util.PaginationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpotifyService {
    private static String resourceServer;
    private static Authentication auth;
    private static HttpClient httpClient;

    public SpotifyService(String resourceServer, Authentication auth) {
        SpotifyService.resourceServer = resourceServer;
        SpotifyService.auth = auth;
        httpClient = HttpClient.newHttpClient();
    }

    public static List<Category> getCategories() {
        String endpoint = resourceServer + "/v1/browse/categories";
        JsonObject response = makeApiRequest(endpoint);

        if (response == null) {
            return new ArrayList<>();
        }

        JsonObject categoriesObject = JsonUtil.getJsonObject(response, "categories");
        if (categoriesObject == null) {
            return new ArrayList<>();
        }

        JsonArray items = JsonUtil.getJsonArray(categoriesObject, "items").getAsJsonArray();
        if (items == null) {
            return new ArrayList<>();
        }

        List<Category> categories = new ArrayList<>();
        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            String id = JsonUtil.getString(obj, "id");
            String name = JsonUtil.getString(obj, "name");
            categories.add(new Category(id, name));
        }

        return categories;
    }

    public static String getCategoryID(String category) {
        String endpoint = resourceServer + "/v1/browse/categories";
        JsonObject response = makeApiRequest(endpoint);

        if (response == null) {
            return "";
        }

        JsonObject categoriesObject = JsonUtil.getJsonObject(response, "categories");
        if (categoriesObject == null) {
            return "";
        }

        JsonArray items = JsonUtil.getJsonArray(categoriesObject, "items").getAsJsonArray();
        if (items == null) {
            return "";
        }

        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            String categoryID = JsonUtil.getString(obj, "id");
            String name = JsonUtil.getString(obj, "name");

            if (category.equalsIgnoreCase(name)) {
                return categoryID;
            }
        }

        return "";
    }

    public static List<Playlist> getPlaylists(String categoryID) {
        String endpoint = resourceServer + "/v1/browse/categories/" + categoryID + "/playlists";
        JsonObject response = makeApiRequest(endpoint);

        if (response == null) {
            System.out.println("Failed to fetch playlists for category: " + categoryID);
            return new ArrayList<>();
        }

        JsonArray items = response.getAsJsonObject("playlists").getAsJsonArray("items");
        List<Playlist> playlists = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            playlists.add(new Playlist(
                    obj.get("name").getAsString(),
                    obj.get("external_urls").getAsJsonObject().get("spotify").getAsString()
            ));
        }

        return playlists;
    }

    public static List<Playlist> getFeatured() {
        String endpoint = resourceServer + "/v1/browse/featured-playlists";
        JsonObject response = makeApiRequest(endpoint);

        if (response == null) {
            System.out.println("Failed to fetch featured playlists");
            return new ArrayList<>();
        }

        JsonArray items = response.getAsJsonObject("playlists").getAsJsonArray("items");
        List<Playlist> playlists = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            playlists.add(new Playlist(
                    obj.get("name").getAsString(),
                    obj.get("external_urls").getAsJsonObject().get("spotify").getAsString()
            ));
        }

        return playlists;
    }

    public static List<Release> getNewReleases() {
        String endpoint = resourceServer + "/v1/browse/new-releases";
        JsonObject response = makeApiRequest(endpoint);

        if (response == null) {
            System.out.println("Failed to fetch new releases.");
            return new ArrayList<>();
        }

        JsonArray items = response.getAsJsonObject("albums").getAsJsonArray("items");
        List<Release> releases = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            releases.add(new Release(
                    obj.get("name").getAsString(),
                    getArtistNames(obj.getAsJsonArray("artists")),
                    obj.get("external_urls").getAsJsonObject().get("spotify").getAsString()
            ));
        }

        return releases;
    }

    private static List<String> getArtistNames(JsonArray artists) {
        List<String> artistNames = new ArrayList<>();
        for (JsonElement artist : artists) {
            artistNames.add(artist.getAsJsonObject().get("name").getAsString());
        }
        return artistNames;
    }

    private static JsonObject makeApiRequest(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + auth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            }

            JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
            String errorMessage = ErrorUtil.extractErrorMessage(errorJson);
            System.out.println("API Error (" + response.statusCode() + "): " + errorMessage);

        } catch (Exception e) {
            System.out.println("Error during API request: " + e.getMessage());
        }

        return null;
    }
}
