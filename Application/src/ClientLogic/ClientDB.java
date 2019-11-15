package ClientLogic;

import java.sql.*;

public class ClientDB {
    //mysql -u root -p
    //use user
    private Connection con = null;
    private Statement state = null;

    private String server = "localhost";    //MySQL Server Address
    private String database = "user";   //MySQL DB name
    private String user_name = "root";  //MysQL Server ID
    private String password = "ky12091010";

    public ClientDB() {
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

    public boolean insertInfo(String iid, String ipw) {
        try {   //공개키 여기서 만들어서 삽입. (id로 만들면 될 듯)
            state = con.createStatement();
            String sql = "";
            sql = "INSERT into info(id,pw) values (iid, ipw)";
            state.executeUpdate(sql);
            state.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            dbDisconnect();
            return false;
        }
    }

    public boolean checkInfo(String id, String pw) {    //check info and then if success, move next page
        try {
            state = con.createStatement();
            String sql = "";
            sql = "SELECT ID, PW FROM info";
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                String getID = rs.getString("id");
                String getPW = rs.getString("pw");
                if (id.equals(getID) && pw.equals(getPW)) {
                    rs.close();
                    state.close();
                    con.close();
                    return true;
                }
            }
            rs.close();
            state.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            dbDisconnect();
            return false;
        }
    }
}
