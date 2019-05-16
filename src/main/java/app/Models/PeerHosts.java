package app.Models;

import java.net.Socket;

public class PeerHosts {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;
    private String peerHost;

    public String getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(String peerPort) {
        this.peerPort = peerPort;
    }

    private String peerPort;

    public PeerHosts() {}

    public PeerHosts(String userName, String peerHost, String peerPort) {
        this.userName = userName;
        this.peerHost = peerHost;
        this.peerPort = peerPort;
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
        return String.format("userName: %d, peerHost: %s, peerPort: %s",
                this.userName, this.peerHost, this.peerPort);
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }
}
