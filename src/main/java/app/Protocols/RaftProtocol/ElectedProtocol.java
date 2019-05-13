package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class ElectedProtocol extends ScrabbleProtocol {
    /**
     This protocol is used to informs other peers that I was elected as the new leader.
     **/
    private int newLeader;
    public int getNewLeader(){return newLeader;}
    public ElectedProtocol(int newLeader){
        super.setTAG("ElectedProtocol");
        this.newLeader = newLeader;
    }
}
