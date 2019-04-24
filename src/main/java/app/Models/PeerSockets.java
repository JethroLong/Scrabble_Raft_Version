package app.Models;

import java.net.Socket;

public class PeerSockets {
    private int peerID;
    private Socket peerSocket;

    public PeerSockets() {}

    public PeerSockets(int peerID, Socket peerSocket) {
        this.peerID = peerID;
        this.peerSocket = peerSocket;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public Socket getPeerSocket() {
        return peerSocket;
    }

    public void setPeerSocket(Socket peerSocket) {
        this.peerSocket = peerSocket;
    }
}
