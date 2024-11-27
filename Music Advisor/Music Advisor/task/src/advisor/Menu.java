package advisor;

import advisor.models.Album;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import static com.google.gson.JsonParser.parseString;

/**
 * This class represents the menu of the music advisor application.
 */

public class Menu {

    /**
     * The base URL for accessing Spotify's authorization and resource server.
     */
    private static String accessServer = "https://accounts.spotify.com";
    private static String resourceServer = "https://api.spotify.com";

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

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Static variable to hold the authorization data.
     */
    static AuthData data = new AuthData();
    static String clientID = "e508e7306ef84fcd9a8769cac582b977";
    static String redirectURI = "http://localhost:8080";
    static String clientSecret = "4d9c33be27594c3183ea23642e0fbd8b";

    protected static void menu() throws IOException, InterruptedException {
        boolean repeat = true, authCodeReceived = false;

        while (repeat) {
            String input = scanner.nextLine().toLowerCase();
            String[] inputArray = input.split(" ");
            if (input.equals("auth")) {
                authCodeReceived = getAuthCode();
            } else if (!authCodeReceived && input.equals("exit")) {
                System.out.println("---GOODBYE---");
                repeat = false;
            } else if (!authCodeReceived && !input.isEmpty()) {
                System.out.println("Please, provide access for application.");
            } else {
                switch (inputArray[0]) {
                    case "new" -> newReleases(resourceServer + "/v1/browse/new-releases");
                    case "featured" -> featuredReleases(resourceServer + "/v1/browse/featured-playlists");
                    case "categories" -> categories(resourceServer + "/v1/browse/categories");
                    case "playlists" -> {
                        StringBuilder playlistsInput = new StringBuilder();
                        for (int i = 1; i < inputArray.length; i++) {
                            playlistsInput.append(inputArray[i] + " ");
                        }
                        getPlaylists(playlistsInput.toString().trim());
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
                .headers("Authorization", "Bearer " + data.getAccessToken())
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

    private static void newReleases(String apiLink) throws IOException {
        JsonObject apiResponse = apiCall(apiLink);

        if (apiResponse != null) {
            List<Album> albums = parseAlbums(apiResponse);
            for (Album album : albums) {
                System.out.println(album);
            }
        }
    }

    private static List<Album> parseAlbums(JsonObject json) {
        JsonArray items = json.getAsJsonObject("albums").getAsJsonArray("items");
        List<Album> albums = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject album = item.getAsJsonObject();
            String name = album.get("name").getAsString();

            JsonArray artistsArray = album.getAsJsonArray("artists");
            List<String> artists = new ArrayList<>();
            for (JsonElement artistElement : artistsArray) {
                artists.add(artistElement.getAsJsonObject().get("name").getAsString());
            }

            String link = album.getAsJsonObject("external_urls").get("spotify").getAsString();
            albums.add(new Album(name, artists, link));
        }

        return albums;
    }


    private static void printPlaylists(JsonObject json) {
        JsonObject playlists = json.getAsJsonObject("playlists");
        JsonArray items = playlists.getAsJsonArray("items");

        List<String> playlistsNames = new ArrayList<>();
        List<String> playlistsLinks = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject playlistName = item.getAsJsonObject();
            playlistsNames.add(playlistName.get("name").getAsString());
            playlistsLinks.add(playlistName.getAsJsonObject("external_urls").get("spotify").getAsString());
        }

        for (int i = 0; i < playlistsNames.size(); i++) {
            System.out.println(playlistsNames.get(i));
            System.out.println(playlistsLinks.get(i));
            System.out.println();
        }
    }

    private static void featuredReleases(String apiLink) throws IOException {
        JsonObject json = apiCall(apiLink);

        printPlaylists(json);
    }

    private static void getPlaylists(String category) {
        String categoryID = categories(resourceServer + "/v1/browse/categories", category);
        JsonObject json = new JsonObject();
        try {
            if (!categoryID.isEmpty()) {
                System.out.println(categoryID);
                json = apiCall(resourceServer + "/v1/browse/categories/" + categoryID + "/playlists");
            } else {
                System.out.println("Unknown category name.");
            }
            if (json != null) {
                printPlaylists(json);
            }
        } catch (NullPointerException e) {
            System.out.println(json.toString());
        }
    }


    private static void categories(String apiLink) {
        System.out.println(apiLink);
        JsonObject json = apiCall(apiLink);

        JsonObject categories = json.getAsJsonObject("categories");
        JsonArray items = categories.getAsJsonArray("items");

        List<String> categoriesList = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject categoriesObject = item.getAsJsonObject();
            categoriesList.add(categoriesObject.get("name").getAsString());
        }

        for (int i = 0; i < categoriesList.size(); i++) {
            System.out.println(categoriesList.get(i));
        }
    }

    private static String categories(String apiLink, String category) {
        JsonObject json = apiCall(apiLink);

        JsonObject categories = json.getAsJsonObject("categories");
        JsonArray items = categories.getAsJsonArray("items");

        List<String> categoriesList = new ArrayList<>();
        List<String> categoryIDs = new ArrayList<>();

        for (JsonElement item : items) {
            JsonObject categoriesObject = item.getAsJsonObject();
            categoriesList.add(categoriesObject.get("name").getAsString().toLowerCase());
            categoryIDs.add(categoriesObject.get("id").getAsString());
        }

        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).equals(category)) {
                return categoryIDs.get(i);
            }
        }
        return "";
    }



    /**
     * Method to get the authorization code from Spotify.
     *
     * @return true if the authorization code and Access Token is received, false otherwise
     * @throws IOException
     * @throws InterruptedException
     */
    private static boolean getAuthCode() throws IOException, InterruptedException {
        // Create an HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Construct the authorization URL for Spotify's API
        String authUrl = String.format("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code", accessServer, clientID, redirectURI);
        System.out.println("use this link to request the access code:");
        System.out.println(authUrl);

        // Flag to check if the code is received
        final boolean[] isCodeReceived = {false};

        // Handle HTTP requests from Spotify's authorization server
        server.createContext("/", httpExchange -> {
            try {
                // Get the query string from the request
                String query = httpExchange.getRequestURI().getQuery();
                String response;

                // Check if the query string contains the authorization code
                if (query != null && query.contains("code=")) {
                    // Extract the authorization code from the query string
                    String code = query.split("code=")[1];
                    // Set the authorization code in the data object
                    data.setCode(code);

                    // Send a response to the client indicating that the code is received
                    response = "Got the code. Return back to your program.";
                    httpExchange.sendResponseHeaders(200, response.length());
                    httpExchange.getResponseBody().write(response.getBytes());

                    // Set the flag to indicate that the code is received
                    isCodeReceived[0] = true;

                    System.out.println("code received\nmaking http request for access_token...");
                } else {
                    // Send a response to the client indicating that the code is not found
                    response = "Authorization code not found. Try again.";
                    httpExchange.sendResponseHeaders(400, response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                }

                httpExchange.getResponseBody().close();
            } catch (Exception e) {
                // Handle any exceptions that occur while handling the request
                String errorMessage = "Error while handling request: " + e.getMessage();
                System.out.println(errorMessage);

                // Send a response to the client indicating an internal server error
                String response = "Internal server error";
                httpExchange.sendResponseHeaders(500, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            }
        });

        // Start the HTTP server
        server.start();
        System.out.println("waiting for code...");

        // Wait for the authorization code to be received
        while (!isCodeReceived[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted while waiting for authorization code.");
                server.stop(0);
                return false;
            }
        }

        // Stop the HTTP server
        server.stop(0);

        // Get the access token from Spotify's API
        return getAccessToken();
    }

    /**
     * Attempts to obtain an access token from the music streaming service using
     * a client ID, secret, and authorization code. This allows the Music Advisor
     * application to make requests to the service on behalf of a user.
     *
     * @return true if the access token was successfully obtained, false otherwise
     * @throws IOException if there is a problem sending the HTTP request or parsing the response
     */
    private static boolean getAccessToken() throws IOException {
        // Construct the request body for getting the access token
        // This includes our client ID and secret, as well as the authorization code
        HttpRequest request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes()))
                .uri(URI.create(accessServer + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + data.getCode() + "&redirect_uri=" + redirectURI))
                .build();

        // Create an instance of the HTTP client
        HttpClient client = HttpClient.newHttpClient();

        try {
            // Send the request and get the response from the service
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("response:");

            if (response.statusCode() == 200) {
                // If the response status code is 200, we assume it was successful
                JsonObject json = parseString(response.body()).getAsJsonObject();
                String token = json.get("access_token").getAsString();

                // Store the access token in our data object for later use
                data.setAccessToken(token);
                System.out.println("---SUCCESS---");
                return true;
            } else {
                // If the response status code is not 200, we print an error message and return false
                System.out.println("Failed to get access token.");
            }
        } catch (InterruptedException | IOException e) {
            // We catch any exceptions that occur while sending the request or parsing the response
            System.out.println("Error occurred while requesting access token: " + e.getMessage());
        }
        return false;
    }


}

