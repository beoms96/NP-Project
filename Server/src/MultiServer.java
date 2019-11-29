import java.io.*;
import java.net.*;
import java.util.*;

public class MultiServer {
    //Member
    private Socket socket;
    private Socket fileSocket;
    private ArrayList<MultiServerThread> list;
    private ArrayList<FileServerThread> fstList;
    private ArrayList<String> idList;
    private ArrayList<String> fileList;

    //Constructor
    public MultiServer() throws IOException {
        list = new ArrayList<MultiServerThread>();
        fstList = new ArrayList<FileServerThread>();
        idList = new ArrayList<String>();
        fileList = findFileList();
        ServerSocket ss = new ServerSocket(8000);   //---1
        ServerSocket fss = new ServerSocket(9000);
        MultiServerThread mst = null;
        FileServerThread fst = null;
        boolean isStop = false;
        while(!isStop) {
            System.out.println("Server Read...");
            socket = ss.accept();   //Waiting Connect---2
            fileSocket = fss.accept();
            mst = new MultiServerThread(this);
            fst = new FileServerThread(this);
            list.add(mst);  //---3
            fstList.add(fst);
            Thread t = new Thread(mst);
            Thread ft = new Thread(fst);
            t.start();  //---4
            ft.start();
        }
    }

    //Method
    public static void main(String[] args) throws IOException {
        new MultiServer();
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

    public Socket getSocket() {
        return socket;
    }

    public Socket getFileSocket() { return fileSocket; }

    public ArrayList<MultiServerThread> getList() { return list; }

    public ArrayList<FileServerThread> getFstList() { return fstList; }

    public ArrayList<String> getIdList() { return idList; }

    public ArrayList<String> getFileList() { return fileList; }

    public void setFileList(ArrayList<String> fileList) { this.fileList = fileList; }
}
