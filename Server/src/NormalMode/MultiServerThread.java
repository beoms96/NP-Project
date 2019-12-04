package NormalMode;

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
        socket = ms.getSocket();
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(ms.getIdList());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    //Method
    public synchronized void run() {
        boolean isStop = false;
        try {
            String msg = null;
            while(!isStop) {
                msg = (String)ois.readObject(); //Input ---5
                String[] str = msg.split("#");
                if(str[1].equals("quit")) {
                    broadCasting(msg);
                    ms.getIdList().remove(str[0]);
                    isStop = true;
                }
                else if(str[1].equals("Enter")) {
                    broadCasting(msg);
                    ms.getIdList().add(str[0]);
                }
                else {
                    broadCasting(msg);
                }
            }   //end while
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Normally Terminate.");
            System.out.println("Current Normal Client: " + ms.getList().size());
            if(ms.getList().size()==0) {
                ms.setStreamUser("");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Abnormally Terminate.");
            System.out.println("Current Normal Client: " + ms.getList().size());
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
