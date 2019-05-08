package app.Protocols.RaftProtocol;
import app.Models.PeerHosts;
import app.Protocols.ScrabbleProtocol;


public class HeartBeatProtocol extends ScrabbleProtocol {

    private PeerHosts[] peerHosts;
    private int initialClientID;
    private String heartbeat;

    public PeerHosts[] getPeerHosts() {
        return peerHosts;
    }

    public void setPeerHosts(PeerHosts[] peerHosts) {
        this.peerHosts = peerHosts;
    }

    public int getInitialClientID() {
        return initialClientID;
    }

    public void setInitialClientID(int initialClientID) {
        this.initialClientID = initialClientID;
    }



    public HeartBeatProtocol(){
        super.setTAG("HeartBeatProtocol");
    }
    public HeartBeatProtocol(PeerHosts[] peerHosts){
        super.setTAG("HeartBeatProtocol");
        this.peerHosts = peerHosts;
        this.heartbeat = "Peng! Peng! Peng!";
    }
}
