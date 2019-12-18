import java.util.Scanner;

/**
 * @author Tomas Dutkus
 */

public class WhAdmApp {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        boolean quit = false;
        String choice;
        printInstructions();
        while (!quit) {
            System.out.println("Enter your choice: ");
            choice = sc.next();
            sc.nextLine();

            switch (choice) {
                case "0":
                    printInstructions();
                    break;
                case "1":
                    Helper.createTable();
                    break;
                case "2":
                    Helper.populateDB();
                    break;
                case "3":
                    Helper.clearDB();
                    break;
                case "4":
                    Helper.viewDB();
                    break;
                case "5":
                    System.out.println("Enter desired quantity: ");
                    Helper.showMissing(sc.nextInt());
                    break;
                case "6":
                    System.out.println("Enter desired date(Format 2019-01-01): ");
                    Helper.showExpiring(sc.next());
                    break;
                case "7":
                    System.out.println("Enter desired date(Format 2019-01-01): ");
                    Helper.showExpired(sc.next());
                case "8":
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
        sc.close();
    }

    private static void printInstructions() {
        System.out.println("\nPress ");
        System.out.println("\t 0 - To print choice options.");
        System.out.println("\t 1 - To create database table if it doesn't exist.");
        System.out.println("\t 2 - To get content of csv file into database.");
        System.out.println("\t 3 - To clear database table content.");
        System.out.println("\t 4 - To view database content.");
        System.out.println("\t 5 - To show goods, that are missing in quantity.");
        System.out.println("\t 6 - To show goods, that are going to expire by entered date.");
        System.out.println("\t 7 - To show expired goods by entered date.");
        System.out.println("\t 8 - To quit the application.");
    }
}