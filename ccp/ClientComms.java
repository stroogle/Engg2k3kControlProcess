package ccp;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ClientComms implements Runnable {

    private ArrayList<String> messages_for_client;
    private ArrayList<String> messages_for_mcp;
    private int socket_server_port;
    private ServerSocket server;
    private Socket client;
    private PrintWriter output;
    private BufferedReader input;

    public ClientComms(int port, ArrayList<String> messages_for_client, ArrayList<String> messages_for_mcp)
            throws Exception {
        this.socket_server_port = port;
        this.server = new ServerSocket(this.socket_server_port);
        this.messages_for_client = messages_for_client;
        this.messages_for_mcp = messages_for_mcp;
    }

    @Override
    public void run() {

        // Start Client Server.
        try {
            this.client = this.server.accept();
            this.output = new PrintWriter(client.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception error) {
            // Output problem, end function
            System.out.println("Failed to retrieve client connection");
            return;
        }

        while (true) {
            int command = -1;

            if (!messages_for_client.isEmpty())
                command = this.parseCommand(messages_for_client.remove(0)); // parse command needs changing to handle
                                                                            // just string, not string[]

            // for (int i = 0; i < commands.length; i++) {
            // this.sendMessage(commands[i]);
            // System.out.println("Command for client from queue: " + commands[i]);
            // }

            if (command != -1)
                this.sendMessage(command);

            String nextLine = "";

            try {
                if (this.input.ready())
                    nextLine = this.input.readLine();

                if (nextLine.equals("KILL"))
                    break;

                if (!nextLine.equals("")) {
                    messages_for_mcp.add(nextLine);
                    System.out.println("Command to MCPComms from ClientComms: " + messages_for_mcp.get(0));
                }

                nextLine = "";
            } catch (Exception error) {
                System.out.println("Something went wrong reading the buffer...");
                return;
            }

        }

        try {
            this.close();
        } catch (Exception error) {
            // Output problem, end function
            System.out.println("Failed to close socket server properly...");
            return;
        }
    }

    /**
     * @precondition - MCPComms has check message is for our client
     * @param command
     * @return
     */
    private String[] parseCommand(String[] command) {
        switch (command.length) {
            case 4:
                return new String[] {
                        command[1]
                };
            default:
                return new String[] {
                        command[1],
                        command[4]
                };
        }
    }

    private int parseCommand(String command) {
        int res;
        switch (command) {
            case "STOPC":
                res = 1;
                break;
            case "STOPO":
                res = 2;
                break;
            case "FSLOWC":
                res = 3;
                break;
            case "FFASTC":
                res = 4;
                break;
            case "RSLOWC":
                res = 5;
                break;
            case "DISCONNECT":
                res = 6;
                break;
            case "STRQ":
                res = 7;
                break;
            case "STOP":
                res = 8;
                break;
            default:
                res = 0;
                break;
        }

        return res;
    }

    private void close() throws Exception {
        this.input.close();
        this.output.close();
        this.client.close();
        this.server.close();
    }

    private void sendMessage(String str) {
        this.output.println(str);
        System.out.println("SENT: " + str);
    }

    private void sendMessage(int command) {
        this.output.println(command);
        System.out.println("Sent: " + command + " to the client. From CCPComms");
    }

}

// {
// "client_type": "ccp", "message": "STAT", "client_id": "BRXX", "timestamp":
// "2019-09-07T15:50+00Z",
// "status": "STOPPED/STARTED/ON/OFF/ERR/CRASH"
// }

// {
// "client_type": "ccp", "message": "STAT", "client_id": "BRXX", "timestamp":
// "2019-09-07T15:50+00Z",
// "status": "STOPPED_AT_STATION",
// "station_id": "STXX"
// }
