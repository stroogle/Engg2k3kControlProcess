import controlprocess.*;

public class Main {

    public static void main(String[] args) {

        SocketServer server = new SocketServer(49203);

        try {
            server.start();
            server.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
