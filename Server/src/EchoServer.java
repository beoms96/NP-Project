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
    public static void main(String[] args) throws IOException {
        /*Socket socket = null;   //Client와 통신하기 위한 Socket
        ServerSocket serverSocket = null;   //서버 생성을 위한 ServerSocket
        BufferedReader in = null;   //Client로부터 데이터를 읽어들이기 위한 입력스트림
        PrintWriter out = null;     //Client로 데이터를 내보내기 위한 스트림

        try {
            serverSocket = new ServerSocket(8000);
        }
        catch (IOException e) {
            System.out.println("Port is already opened");
        }

        try {
            System.out.println("Open Server!");
            socket = serverSocket.accept(); //서버 생성, Client 접속 대기
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //입력 스트림 생성
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));    //출력스트림 생성

            //Stream 통한 데이터 송수신
            for (; ; ) {
                String str = null;
                str = in.readLine();    //Client로 부터 데이터를 읽어옴

                System.out.println("From Client Msg: " + str);

                out.write(str);
                out.flush();
                //socket.close();
            }
        }
        catch(IOException e){}*/

        new EchoServer(8000);
    }
}
