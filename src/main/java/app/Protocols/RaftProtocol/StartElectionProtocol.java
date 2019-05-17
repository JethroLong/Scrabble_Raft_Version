package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class StartElectionProtocol extends ScrabbleProtocol {
    private int term = 0;
    private String candidate;
    public int getTerm(){return this.term;}
    public String getCandidate(){return this.candidate;}

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public StartElectionProtocol(){
        super.setTAG("StartElectionProtocol");
    }
    public StartElectionProtocol(int term, String candidate){
        super.setTAG("StartElectionProtocol");
        this.term = term;
        this.candidate = candidate;
    }

    @Override
    public String toString() {
        return String.format("TAG: %s, CANDIDATE: %d, TERM: %d",
                super.getTAG(), candidate, term);
    }
}
