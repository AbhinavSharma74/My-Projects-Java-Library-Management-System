package library.management.system;

import java.sql.*;
import java.util.Scanner;

public class IssuedBook extends Conn {

    int issued_id;
    int book_id;
    String book_name;
    String username;

    Scanner sc = new Scanner(System.in);

    public void issueBook() {
        try {
            System.out.print("Enter book name: ");
            book_name = sc.nextLine();
            System.out.print("Enter username: ");
            username = sc.nextLine();

            String query = "Select * from book where title like ?";
            PreparedStatement pst = c.prepareStatement(query);
            pst.setString(1, "%" + book_name + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                book_id = rs.getInt("book_id");
                int quantity = rs.getInt("quantity");
                if (quantity > 0) {

                    //Updating Book quantity in the book table
                    String updateBook = "update book set quantity = quantity - 1 where book_id = ?";
                    PreparedStatement pstUpdate = c.prepareStatement(updateBook);
                    pstUpdate.setInt(1, book_id);
                    pstUpdate.executeUpdate();

                    //Inserting into issuedBook table
                    String insertQuery = "insert into issuedbooks (book_id,username,issued_date) values(?,?,?)";
                    PreparedStatement pstInsert = c.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                    pstInsert.setInt(1, book_id);
                    pstInsert.setString(2, username);

                    //Converting java.date to sql.date
                    java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
                    pstInsert.setDate(3, date);
                    pstInsert.executeUpdate();

                    ResultSet generatedKeys = pstInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        issued_id = generatedKeys.getInt(1);
                    }

                    System.out.println("Issued ID | Username           | Issued Date");
                    System.out.println("--------------------------------------------");
                    System.out.printf("%9d | %-13s | %s\n", issued_id, username, date);

                } else {
                    System.out.println("Book not available at this time...");
                }
            } else {
                System.out.println("Book not found!");
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public void bookReturned() {
        boolean done = false;
        try {
            while (!done) {
                System.out.print("Enter book name: ");
                String inbook_name = sc.nextLine().trim();
                System.out.print("Enter username: ");
                String inusername = sc.nextLine().trim();

                String selectQuery = "SELECT ib.issued_id, ib.book_id, b.title, ib.username "
                        + "FROM issuedbooks ib JOIN book b ON ib.book_id = b.book_id "
                        + "WHERE LOWER(b.title) LIKE LOWER(?) AND ib.username = ? AND ib.return_date IS NULL";

                try (PreparedStatement selectpst = c.prepareStatement(selectQuery);) {
                    selectpst.setString(1, "%" + inbook_name + "%");
                    selectpst.setString(2, inusername);

                    try (ResultSet rs = selectpst.executeQuery();) {

                        if (rs.next()) {
                            int issue_id = rs.getInt("issued_id");
                            int book_id = rs.getInt("book_id");
                            String title = rs.getString("title");

                            java.sql.Date returnsqlDate = new java.sql.Date(new java.util.Date().getTime());

                            //Update quantity
                            String updateQuantity = "UPDATE book SET quantity = quantity + 1 WHERE book_id = ?";
                            try (PreparedStatement updtpst = c.prepareStatement(updateQuantity);) {
                                updtpst.setInt(1, book_id);
                                updtpst.executeUpdate();
                            }

                            //Insert New record
                            String returnDateQuery = "UPDATE issuedbooks SET return_date = ? WHERE issued_id = ?";
                            try (PreparedStatement updtpst1 = c.prepareStatement(returnDateQuery);) {
                                updtpst1.setDate(1, returnsqlDate);
                                updtpst1.setInt(2, issue_id);
                                updtpst1.executeUpdate();
                            }

                            System.out.println("Book returned successfully.");
                            System.out.println("Issued ID | Username           | Issued Date");
                            System.out.println("--------------------------------------------");
                            System.out.printf("%9d | %-13s | %s\n", issue_id, inusername, returnsqlDate);
                        } else {
                            System.out.println("Book not found! Try other keywords.");
                        }
                    }
                }
                while (true) {
                    System.out.print("Do you want to return another book (Y/N): ");
                    String choice = sc.nextLine().trim();
                    if (choice.equalsIgnoreCase("y")) {
                        break;
                    } else if (choice.equalsIgnoreCase("n")) {
                        done = true;
                        break;
                    } else {
                        System.out.println("Invalid input! Please enter Y or N.");
                    }
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IssuedBook i = new IssuedBook();
        i.bookReturned();
//        i.issueBook();

    }
}
