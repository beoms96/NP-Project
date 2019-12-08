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
    private String[] idListFromDB;
    private String[] publicKeyListFromDB;

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

    public void updateIdAndPublicKeyList(String[] iid) {
        dbConnect();
        ArrayList<String> idArr = new ArrayList<String>();
        ArrayList<String> pkArr = new ArrayList<String>();
        try {
            state = con.createStatement();
            String sql = "";
            String str = "";
            for (int i=0;i<iid.length;i++) {
                if(i != iid.length-1)
                    str = str.concat("'" + iid[i] + "', ");
                else
                    str = str.concat("'" + iid[i] + "'");
            }
            if(iid.length==0) {
                str = "''";
            }

            sql = "SELECT id, PK FROM info Where id in (" + str + ")";
            ResultSet rs = state.executeQuery(sql);
            while(rs.next()) {
                String getId = rs.getString("id");
                String getPK = rs.getString("pk");
                idArr.add(getId);
                pkArr.add(getPK);
            }
            rs.close();
            state.close();
            dbDisconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            dbDisconnect();
        }

        idListFromDB = idArr.toArray(new String[idArr.size()]);
        publicKeyListFromDB = pkArr.toArray(new String[pkArr.size()]);
    }

    public String[] getIdListFromDB() {
        return idListFromDB;
    }

    public String[] getPublicKeyListFromDB() {
        return publicKeyListFromDB;
    }
}

