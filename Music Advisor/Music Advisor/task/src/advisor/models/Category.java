package advisor.models;

public class Category {
    private String id;
    private String name;

    // Constructor
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString for easy printing
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
