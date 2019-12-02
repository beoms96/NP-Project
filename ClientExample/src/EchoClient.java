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

    /*public static void main(String[] args) throws IOException{
        new EchoClient("localhost", 8000);
    }*/
}
