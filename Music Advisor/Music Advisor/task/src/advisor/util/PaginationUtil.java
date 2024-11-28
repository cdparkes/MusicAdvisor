package advisor.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import static advisor.Menu.scanner;

public class PaginationUtil {

    /**
     * Prints a paginated list to the console.
     *
     * @param items    The list of items to display.
     * @param pageSize Number of items per page.
     */
    public static <T> void displayPaginated(List<T> items, int pageSize) {
        int totalPages = (int) Math.ceil((double) items.size() / pageSize);
        int currentPage = 0;
        boolean pageError = false;

        while (true) {
            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, items.size());

            // Print the current page
            if (!pageError) {
                for (int i = start; i < end; i++) {
                    System.out.println(items.get(i));
                }

                // Print footer and wait for user input

                System.out.printf("---PAGE %d OF %d---%n", currentPage + 1, totalPages);

                System.out.println("Enter 'next' or 'prev' to navigate pages, or 'exit' to quit:");
            }
            String input = scanner.nextLine().toLowerCase();

            if("next".equals(input)) {
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    pageError = false;
                } else {
                    System.out.println("No more pages");
                    pageError = true;
                }
            } else if ("prev".equals(input)) {
                if (currentPage > 0) {
                    currentPage--;
                    pageError = false;
                } else {
                    System.out.println("No more pages");
                    pageError = true;
                }
            } else if ("exit".equals(input)) {
                break;
            } else {
                System.out.println("Invalid command. Try 'next', 'prev', or 'exit'.");
            }
        }
    }
}
