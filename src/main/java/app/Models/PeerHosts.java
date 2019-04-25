package app.Models;

import java.net.Socket;

public class PeerHosts {
    private int peerID;
    private String peerHost;

    public PeerHosts() {}

    public PeerHosts(int peerID, String peerHost) {
        this.peerID = peerID;
        this.peerHost = peerHost;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public String getPeerHost() {
        return peerHost;
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }
}
