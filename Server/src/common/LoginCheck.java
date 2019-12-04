package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginCheck implements Runnable{
    //Member
    private Socket socket;

    //Constructor
    public LoginCheck() {
        System.out.println("Login Server Start");
    }

    //Method
    public void run() {
        boolean isStop = false;
        try {
            ServerSocket ss = new ServerSocket(5000);
            LoginCheckThread lct = null;
            while(!isStop) {
                System.out.println("Login Server Read...");
                socket = ss.accept();
                lct = new LoginCheckThread(this);
                Thread lt = new Thread(lct);
                lt.start();
            }

        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public Socket getSocket() {
        return socket;
    }
}
