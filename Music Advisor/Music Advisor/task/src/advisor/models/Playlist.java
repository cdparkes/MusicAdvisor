package advisor.models;

public class Playlist {
    private String name;
    private String link;

    // Constructor
    public Playlist(String name, String link) {
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
}
