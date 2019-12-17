import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Helper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/whadmin?serverTimezone=EET";
    private static final String USER = "springstudent";
    private static final String PASS = "springstudent";

    public static void createTable() throws Exception {
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "CREATE TABLE IF NOT EXISTS goods(id int(11) not null auto_increment, item_name varchar(30), code bigint(30), quantity int(11), expiration_date date, primary key(id))";
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
        con.close();

    }

    public static void populateDB() throws Exception {
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

    public static void clearDB() throws Exception {
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "TRUNCATE TABLE goods";
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
        con.close();
    }

    public static void viewDB() throws Exception {
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "GROUP BY item_name, code, expiration_date ASC;\n";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnsNumber = rsmd.getColumnCount();
        System.out.println("Item Name - Code - Expiration Date - Quantity");

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();
    }

    public static void showMissing(int input) throws Exception {
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        String query = "SELECT item_name, code, expiration_date, SUM(quantity) as quantity\n" +
                "FROM goods\n" +
                "WHERE quantity < " + input + " \n" +
                "GROUP BY item_name, code, expiration_date ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnsNumber = rsmd.getColumnCount();
        System.out.println("Item Name - Code - Expiration Date - Quantity");

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();

    }

    public static void showExpiring(String input) throws Exception {
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
        System.out.println("Item Name - Code - Expiration Date - Quantity");

        int columnsNumber = rsmd.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {

                System.out.print(rs.getString(i) + " | ");

            }

            System.out.println();

        }
        con.close();
    }

    public static void showExpired(String input) throws Exception {
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
        System.out.println("Item Name - Code - Expiration Date - Quantity");

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
