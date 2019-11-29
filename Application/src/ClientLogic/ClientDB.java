package ClientLogic;

import Crypto.CliRSA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class ClientDB {
    //mysql -u root -p
    //use user
    private Connection con = null;
    private Statement state = null;

    private String server = "localhost";    //MySQL Server Address
    private String database = "user";   //MySQL DB name
    private String user_name = "root";  //MysQL Server ID
    private String password = "123456";

    private CliRSA crsa;
    private HashMap<String, String> rsaKeyPair;
    private String publicKey;

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
        dbConnect();
        crsa = new CliRSA();
        rsaKeyPair = crsa.createKeyPairAsString();
        String publicKey = rsaKeyPair.get("publicKey");
        try {
            BufferedWriter bw1 = new BufferedWriter(new FileWriter("PrivateKey.txt", true));
            bw1.append(new String(iid + " " + rsaKeyPair.get("privateKey")));
            bw1.newLine();
            bw1.close();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            state = con.createStatement();
            String sql = "";
            sql = "Insert into info(id, pw, PK) values('" + iid + "', '" + ipw + "', '" + publicKey + "');";
            state.executeUpdate(sql);
            state.close();
            dbDisconnect();
            return true;
        } catch (SQLException e) {
            dbDisconnect();
            return false;
        }
    }

    public boolean checkInfo(String iid, String ipw) {    //check info and then if success, move next page
        dbConnect();
        try {
            state = con.createStatement();
            String sql = "";
            sql = "SELECT ID, PW, PK FROM info";
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                String getID = rs.getString("id");
                String getPW = rs.getString("pw");
                publicKey = rs.getString("pk");
                if (iid.equals(getID) && ipw.equals(getPW)) {
                    rs.close();
                    state.close();
                    con.close();
                    dbDisconnect();
                    return true;
                }
            }
            rs.close();
            state.close();
            dbDisconnect();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            dbDisconnect();
            return false;
        }
    }

    public String getPublicKey() {
        return publicKey;
    }
}
