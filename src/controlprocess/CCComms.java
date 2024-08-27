package controlprocess;


public class CCComms implements Runnable{
    SocketServer ardServer;


    public CCComms (SocketServer srv){
        ardServer = srv;
    }

    @Override
    public void run() {
        try {
            String lastMessage = null;
        
            while (lastMessage == null || !lastMessage.equals("STOP")) {
                lastMessage = ardServer.start();
                ardServer.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
}
