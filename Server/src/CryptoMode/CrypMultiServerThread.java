package CryptoMode;

import common.ServerDB;

import java.io.*;
import java.net.*;

public class CrypMultiServerThread implements Runnable {
    //Member
    private CrypMultiServer ms;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;
    private ServerDB sdb;
    private String id;

    //Constructor
    public CrypMultiServerThread(CrypMultiServer ms) {
        this.ms = ms;
        socket = ms.getSocket();
        sdb = new ServerDB();
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
                    sdb.updateIdAndPublicKeyList(ms.getIdList().toArray(new String[ms.getIdList().size()]));
                    String[] idArr = sdb.getIdListFromDB();
                    String[] pkArr = sdb.getPublicKeyListFromDB();
                    for(int i=0; i<idArr.length; i++) {
                        broadCasting(idArr[i]);
                        broadCasting(pkArr[i]);
                    }
                    isStop = true;
                }
                else if(str[1].equals("Enter")) {
                    this.id = str[0];
                    broadCasting(msg);
                    ms.getIdList().add(str[0]);
                    sdb.updateIdAndPublicKeyList(ms.getIdList().toArray(new String[ms.getIdList().size()]));
                    String[] idArr = sdb.getIdListFromDB();
                    String[] pkArr = sdb.getPublicKeyListFromDB();
                    for(int i=0; i<idArr.length; i++) {
                        broadCasting(idArr[i]);
                        broadCasting(pkArr[i]);
                    }
                }
                else if(str[1].equals("SendKey")) { //Key Send Client
                    String idAndEKey = (String)ois.readObject();
                    String[] idEKey = idAndEKey.split("#");
                    for(CrypMultiServerThread sst: ms.getList()) {
                        if(sst.getId().equals(idEKey[0])) {
                            sst.send(idEKey[1]);
                        }
                    }
                }
                else {
                    broadCasting(msg);
                }
            }   //end while
            ms.getList().remove(this);
            System.out.println(socket.getInetAddress() + " Normally Terminate.");
            System.out.println("Current Crypto Client: " + ms.getList().size());
            if(ms.getList().size()==0) {
                ms.setStreamUser("");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
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

    public String getId() { return id; }
}

