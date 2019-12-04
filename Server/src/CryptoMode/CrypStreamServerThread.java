package CryptoMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CrypStreamServerThread implements Runnable{
    //Member
    private CrypMultiServer ms;
    private DataOutputStream dos;
    private DataInputStream dis;
    private DataOutputStream rcvdos;

    private Socket socket;
    private Socket rcvSocket;

    private boolean start;

    public CrypStreamServerThread(CrypMultiServer ms) {
        this.ms = ms;
        socket = ms.getVideoSocket();
        rcvSocket = ms.getRcvSocket();
        this.start = false;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            rcvdos = new DataOutputStream(rcvSocket.getOutputStream());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    @Override
    public synchronized void run() {
        boolean isStop = false;
        try {
            String msg = null;
            while(!isStop) {
                msg = dis.readUTF();
                if(msg.equals("Enter")) {
                    dos.writeUTF(ms.getStreamUser());
                    if(ms.getStreamUser().equals("")) { //Streaming owner
                        ms.setStreamUser(dis.readUTF());
                    }
                    else {  //Streaming Client
                        dos.writeUTF(ms.getStreamUser());
                    }
                    start = true;
                }
                else if(msg.equals("Send")) {
                    int length = dis.readInt();
                    byte[] data = null;

                    if (length > 0) {
                        data = new byte[length];
                        dis.readFully(data, 0, data.length); // read the message
                    }
                    broadCasting(data);
                }
                else if(msg.contains("#quit")) {
                    isStop = true;
                    start = false;
                }
                else if(msg.equals("OwnerQuit")) {
                    int length = dis.readInt();
                    if(length == 0) {
                        for (CrypStreamServerThread sst: ms.getSstList()) {
                            if(sst.getStart())
                                sst.send(length);
                        }
                    }
                    ms.setStreamUser("");
                    for(CrypStreamServerThread sst: ms.getSstList()) {
                        if(sst.getStart())
                            sst.setStart(false);
                    }
                }
                else if(msg.equals("ClientQuit")) {
                    if(start)
                        start = false;
                }
            }
            ms.getSstList().remove(this);
        }
        catch(IOException ioe) {
            ms.getSstList().remove(this);
            ioe.printStackTrace();
        }
        if(ms.getSstList().size()==0) {
            ms.setStreamUser("");
        }
    }

    public void broadCasting(byte[] msg) throws IOException {
        for (CrypStreamServerThread sst: ms.getSstList()) {
            if(sst.getStart())
                sst.send(msg);
        }
    }
    public void send(byte[] msg) throws IOException {
        rcvdos.writeInt(msg.length);
        rcvdos.write(msg);
    }

    public void send(int i) throws IOException {
        rcvdos.writeInt(i);
    }

    public boolean getStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}

