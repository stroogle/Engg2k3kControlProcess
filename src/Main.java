import java.net.InetAddress;

import controlprocess.*;

public class Main {

    public static void main(String[] args) {
        try {
            InetAddress addr = InetAddress.getByName("10.20.30.108");
            MCPComms mcp = new MCPComms(addr, 3008);

            SocketServer server = new SocketServer(49203);

            String lastMessage = null;
            // In one thread, we use MCPComms to talk to MCP
            // In another, we use CCComms to talk to the ESP
            // Thread mcpThread = new Thread(new MCPComms("10.20.30.108", 3008));
            // Thread espThread = new Thread(new CCComms());

            // mcpThread.start();
            // espThread.start();


            try {
                while (lastMessage == null || !lastMessage.equals("STOP")) {
                    lastMessage = server.start();
                    server.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
