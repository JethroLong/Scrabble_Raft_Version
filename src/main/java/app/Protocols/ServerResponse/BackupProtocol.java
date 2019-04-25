package app.Protocols.ServerResponse;

import app.Models.GameState;
import app.Models.PeerSockets;
import app.Protocols.ScrabbleProtocol;

public class BackupProtocol extends ScrabbleProtocol {
    private GameState gameState;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public PeerSockets[] getPeerSockets() {
        return peerSockets;
    }

    public void setPeerSockets(PeerSockets[] peerSockets) {
        this.peerSockets = peerSockets;
    }

    private PeerSockets[] peerSockets;


    public BackupProtocol(){
        super.setTAG("BackupProtocol");
    }
    public BackupProtocol(GameState gameState, PeerSockets[] peerSockets){
        super.setTAG("BackupProtocol");
        this.gameState = gameState;
        this.peerSockets = peerSockets;
    }
}
