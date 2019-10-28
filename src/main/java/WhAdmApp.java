import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author Tomas Dutkus
 */

public class WhAdmApp {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/whadmin?serverTimezone=EET";
    private static final String USER = "springstudent";
    private static final String PASS = "springstudent";

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        boolean quit = false;
        int choice;
        printInstructions();
        while (!quit) {
            System.out.println("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 0:
                    printInstructions();
                    break;
                case 1:
                    createTable();
                    break;
                case 2:
                    populateDB();
                    break;
                case 3:
                    clearDB();
                    break;
                case 4:
                    viewDB();
                    break;
                case 5:
                    System.out.println("Enter desired quantity: ");
                    showMissing(sc.nextInt());
                    break;
                case 6:
                    System.out.println("Enter desired date(Format 2019-01-01): ");
                    showExpiring(sc.next());
                    break;
                case 7:
                    System.out.println("Enter desired date(Format 2019-01-01): ");
                    showExpired(sc.next());
                case 8:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        } sc.close();
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

    private static void createTable() throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "CREATE TABLE IF NOT EXISTS goods(id int(11) not null auto_increment, item_name varchar(30), code bigint(30), quantity int(11), expiration_date date, primary key(id))";
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
        con.close();

    }

    private static void populateDB() throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/sample.csv"));
        String line;

        //skipping first line
        br.readLine();

        while ((line = br.readLine()) != null) {

            String[] goods = line.split(",");

            String query = "INSERT INTO goods values(null,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, goods[0]);
            ps.setString(2, goods[1]);
            ps.setString(3, goods[2]);
            ps.setString(4, goods[3]);
            ps.executeUpdate();
        }
        con.close();
    }

    private static void clearDB() throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "TRUNCATE TABLE goods";
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
        con.close();
    }

    private static void viewDB() throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "GROUP BY item_name, code, expiration_date ASC;\n";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnsNumber = rsmd.getColumnCount();
        System.out.println("Item Name - Code - Quantity - Expiration Date");

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();
    }

    private static void showMissing(int input) throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "WHERE quantity < " + input + " \n" +
                "GROUP BY item_name, code, expiration_date ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnsNumber = rsmd.getColumnCount();
        System.out.println("Item Name - Code - Quantity - Expiration Date");

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();

    }

    private static void showExpiring(String input) throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(input, formatter);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "WHERE DATE (expiration_date) < ('" + date + "')\n" +
                "GROUP BY item_name, code, expiration_date ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        System.out.println("Item Name - Code - Quantity - Expiration Date");

        int columnsNumber = rsmd.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();
    }

    private static void showExpired(String input) throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(input, formatter);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "WHERE DATE (expiration_date) > ('" + date + "')\n" +
                "GROUP BY item_name, code, expiration_date ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        System.out.println("Item Name - Code - Quantity - Expiration Date");

        int columnsNumber = rsmd.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();
    }
}