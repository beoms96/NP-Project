package CryptoMode;

import java.io.*;
import java.net.Socket;

public class CrypStreamServerThread implements Runnable{
    //Member
    private CrypMultiServer ms;
    private DataOutputStream dos;
    private DataInputStream dis;
    private DataOutputStream rcvdos;
    private DataInputStream audiois;
    private DataOutputStream audioos;

    private Socket socket;
    private Socket rcvSocket;
    private Socket audioSocket;

    private boolean start;
    private boolean videostart;
    private String clientId;

    public CrypStreamServerThread(CrypMultiServer ms) {
        this.ms = ms;
        socket = ms.getVideoSocket();
        rcvSocket = ms.getRcvSocket();
        audioSocket = ms.getAudioSocket();
        this.start = false;
        this.videostart = false;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            rcvdos = new DataOutputStream(rcvSocket.getOutputStream());
            audiois = new DataInputStream(audioSocket.getInputStream());
            audioos = new DataOutputStream(audioSocket.getOutputStream());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    @Override
    public synchronized void run() {
        boolean isStop = false;
        try {
            String msg = null;
            while(!isStop) {
                msg = dis.readUTF();
                if(msg.contains("#Enter")) {
                    String[] idAndMsg = msg.split("#");
                    this.clientId = idAndMsg[0];
                    dos.writeUTF(ms.getStreamUser());
                    if(ms.getStreamUser().equals("")) { //Streaming owner
                        ms.setStreamUser(dis.readUTF());
                        Thread captureThread = new CaptureThread(clientId);
                        captureThread.start();
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
                    videostart = false;
                }
                else if(msg.equals("OwnerQuit")) {
                    int length = dis.readInt();
                    if(length == 0) {
                        for (CrypStreamServerThread sst: ms.getSstList()) {
                            if(sst.getStart())
                                sst.send(length);
                            if(sst.getStart() && !sst.getClientId().equals(clientId)) {
                                sst.getAudioos().writeInt(length);
                            }
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
                    if(videostart)
                        videostart = false;
                }
                else if(msg.contains("#Video")) {
                    String[] idAndMsg = msg.split("#");
                    videostart = true;
                    String requestFile = dis.readUTF();
                    String key = dis.readUTF();
                    CrypVideoSendThread vst = new CrypVideoSendThread(this, requestFile, key, idAndMsg[0]);
                    Thread vt = new Thread(vst);
                    vt.start();
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

    class CaptureThread extends Thread {
        byte[] tempBuffer = null;
        String id;
        public CaptureThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int Elength = audiois.readInt();
                    if(Elength==0) {
                        for(CrypStreamServerThread sst: ms.getSstList()) {
                            if(sst.getStart() && !sst.getClientId().equals(id)) {
                                sst.getAudioos().writeInt(Elength);
                            }
                        }
                        break;
                    }
                    else {
                        tempBuffer = new byte[Elength];
                        int cnt = audiois.read(tempBuffer, 0, Elength);
                        for(CrypStreamServerThread sst: ms.getSstList()) {
                            if(sst.getStart() && !sst.getClientId().equals(id)) {
                                sst.getAudioos().writeInt(Elength);
                                sst.getAudioos().write(tempBuffer);
                            }
                        }
                    }
                    audioos.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public boolean getVideostart() {
        return videostart;
    }

    public String getClientId() { return clientId; }

    public DataOutputStream getRcvdos() {
        return rcvdos;
    }

    public DataOutputStream getAudioos() { return audioos; }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void setVideostart(boolean videostart) {
        this.videostart = videostart;
    }


}

