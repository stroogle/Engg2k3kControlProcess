package controlprocess;

import java.net.*;
import java.io.*;

public class SocketServer {

    private ServerSocket server;
    private Socket client;
    private PrintWriter output;
    // private DataOutputStream output2;
    private BufferedReader input;
    private int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public String start() throws Exception {
        server = new ServerSocket(this.port);
        client = server.accept();

        output = new PrintWriter(client.getOutputStream(), true);
        // output2 = new DataOutputStream(client.getOutputStream());
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String nextLine = input.readLine();

        while (nextLine != null) {

            if (nextLine.equals("HELLO")) {
                sendMessage("HI THERE");
                // sendMessage2("HI THERE");
            }

            if (nextLine.equals("STOP")) {
                sendMessage("STOP");
                // sendMessage2("HI THERE");
                break;
            }

            nextLine = input.readLine();
        }

        return nextLine;

    }

    public void close() throws Exception {

        // output2.close();

        this.input.close();
        this.output.close();
        this.client.close();
        this.server.close();

    }

    // PrintWriter variant.
    public void sendMessage(String str) {
        output.println(str);
        System.out.println("SENT: " + str);
         
    }
    

    // DataOutputStream variant.
    // public void sendMessage2(String str) {
    //     try{
    //         String msgWithNewLnChar = str + "\n";
    //         output2.write(msgWithNewLnChar.getBytes());
    //         output2.flush();
    //         System.out.println("SENT: " + str);
    //     } 
    //     catch(Exception e){
    //         System.out.println(e);
    //     }
    // }

}
