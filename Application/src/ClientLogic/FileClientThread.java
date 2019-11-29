package ClientLogic;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class FileClientThread implements Runnable{

    //Member
    private MultiClient mc;

    private FileInputStream fis;
    private FileOutputStream fos;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    private String path;
    private ArrayList<File> fileList;
    private String fileName;
    private int loadVersion;

    public FileClientThread(MultiClient mc) {
        this.mc = mc;
        updateFileList();
    }
    @Override
    public void run() {
        boolean isStop = false;
        String result = null;
        try {
            while(!isStop) {
                result = mc.getDis().readUTF();
                System.out.println(result);
                if(result.equals("normal")) {
                    if(loadVersion == 0) {
                        try{
                            mc.getDos().writeInt(fileList.size());
                            for (int i=0; i<fileList.size(); i++) {
                                result = fileRead(mc.getDos(), i);
                            }
                            JOptionPane.showMessageDialog(mc.getJf(), result);
                            mc.getDos().writeUTF("List");
                        } catch(IOException ioe) { ioe.printStackTrace(); }
                    }
                    else if(loadVersion == 1) {
                        try{
                            mc.getDos().writeUTF(fileName);
                            mc.getDos().flush();
                            result = fileWrite(mc.getDis());
                            JOptionPane.showMessageDialog(mc.getJf(), result);
                        } catch(IOException ioe) { ioe.printStackTrace(); }
                    }
                }
                else if(result.equals("update")) {
                    updateFileList();
                }
                else if(result.equals("quit")) {
                    isStop = true;
                }
            }
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public String fileRead(DataOutputStream dos, int i) {
        String result;
        try {
            dos.writeUTF(fileList.get(i).getName());
            dos.writeLong(fileList.get(i).length());
            JOptionPane.showMessageDialog(mc.getJf(), "Send (" + fileList.get(i).getName() + ")");
            File file = new File(path + "/" + fileList.get(i).getName());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            int len;
            int size = 1024;
            int totalSize = 0;
            Long fileSize = fileList.get(i).length();
            byte[] data = new byte[size];
            while(totalSize<fileSize) {
                len = bis.read(data);
                totalSize += len;
                dos.write(data, 0, len);
            }
            dos.flush();
            result = "UPLOAD SUCCESS";

        } catch(IOException ioe) {
            result = "UPLOAD ERROR";
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
            Long fileSize = dis.readLong();
            JOptionPane.showMessageDialog(mc.getJf(), "File Size: " + fileSize);

            File file = new File(path + "/" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            int len;
            int size = 1024;
            int totalSize = 0;
            byte[] data = new byte[size];
            while(totalSize<fileSize) {
                len = dis.read(data);
                totalSize += len;
                bos.write(data, 0, len);
            }
            result = "DOWNLOAD SUCCESS";
        } catch(IOException ioe) {
            result = "DOWNLOAD ERROR";
            ioe.printStackTrace();
        } finally{
            try{ bos.close(); } catch(IOException ioe) {ioe.printStackTrace();}
            try{ fos.close(); } catch(IOException ioe) {ioe.printStackTrace();}
        }
        return result;
    }

    public void updateFileList() {
        ArrayList<String> arr = new ArrayList<String>();
        String s = null;
        try {
            while (!(s = mc.getDis().readUTF()).equals("")) {
                arr.add(s);
            }
            mc.setFilearr(arr);
            mc.getServerList().setText(" Server File List ");
            for (String str : mc.getFilearr()) {
                mc.getServerList().append(System.getProperty("line.separator") + " " + str);
                mc.getServerList().setCaretPosition(mc.getServerList().getDocument().getLength());
            }
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileList(ArrayList<File> fileList) {
        this.fileList = fileList;
    }

    public void setLoadVersion(int loadVersion) {
        this.loadVersion = loadVersion;
    }
}
