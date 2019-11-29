package ClientLogic;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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

    private JFrame jf;
    private JTextArea jta;
    private JTextArea idList;
    private JTextArea serverList;

    private String id, pk;
    private String ip;
    private ArrayList<String> idarr;
    private ArrayList<String> filearr;

    private ArrayList<String> publicKeyList;
    private String myPrivateKey;

    //Constructor
    public MultiClient(String id, String ip) {
        this.id = id;
        this.ip = ip;
        idarr = new ArrayList<String>();
        filearr = new ArrayList<String>();
    }

    public MultiClient(String id, String ip, String pk) {
        this.id = id;
        this.ip = ip;
        this.pk = pk;
        idarr = new ArrayList<String>();
        filearr = new ArrayList<String>();
    }

    public void connect() throws IOException {
        socket = new Socket(ip, 8000);
        fileSocket = new Socket(ip, 9000);
        jta.setText("Connect Success" + System.getProperty("line.separator"));
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        dos = new DataOutputStream(fileSocket.getOutputStream());
        dis = new DataInputStream(fileSocket.getInputStream());
        ct = new MultiClientThread(this);
        fct = new FileClientThread(this);
        Thread t = new Thread(ct);
        t.start();
        Thread fc = new Thread(fct);
        fc.start();
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
        jf.setVisible(false);
        System.exit(0);
    }

    public void useJf(JFrame jf) { this.jf = jf;}

    public void useJta(JTextArea jta) { this.jta = jta; }

    public void useIdList(JTextArea idList) {this.idList = idList;}

    public void useServerList(JTextArea serverList) {this.serverList = serverList;}

    public String getId() { return id; }

    public ObjectInputStream getOis() { return ois; }

    public DataInputStream getDis() { return dis; }

    public DataOutputStream getDos() { return dos; }

    public ArrayList<String> getFilearr() { return filearr; }

    public ArrayList<String> getIdarr() { return idarr; }

    public void setFilearr(ArrayList<String> filearr) { this.filearr = filearr; }

    public void setIdarr(ArrayList<String> idarr) { this.idarr = idarr; }

    public JFrame getJf() { return jf; }

    public JTextArea getJta() { return jta; }

    public JTextArea getIdList() { return idList; }

    public JTextArea getServerList() { return serverList; }

}
