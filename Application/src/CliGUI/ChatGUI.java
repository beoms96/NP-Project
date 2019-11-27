package CliGUI;

import ClientLogic.MultiClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatGUI implements ActionListener{
        //Member
        private MultiClient mc;

        private JFrame jf;
        private JPanel chatPanel, mediaPanel;
        private JTextField msgField; //For Send Msg
        private JTextArea chatArea;  //For Chatting Msg
        private JTextArea idList, serverList;
        private JButton sendBt, downloadBt, quitBt, streamBt;
        private JLabel idLabel, ipLabel;

        private String ip;
        private String id;

    //Constructor
    public ChatGUI(String iid, int check, String ip) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mc = new MultiClient(iid, check, ip);
        id = iid;
        this.ip = ip;
        setForm();
    }

    public ChatGUI(String iid, int check, String ip, String ipk) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mc = new MultiClient(iid, check, ip, ipk);
        id = iid;
        this.ip = ip;
        setForm();
    }

    //Method
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String msg = msgField.getText().trim();
        if(obj == msgField || obj == sendBt) {
            if(msg == null || msg.length() == 0) {
                JOptionPane.showMessageDialog(jf, "Write the msg", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {  //send normal msg
                //mc.Send(id + "#" + msg);
                msgField.setText("");
                msgField.requestFocus();
            }
        }
        else if(obj == downloadBt) {
            //ftp
            msgField.requestFocus();
        }
        else if(obj == streamBt) {
            //Streaming
            msgField.requestFocus();
        }
        else if(obj == quitBt) {    //push quit
            //mc.send(id + "#quit");
            jf.setVisible(false);
            System.exit(0);
        }
    }

    public void setForm() {
        jf = new JFrame("Chatting");
        jf.setLayout(null);
        jf.setSize(1425,845);
        jf.setBackground(Color.white);
        chatPanel = new JPanel();
        mediaPanel = new JPanel();
        chatPanel.setLayout(null);
        mediaPanel.setLayout(null);
        chatArea = new JTextArea("", 50, 30);
        msgField = new JTextField(30);
        idList = new JTextArea(" Chatting List ", 10,10);
        serverList = new JTextArea(" Server File List ", 10, 30);
        chatArea.setEditable(false);
        idList.setEditable(false);
        serverList.setEditable(false);
        sendBt = new JButton("Send");
        downloadBt = new JButton("From Server\nDownload");
        streamBt = new JButton("Streaming");
        quitBt = new JButton("QUIT");
        idLabel = new JLabel("User ID: [" + id + "]");
        ipLabel = new JLabel("IP: " + ip);

        chatArea.setBackground(Color.green);
        JScrollPane chatsp = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(chatsp);
        JScrollPane idsp = new JScrollPane(idList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(idsp);
        idLabel.setBackground(Color.yellow);
        ipLabel.setBackground(Color.yellow);
        chatPanel.add(msgField);
        chatPanel.add(idLabel);
        chatPanel.add(ipLabel);
        chatPanel.add(sendBt);
        chatPanel.add(quitBt);

        serverList.setBackground(Color.white);
        JScrollPane serversp = new JScrollPane(serverList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mediaPanel.add(serversp);
        mediaPanel.add(downloadBt);
        mediaPanel.add(streamBt);

        jf.add(chatPanel);
        jf.add(mediaPanel);

        //jf
        chatPanel.setBounds(10,10, 1400,500);
        mediaPanel.setBounds(10,510,1400,300);
        //chatPanel
        idLabel.setBounds(0,0,500,10);
        ipLabel.setBounds(500,0,500,10);
        chatsp.setBounds(0,15,1000,450);
        msgField.setBounds(0,465,1000,35);
        idsp.setBounds(1000,15,400,285);
        sendBt.setBounds(1000,300,400,100);
        quitBt.setBounds(1000,400,400,100);
        //MediaPanel
        serversp.setBounds(0,0,800,300);
        downloadBt.setBounds(800,0,200,300);
        streamBt.setBounds(1000,0,400,300);

        msgField.addActionListener(this);
        sendBt.addActionListener(this);
        quitBt.addActionListener(this);
        downloadBt.addActionListener(this);
        streamBt.addActionListener(this);

        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            public void windowOpened(WindowEvent e) {   msgField.requestFocus();    }
        });

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int scrHeight = d.height;
        int scrWidth = d.width;
        jf.setLocation((scrWidth - jf.getWidth()) /2, (scrHeight - jf.getHeight())/2);
        jf.setResizable(false);
        jf.setVisible(true);
    }

    public void exit() {
        jf.setVisible(false);
        System.exit(0);
        //mc.exit();
    }

}
