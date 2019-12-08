package ClientLogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CrypMultiClientThread implements Runnable {
    //Member
    private MultiClient mc;

    //Constructor
    public CrypMultiClientThread(MultiClient mc) {
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
                    mc.getIdarr().remove(receive[0]);
                    updatePKList();
                    mc.getJf().setVisible(false);
                    System.exit(0);
                }
                else {
                    mc.getJta().append("Client " + receive[0] +" terminates" + System.getProperty("line.separator"));
                    mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
                    mc.getIdarr().remove(receive[0]);
                    updateIDList();
                    updatePKList();
                }
            }
            else if(receive[1].equals("Enter")) {
                mc.getJta().append("Client " + receive[0] + " Come in" + System.getProperty("line.separator"));
                mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
                mc.getIdarr().add(receive[0]);
                updateIDList();
                updatePKList();
                if(mc.getIdarr().size() == 1) {
                    String key = mc.getCaes().createRandomKey();    //Create AES Random Key
                    mc.setChatAESKey(key);
                    mc.setChatSet(true);
                }
                else {
                    if(mc.getIdarr().get(0).equals(mc.getId())) {   //Send Key Client, Already set key
                        String encryptedKey = mc.getCrsa().encode(mc.getChatAESKey(), mc.getPublicKeyList().get(receive[0]));
                        try {
                            mc.getOos().writeObject(mc.getId()+"#SendKey");
                            mc.getOos().writeObject(receive[0] + "#" + encryptedKey);
                        } catch(IOException ioe) { ioe.printStackTrace(); }
                    }
                    else if(receive[0].equals(mc.getId())){  //Receive Key Client, Not set key yet
                        try {
                            String encryptedKey = (String)mc.getOis().readObject();
                            mc.setChatAESKey(mc.getCrsa().decode(encryptedKey, mc.getPrivateKey()));
                            mc.setChatSet(true);
                        }catch(IOException | ClassNotFoundException ioe) { ioe.printStackTrace(); }
                    }
                }
            }
            else {  //normal msg
                String result = "";
                try {
                    mc.getCaes().createKey(mc.getChatAESKey());
                    mc.getCaes().modeDecrypt();
                    result = mc.getCaes().msgAESDecrypt(receive[1]);
                } catch(Exception e) { e.printStackTrace(); }
                mc.getJta().append(receive[0] + " : " + result + System.getProperty("line.separator"));
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

    public void updatePKList() {
        mc.setPublicKeyList(new HashMap<String, String>());
        try {
            ArrayList<String> idarr = new ArrayList<String>();
            ArrayList<String> pkarr = new ArrayList<String>();
            for(int i=0; i<mc.getIdarr().size();i++) {
                String id = (String)mc.getOis().readObject();
                String pk = (String)mc.getOis().readObject();
                idarr.add(id);
                pkarr.add(pk);
            }
            String[] idList = idarr.toArray(new String[idarr.size()]);
            String[] pkList = pkarr.toArray(new String[pkarr.size()]);
            for (int i=0;i<idList.length;i++) {
                mc.getPublicKeyList().put(idList[i], pkList[i]);
            }
        } catch(ClassNotFoundException | IOException e) { e.printStackTrace(); }
    }

}
