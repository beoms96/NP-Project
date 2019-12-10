package NormalMode;

import java.io.*;
import java.net.Socket;

public class StreamServerThread implements Runnable{
    //Member
    private MultiServer ms;
    private DataOutputStream dos;
    private DataInputStream dis;
    private DataOutputStream rcvdos;
    private InputStream audiois;
    private OutputStream audioos;

    private Socket socket;
    private Socket rcvSocket;
    private Socket audioSocket;

    private boolean start;
    private boolean videostart;
    private String clientId;

    public StreamServerThread(MultiServer ms) {
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
            audiois = new BufferedInputStream(audioSocket.getInputStream());
            audioos = new BufferedOutputStream(audioSocket.getOutputStream());
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
                        for (StreamServerThread sst: ms.getSstList()) {
                            if(sst.getStart())
                                sst.send(length);
                        }
                    }
                    ms.setStreamUser("");
                    for(StreamServerThread sst: ms.getSstList()) {
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
                else if(msg.equals("Video")) {
                    videostart = true;
                    String requestFile = dis.readUTF();
                    VideoSendThread vst = new VideoSendThread(this, requestFile);
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
        byte[] tempBuffer = new byte[10000];
        String id;
        public CaptureThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (start) {
                    int cnt = audiois.read(tempBuffer);
                    for(StreamServerThread sst: ms.getSstList()) {
                        if(sst.getStart() && !sst.getClientId().equals(id))
                            sst.getAudioos().write(tempBuffer);
                    }
                    audioos.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadCasting(byte[] msg) throws IOException {
        for (StreamServerThread sst: ms.getSstList()) {
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

    public OutputStream getAudioos() { return audioos; }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void setVideostart(boolean videostart) {
        this.videostart = videostart;
    }

}
