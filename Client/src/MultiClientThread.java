public class MultiClientThread implements Runnable {
    //Member
    private MultiClient mc;
    //Constructor
    public MultiClientThread(MultiClient mc) {
        this.mc = mc;
    }
    //Method
    public void run() {
        String msg = null;
        String[] receive = null;
        boolean isStop = false;
        while(!isStop) {
            try {
                msg = (String)mc.getOis().readObject(); //Wating input
                receive = msg.split("#");
            }
            catch(Exception e) {
                e.printStackTrace();
                isStop = true;
            }
            if (receive[1].equals("quit")) {    //quitMsg
                if(receive[0].equals(mc.getId())) { //I terminate
                    mc.exit();
                }
                else {  //Another terminate
                    mc.getJta().append("Client " + receive[0] +" terminates" + System.getProperty("line.separator"));
                    mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
                }
            }
            else {  //normal msg
                mc.getJta().append(receive[0] + " : " + receive[1] + System.getProperty("line.separator"));
                mc.getJta().setCaretPosition(mc.getJta().getDocument().getLength());
            }//end if
        }   //end while
    }
}
