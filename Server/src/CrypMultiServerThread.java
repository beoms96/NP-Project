import java.io.*;
import java.net.*;

public class CrypMultiServerThread implements Runnable {
    //Member
    private CrypMultiServer ms;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;

    //Constructor
    public CrypMultiServerThread(CrypMultiServer ms) {
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
                System.out.println(msg);
                String[] str = msg.split("#");
                if(str[1].equals("quit")) {
                    broadCasting(msg);
                    ms.getIdList().remove(str[0]);
                    isStop = true;
                }
                else if(str[1].equals("Enter")) {
                    broadCasting(msg);
                    ms.getIdList().add(str[0]);
                    if(ms.getIdList().size() == 1) {
                        String idAndKey = (String)ois.readObject();
                        String[] idKey = idAndKey.split("#");
                        ms.setFirstID(idKey[0]);        //First User -> Public Key
                        ms.setEncryptedKey(idKey[1]);   //RSA Encrypt AES Key
                    }
                    else {
                        String idAndKey = ms.getFirstID() + "#" + ms.getEncryptedKey();
                        oos.writeObject(idAndKey);
                    }
                }
                else {
                    broadCasting(msg);
                }
            }   //end while
            int index = ms.getList().indexOf(this);
            System.out.println("index: " + index);
            ms.getList().remove(this);
            ms.getFstList().remove(index);
            ms.getSstList().remove(index);
            System.out.println(socket.getInetAddress() + " Normally Terminate.");
            System.out.println("Current Crypto Client: " + ms.getList().size());
            if(ms.getList().size()==0) {
                ms.setStreamUser("");
            }
        }
        catch(Exception e) {
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Abnormally Terminate.");
            System.out.println("Current Crypto Client: " + ms.getList().size());
            if(ms.getList().size()==0) {
                ms.setStreamUser("");
            }
        }
    }   //end run
    public void broadCasting(String msg) throws IOException {
        //Send Msg To all Client
        for (CrypMultiServerThread mt: ms.getList()) {
            mt.send(msg);
        }
    }
    public void send(String msg) throws IOException {
        oos.writeObject(msg);   //---6 Send Real Msg
    }
}

