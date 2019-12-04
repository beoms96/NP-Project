package common;

import CryptoMode.CrypMultiServer;
import NormalMode.MultiServer;

public class OneServer {
    //Member
    LoginCheck lc = null;
    MultiServer ms = null;
    CrypMultiServer cms = null;

    public OneServer() {
        System.out.println("All management Start");
        lc = new LoginCheck();
        ms = new MultiServer();
        cms = new CrypMultiServer();
        Thread lt = new Thread(lc);
        Thread t = new Thread(ms);
        Thread ct = new Thread(cms);
        lt.start();
        t.start();
        ct.start();
    }

    public static void main(String[] args) {
        new OneServer();
    }


}
