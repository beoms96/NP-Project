import java.io.*;
import java.net.*;
import java.util.*;

public class CrypMultiServer implements Runnable{
    //Member
    private Socket socket;
    private Socket fileSocket;
    private ArrayList<CrypMultiServerThread> list;
    private ArrayList<CrypFileServerThread> fstList;
    private ArrayList<String> idList;
    private ArrayList<String> fileList;
    private ServerSocket ss;
    private ServerSocket fss;

    private String encryptedKey;
    private String firstID;

    public CrypMultiServer() {
        System.out.println("Crypto Server Start");
        list = new ArrayList<CrypMultiServerThread>();
        fstList = new ArrayList<CrypFileServerThread>();
        idList = new ArrayList<String>();
        fileList = findFileList();
    }

    @Override
    public void run() {
        boolean isStop = false;
        try {
            ServerSocket ss = new ServerSocket(10000);   //---1
            ServerSocket fss = new ServerSocket(11000);
            CrypMultiServerThread mst = null;
            CrypFileServerThread fst = null;
            while(!isStop) {
                System.out.println("Crypto Server Read...");
                socket = ss.accept();   //Waiting Connect---2
                fileSocket = fss.accept();
                mst = new CrypMultiServerThread(this);
                fst = new CrypFileServerThread(this);
                list.add(mst);  //---3
                fstList.add(fst);
                Thread t = new Thread(mst);
                Thread ft = new Thread(fst);
                t.start();  //---4
                ft.start();
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public ArrayList<String> findFileList() {
        ArrayList<String> result = new ArrayList<String>();
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        String files[]  = dir.list();
        for (String fn: files)
            result.add(fn);
        return result;
    }

    public Socket getSocket() { return socket; }

    public Socket getFileSocket() { return fileSocket; }

    public ArrayList<CrypMultiServerThread> getList() { return list; }

    public ArrayList<CrypFileServerThread> getFstList() { return fstList; }

    public ArrayList<String> getIdList() { return idList; }

    public ArrayList<String> getFileList() { return fileList; }

    public String getEncryptedKey() { return encryptedKey; }

    public void setEncryptedKey(String encryptedKey) { this.encryptedKey = encryptedKey; }

    public String getFirstID() { return firstID; }

    public void setFirstID(String firstID) { this.firstID = firstID; }

    public void setFileList(ArrayList<String> fileList) { this.fileList = fileList; }
}
