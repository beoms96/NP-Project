package ClientLogic;

import Crypto.CliAES;
import Crypto.CliRSA;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

public class MultiClient {
    //Member
    private Socket socket;
    private Socket fileSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private MultiClientThread ct;
    private FileClientThread fct;
    private CrypMultiClientThread cct;
    private CrypFileClientThread cfct;

    private JFrame jf;
    private JTextArea jta;
    private JTextArea idList;
    private JTextArea serverList;

    private String id, pk;
    private String ip;
    private int check;
    private ArrayList<String> idarr;
    private ArrayList<String> filearr;

    private CliAES caes;
    private CliRSA crsa;

    private ClientDB cdb;
    private String chatAESKey;
    private String firstUser;
    private HashMap<String, String> publicKeyList;


    //Constructor
    public MultiClient(String id, String ip, int check) {
        this.id = id;
        this.ip = ip;
        this.check = check;
        idarr = new ArrayList<String>();
        filearr = new ArrayList<String>();
    }

    public MultiClient(String id, String ip, String pk, int check) {
        this.id = id;
        this.ip = ip;
        this.pk = pk;
        this.check = check;
        idarr = new ArrayList<String>();
        filearr = new ArrayList<String>();
        this.firstUser = "";

        caes = new CliAES();
        crsa = new CliRSA();
        cdb = new ClientDB();
        publicKeyList = new HashMap<>();
    }

    public void connect() throws IOException {
        if(check==0) {
            socket = new Socket(ip, 8000);
            fileSocket = new Socket(ip, 9000);
        }
        else if(check==1) {
            socket = new Socket(ip, 10000);
            fileSocket = new Socket(ip, 11000);
        }
        jta.setText("Connect Success" + System.getProperty("line.separator"));
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        dos = new DataOutputStream(fileSocket.getOutputStream());
        dis = new DataInputStream(fileSocket.getInputStream());
        if(check == 0) {
            ct = new MultiClientThread(this);
            fct = new FileClientThread(this);
            Thread t = new Thread(ct);
            t.start();
            Thread fc = new Thread(fct);
            fc.start();
        }
        else if(check==1) {
            cct = new CrypMultiClientThread(this);
            cfct = new CrypFileClientThread(this);
            Thread t = new Thread(cct);
            t.start();
            Thread fc = new Thread(cfct);
            fc.start();
        }
        enter();
    }

    public void sendNormal(String msg) {
        try {
            oos.writeObject(id + "#" + msg);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void uploadNormal(File[] files, String path) {
        int loadVersion = 0;
        ArrayList<File> arr = new ArrayList<File>();
        for (File f: files)
            arr.add(f);
        fct.setFileList(arr);
        fct.setPath(path);
        fct.setLoadVersion(loadVersion);
        try {
            dos.writeUTF("upload");
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void downloadNormal(String filename) {
        int loadVersion = 1;
        fct.setFileName(filename);
        fct.setLoadVersion(loadVersion);
        try {
            dos.writeUTF("download");
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void streamNormal() {

    }

    public void sendCrypto(String msg) throws UnsupportedEncodingException, GeneralSecurityException{
        caes.createKey(chatAESKey);
        caes.modeEncrypt();
        String result = caes.msgAESEncrypt(msg);
        System.out.println(result);
        try {
            oos.writeObject(id + "#" + result);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void uploadCrypto(File[] files, String path, String key) throws UnsupportedEncodingException{
        caes.createKey(key);
        int loadVersion = 0;
        ArrayList<File> arr = new ArrayList<File>();
        for (File f: files)
            arr.add(f);
        cfct.setFileList(arr);
        cfct.setPath(path);
        cfct.setLoadVersion(loadVersion);
        cfct.setEncryptFile(new ArrayList<File>());
        try {
            dos.writeUTF("upload");
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void downloadCrypto(String filename, String key) throws UnsupportedEncodingException{
        caes.createKey(key);
        int loadVersion = 1;
        cfct.setFileName(filename);
        cfct.setLoadVersion(loadVersion);
        try {
            dos.writeUTF("download");
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void streamCrypto() {

    }

    public void enter() {
        try {
            oos.writeObject(id+"#Enter");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void exit() {
        try {
            oos.writeObject(id+"#quit");
            dos.writeUTF("quit");
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public String getPrivateKey() {
        String sPrivateKey = null;
        BufferedReader brPrivateKey = null;
        try{
            brPrivateKey = new BufferedReader(new FileReader(id + " " + "PrivateKey.txt"));
            sPrivateKey = brPrivateKey.readLine();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try{
                if(brPrivateKey != null)
                    brPrivateKey.close();
            } catch (IOException ioe) { ioe.printStackTrace(); }
        }
        return sPrivateKey;
    }

    public void useJf(JFrame jf) { this.jf = jf;}

    public void useJta(JTextArea jta) { this.jta = jta; }

    public void useIdList(JTextArea idList) {this.idList = idList;}

    public void useServerList(JTextArea serverList) {this.serverList = serverList;}

    public String getId() { return id; }

    public ObjectOutputStream getOos() { return oos; }

    public ObjectInputStream getOis() { return ois; }

    public DataInputStream getDis() { return dis; }

    public DataOutputStream getDos() { return dos; }

    public ArrayList<String> getFilearr() { return filearr; }

    public ArrayList<String> getIdarr() { return idarr; }

    public HashMap<String, String> getPublicKeyList() { return publicKeyList; }

    public String getChatAESKey() { return chatAESKey; }

    public String getFirstUser() { return firstUser; }

    public CliAES getCaes() { return caes; }

    public CliRSA getCrsa() { return crsa; }

    public ClientDB getCdb() { return cdb; }

    public void setFirstUser(String firstUser) { this.firstUser = firstUser; }

    public void setFilearr(ArrayList<String> filearr) { this.filearr = filearr; }

    public void setIdarr(ArrayList<String> idarr) { this.idarr = idarr; }

    public void setChatAESKey(String chatAESKey) { this.chatAESKey = chatAESKey; }

    public JFrame getJf() { return jf; }

    public JTextArea getJta() { return jta; }

    public JTextArea getIdList() { return idList; }

    public JTextArea getServerList() { return serverList; }

}
