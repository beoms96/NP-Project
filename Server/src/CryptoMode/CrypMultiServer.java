package CryptoMode;

import java.io.*;
import java.net.*;
import java.util.*;

public class CrypMultiServer implements Runnable{
    //Member
    private Socket socket;
    private Socket fileSocket;
    private Socket videoSocket;
    private Socket rcvSocket;
    private ArrayList<CrypMultiServerThread> list;
    private ArrayList<CrypFileServerThread> fstList;
    private ArrayList<CrypStreamServerThread> sstList;
    private ArrayList<String> idList;
    private ArrayList<String> fileList;

    private String encryptedKey;
    private String firstID;

    private String streamUser;

    //Constructor
    public CrypMultiServer() {
        System.out.println("Crypto Server Start");
        list = new ArrayList<CrypMultiServerThread>();
        fstList = new ArrayList<CrypFileServerThread>();
        sstList = new ArrayList<CrypStreamServerThread>();
        idList = new ArrayList<String>();
        fileList = findFileList();
        streamUser = "";
    }

    //Method
    @Override
    public void run() {
        boolean isStop = false;
        try {
            ServerSocket ss = new ServerSocket(10000);   //---1
            ServerSocket fss = new ServerSocket(11000);
            ServerSocket vss = new ServerSocket(13000);
            ServerSocket rss = new ServerSocket(15000);
            CrypMultiServerThread mst = null;
            CrypFileServerThread fst = null;
            CrypStreamServerThread sst = null;
            while(!isStop) {
                System.out.println("Crypto Server Read...");
                socket = ss.accept();   //Waiting Connect---2
                fileSocket = fss.accept();
                videoSocket = vss.accept();
                rcvSocket = rss.accept();
                mst = new CrypMultiServerThread(this);
                fst = new CrypFileServerThread(this);
                sst = new CrypStreamServerThread(this);
                list.add(mst);  //---3
                fstList.add(fst);
                sstList.add(sst);
                Thread t = new Thread(mst);
                Thread ft = new Thread(fst);
                Thread st = new Thread(sst);
                t.start();  //---4
                ft.start();
                st.start();
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public ArrayList<String> findFileList() {
        ArrayList<String> result = new ArrayList<String>();
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File fn: files) {
            if(fn.isFile())
                result.add(fn.getName());
        }
        return result;
    }

    public Socket getSocket() { return socket; }

    public Socket getFileSocket() { return fileSocket; }

    public Socket getVideoSocket() { return videoSocket; }

    public Socket getRcvSocket() { return rcvSocket; }

    public ArrayList<CrypStreamServerThread> getSstList() { return sstList; }

    public ArrayList<CrypMultiServerThread> getList() { return list; }

    public ArrayList<CrypFileServerThread> getFstList() { return fstList; }

    public ArrayList<String> getIdList() { return idList; }

    public ArrayList<String> getFileList() {
        fileList = findFileList();
        return fileList;
    }

    public String getEncryptedKey() { return encryptedKey; }

    public String getFirstID() { return firstID; }

    public String getStreamUser() { return streamUser; }

    public void setStreamUser(String streamUser) { this.streamUser = streamUser; }

    public void setEncryptedKey(String encryptedKey) { this.encryptedKey = encryptedKey; }

    public void setFirstID(String firstID) { this.firstID = firstID; }

    public void setFileList(ArrayList<String> fileList) { this.fileList = fileList; }
}
