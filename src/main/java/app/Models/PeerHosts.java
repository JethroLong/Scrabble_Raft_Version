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

    public PeerHosts(int peerID, String peerHost, String peerPort) {
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

    public PeerHosts(String peerHost, String peerPort) {
        this.peerHost = peerHost;
        this.peerPort = peerPort;
    }

    public String getPeerHost() {
        return peerHost;
    }

    @Override
    public String toString() {
        return String.format("peerID: %d, peerHost: %s, peerPort: %s",
                this.peerID, this.peerHost, this.peerPort);
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }
}
