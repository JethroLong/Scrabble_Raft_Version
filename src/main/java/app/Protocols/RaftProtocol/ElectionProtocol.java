package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class ElectionProtocol extends ScrabbleProtocol {
    /**
     This class defines the format of an election ticket(message).
     **/
    private boolean vote = false;
    private String elector;
    private int term;
    private String candidate;
    public boolean getVote(){return this.vote;}

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public String getElector(){return this.elector;}
    public void setElector(String elector){this.elector = elector;}
    public ElectionProtocol(){
        super.setTAG("ElectionProtocol");
    }

    public int getTerm() {return term; }

    public void setTerm(int term) { this.term = term; }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public ElectionProtocol(boolean vote, int term, String elector, String candidate){
        super.setTAG("ElectionProtocol");
        this.vote = vote;
        this.term = term;
        this.elector = elector;
        this.candidate = candidate;
    }

    @Override
    public String toString() {
        return String.format("TAG: %s, ELECTOR: %s, CANDIDATE: %s, TERM: %d, VOTE: %b",
                super.getTAG(), elector, candidate, term, vote);
    }
}
