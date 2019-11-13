import java.io.*;
import java.net.*;

public class MultiServerThread implements Runnable {
    //Member
    private MultiServer ms;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    //Constructor
    public MultiServerThread(MultiServer ms) {
        this.ms = ms;
    }
    //Method
    public synchronized void run() {
        boolean isStop = false;
        try {
            socket = ms.getSocket();
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            String msg = null;
            while(!isStop) {
                msg = (String)ois.readObject(); //Input ---5
                System.out.println(msg);
                String[] str = msg.split("#");
                if(str[1].equals("quit")) {
                    broadCasting(msg);
                    isStop = true;
                }
                else {
                    broadCasting(msg);
                }
            }   //end while
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Normally Terminate.");
            System.out.println("Current Client: " + ms.getList().size());
        }
        catch(Exception e) {
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Abnormally Terminate.");
            System.out.println("Current Client: " + ms.getList().size());
        }
    }   //end run
    public void broadCasting(String msg) throws IOException {
        //Send Msg To all Client
        for (MultiServerThread mt: ms.getList()) {
            mt.send(msg);
        }
    }
    public void send(String msg) throws IOException {
        oos.writeObject(msg);   //---6 Send Real Msg
    }
}
