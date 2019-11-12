import java.io.*;
import java.net.Socket;

public class UnicastClient {
    private String ip;
    private int port;
    private String msg;
    private BufferedReader br;  //For Msg Input
    private BufferedReader bufferR; //For socket IO
    private BufferedWriter bufferW;

    public UnicastClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        Socket socket = getSocket();
        boolean isStop = false;
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        bufferW = new BufferedWriter(new OutputStreamWriter(os));
        bufferR = new BufferedReader(new InputStreamReader(is));

        while(!isStop) {
            System.out.print("Message: ");
            br = new BufferedReader(new InputStreamReader(System.in));
            msg = br.readLine();    //Input message
            msg += System.getProperty("line.separator");    //Add enter
            bufferW.write(msg);
            bufferW.flush();

            msg = bufferR.readLine();   //Receive Msg
            if(msg.equals("quit")) {
                isStop = true;
                System.out.println("Exit...");
            }
            else {
                System.out.println("Receive Msg: " + msg);
            }
        }
        bufferW.close();
        bufferR.close();
        socket.close();
    }

    public Socket getSocket() {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Failed Create Socket");
        }

        return socket;
    }

    public static void main(String[] args) throws IOException{
        new UnicastClient("localhost", 8000);
    }
}

