package ClientLogic;

import Crypto.CliAES;
import Crypto.CliRSA;
import Streaming.*;

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
    private Socket videoSocket;
    private Socket rcvSocket;
    private Socket audioSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private DataInputStream streamis;
    private DataOutputStream streamos;
    private DataInputStream rcvstreamis;
    private BufferedOutputStream audioos;
    private BufferedInputStream audiois;

    private MultiClientThread ct;
    private FileClientThread fct;
    private CrypMultiClientThread cct;
    private CrypFileClientThread cfct;

    private JFrame jf;
    private JTextArea jta;
    private JTextArea idList;
    private JTextArea serverList;

    private String id;
    private String ip;
    private int check;
    private ArrayList<String> idarr;
    private ArrayList<String> filearr;

    private CliAES caes;
    private CliRSA crsa;

    private String chatAESKey;
    private HashMap<String, String> publicKeyList;
    private boolean chatSet;

    private String streamUser;
    private boolean isStop;

    private boolean isFTP;

    //Constructor
    public MultiClient(String id, String ip, int check) {
        this.id = id;
        this.ip = ip;
        this.check = check;
        idarr = new ArrayList<String>();
        filearr = new ArrayList<String>();
        streamUser = "";
        isStop = true;

        caes = new CliAES();
        crsa = new CliRSA();
        publicKeyList = new HashMap<>();
        chatSet = false;

        isFTP = false;
    }

    public void connect() throws IOException {
        if(check==0) {
            socket = new Socket(ip, 8000);
            fileSocket = new Socket(ip, 9000);
            videoSocket = new Socket(ip, 12000);
            rcvSocket = new Socket(ip, 14000);
            audioSocket = new Socket(ip, 16000);
        }
        else if(check==1) {
            socket = new Socket(ip, 10000);
            fileSocket = new Socket(ip, 11000);
            videoSocket = new Socket(ip, 13000);
            rcvSocket = new Socket(ip, 15000);
            audioSocket = new Socket(ip, 17000);
        }
        jta.setText("Connect Success" + System.getProperty("line.separator"));
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        dos = new DataOutputStream(fileSocket.getOutputStream());
        dis = new DataInputStream(fileSocket.getInputStream());
        streamos = new DataOutputStream(videoSocket.getOutputStream());
        audioos = new BufferedOutputStream(audioSocket.getOutputStream());

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

    public void streamVideoNormal(String filename) {
        try {
            streamis = new DataInputStream(videoSocket.getInputStream());
            isStop = false;
            streamos.writeUTF("Video");
            streamos.writeUTF(filename);
            rcvstreamis = new DataInputStream(rcvSocket.getInputStream());
            ReceiveVideo rv = new ReceiveVideo(this, filename);
            Thread rvt = new Thread(rv);
            rvt.start();
        }catch(IOException ioe) {ioe.printStackTrace();}
    }

    public void streamWebCamNormal() {
        try {
            streamis = new DataInputStream(videoSocket.getInputStream());
            isStop = false;
            streamos.writeUTF(id+"#Enter");
            streamUser = streamis.readUTF();
            if(streamUser.equals("")) { //become Streaming Owner
                streamUser = id;
                streamos.writeUTF(streamUser);
                MyWebCam mwc = new MyWebCam(this);
                MyAudio ma = new MyAudio(this);
                Thread mwct = new Thread(mwc);
                Thread mat = new Thread(ma);
                mwct.start();
                mat.start();
            }
            else {  //become Streaming Client
                streamUser = streamis.readUTF();
            }
            rcvstreamis = new DataInputStream(rcvSocket.getInputStream());
            audiois = new BufferedInputStream(audioSocket.getInputStream());
            ReceiveWebCam rwc = new ReceiveWebCam(this);
            ReceiveAudio ra = new ReceiveAudio(this);
            Thread rwct = new Thread(rwc);
            Thread rat = new Thread(ra);
            rwct.start();
            rat.start();

        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void sendCrypto(String msg) throws UnsupportedEncodingException, GeneralSecurityException{
        caes.createKey(chatAESKey);
        caes.modeEncrypt();
        String result = caes.msgAESEncrypt(msg);
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

    public void streamVideoCrypto(String filename, String key){
        try {
            streamis = new DataInputStream(videoSocket.getInputStream());
            isStop = false;
            streamos.writeUTF(id + "#Video");
            streamos.writeUTF(filename);
            streamos.writeUTF(key);
            rcvstreamis = new DataInputStream(rcvSocket.getInputStream());
            ReceiveVideo rv = new ReceiveVideo(this, filename);
            Thread rvt = new Thread(rv);
            rvt.start();
        }catch(IOException ioe) {ioe.printStackTrace();}

    }

    public void streamWebCamCrypto(String key) {
        try {
            streamis = new DataInputStream(videoSocket.getInputStream());
            isStop = false;
            streamos.writeUTF("Enter");
            streamUser = streamis.readUTF();
            if(streamUser.equals("")) { //become Streaming Owner
                streamUser = id;
                streamos.writeUTF(streamUser);
                CrypWebCam mwc = new CrypWebCam(this, key);
                Thread mwct = new Thread(mwc);
                mwct.start();
            }
            else {  //become Streaming Client
                streamUser = streamis.readUTF();
            }
            rcvstreamis = new DataInputStream(rcvSocket.getInputStream());
            CrypRcvWebCam rwc = new CrypRcvWebCam(this, key);
            Thread rwct = new Thread(rwc);
            rwct.start();

        }catch(IOException ioe) { ioe.printStackTrace(); }
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
            streamos.writeUTF(id+"#quit");
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

    public ArrayList<String> getMp4Files(String[] files) {
        ArrayList<String> mp4Files = new ArrayList<String>();
        for(String fname: files) {
            if(fname.contains(".mp4")) {
                mp4Files.add(fname);
            }
        }
        return mp4Files;
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

    public DataOutputStream getStreamos() { return streamos; }

    public DataInputStream getRcvstreamis() { return rcvstreamis; }

    public BufferedOutputStream getAudioos() { return audioos; }

    public BufferedInputStream getAudiois() { return audiois; }

    public ArrayList<String> getFilearr() { return filearr; }

    public ArrayList<String> getIdarr() { return idarr; }

    public HashMap<String, String> getPublicKeyList() { return publicKeyList; }

    public String getChatAESKey() { return chatAESKey; }

    public boolean getIsStop() { return isStop; }

    public boolean getChatSet() { return chatSet; }

    public String getStreamUser() { return streamUser; }

    public CliAES getCaes() { return caes; }

    public CliRSA getCrsa() { return crsa; }

    public boolean getIsFTP() { return isFTP; }

    public void setFilearr(ArrayList<String> filearr) { this.filearr = filearr; }

    public void setIdarr(ArrayList<String> idarr) { this.idarr = idarr; }

    public void setChatAESKey(String chatAESKey) { this.chatAESKey = chatAESKey; }

    public void setIsStop(boolean isStop) { this.isStop = isStop; }

    public void setPublicKeyList(HashMap<String, String> publicKeyList) { this.publicKeyList = publicKeyList; }

    public void setChatSet(boolean chatSet) { this.chatSet = chatSet; }

    public void setIsFTP(boolean isFTP) { this.isFTP = isFTP; }

    public JFrame getJf() { return jf; }

    public JTextArea getJta() { return jta; }

    public JTextArea getIdList() { return idList; }

    public JTextArea getServerList() { return serverList; }

}
