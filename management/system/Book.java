package library.management.system;

import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;

public class Book extends Conn {

    String title;
    String author;
    int quantity;

    Scanner sc = new Scanner(System.in);

    public void addBook() {
        try {
            boolean adding = true;

            while (adding) {
                System.out.print("Book name: ");
                title = sc.nextLine().trim();
                System.out.print("Author name: ");
                author = sc.nextLine().trim();
                System.out.print("Quantity of Book: ");
                quantity = sc.nextInt();
                sc.nextLine();

                String checkQuery = "SELECT book_id, title, author, quantity FROM book WHERE lower(trim(title)) = lower(trim(?)) AND lower(trim(author)) = lower(trim(?))";
                try (PreparedStatement checkpst = c.prepareStatement(checkQuery)) {
                    checkpst.setString(1, title);
                    checkpst.setString(2, author);
                    try (ResultSet chckrs = checkpst.executeQuery()) {
                        if (chckrs.next()) {
                            //Book Exists, updating quantity
                            int existingQuantity = chckrs.getInt("quantity");
                            int book_id = chckrs.getInt("book_id");

                            String updateQuery = "UPDATE book SET quantity = ? WHERE book_id = ?";
                            try (PreparedStatement updatepst = c.prepareStatement(updateQuery)) {
                                updatepst.setInt(1, existingQuantity + quantity);
                                updatepst.setInt(2, book_id);
                                updatepst.executeUpdate();
                            }

                            System.out.println("\nBook exists, quantity updated...");

                            System.out.println("Book ID | Title           | Author          | Quantity");
                            System.out.println("-------------------------------------------------------");
                            System.out.printf("%7d | %-15s | %-15s | %8d\n", chckrs.getInt("book_id"), chckrs.getString("title"), chckrs.getString("author"), existingQuantity + quantity);

                        } else {
                            //Inserting new book and generating id
                            String bookCreation = "INSERT INTO book(title, author, quantity) VALUES (?, ?, ?)";
                            try (PreparedStatement insertpst = c.prepareStatement(bookCreation, Statement.RETURN_GENERATED_KEYS)) {
                                insertpst.setString(1, title);
                                insertpst.setString(2, author);
                                insertpst.setInt(3, quantity);
                                insertpst.executeUpdate();

                                try (ResultSet keys = insertpst.getGeneratedKeys()) {
                                    if (keys.next()) {
                                        int newId = keys.getInt(1);
                                        System.out.println("Book added successfully...");

                                        System.out.println("Book ID | Title           | Author          | Quantity");
                                        System.out.println("-------------------------------------------------------");
                                        System.out.printf("%7d | %-15s | %-15s | %8d\n", newId, title, author, quantity);

                                    }
                                }
                            }
                        }
                    }
                }

                // Stop loop (or ask if the user wants to add another)
                System.out.print("Do you want to add more books(Y/N): ");
                String choice = sc.nextLine().trim();
                if (!choice.equalsIgnoreCase("y")) {
                    System.out.println("Thanks for coming to the library...");
                    adding = false;
                }

            }

        } catch (SQLException s) {
            System.out.println("Database error: " + s.getMessage());
        }
    }

    public void getAllBook() {
        try (Statement s = c.createStatement()) {
            String query = "select * from book";
            ResultSet rs = s.executeQuery(query);

            System.out.println("Book ID | Title           | Author          | Quantity");
            System.out.println("-------------------------------------------------------");
            while (rs.next()) {

                System.out.printf("%7d | %-15s | %-15s | %8d\n", rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity"));
            }
        } catch (SQLException s) {
            System.out.println("Error while fetching details! " + s.getMessage());
        }
    }

    public void getSpecificBook() {

        try {
            System.out.print("Enter book name: ");
            String bookName = sc.nextLine();

            String query = "select * from book where title like ?";

            try (PreparedStatement pst = c.prepareStatement(query);) {
                pst.setString(1, "%" + bookName + "%");

                try (ResultSet rs = pst.executeQuery();) {
                    boolean found = false;
                    System.out.println("Book ID | Title           | Author          | Quantity");
                    System.out.println("-------------------------------------------------------");
                    while (rs.next()) {
                        found = true;
                        System.out.printf("%7d | %-15s | %-15s | %5d\n", rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity"));

                    }
                    if (!found) {
                        System.out.println("Book not found!");
                    }
                }
            }

        } catch (SQLException s) {
            System.out.println("Error: " + s.getMessage());
        }
    }

    public void deleteBook() {
        boolean done = false;
        try {
            while (!done) {
                System.out.print("Enter book title: ");
                String titleCheck = sc.nextLine().trim();
                System.out.print("Enter author name: ");
                String authorCheck = sc.nextLine().trim();

                String checkQuery = "Select book_id,title,author,quantity from book where lower(trim(title))=lower(trim(?)) and lower(trim(author))=lower(trim(?))";
                PreparedStatement checkpst = c.prepareStatement(checkQuery);
                checkpst.setString(1, titleCheck);
                checkpst.setString(2, authorCheck);

                try (ResultSet rs = checkpst.executeQuery()) {
                    boolean found = false;
                    if (rs.next()) {
                        found = true;
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        int quantity = rs.getInt("quantity");
                        int id = rs.getInt("book_id");

                        System.out.println("Record found...");
                        System.out.println("Book ID | Title           | Author          | Quantity");
                        System.out.println("-------------------------------------------------------");
                        System.out.printf("%7d | %-15s | %-15s | %5d\n", id, title, author, quantity);

                        while (true) {
                            System.out.print("Do you want to delete this record(Y/N): ");
                            String agreed = sc.nextLine();
                            if (agreed.equalsIgnoreCase("y")) {
                                String deleteQuery = "delete from book where book_id = ?";
                                PreparedStatement deletepst = c.prepareStatement(deleteQuery);
                                deletepst.setInt(1, id);
                                deletepst.executeUpdate();
                                System.out.println("Book deleted successfully...");
                                break;
                            } else if (agreed.equalsIgnoreCase("n")) {
                                System.out.println("Delete cancelled.");
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter Y or N.");
                            }
                        }
                    } else if (!found) {
                        System.out.println("Book not found! Please try other keywords..");
                    }

                    while (true) {
                        System.out.print("Do you want to delete another book? (Y/N): ");
                        String response = sc.nextLine().trim();

                        if (response.equalsIgnoreCase("y")) {
                            break; // continue outer loop
                        } else if (response.equalsIgnoreCase("n")) {
                            done = true; // exit loop
                            System.out.println("Exited Successfully...");
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter Y or N.");
                        }
                    }
                }
            }
            // end while (!done)
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Book b = new Book();
//        b.deleteBook();
//        b.addBook();
//        b.getAllBook();
//        b.getSpecificBook();

    }
}