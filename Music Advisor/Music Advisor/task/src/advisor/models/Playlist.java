package advisor.models;

import advisor.service.SpotifyService;
import advisor.util.PaginationUtil;
import com.google.gson.JsonObject;

import java.util.List;

public class Playlist {
    private String name;
    private String link;

    // Constructor
    public Playlist(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Playlist(String name, String link, String category) {
        this.name = name;
        this.link = link;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // toString for easy printing
    @Override
    public String toString() {
        return name + "\n" + link;
    }

    public static void displayPlaylists(String category) {
        String categoryID = SpotifyService.getCategoryID(category);
        List<Playlist> playlists = SpotifyService.getPlaylists(categoryID);

        if (playlists.isEmpty()) {
            System.out.println("No playlists found");
            return;
        }

        List<String> playlistDetails = playlists.stream()
                .map(playlist -> playlist.getName() + "\n" +
                        playlist.getLink())
                .toList();

        PaginationUtil.displayPaginated(playlistDetails, 5);
    }

    public static void displayFeatured() {
        List<Playlist> playlists = SpotifyService.getFeatured();

        if (playlists.isEmpty()) {
            System.out.println("No playlists found");
            return;
        }

        List<String> playlistDetails = playlists.stream()
                .map(playlist -> playlist.getName() + "\n" +
                        playlist.getLink())
                .toList();

        PaginationUtil.displayPaginated(playlistDetails, 5);
    }
}
