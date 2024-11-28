package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class Authentication {
    private static final String CLIENT_ID = "e508e7306ef84fcd9a8769cac582b977";
    private static final String CLIENT_SECRET = "4d9c33be27594c3183ea23642e0fbd8b";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static String accessServer = "https://accounts.spotify.com";

    private String accessToken;
    private String refreshToken;

    // Public method to start the authentication process
    public boolean authenticate(String accessServer) throws IOException, InterruptedException {
        Authentication.accessServer = accessServer;
        String authUrl = buildAuthUrl(accessServer);
        System.out.println("Use this link to request the access code:");
        System.out.println(authUrl);

        String authCode = receiveAuthCode();
        if (authCode == null) {
            System.out.println("Failed to receive the authorization code.");
            return false;
        }
        return requestAccessToken(authCode);
    }

    // Build the authorization URL
    private String buildAuthUrl(String accessServer) {
        return String.format("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                accessServer, CLIENT_ID, REDIRECT_URI
        );
    }

    // Start a server to receive the authorization code
    private String receiveAuthCode() throws IOException {
        final String[] codeHolder = {null};

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String response;

            if (query != null && query.contains("code=")) {
                codeHolder[0] = query.split("code=")[1];
                response = "Got the code. Return back to your program.";
                exchange.sendResponseHeaders(200, response.length());
            } else {
                response = "Authorization code not found. Try again.";
                exchange.sendResponseHeaders(400, response.length());
            }

            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });

        server.start();
        System.out.println("Waiting for authorization code...");
        while (codeHolder[0] == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                server.stop(0);
                return null;
            }
        }

        server.stop(0);
        return codeHolder[0];
    }

    private boolean requestAccessToken(String authCode) {
        try {
            String credentials = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(accessServer + "/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + credentials)
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "grant_type=authorization_code&code=" + authCode + "&redirect_uri=" + REDIRECT_URI))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                this.accessToken = json.get("access_token").getAsString();
                this.refreshToken = json.get("refresh_token").getAsString();
                System.out.println("---SUCCESS---");
                return true;
            } else {
                System.out.println("Failed to get access token: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error while requesting access token: " + e.getMessage());
        }
        return false;
    }

    // Refresh the access token using the refresh token
    public boolean refreshAccessToken() {
        try {
            String credentials = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(accessServer + "/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + credentials)
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "grant_type=refresh_token&refresh_token=" + refreshToken))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                this.accessToken = json.get("access_token").getAsString();
                System.out.println("---Token Refreshed Successfully---");
                return true;
            } else {
                System.out.println("Failed to refresh token: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error while refreshing token: " + e.getMessage());
        }
        return false;
    }

    // Getter for access token
    public String getAccessToken() {
        return accessToken;
    }

    // Getter for refresh token
    public String getRefreshToken() {
        return refreshToken;
    }
}
