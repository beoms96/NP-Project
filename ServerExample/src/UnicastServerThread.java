import java.io.*;
import java.net.Socket;

public class UnicastServerThread implements Runnable {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;
    private String msg;

    @Override
    public void run() {
        boolean isStop = false;
        try {
            is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            os = socket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
        }
        catch (Exception e) {
            e.printStackTrace();
            isStop = true;
        }

        try {
            while(!isStop) { //Repeat Msg Send/Receive
                msg = br.readLine();
                if (msg.equals("quit")) {
                    isStop = true;
                }
                System.out.println("Receive Msg: " + msg);

                msg += System.getProperty("line.separator"); //For ReSend, Input Enter
                bw.write(msg);
                bw.flush();
            }
        }
        catch(Exception e) {
            System.out.println("Exit Client Abnormally");
            isStop = true;
        }
        finally {
            try{
                if(br!=null) br.close();
                if(bw!=null) bw.close();
                if(socket!=null) socket.close();
            }
            catch (Exception e2) {}
        }
    }

    public UnicastServerThread(Socket socket) {
        this.socket = socket;
    }
}
