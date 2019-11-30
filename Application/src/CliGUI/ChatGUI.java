package CliGUI;

import ClientLogic.MultiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ChatGUI implements ActionListener{
    //Member
    private MultiClient mc;

    private JFrame jf;
    private JPanel chatPanel, mediaPanel;
    private JTextField msgField; //For Send Msg
    private JTextArea chatArea;  //For Chatting Msg
    private JTextArea idList, serverList;
    private JButton sendBt, downloadBt, quitBt, streamBt, uploadBt;
    private JLabel idLabel, ipLabel;

    private String ip;
    private String id;
    private int check;

    //Constructor
    public ChatGUI(String iid, int check, String ip) throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mc = new MultiClient(iid, ip, check);
        id = iid;
        this.check = check;
        this.ip = ip;
        setForm();
        mc.useJf(jf);
        mc.useJta(chatArea);
        mc.useIdList(idList);
        mc.useServerList(serverList);
        mc.connect();
    }

    public ChatGUI(String iid, int check, String ip, String ipk) throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mc = new MultiClient(iid, ip, ipk, check);
        id = iid;
        this.check = check;
        this.ip = ip;
        setForm();
        mc.useJf(jf);
        mc.useJta(chatArea);
        mc.useIdList(idList);
        mc.useServerList(serverList);
        mc.connect();
    }

    //Method
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String msg = msgField.getText().trim();
        if(obj == msgField || obj == sendBt) {
            if(msg == null || msg.length() == 0) {
                JOptionPane.showMessageDialog(jf, "Write the msg", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {  //send msg
                if (check == 0)
                    mc.sendNormal(msg);
                else if (check == 1)
                    mc.sendCrypto(msg);
            }

            msgField.setText("");
            msgField.requestFocus();
        }
        else if(obj == uploadBt) {
            File[] f = null;
            String path = null;
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setAcceptAllFileFilterUsed(true);
            int result = fc.showOpenDialog(jf);
            if(result == JFileChooser.APPROVE_OPTION) {
                f = fc.getSelectedFiles();
                path = fc.getCurrentDirectory().getPath();
                if(check == 0)
                    mc.uploadNormal(f, path);
                else if(check == 1){
                    String key = JOptionPane.showInputDialog(jf, "Input Key For Encrypted > 32","12345678901234567890123456789012345");
                    try {
                        mc.uploadCrypto(f, path, key);
                    } catch(UnsupportedEncodingException u) { u.printStackTrace(); }
                }
            }
            msgField.requestFocus();
        }
        else if(obj == downloadBt) {
            String[] fileList = mc.getFilearr().toArray(new String[mc.getFilearr().size()]);
            Object selected = JOptionPane.showInputDialog(jf, "What do yot want to download?", "download", JOptionPane.QUESTION_MESSAGE, null, fileList, fileList[0]);
            if(selected == null)
                JOptionPane.showMessageDialog(jf, "Not Download!");
            else {
                if(check == 0)
                    mc.downloadNormal((String) selected);
                else if(check == 1) {
                    String key = JOptionPane.showInputDialog(jf, "Input Key for Decrypted > 32","12345678901234567890123456789012345");
                    if(key!=null) {
                        try {
                            mc.downloadCrypto((String) selected, key);
                        } catch (UnsupportedEncodingException u) {
                            u.printStackTrace();
                        }
                    }
                }
            }
            msgField.requestFocus();
        }
        else if(obj == streamBt) {
            if(check == 0) {
                mc.streamNormal();
            }
            else if(check == 1) {
                mc.streamCrypto();
            }
            msgField.requestFocus();
        }
        else if(obj == quitBt) {    //push quit
            mc.exit();
        }
    }

    public void setForm() {
        jf = new JFrame("Chatting");
        jf.setLayout(null);
        jf.setSize(1420,750);
        jf.setBackground(Color.white);
        chatPanel = new JPanel();
        mediaPanel = new JPanel();
        chatPanel.setLayout(null);
        mediaPanel.setLayout(null);
        chatPanel.setBackground(Color.lightGray);
        mediaPanel.setBackground(Color.lightGray);
        chatArea = new JTextArea("", 50, 30);
        msgField = new JTextField(30);
        idList = new JTextArea(" Chatting List ", 10,10);
        serverList = new JTextArea(" Server File List ", 30, 20);
        chatArea.setEditable(false);
        idList.setEditable(false);
        serverList.setEditable(false);
        sendBt = new JButton("Send");
        uploadBt = new JButton("Upload");
        downloadBt = new JButton("Download");
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
        mediaPanel.add(uploadBt);
        mediaPanel.add(downloadBt);
        mediaPanel.add(streamBt);

        jf.add(chatPanel);
        jf.add(mediaPanel);

        //jf
        chatPanel.setBounds(10,10, 1400,450);
        mediaPanel.setBounds(10,460,1400,250);
        //chatPanel
        idLabel.setBounds(0,0,500,15);
        ipLabel.setBounds(500,0,500,15);
        chatsp.setBounds(0,15,1000,400);
        msgField.setBounds(0,415,1000,30);
        idsp.setBounds(1000,15,400,235);
        sendBt.setBounds(1000,250,400,100);
        quitBt.setBounds(1000,350,400,100);
        //MediaPanel
        serversp.setBounds(0,0,800,250);
        uploadBt.setBounds(800,0,100,250);
        downloadBt.setBounds(900,0,100,250);
        streamBt.setBounds(1000,0,400,250);

        msgField.addActionListener(this);
        sendBt.addActionListener(this);
        quitBt.addActionListener(this);
        downloadBt.addActionListener(this);
        streamBt.addActionListener(this);
        uploadBt.addActionListener(this);

        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mc.exit();
            }
            public void windowOpened(WindowEvent e) {   msgField.requestFocus();    }
        });

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int scrHeight = d.height;
        int scrWidth = d.width;
        jf.setLocation((scrWidth - jf.getWidth()) /2, (scrHeight - jf.getHeight())/2);
        jf.setResizable(true);
        jf.setVisible(true);
    }
}
