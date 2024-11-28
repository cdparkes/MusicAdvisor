package advisor.models;

import advisor.Menu;
import advisor.service.SpotifyService;
import advisor.util.PaginationUtil;

import java.util.List;

public class Release {
    private  String name;
    private List<String> artists;
    private String link;

    // Constructor
    public Release(String name, List<String> artists, String link) {
        this.name = name;
        this.artists = artists;
        this.link = link;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
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
        return name + "\n[" + String.join(", ", artists) + "]\n" + link + "\n";
    }

    public static void displayNewReleases() {
        List<Release> releases = SpotifyService.getNewReleases();

        if (releases.isEmpty()) {
            System.out.println("No new releases found.");
            return;
        }

        // Prepare paginated content
        List<String> releaseDetails = releases.stream()
                .map(release -> release.getName() + "\n" +
                        "[" + String.join(", ", release.getArtists()) + "]\n" +
                        release.getLink())
                .toList();

        PaginationUtil.displayPaginated(releaseDetails, 5);
    }
}
