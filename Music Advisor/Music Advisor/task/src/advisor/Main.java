package advisor;


import java.io.IOException;

/**
 * Main class of the Music Advisor application.
 * This class serves as the entry point for the program and handles command line arguments.
 */
public class Main {

    // Default access server URL for the Spotify API
    private static final String DEFAULT_RESOURCE_SERVER = "https://api.spotify.com";
    private static final String DEFAULT_ACCESS_SERVER = "https://accounts.spotify.com";

    public static void main(String[] args) throws IOException, InterruptedException {

        // Determine if an alternative access or resource server was provided through command line arguments
        String accessServer = DEFAULT_ACCESS_SERVER;
        String resourceServer = DEFAULT_RESOURCE_SERVER;

        for (int i = 0; i < args.length; i++) {
            if ("-access".equals(args[i]) && i + 1 < args.length) {
                accessServer = args[i + 1];
            } else if ("-resource".equals(args[i]) && i + 1 < args.length) {
                resourceServer = args[i + 1];
            }
        }

        // Set the access server for the Menu class
        Menu.setAccessServer(accessServer);
        Menu.setResourceServer(resourceServer);
        // Display the main menu to the user
        Menu.menu();
    }
}
