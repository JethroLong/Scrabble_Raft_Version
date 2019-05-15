package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class ElectedProtocol extends ScrabbleProtocol {
    /**
     This protocol is used to informs other peers that I was elected as the new leader.
     **/
    private String newLeader;
    public String  getNewLeader(){return newLeader;}

    public void setNewLeader(String newLeader) {
        this.newLeader = newLeader;
    }

    public ElectedProtocol(String newLeader){
        super.setTAG("ElectedProtocol");
        this.newLeader = newLeader;
    }
}
