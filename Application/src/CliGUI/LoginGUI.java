package CliGUI;

import ClientLogic.ClientDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginGUI implements ActionListener {
    //Member
    private ClientDB cdb;

    private JFrame jf;
    private JPanel idPanel, pwPanel, loginPanel;
    private JTextField idt;
    private JPasswordField pwt;
    private JButton signupBt;
    private JButton loginBt;
    private JLabel idLabel;
    private JLabel pwLabel;

    //Constructor
    public LoginGUI() {
        cdb = new ClientDB();
        cdb.dbConnect();
        setForm();
    }

    //Method
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String inputID = idt.getText();
        String inputPW = new String(pwt.getPassword());

        if(obj == pwt || obj == loginBt) {
            if(cdb.checkInfo(inputID, inputPW)==false) {
                JOptionPane.showMessageDialog(jf, "Wrong ID or PW", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {
                //Login Complete chatting GUI (chat service + FTP Button + Real Video Button)
                JOptionPane.showMessageDialog(jf, "Login Complete", "Success", JOptionPane.INFORMATION_MESSAGE);
                jf.setVisible(false);
            }
        }
        else if(obj == signupBt) {
            if(cdb.insertInfo(inputID, inputPW)==false) {
                JOptionPane.showMessageDialog(jf, "Sign Up Failed", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {  //Stay Display
                JOptionPane.showMessageDialog(jf,"Sign Up Complete", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void setForm() {
        jf = new JFrame("Login");
        jf.setLayout(new GridLayout(3,1,0,5));
        idPanel = new JPanel();
        pwPanel = new JPanel();
        idLabel = new JLabel(" ID ");
        pwLabel = new JLabel(" Password ");
        idt = new JTextField(20);
        pwt = new JPasswordField(20);
        idPanel.setLayout(new GridLayout(2,1));
        pwPanel.setLayout(new GridLayout(2,1));
        idPanel.add(idLabel);
        idPanel.add(idt);
        pwPanel.add(pwLabel);
        pwPanel.add(pwt);
        loginPanel = new JPanel();
        signupBt = new JButton("Sign Up");
        loginBt = new JButton("Login");
        loginPanel.add(signupBt);
        loginPanel.add(loginBt);
        jf.add(idPanel);
        jf.add(pwPanel);
        jf.add(loginPanel);
        jf.setSize(500,400);

        pwt.addActionListener(this);
        signupBt.addActionListener(this);
        loginBt.addActionListener(this);

        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    cdb.dbDisconnect();
                } catch(Exception e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }

            public void windowOpened(WindowEvent e) {idt.requestFocus();}
        });

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int scrHeight = d.height;
        int scrWidth = d.width;
        jf.pack();
        jf.setLocation((scrWidth - jf.getWidth()) /2, (scrHeight - jf.getHeight())/2);
        jf.setResizable(false);
        jf.setVisible(true);

        //jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        LoginGUI lg = new LoginGUI();
    }
}
