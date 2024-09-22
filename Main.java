import java.net.InetAddress;
import java.util.ArrayList;

import ccp.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        ArrayList<String[]> messages_for_client = new ArrayList<String[]>();
        ArrayList<String> messages_for_mcp = new ArrayList<String>();

        int SOCKET_SERVER_PORT = 2006;
        int MCP_PORT_NUMBER = 2000;

        ClientComms carriage_client = new ClientComms(SOCKET_SERVER_PORT, messages_for_client, messages_for_mcp);

        Thread client_thread = new Thread(carriage_client);
        client_thread.start();

        InetAddress addr = InetAddress.getByName("127.0.0.1");
        MCPComms mcp_socket = new MCPComms(addr, MCP_PORT_NUMBER, messages_for_client, messages_for_mcp);
        Thread mcp_thread = new Thread(mcp_socket);

        mcp_thread.start();

    }

}