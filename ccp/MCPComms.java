package ccp;

import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

public class MCPComms implements Runnable {
    public DatagramSocket socket;
    public InetAddress hostAddress;
    public int portNumber;
    public String[] receivedMsg;
    public LocalTime time;
    public ArrayList<String[]> jobsCCP;
    public ArrayList<String> jobsMCP;
    private int SO_TIMEOUT = 5;

    public MCPComms(InetAddress hostAddress, int portNumber, ArrayList<String[]> jobsC, ArrayList<String> jobsM) {
        try {
            this.hostAddress = hostAddress;
            this.portNumber = portNumber;
            this.socket = new DatagramSocket(55101, InetAddress.getByName("127.0.0.1"));
            this.jobsCCP = jobsC;
            this.jobsMCP = jobsM;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while (jobsMCP.isEmpty()) { // waiting to hear that client-carriage connection is established.
            try {
                Thread.sleep(10);
            } catch (Exception error) {
                System.out.println("I HATE SLEEP");
            }
        }
        jobsMCP.remove(0); // we received that msg, saying connection all good - pop this message off the
                           // queue and init with MCP.

        init(); // Send CCIN
        receiveMsg(); // Get AKIN (acknowledged)
        System.out.println("Line 50");

        try {
            this.socket.setSoTimeout(SO_TIMEOUT);
        } catch (SocketException e) {
            System.out.println("Failed to set SO_TIMEOUT on SocketDatagram");
            return;
        }
        // should now be connected ---------

        this.receivedMsg = new String[] {};

        while (true) {
            // Handles incoming message
            receiveMsg();

            boolean is_kill = false;

            if (this.receivedMsg.length != 0)
                is_kill = handleMCPMessage(receivedMsg); // adds recieved message to the job queue that the ccp should
                                                         // be able to see.

            if (is_kill) {
                break;
            }

            this.receivedMsg = new String[] {};

            // Handles jobs in queue.
            if (!jobsMCP.isEmpty()) { // we check that our outgoing msg queue is not empty, if there is something - it
                // is a stat msg we must send.
                String status = jobsMCP.remove(0); // get the status and pop from queue
                sendMsg(serialize(status)); // serialize it and send it.
            }

        }

        kill();

    }

    // ------------ HELPERS
    // ------------------------------------------------------------------------

    public void init() {
        try {
            time = LocalTime.now();
            String timeStr = time.toString();
            String hello = ("{\"client_type\":\"ccp\",\"message\":\"CCIN\",\"client_id\":\"BR08\",\"timestamp\":\""
                    + timeStr + "\"}"); // Our HELLO to MCP
            sendMsg(hello);

            receiveMsg();
            if (receivedMsg[1] == "AKIN") {
                System.out.println("Connection Established!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void sendMsg(String msg) {
        try {
            byte[] sendData = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetToSend = new DatagramPacket(sendData, sendData.length, hostAddress, portNumber);
            socket.send(packetToSend);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void receiveMsg() {
        try {
            byte[] receiveData = new byte[128]; // assume incoming packet wont be any larger than this.
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);
                String rawStr = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8);
                receivedMsg = deserialize(rawStr);
                for (int i = 0; i < receivedMsg.length; i++)
                    System.out.println(receivedMsg[i]);
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String[] deserialize(String jsonStr) {
        String[] data = jsonStr.substring(1, jsonStr.length() - 1).split(",");

        List<String> values = new ArrayList<>();

        for (String pair : data) {
            int colonIndex = pair.indexOf(": ");
            if (colonIndex != -1) {
                String value = pair.substring(colonIndex + 2).trim().replace("\"", "");
                values.add(value);
            }
        }

        return data;
    }

    public boolean handleMCPMessage(String[] message) {
        if (message[4] != "KILL") {
            return true;
        }

        this.jobsCCP.add(message);

        return false;
    }

    public void kill() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public String serialize(String status) {
        String message;
        String stationID;
        time = LocalTime.now();
        String timeStr = time.toString();
        if (status.length() > 8) { // message status is STOPPED_AT_STATION+STXX.
            stationID = status.substring(status.length() - 4);
            status = status.substring(0, status.length() - 4);
            message = ("{\"client_type\":\"ccp\",\"message\":\"STAT\",\"client_id\":\"BR08\",\"timestamp\":\"" + timeStr
                    + "\",\"status\":\"" + status + "\",\"station_id\":\"" + stationID + "\"}");
        } else { // normal status, not at a station.
            message = ("{\"client_type\":\"ccp\",\"message\":\"STAT\",\"client_id\":\"BR08\",\"timestamp\":\"" + timeStr
                    + "\",\"status\":\"" + status + "\"}");
        }

        return message;
    }

}