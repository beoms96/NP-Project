import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class UnicastServer {

    private ServerSocket serverSocket;

    public UnicastServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed Create Socket!! Exit Program");
            System.exit(0);
        }

        UnicastServerThread ust = null; //Server Thread
        while(true) {
            System.out.println("Server Ready... ...Client waiting...");
            Socket sc = null;
            try {
                sc = serverSocket.accept(); //Waiting Client
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            System.out.println("Client IP: "+ sc.getInetAddress().getHostAddress());    //Client IP
            System.out.println("Client Port: "+ sc.getPort());
            ust = new UnicastServerThread(sc);
            Thread t = new Thread(ust); //Create Thread
            t.start();  //Thread start
        }
    }

    /*public static void main(String[] args) {
        new UnicastServer(8000);
    }*/

}