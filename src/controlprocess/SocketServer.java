package controlprocess;


import java.net.*;
import java.io.*;

public class SocketServer implements Runnable{

    private ServerSocket server;
    private Socket client;
    private PrintWriter output;
    private BufferedReader input;
    private int port;
    public CCComms comms;
    public String status = "";

    public SocketServer(int port, CCComms c) {
        this.port = port;
        this.comms = c;
    }

    public String start() throws Exception {
        server = new ServerSocket(this.port);
        client = server.accept();

        output = new PrintWriter(client.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String nextLine = input.readLine();

        while (nextLine != null) {

            // if (nextLine.equals("HELLO")) {
            //     sendMessage("HI THERE");
            // }

            // if (nextLine.equals("STOP")) {
            //     sendMessage("STOP");
            //     break;
            // }

            status = nextLine;

            this.comms.readJob();
            sendMessage(comms.msgToESP);

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


    @Override
    public void run() {
        try {
            String lastMessage = null;
        
            while (lastMessage == null || !lastMessage.equals("STOP")) {
                lastMessage = this.start();
                this.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }


}
