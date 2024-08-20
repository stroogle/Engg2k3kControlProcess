import controlprocess.*;

public class Main {

    public static void main(String[] args) {

        SocketServer server = new SocketServer(49203);

        String lastMessage = null;

        try {
            while (lastMessage == null || !lastMessage.equals("STOP")) {
                lastMessage = server.start();
                server.close();
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
