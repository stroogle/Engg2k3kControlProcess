import java.net.InetAddress;

import controlprocess.*;

public class Main {

    public static void main(String[] args) {
        try {
            InetAddress addr = InetAddress.getByName("10.20.30.1");
            MCPComms mcp = new MCPComms(addr, 2000);

            SocketServer server = new SocketServer(49203);

            // In one thread, we use MCPComms to talk to MCP
            // In another, we use CCComms to talk to the ESP
            
            Thread mcpThread = new Thread(mcp);
            Thread espThread = new Thread(new CCComms(server));

            mcpThread.start();
            espThread.start();
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

}
