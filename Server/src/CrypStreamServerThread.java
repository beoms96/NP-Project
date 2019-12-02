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

    public CrypStreamServerThread(CrypMultiServer ms) {
        this.ms = ms;
        socket = ms.getVideoSocket();
        rcvSocket = ms.getRcvSocket();
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            rcvdos = new DataOutputStream(rcvSocket.getOutputStream());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    @Override
    public void run() {
        boolean isStop = false;
        try {
            String msg = null;
            while(!isStop) {
                msg = dis.readUTF();
                if(msg.equals("Enter")) {
                    if(ms.getStreamUser().equals("")) { //Streaming owner
                        ms.setStreamUser(dis.readUTF());
                    }
                    else {  //Streaming Client
                        dos.writeUTF(ms.getStreamUser());
                    }
                }
                else if(msg.equals("Send")) {
                    int length = dis.readInt();
                    byte[] data = null;

                    if (length > 0) {
                        data = new byte[length];
                        dis.readFully(data, 0, data.length); // read the message
                    }
                    System.out.println("data length : " + data.length);
                    broadCasting(data);
                }
                else if(msg.contains("#Quit")) {
                    String[] result = msg.split("#");
                    if(result[0].equals(ms.getStreamUser())) {
                        ms.setStreamUser("");
                    }
                }
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void broadCasting(byte[] msg) throws IOException {
        for (CrypStreamServerThread sst: ms.getSstList()) {
            sst.send(msg);
        }
    }
    public void send(byte[] msg) throws IOException {
        rcvdos.writeInt(msg.length);
        rcvdos.write(msg);
    }
}

