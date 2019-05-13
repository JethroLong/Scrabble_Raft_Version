package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class StartElectionProtocol extends ScrabbleProtocol {
    private int term = 0;
    private int candidate;
    public int getTerm(){return this.term;}
    public int getCandidate(){return this.candidate;}
    public StartElectionProtocol(){
        super.setTAG("StartElectionProtocol");
    }
    public StartElectionProtocol(int term, int candidate){
        super.setTAG("StartElectionProtocol");
        this.term = term;
        this.candidate = candidate;
    }
}
