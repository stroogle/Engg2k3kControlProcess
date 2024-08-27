package controlprocess;


import java.net.*;
import java.io.*;

public class SocketServer {

    private ServerSocket server;
    private Socket client;
    private PrintWriter output;
    private BufferedReader input;
    private int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public String start() throws Exception {
        server = new ServerSocket(this.port);
        client = server.accept();

        output = new PrintWriter(client.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String nextLine = input.readLine();

        while (nextLine != null) {

            if (nextLine.equals("HELLO")) {
                sendMessage("HI THERE");
            }

            if (nextLine.equals("STOP")) {
                sendMessage("STOP");
                break;
            }

            nextLine = input.readLine();
        }

        return nextLine;

    }

    public void close() throws Exception {
        this.input.close();
        this.output.close();
        this.client.close();
        this.server.close();

    }

    public void sendMessage(String str) {
        output.println(str);
        System.out.println("SENT: " + str);
         
    }


}
