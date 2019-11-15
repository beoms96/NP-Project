import java.sql.*;

public class UserData { //Manage UserData In Database
    //mysql -u root -p
    //use user
    public static void main(String[] args) {
        Connection con = null;
        Statement state = null;

        String server = "localhost";    //MySQL Server Address
        String database = "user";   //MySQL DB name
        String user_name = "root";  //MysQL Server ID
        String password = "ky12091010";

        // 1. Driver loading
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(ClassNotFoundException e) {
            System.err.println(" !! <JDBC Error> Driver load Error: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Connect
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?serverTimezone = UTC & useSSL=false", user_name, password);
            System.out.println("Connect Successfully");
            state = con.createStatement();
            String sql = "";
            sql = "SELECT * FROM info";
            ResultSet rs = state.executeQuery(sql);

            while(rs.next()) {
                String id = rs.getString("id");
                String pw = rs.getString("pw");
                System.out.println(id +" "+ pw);
            }

            state.close();
            rs.close();
            con.close();

        } catch(SQLException e) {
            System.err.println("Connect Error: " + e.getMessage());
            e.printStackTrace();
        }

        // 3.Disconnect
        try {
            if(state!=null)
                state.close();
        } catch(SQLException e) {}

        try {
            if(con!=null) {
                con.close();
            }
        } catch(SQLException e) {}

        System.out.println("MySQL Close");
    }
}
