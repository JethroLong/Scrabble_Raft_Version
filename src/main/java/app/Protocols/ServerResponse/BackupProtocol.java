package app.Protocols.ServerResponse;

import app.Models.GameState;
import app.Models.PeerHosts;
import app.Protocols.ScrabbleProtocol;

public class BackupProtocol extends ScrabbleProtocol {
    private GameState gameState;

    public GameState getGameState() {
        return gameState;
    }

    private PeerHosts[] peerHosts;

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

    private int initialClientID;

    public int getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    private int leaderID;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public BackupProtocol(){
        super.setTAG("BackupProtocol");
    }
    public BackupProtocol(GameState gameState, PeerHosts[] peerHosts, int leaderID){
        super.setTAG("BackupProtocol");
        this.gameState = gameState;
        this.peerHosts = peerHosts;
        this.leaderID = leaderID;
    }
}
