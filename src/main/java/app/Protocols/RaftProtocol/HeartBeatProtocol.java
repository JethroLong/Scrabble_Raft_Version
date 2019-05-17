package app.Protocols.RaftProtocol;
import app.Models.PeerHosts;
import app.Protocols.ScrabbleProtocol;


public class HeartBeatProtocol extends ScrabbleProtocol {

    private String initiator;
    private String heartbeat = "Heart beat message.";
    public String getHeartbeat() {
        return heartbeat;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
    }


    public HeartBeatProtocol(String initiator){
        super.setTAG("HeartBeatProtocol");
        this.initiator = initiator;
    }
}
