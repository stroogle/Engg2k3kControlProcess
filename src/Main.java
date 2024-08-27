import java.net.InetAddress;

import controlprocess.*;
import java.util.ArrayList;


public class Main {
    
    
    public static void main(String[] args) {
        ArrayList<String[]> CCP2MCP = new ArrayList<>(); 
        ArrayList<String[]> MCP2CCP = new ArrayList<>();

        try {
            SocketServer server = new SocketServer(49203, new CCComms(MCP2CCP, CCP2MCP));
            Thread espThread = new Thread(server);
            espThread.start();


            
            
            // InetAddress addr = InetAddress.getByName("10.20.30.1");
            // MCPComms mcp = new MCPComms(addr, 2000);


            // // In one thread, we use MCPComms to talk to MCP
            // // In another, we use CCComms to talk to the ESP
            
            // Thread mcpThread = new Thread(mcp);

            // mcpThread.start();
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

}
