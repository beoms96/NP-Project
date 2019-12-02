import java.io.*;
import java.net.*;
import java.util.*;

public class MultiServer implements Runnable{
    //Member
    private Socket socket;
    private Socket fileSocket;
    private Socket videoSocket;
    private Socket rcvSocket;
    private ArrayList<MultiServerThread> list;
    private ArrayList<FileServerThread> fstList;
    private ArrayList<StreamServerThread> sstList;
    private ArrayList<String> idList;
    private ArrayList<String> fileList;

    private String streamUser;

    //Constructor
    public MultiServer() {
        System.out.println("Normal Server Start");
        list = new ArrayList<MultiServerThread>();
        fstList = new ArrayList<FileServerThread>();
        sstList = new ArrayList<StreamServerThread>();
        idList = new ArrayList<String>();
        fileList = findFileList();
        streamUser = "";
    }

    //Method
    @Override
    public void run() {
        boolean isStop = false;
        try {
            ServerSocket ss = new ServerSocket(8000);   //---1
            ServerSocket fss = new ServerSocket(9000);
            ServerSocket vss = new ServerSocket(12000);
            ServerSocket rss = new ServerSocket(14000);
            MultiServerThread mst = null;
            FileServerThread fst = null;
            StreamServerThread sst = null;
            while(!isStop) {
                System.out.println("Normal Server Read...");
                socket = ss.accept();   //Waiting Connect---2
                fileSocket = fss.accept();
                videoSocket = vss.accept();
                rcvSocket = rss.accept();
                mst = new MultiServerThread(this);
                fst = new FileServerThread(this);
                sst = new StreamServerThread(this);
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

    //Method
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

    public Socket getVideoSocket() { return videoSocket; }

    public Socket getRcvSocket() { return rcvSocket; }

    public ArrayList<StreamServerThread> getSstList() { return sstList; }

    public ArrayList<MultiServerThread> getList() { return list; }

    public ArrayList<FileServerThread> getFstList() { return fstList; }

    public ArrayList<String> getIdList() { return idList; }

    public ArrayList<String> getFileList() { return fileList; }

    public String getStreamUser() { return streamUser; }

    public void setStreamUser(String streamUser) { this.streamUser = streamUser; }

    public void setFileList(ArrayList<String> fileList) { this.fileList = fileList; }
}
