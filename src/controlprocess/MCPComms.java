package controlprocess;


import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.*;
import java.nio.charset.StandardCharsets;


public class MCPComms implements Runnable {
    public DatagramSocket socket;
    public InetAddress hostAddress;
    public int portNumber;
    public String[] receivedMsg;
    public LocalTime time;
    public static final int BUFFER_SIZE = 1024;
    public ArrayList<String[]> jobs4CCP = new ArrayList<>();
    public ArrayList<String[]> jobs4MCP = new ArrayList<>();

    public MCPComms(InetAddress hostAddress, int portNumber, ArrayList<String[]> MCP2CCPlist, ArrayList<String[]> CCP2MCPlist) {
        try {
            this.hostAddress = hostAddress;
            this.portNumber = portNumber;
            this.socket = new DatagramSocket();
            jobs4CCP = MCP2CCPlist;
            jobs4MCP = CCP2MCPlist;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void init(){
        try{
            time = LocalTime.now();
            String timeStr = time.toString();
            String hello = ("{\"client_type\":\"ccp\",\"message\":\"CCIN\",\"client_id\":\"BR08\",\"timestamp\": " + timeStr + "}");  // Our HELLO
            sendMsg(hello);

            receiveMsg();
            if(receivedMsg[1]=="AKIN"){
                System.out.println("Connection Established!");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }

    public void sendMsg(String msg) {
        try {
            byte[] sendData = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, hostAddress, portNumber);
            socket.send(sendPacket);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void receiveMsg() {
        try {
            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String rawStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
            receivedMsg = deserialize(rawStr);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String[] deserialize(String jsonStr){
        String[] data = jsonStr.substring(1, jsonStr.length() - 1).split(",");
        
        List<String> values = new ArrayList<>();
        
        for (String pair : data) {
            int colonIndex = pair.indexOf(": ");
            if (colonIndex != -1) {
                String value = pair.substring(colonIndex + 2).trim().replace("\"", "");
                values.add(value);
            }
        }
        
        jobs4CCP.add(data);

        return data;
    }

    public void kill() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public void run() {

    }
}
