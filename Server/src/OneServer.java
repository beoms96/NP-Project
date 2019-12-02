public class OneServer {
    //Member
    MultiServer ms = null;
    CrypMultiServer cms = null;

    public OneServer() {
        System.out.println("All management Start");
        ms = new MultiServer();
        cms = new CrypMultiServer();
        Thread t = new Thread(ms);
        Thread ct = new Thread(cms);
        t.start();
        ct.start();
    }

    public static void main(String[] args) {
        new OneServer();
    }


}
