package common;

import common.LoginCheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginCheckThread implements Runnable{
    //Member
    private DataInputStream dis;
    private DataOutputStream dos;
    private ServerDB sdb;

    private Socket socket;

    //Constructor
    public LoginCheckThread(LoginCheck lc) {
        sdb = new ServerDB();
        socket = lc.getSocket();
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    //Method
    @Override
    public void run() {
        boolean isStop = false;
        String msg = null;
        try {
            while(!isStop) {
                msg = dis.readUTF();
                if(msg.equals("checkInfo")) {
                    String info = dis.readUTF();
                    String[] receive = info.split("#");
                    if(sdb.checkInfo(receive[0], receive[1])) {
                        dos.writeUTF("SUCCESS");
                        isStop = true;
                        dos.close();
                        dis.close();
                        socket.close();
                    }
                    else {
                        dos.writeUTF("Fail");
                    }
                }
                else if(msg.equals("insertInfo")) {
                    String info = dis.readUTF();
                    String[] receive = info.split("#");
                    if(sdb.insertInfo(receive[0], receive[1])) {
                        dos.writeUTF("SUCCESS");
                        dos.writeUTF(sdb.getPrivateKey());
                    }
                    else {
                        dos.writeUTF("Fail");
                    }
                }
            }

        }catch(IOException ioe) { ioe.printStackTrace(); }
    }
}
