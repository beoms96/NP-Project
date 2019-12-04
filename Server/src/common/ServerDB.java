package common;

import java.sql.*;
import java.util.ArrayList;
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

    private ServerRSA srsa;
    private HashMap<String, String> rsaKeyPair;
    private String privateKey;
    private String[] publicKeyList;

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

    public boolean insertInfo(String iid, String ipw) {
        dbConnect();
        srsa = new ServerRSA();
        rsaKeyPair = srsa.createKeyPairAsString();
        String publicKey = rsaKeyPair.get("publicKey");
        privateKey = rsaKeyPair.get("privateKey");
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
            sql = "SELECT ID, PW FROM info";
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                String getID = rs.getString("id");
                String getPW = rs.getString("pw");
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

    public String getPrivateKey() {
        return privateKey;
    }

    public String[] getPublicKeyList(String[] id) {
        dbConnect();
        ArrayList<String> arr = new ArrayList<String>();
        try {
            state = con.createStatement();
            String sql = "";
            String str = "";
            for (int i=0;i<id.length;i++) {
                if(i != id.length-1)
                    str = str.concat("'" + id[i] + "', ");
                else
                    str = str.concat("'" + id[i] + "'");
            }
            sql = "SELECT PK FROM info Where id in (" + str + ")";
            ResultSet rs = state.executeQuery(sql);
            while(rs.next()) {
                String getPK = rs.getString("pk");
                arr.add(getPK);
            }
            rs.close();
            state.close();
            dbDisconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            dbDisconnect();
        }
        publicKeyList = arr.toArray(new String[arr.size()]);
        return publicKeyList;
    }

    public String getFUPublicKey(String firstUser) {
        dbConnect();
        String result = "";
        try {
            state = con.createStatement();
            String sql = "";
            sql = "SELECT PK FROM info Where id = '" + firstUser + "'";
            ResultSet rs = state.executeQuery(sql);
            while(rs.next()) {
                String getPK = rs.getString("pk");
                result = getPK;
            }
            rs.close();
            state.close();
            dbDisconnect();
        } catch(SQLException e) {
            e.printStackTrace();
            dbDisconnect();
            result = null;
        }
        return result;
    }
}

