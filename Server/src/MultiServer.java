import java.io.*;
import java.net.*;
import java.util.*;

public class MultiServer {
    //Member
    private Socket socket;
    private ArrayList<MultiServerThread> list;
    //Constructor
    public MultiServer() throws IOException {
        list = new ArrayList<MultiServerThread>();
        ServerSocket ss = new ServerSocket(8000);   //---1
        MultiServerThread mst = null;
        boolean isStop = false;
        while(!isStop) {
            System.out.println("Server Read...");
            socket = ss.accept();   //Waiting Connect---2
            mst = new MultiServerThread(this);
            list.add(mst);  //---3
            Thread t = new Thread(mst);
            t.start();  //---4
        }
    }
    //Method
    public static void main(String[] args) throws IOException {
        new MultiServer();
    }

    public Socket getSocket() {
        return socket;
    }

    public ArrayList<MultiServerThread> getList() {
        return list;
    }
}
