import java.io.*;
import java.net.*;

public class EchoServer {
    private ServerSocket serverSocket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;

    public EchoServer(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            e.printStackTrace();
            System.out.println("Failed Create Socket");
            System.exit(0);
        }

        while(true) {
            System.out.println("...Waiting Client...");
            Socket socket = serverSocket.accept();
            System.out.println("Client IP : " + socket.getInetAddress().getHostAddress());

            is = socket.getInputStream();
            os = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
            bw = new BufferedWriter(new OutputStreamWriter(os));

            while(true) {
                String msg = br.readLine(); //From Client Msg
                System.out.println("From Client Msg: " + msg);
                if(msg.equals("quit")) {
                    break;
                }
                //Resend Msg
                msg += System.getProperty("line.separator");
                bw.write(msg);
                bw.flush();
            }

            bw.close();
            br.close();
            socket.close();

        } // waiting new connect

    }
    /*public static void main(String[] args) throws IOException {
        new EchoServer(8000);
    }*/
}
