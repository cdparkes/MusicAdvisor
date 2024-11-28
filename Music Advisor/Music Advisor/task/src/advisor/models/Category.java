package advisor.models;

import advisor.service.SpotifyService;
import advisor.util.PaginationUtil;

import java.util.List;

public class Category {
    private String id;
    private String name;

    // Constructor
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name) {
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

    public static void displayCategories() {
        List<Category> categories = SpotifyService.getCategories();

        if (categories.isEmpty()) {
            return;
        }

        // Prepare paginated content
        List<String> categoryDetails = categories.stream()
                .map(Category::getName)
                .toList();

        PaginationUtil.displayPaginated(categoryDetails, 5);
    }
}
