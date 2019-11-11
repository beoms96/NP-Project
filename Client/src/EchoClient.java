import java.io.*;
import java.net.*;

public class EchoClient {
    private Socket socket;
    private BufferedReader br;
    private String ip;
    private int port;

    public EchoClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        socket = getSocket(); //Exception 처리를 위해 Method 처리
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

        br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.print("To Server Msg: ");
            String msg = br.readLine(); //키보드로 입력
            msg+=System.getProperty("line.separator");
            bw.write(msg); //메시지 전송
            if(msg.equals("quit")) {
                bw.flush(); //전송 완료
                break;
            }
            bw.flush(); //전송 완료
            msg = bfr.readLine(); //서버로 부터 받은 메시지
            System.out.println("From Server Msg: " + msg);
        }
        bw.close();
        br.close();
        bfr.close();
        socket.close();
    }

    public Socket getSocket(){
        Socket sc = null;
        try {
            sc = new Socket(ip, port);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed Connet");
            System.exit(0);
        }
        return sc;
    }

    public static void main(String[] args) throws IOException{
        /*Socket socket = null;   //Server와 통신하기 위한 Socket
        BufferedReader in = null;   //Server로부터 데이터를 읽어들이기 위한 입력스트림
        BufferedReader in2 = null;  //키보드로부터 읽어들이기 위한 입력스트림
        PrintWriter out = null; //서버로 내보내기 위한 출력 스트림
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("127.0.0.1");
            socket = new Socket(ia, 8000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            System.out.println(socket.toString());
        }
        catch(IOException e){
        }

        try {
            while(true) {
                System.out.print("To Server Msg: ");
                String data = in2.readLine();   //키보드로부터 입력
                out.println(data);  //서버로 데이터 전송
                out.flush();

                String str2 = in.readLine();    //서버로부터 되돌아오는 데이터 읽어들임.
                if(str2 == "/quit")
                    break;
                System.out.println("From Server Msg: " + str2);
            }
            socket.close();
        }
        catch(IOException e){}*/

        new EchoClient("localhost", 8000);
    }
}
