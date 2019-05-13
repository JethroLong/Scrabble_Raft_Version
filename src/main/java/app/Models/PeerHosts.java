package app.Models;

import java.net.Socket;

public class PeerHosts {
    private int peerID;
    private String peerHost;

    public String getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(String peerPort) {
        this.peerPort = peerPort;
    }

    private String peerPort;

    public PeerHosts() {}

    public PeerHosts(int peerID, String peerHost,String peerPort) {
        this.peerID = peerID;
        this.peerHost = peerHost;
        this.peerPort = peerPort;
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
