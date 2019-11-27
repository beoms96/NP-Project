package ClientLogic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MultiClient {
    //Member
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private String id, pk;
    private String ip;
    private int check;

    //Constructor
    public MultiClient(String id, int check, String ip) {
        this.id=id;
        this.ip = ip;
        this.check = check;
    }

    public MultiClient(String id, int check, String ip, String pk) {
        this.id=id;
        this.ip = ip;
        this.check = check;
        this.pk = pk;
    }

    public String getIp() {
        return ip;
    }

}
