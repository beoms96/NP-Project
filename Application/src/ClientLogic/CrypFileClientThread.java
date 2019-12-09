package ClientLogic;

import javax.swing.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class CrypFileClientThread implements Runnable {

    //Member
    private MultiClient mc;

    private FileInputStream fis;
    private FileOutputStream fos;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    private String path;
    private String Epath;
    private ArrayList<File> fileList;
    private ArrayList<File> encryptFileList;
    private String fileName;
    private int loadVersion;

    public CrypFileClientThread(MultiClient mc) {
        this.mc = mc;
        updateFileList();
    }

    @Override
    public void run() {
        boolean isStop = false;
        String result = null;
        try {
            while (!isStop) {
                result = mc.getDis().readUTF();
                System.out.println(result);
                if (result.equals("normal")) {
                    mc.setIsFTP(true);
                    if (loadVersion == 0) {
                        try {
                            mc.getDos().writeInt(fileList.size());
                            try {
                                mc.getCaes().modeEncrypt();
                            }catch (GeneralSecurityException gse) { gse.printStackTrace(); }

                            for (int i = 0; i < fileList.size(); i++) {
                                fileEncrypt(i);
                            }
                            for (int i = 0; i < fileList.size(); i++) {
                                result = fileRead(mc.getDos(), i);
                            }
                            for (int i = 0; i < fileList.size(); i++) {
                                removeEncryptFile(i);
                            }
                            JOptionPane.showMessageDialog(mc.getJf(), result);
                            mc.getDos().writeUTF("List");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    } else if (loadVersion == 1) {
                        try {
                            mc.getDos().writeUTF(fileName);
                            mc.getDos().flush();
                            try {
                                mc.getCaes().modeDecrypt();
                            }catch (GeneralSecurityException gse) { gse.printStackTrace(); }
                            result = fileWrite(mc.getDis());
                            JOptionPane.showMessageDialog(mc.getJf(), result);
                            mc.setIsFTP(false);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                } else if (result.equals("update")) {
                    updateFileList();
                    mc.setIsFTP(false);
                } else if (result.equals("quit")) {
                    isStop = true;
                    mc.setIsFTP(false);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String fileRead(DataOutputStream dos, int i) {
        String result;
        try {
            dos.writeUTF(encryptFileList.get(i).getName());
            dos.writeLong(encryptFileList.get(i).length());
            JOptionPane.showMessageDialog(mc.getJf(), "Send (" + encryptFileList.get(i).getName() + ")");
            Epath = System.getProperty("user.dir");
            File file = new File(Epath + "/" + encryptFileList.get(i).getName());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            int len;
            int size = 1024;
            int totalSize = 0;
            Long fileSize = encryptFileList.get(i).length();
            byte[] data = new byte[size];
            while (totalSize < fileSize) {
                len = bis.read(data);
                totalSize += len;
                dos.write(data, 0, len);
            }
            dos.flush();
            result = "UPLOAD SUCCESS";

        } catch (IOException ioe) {
            result = "UPLOAD ERROR";
            ioe.printStackTrace();
        } finally {
            try {
                fis.close();
                bis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return result;
    }

    public String fileWrite(DataInputStream dis) {
        String result;
        String path = System.getProperty("user.dir");

        try {
            Long fileSize = dis.readLong();
            JOptionPane.showMessageDialog(mc.getJf(), "File Size: " + fileSize);

            int index = fileName.lastIndexOf(".");
            fileName = fileName.substring(0, index);

            File file = new File(path + "/" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            int len;
            int size = 1024;
            int totalSize = 0;
            byte[] data = new byte[size];
            byte[] decryptData = null;
            try {
                while (totalSize < fileSize) {
                    len = dis.read(data);
                    totalSize += len;
                    decryptData = mc.getCaes().AESDecrypt(data, len);
                    if(decryptData != null) {
                        bos.write(decryptData);
                    }
                }
                decryptData = mc.getCaes().getCipher().doFinal();
                if(decryptData != null) {
                    bos.write(decryptData);
                }
                result = "DOWNLOAD SUCCESS";
            } catch(GeneralSecurityException gse) {
                JOptionPane.showMessageDialog(mc.getJf(), "Not Matched Key");
                result = "DOWNLOAD ERROR";
            }

        } catch (IOException ioe) {
            result = "DOWNLOAD ERROR";
            ioe.printStackTrace();
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return result;
    }

    public void updateFileList() {
        ArrayList<String> arr = new ArrayList<String>();
        String s = null;
        try {
            while (!(s = mc.getDis().readUTF()).equals("")) {
                if(s.contains(".cipher"))
                    arr.add(s);
            }
            mc.setFilearr(arr);
            mc.getServerList().setText(" Server File List ");
            for (String str : mc.getFilearr()) {
                mc.getServerList().append(System.getProperty("line.separator") + " " + str);
                mc.getServerList().setCaretPosition(mc.getServerList().getDocument().getLength());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void fileEncrypt(int i) {
        try {
            File file = new File(path + "/" + fileList.get(i).getName());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            Epath = System.getProperty("user.dir");
            File Efile = new File(Epath + "/" + fileList.get(i).getName() + ".cipher");
            fos = new FileOutputStream(Efile);
            bos = new BufferedOutputStream(fos);

            int len = 0;
            int size = 1024;
            int totalSize = 0;
            Long fileSize = fileList.get(i).length();
            byte[] data = new byte[size];
            byte[] encryptedData = null;
            try {
                while (totalSize < fileSize) {
                    len = bis.read(data);
                    totalSize += len;
                    encryptedData = mc.getCaes().AESEncrypt(data, len);
                    if(encryptedData != null) {
                        bos.write(encryptedData);
                    }
                }
                encryptedData = mc.getCaes().getCipher().doFinal();
                if(encryptedData != null) {
                    bos.write(encryptedData);
                }
            }catch(GeneralSecurityException gse) { gse.printStackTrace(); }

            encryptFileList.add(Efile);

        }catch(IOException ioe) { ioe.printStackTrace(); }
        finally {
            try {
                fis.close();
                bis.close();
                bos.close();
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void removeEncryptFile(int i) {
        File file = new File(System.getProperty("user.dir") + "/" +encryptFileList.get(i).getName());
        if(file.exists())
            file.delete();
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

    public void setEncryptFile(ArrayList<File> encryptFileList) {
        this.encryptFileList = encryptFileList;
    }
}
