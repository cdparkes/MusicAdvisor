package advisor.models;

import java.util.List;

public class Album {
    private  String name;
    private List<String> artists;
    private String link;

    public Album(String name, List<String> artists, String link) {
        this.name = name;
        this.artists = artists;
        this.link = link;
    }

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

    @Override
    public String toString() {
        return name + "\n[" + String.join(", ", artists) + "]\n" + link + "\n";
    }
}
