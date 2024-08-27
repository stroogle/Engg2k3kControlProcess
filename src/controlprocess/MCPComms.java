package controlprocess;


import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class MCPComms implements Runnable {
    public static PrintWriter dataOut;
    public static BufferedReader dataIn;
    public static String[] recievedMsg;
    public static Socket socketConnection;

    public MCPComms(InetAddress hostAddress, int portNumber){
        try{
            socketConnection = new Socket(hostAddress, portNumber); 
            dataOut = new PrintWriter(socketConnection.getOutputStream(), true);
            dataIn = new BufferedReader(new InputStreamReader(socketConnection.getInputStream()));
            
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    public void init(){
        try{
            String hello = "{\"client_type\":\"blade_runner\",\"message\":\"BRIN\",\"client_id\":\"BR08\"}";  // Our HELLO
            dataOut.println(hello);
    
            recieveMsg();
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }

    public void recieveMsg(){
        try {
            String rawStr = dataIn.readLine();
            recievedMsg = deserialize(rawStr);
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

        return data;
    }

    public void kill(){
        try{
            dataIn.close();
            dataOut.close();
            socketConnection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }

    @Override
    public void run() {

    }
}
