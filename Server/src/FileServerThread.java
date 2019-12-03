import java.io.*;
import java.net.Socket;

public class FileServerThread implements Runnable{
    //Member
    private MultiServer ms;
    private Socket fileSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    //Constructor
    public FileServerThread(MultiServer ms) {
        this.ms = ms;
        fileSocket = ms.getFileSocket();
        try {
            dis = new DataInputStream(fileSocket.getInputStream());
            dos = new DataOutputStream(fileSocket.getOutputStream());
            for(String f: ms.getFileList())
                dos.writeUTF(f);
            dos.writeUTF("");
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    //Method
    @Override
    public synchronized void run() {
        boolean isStop = false;
        try {
            while(!isStop) {
                String type = dis.readUTF();
                System.out.println(type);
                if(type.equals("upload")) {
                    dos.writeUTF("normal");
                    int filesize = dis.readInt();
                    String result = null;
                    for(int i=0;i<filesize;i++) {
                        result = fileWrite(dis);
                        if (result.equals("ERROR"))
                            break;
                    }
                    System.out.println("result: " + result);
                }
                else if(type.equals("download")) {
                    dos.writeUTF("normal");
                    String fileName = dis.readUTF();
                    String result = fileRead(dos, fileName);
                    System.out.println("result: " + result);
                }
                else if(type.equals("quit")) {
                    dos.writeUTF("quit");
                    isStop=true;
                }
                else if(type.equals("List")) {
                    broadCasting("update");
                    broadCastingFL();
                }
            }
            ms.getFstList().remove(this);
        } catch(IOException ioe) {
            ms.getFstList().remove(this);
            ioe.printStackTrace();
        }
    }

    public String fileRead(DataOutputStream dos, String fileName) {
        String result;
        String path = System.getProperty("user.dir");
        try {
            System.out.println("Send (" + fileName + ")");
            File file = new File(path + "/" + fileName);
            dos.writeLong(file.length());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            int len;
            int size = 1024;
            int totalSize = 0;
            Long fileSize = file.length();
            byte[] data = new byte[size];
            while(totalSize<fileSize) {
                len = bis.read(data);
                totalSize+=len;
                dos.write(data, 0, len);
            }
            dos.flush();
            result = "SUCCESS";

        } catch(IOException ioe) {
            result = "ERROR";
            ioe.printStackTrace();
        }finally {
            try{
                fis.close();
                bis.close();
            } catch (IOException ioe) { ioe.printStackTrace(); }
        }
        return result;
    }

    public String fileWrite(DataInputStream dis) {
        String result;
        String path = System.getProperty("user.dir");

        try {
            System.out.println("Receive File");
            String fileName = dis.readUTF();
            System.out.println("File Name: " + fileName);
            Long fileSize = dis.readLong();
            System.out.println("File Size: " + fileSize);

            File file = new File(path + "/" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            System.out.println(fileName + " File Create");

            int len;
            int size = 1024;
            int totalSize = 0;
            byte[] data = new byte[size];
            while(totalSize<fileSize) {
                len = dis.read(data);
                totalSize += len;
                bos.write(data, 0, len);
            }
            result = "SUCCESS";
            System.out.println("Complete File Download");

        } catch(IOException ioe) {
            result = "ERROR";
            ioe.printStackTrace();
        } finally{
            try{
                bos.close();
                fos.close();
            } catch(IOException ioe) {ioe.printStackTrace();}
        }

        return result;
    }

    public void broadCasting(String msg) throws IOException {
        for (FileServerThread fst: ms.getFstList()) {
            fst.send(msg);
        }
    }
    public void send(String msg) throws IOException {
        dos.writeUTF(msg);
    }

    public void broadCastingFL() throws  IOException {
        for (FileServerThread fst: ms.getFstList()) {
            fst.sendFileList();
        }
    }

    public void sendFileList() throws IOException {
        ms.setFileList(ms.findFileList());
        for(String f: ms.getFileList())
            dos.writeUTF(f);
        dos.writeUTF("");
    }

}
