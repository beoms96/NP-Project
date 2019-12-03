package ClientLogic;

import java.io.IOException;
import java.util.ArrayList;

public class MultiClientThread implements Runnable {
    //Member
    private MultiClient mc;

    //Constructor
    public MultiClientThread(MultiClient mc) {
        this.mc = mc;
        try {
            mc.setIdarr((ArrayList<String>)mc.getOis().readObject());
        } catch(Exception e) { e.printStackTrace(); }
    }

    //Method
    @Override
    public void run() {
        String msg = null;
        String[] receive = null;
        boolean isStop = false;
        while(!isStop) {
            try {
                msg = (String)mc.getOis().readObject();
                receive = msg.split("#");
            } catch(Exception e) {
                e.printStackTrace();
                isStop = true;
            }
            if(receive[1].equals("quit")) { //quit msg
                if(receive[0].equals(mc.getId())) {
                    mc.getJf().setVisible(false);
                    System.exit(0);
                }
                else {
                    mc.getJta().append("Client " + receive[0] +" terminates" + System.getProperty("line.separator"));
                    mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
                    mc.getIdarr().remove(receive[0]);
                    updateIDList();
                }
            }
            else if(receive[1].equals("Enter")) {
                mc.getJta().append("Client " + receive[0] +" Come in" + System.getProperty("line.separator"));
                mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
                mc.getIdarr().add(receive[0]);
                updateIDList();
            }
            else {  //normal msg
                mc.getJta().append(receive[0] + " : " + receive[1] + System.getProperty("line.separator"));
                mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
            }
        }
    }

    public void updateIDList() {
        mc.getIdList().setText(" Chatting List ");
        for (String str : mc.getIdarr()) {
            mc.getIdList().append(System.getProperty("line.separator") + " " + str);
            mc.getIdList().setCaretPosition(mc.getIdList().getDocument().getLength());
        }
    }

}
