import java.io.*;
import java.util.*;

// Abstract class for library items
abstract class LibraryItem {
    protected String title;
    protected String author;
    protected String category;
    protected boolean isAvailable;
    protected int finePerDay = 10; // Fine for overdue

    public LibraryItem(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.isAvailable = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public abstract String getDetails();
}

// Book class
class Book extends LibraryItem {
    public Book(String title, String author, String category) {
        super(title, author, category);
    }

    @Override
    public String getDetails() {
        return "Book: " + title + " by " + author + " (" + category + ")";
    }
}

// Magazine class
class Magazine extends LibraryItem {
    public Magazine(String title, String author, String category) {
        super(title, author, category);
    }

    @Override
    public String getDetails() {
        return "Magazine: " + title + " by " + author + " (" + category + ")";
    }
}

// DVD class
class DVD extends LibraryItem {
    public DVD(String title, String author, String category) {
        super(title, author, category);
    }

    @Override
    public String getDetails() {
        return "DVD: " + title + " by " + author + " (" + category + ")";
    }
}

// Abstract class for users (Librarian and Patron)
abstract class User {
    protected String name;

    public User(String name) {
        this.name = name;
    }

    public abstract void showMenu();
}

// Librarian class
class Librarian extends User {
    private Library library;

    public Librarian(String name, Library library) {
        super(name);
        this.setLibrary(library);
    }

    public Library getLibrary() {
        return library;

    }

    public void setLibrary(Library library) {
        this.library = library;

    }

    @Override
    public void showMenu() {
        System.out.println("\nWelcome, Librarian " + name);
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Search Item");
        System.out.println("4. View All Items");
        System.out.println("5. Logout");
    }

    public void addItem(Scanner scanner) {
        System.out.println("Choose type (1: Book, 2: Magazine, 3: DVD): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear input buffer

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();

        LibraryItem item;
        switch (choice) {
            case 1 -> item = new Book(title, author, category);
            case 2 -> item = new Magazine(title, author, category);
            case 3 -> item = new DVD(title, author, category);
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }

        getLibrary().addItem(item);
        System.out.println("Item added successfully.");
    }

    public void removeItem(Scanner scanner) {
        System.out.print("Enter Title of Item to remove: ");
        String title = scanner.nextLine();
        getLibrary().removeItem(title);
    }
}

// Patron class
class Patron extends User {
    private Library library;

    public Patron(String name, Library library) {
        super(name);
        this.setLibrary(library);
    }

    public Library getLibrary() {
        return library;

    }

    public void setLibrary(Library library) {
        this.library = library;

    }

    @Override
    public void showMenu() {
        System.out.println("\nWelcome, Patron " + name);
        System.out.println("1. Search Item");
        System.out.println("2. Check Out Item");
        System.out.println("3. Return Item");
        System.out.println("4. View All Items");
        System.out.println("5. Logout");
    }

    public void checkOutItem(Scanner scanner) {
        System.out.print("Enter Title of Item to check out: ");
        String title = scanner.nextLine();
        getLibrary().checkOutItem(title);
    }

    public void returnItem(Scanner scanner) {
        System.out.print("Enter Title of Item to return: ");
        String title = scanner.nextLine();
        getLibrary().returnItem(title);
    }
}

// Library class for managing resources
class Library {
    private List<LibraryItem> items;
    private final String filePath = "library_data.txt"; // Data persistence

    public Library() {
        items = new ArrayList<>();
        loadItemsFromFile();
    }

    public void addItem(LibraryItem item) {
        items.add(item);
        saveItemsToFile();
    }

    public void removeItem(String title) {
        items.removeIf(item -> item.getTitle().equalsIgnoreCase(title));
        saveItemsToFile();
        System.out.println("Item removed successfully.");
    }

    public void checkOutItem(String title) {
        for (LibraryItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title) && item.isAvailable()) {
                item.setAvailable(false);
                saveItemsToFile();
                System.out.println("Item checked out successfully.");
                return;
            }
        }
        System.out.println("Item not available.");
    }

    public void returnItem(String title) {
        for (LibraryItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title) && !item.isAvailable()) {
                item.setAvailable(true);
                saveItemsToFile();
                System.out.println("Item returned successfully.");
                return;
            }
        }
        System.out.println("Item not found.");
    }

    public void searchItem(String title) {
        for (LibraryItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title)) {
                System.out.println(item.getDetails() + (item.isAvailable() ? " [Available]" : " [Checked Out]"));
                return;
            }
        }
        System.out.println("Item not found.");
    }

    public void viewAllItems() {
        for (LibraryItem item : items) {
            System.out.println(item.getDetails() + (item.isAvailable() ? " [Available]" : " [Checked Out]"));
        }
    }

    private void saveItemsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (LibraryItem item : items) {
                writer.write(item.getDetails() + ";" + item.isAvailable() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadItemsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String[] details = parts[0].split(": ");
                String type = details[0];
                String[] info = details[1].split(" by ");
                String title = info[0];
                String author = info[1].split(" \\(")[0];
                String category = info[1].split("\\(")[1].replace(")", "");
                boolean isAvailable = Boolean.parseBoolean(parts[1]);

                LibraryItem item;
                switch (type) {
                    case "Book" -> item = new Book(title, author, category);
                    case "Magazine" -> item = new Magazine(title, author, category);
                    case "DVD" -> item = new DVD(title, author, category);
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
                item.setAvailable(isAvailable);
                items.add(item);
            }
        } catch (IOException e) {
            System.out.println("No previous data found.");
        }
    }
}

// Main class
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Library System");
            System.out.println("1. Librarian Login");
            System.out.println("2. Patron Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            if (choice == 1) {
                Librarian librarian = new Librarian("Librarian1", library);
                librarianMenu(librarian, scanner);
            } else if (choice == 2) {
                Patron patron = new Patron("Patron1", library);
                patronMenu(patron, scanner);
            } else if (choice == 3) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice! Try again.");
            }
        }

        scanner.close();
    }

    private static void librarianMenu(Librarian librarian, Scanner scanner) {
        while (true) {
            librarian.showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1 -> librarian.addItem(scanner);
                case 2 -> librarian.removeItem(scanner);
                case 3 -> {
                    System.out.print("Enter Title to search: ");
                    String title = scanner.nextLine();
                    librarian.getLibrary().searchItem(title);
                }
                case 4 -> librarian.getLibrary().viewAllItems();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void patronMenu(Patron patron, Scanner scanner) {
        while (true) {
            patron.showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Title to search: ");
                    String title = scanner.nextLine();
                    patron.getLibrary().searchItem(title);
                }
                case 2 -> patron.checkOutItem(scanner);
                case 3 -> patron.returnItem(scanner);
                case 4 -> patron.getLibrary().viewAllItems();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
