package ClientLogic;

import java.io.*;
import java.net.Socket;

public class RequestLogin {
    //Member
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    public RequestLogin(String ip) {
        try {
            socket = new Socket(ip, 5000);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public String receiveResult() {
        String result = "";
        try {
            result = dis.readUTF();
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return result;
    }

    public boolean checkInfo(String id, String pw) {
        boolean result = false;
        try {
            dos.writeUTF("checkInfo");
            dos.writeUTF(id + "#" +pw);
            if(receiveResult().equals("SUCCESS")) {
                result = true;
                dos.close();
                dis.close();
                socket.close();
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return result;
    }

    public boolean insertInfo(String id, String pw) {
        boolean result = false;
        try {
            dos.writeUTF("insertInfo");
            dos.writeUTF(id + "#" +pw);
            if(receiveResult().equals("SUCCESS")) {
                String privateKey = dis.readUTF();
                BufferedWriter bw1 = new BufferedWriter(new FileWriter(id + " " +"PrivateKey.txt"));
                bw1.write(privateKey);
                bw1.newLine();
                bw1.close();
                result = true;
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return result;
    }
}
