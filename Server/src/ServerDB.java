import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ServerDB {
    //mysql -u root -p
    //use user
    private Connection con = null;
    private Statement state = null;

    private String server = "localhost";    //MySQL Server Address
    private String database = "user";   //MySQL DB name
    private String user_name = "root";  //MysQL Server ID
    private String password = "123456";

    private HashMap<String, String> rsaKeyPair;
    private String publicKey;

    public ServerDB() {
        // 1. Driver loading
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(ClassNotFoundException e) {
            System.err.println(" !! <JDBC Error> Driver load Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void dbConnect() {
        // 2. Connect
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?serverTimezone = UTC & useSSL=false", user_name, password);
            System.out.println("Connect Successfully");
        } catch(SQLException e) {
            System.err.println("Connect Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void dbDisconnect() {
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
