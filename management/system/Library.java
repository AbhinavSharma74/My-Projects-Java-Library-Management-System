package library.management.system;

import java.util.Scanner;

public class Library {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Book book = new Book();
        IssuedBook issue = new IssuedBook();
        System.out.println("===========Welcome to the Library==========");

        boolean flag = true;
        while (flag) {
            System.out.println("1.Add Book");
            System.out.println("2.All available Book");
            System.out.println("3.Get Specific Book");
            System.out.println("4.Delete Book");
            System.out.println("5.Issue Book");
            System.out.println("6.Return Book");
            System.out.println("7.Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" ->
                    book.addBook();
                case "2" ->
                    book.getAllBook();
                case "3" ->
                    book.getSpecificBook();
                case "4" ->
                    book.deleteBook();
                case "5" ->
                    issue.issueBook();
                case "6" ->
                    issue.bookReturned();
                case "7" -> {
                    flag = false;
                    System.out.println("Thanks for coming to library! Sucessfully exited Library...");
                }
                default ->
                    System.out.println("Invalid choice select from 1-7");

            }

        }
    }

}