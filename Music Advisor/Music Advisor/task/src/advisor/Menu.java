package advisor;

import advisor.models.Album;
import advisor.models.Category;
import advisor.models.Playlist;
import advisor.models.Release;
import advisor.service.SpotifyService;
import advisor.util.StringUtil;
import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the menu of the music advisor application.
 */

public class Menu {

    /**
     * The base URL for accessing Spotify's authorization and resource server.
     */
    protected static String accessServer = "https://accounts.spotify.com";
    protected static String resourceServer = "https://api.spotify.com";

    /**
     * Method to set the access server URL.
     *
     * @param server the new access server URL
     */
    public static void setAccessServer(String server) {
        accessServer = server;
    }
    public static void setResourceServer(String server) {
        resourceServer = server;
    }

    public static String getAccessServer() {
        return accessServer;
    }

    public static String getResourceServer() {
        return resourceServer;
    }

    public static final Scanner scanner = new Scanner(System.in);
    private static final Authentication auth = new Authentication();
    private static final SpotifyService service = new SpotifyService(resourceServer, auth);

    protected static void menu() throws IOException, InterruptedException {
        boolean repeat = true, authCodeReceived = false;

        while (repeat) {
            String input = StringUtil.sanitize(scanner.nextLine());
            String[] inputArray = input.split(" ");
            if (input.equals("auth")) {
                if (auth.authenticate(accessServer)) {
                    authCodeReceived = true;
                    System.out.println("Access Token: " + auth.getAccessToken());
                    System.out.println("Refresh Token: " + auth.getRefreshToken());
                } else {
                    System.out.println("Failed to authenticate.");
                }
            } else if (!authCodeReceived && input.equals("exit")) {
                System.out.println("---GOODBYE---");
                repeat = false;
            } else if (!authCodeReceived && !input.isEmpty()) {
                System.out.println("Please, provide access for application.");
            } else {
                switch (inputArray[0]) {
                    case "new" -> Release.displayNewReleases();
                    case "featured" -> Playlist.displayFeatured();
                    case "categories" -> Category.displayCategories();
                    case "playlists" -> {
                        StringBuilder playlistsInput = new StringBuilder();
                        for (int i = 1; i < inputArray.length; i++) {
                            playlistsInput.append(inputArray[i] + " ");
                        }
                        Playlist.displayPlaylists(playlistsInput.toString().trim());
                    }
                    case "exit" -> {
                        System.out.println("---GOODBYE!---");
                        repeat = false;
                    }
                }
            }
        }
    }

    private static JsonObject apiCall(String apiLink) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .headers("Authorization", "Bearer " + auth.getAccessToken())
                .uri(URI.create(apiLink))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = gson.toJson(json);

                try (FileWriter file = new FileWriter("C:\\Hyperskill\\Music Advisor\\Music Advisor\\task\\src\\advisor\\output.json")) {
                    file.write(prettyJson);
                }

                return json;
            } else {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                JsonObject error = json.getAsJsonObject("error");

                String errorMessage = error.get("message").getAsString();

                System.out.println(errorMessage);
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Error occurred while requesting access token: " + e.getMessage());
        }
        return null;
    }

    // This is no longer needed as Spotify depricated all api points calling playlists
    // Keeping it in the code so that the tasks from the course still funcion correctly
    @Deprecated
    private static void getPlaylists(String category) {
        String categoryID = categories(resourceServer + "/v1/browse/categories", category);
        JsonObject json = new JsonObject();
        try {
            if (!categoryID.isEmpty()) {
                json = apiCall(resourceServer + "/v1/browse/categories/" + categoryID + "/playlists");
            } else {
                System.out.println("Unknown category name.");
            }
        } catch (NullPointerException e) {
            System.out.println(json.toString());
        }
    }


    private static void categories(String apiLink) {
        JsonObject json = apiCall(apiLink);
        JsonArray items = json.getAsJsonObject("categories").getAsJsonArray("items");

        List<Category> categories = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject categoriesObject = item.getAsJsonObject();
            String name = categoriesObject.get("name").getAsString();
            categories.add(new Category(name));
        }

        for (Category category : categories) {
            System.out.println(category.getName());
        }
    }

    private static String categories(String apiLink, String category) {
        JsonObject json = apiCall(apiLink);
        JsonArray items = json.getAsJsonObject("categories").getAsJsonArray("items");

        List<Category> categories = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject categoriesObject = item.getAsJsonObject();
            String name = categoriesObject.get("name").getAsString();
            String categoryID = categoriesObject.get("id").getAsString();
            categories.add(new Category(categoryID, name));
        }

        for (int i = 0; i < categories.size(); i++) {
            if (category.equals(categories.get(i).getName().toLowerCase())) {
                return categories.get(i).getId();
            }
        }
        return "";
    }
}

