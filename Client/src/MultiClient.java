import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MultiClient implements ActionListener {
    //Member
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private JFrame jf;
    private JTextField jtf;
    private JTextArea jta;
    private JButton jbt;
    private JLabel jlb1, jlb2;
    private JPanel jp1, jp2;

    private String ip;
    private String id;

    //Constructor
    public MultiClient(String ip, String id) {
        this.ip = ip;
        this.id = id;
        setForm();  //Display Process
    }

    //Method
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String msg = jtf.getText().trim();
        if (obj == jtf) {
            if (msg == null || msg.length() == 0) {
                JOptionPane.showMessageDialog(jf, "Write the message", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {  //send normal msg
                try {
                    oos.writeObject(id + "#" + msg);    //alarm to server that client exit;
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                jtf.setText("");
                jtf.requestFocus();
            }
        }
        else if (obj == jbt) { //Push quit
            try {
                oos.writeObject(id + "#quit");  //inform I exit Server to Server
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.exit(0);
        }
    }

    public void connect() throws IOException {
        socket = new Socket(ip, 8000);  //---1 try connect
        jta.setText("Connect Success" + System.getProperty("line.separator"));
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        MultiClientThread ct = new MultiClientThread(this);
        Thread t = new Thread(ct);
        t.start();    //---2 Waiting input...Done ready input
    }

    public static void main(String[] args) throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        MultiClient mc = new MultiClient(args[0], args[1]);
        mc.connect();
    }

    public void exit() {
        jf.setVisible(false);
        System.exit(0);
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public String getId() {
        return id;
    }

    public JTextArea getJta() {
        return jta;
    }

    public void setForm() {
        jf = new JFrame("MultiCasting");
        jtf = new JTextField(30);
        jta = new JTextArea("", 10, 30);
        jlb1 = new JLabel("User ID: [" + id + "]");
        jlb2 = new JLabel("IP: " + ip);
        jbt = new JButton("Quit");
        jp1 = new JPanel();
        jp2 = new JPanel();
        jlb1.setBackground(Color.yellow);
        jlb2.setBackground(Color.green);
        jta.setBackground(Color.green);
        jp1.setLayout(new BorderLayout());
        jp2.setLayout(new BorderLayout());
        jp1.add("East", jbt);
        jp1.add("Center", jtf);
        jp2.add("Center", jlb1);
        jp2.add("East", jlb2);
        jf.add("South", jp1);
        jf.add("North", jp2);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jf.add("Center", jsp);

        jtf.addActionListener(this);
        jbt.addActionListener(this);

        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //Send msg to Server that I closed
                try {
                    oos.writeObject(id + "#quit");  //inform I exit Server to Server
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                System.exit(0);
            }

            public void windowOpened(WindowEvent e) {
                jtf.requestFocus();
            }
        });

        jta.setEditable(false);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();   //Screen size
        int scrHeight = d.height;
        int scrWidth = d.width;
        jf.pack();
        jf.setLocation((scrWidth - jf.getWidth()) / 2, (scrHeight - jf.getHeight()) / 2);
        jf.setResizable(false);
        jf.setVisible(true);

        //jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}