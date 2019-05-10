package app.Protocols.RaftProtocol;
import app.Models.PeerHosts;
import app.Protocols.ScrabbleProtocol;


public class HeartBeatProtocol extends ScrabbleProtocol {

    private String heartbeat = "Peng! Peng! Peng!";

    public String getHeartbeat() {
        return heartbeat;
    }

    public HeartBeatProtocol(){
        super.setTAG("HeartBeatProtocol");
    }
    public HeartBeatProtocol(String heartbeat){
        super.setTAG("HeartBeatProtocol");
        this.heartbeat = heartbeat;
    }
}
