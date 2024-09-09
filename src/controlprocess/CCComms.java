package controlprocess;
import java.util.ArrayList;


public class CCComms {
    
    public ArrayList<String[]> jobs4CCP = new ArrayList<>();
    public ArrayList<String[]> jobs4MCP = new ArrayList<>();
    public String msgToESP = "";


    public CCComms (ArrayList<String[]> MCP2CCPlist, ArrayList<String[]> CCP2MCPlist){
        jobs4CCP = MCP2CCPlist;
        jobs4MCP = CCP2MCPlist;

    }

    public void readJob(){
        if(jobs4CCP.isEmpty()){
        } else if(jobs4CCP.get(0)[1]=="AKIN"){
            jobs4CCP.remove(0);
        } else if(jobs4CCP.get(0)[1]=="STAT"){

            // response after request or executing command. 


            jobs4CCP.remove(0);
        } else if(jobs4CCP.get(0)[1]=="EXEC"){
            if(jobs4CCP.get(0)[4]=="START") {
                msgToESP = "STRT";
            }
            else if(jobs4CCP.get(0)[4]=="STOP") {
                msgToESP = "STOP";
            }
            else if(jobs4CCP.get(0)[4]=="MOVE") {
                msgToESP = "MOVE";
            }
            else if(jobs4CCP.get(0)[4]=="DOPN") {
                msgToESP = "DOPN";
            }
            else if(jobs4CCP.get(0)[4]=="DCLS") {
                msgToESP = "DCLS";
            }

            jobs4CCP.remove(0);
        }
    }

    
    
}
