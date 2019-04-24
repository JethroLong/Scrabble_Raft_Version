package app.Protocols.ServerResponse;

import app.Protocols.ScrabbleProtocol;

public class VoteRequest extends ScrabbleProtocol {

    private String command; //voteRequest

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int[] getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int[] startPosition) {
        this.startPosition = startPosition;
    }

    public int[] getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int[] endPosition) {
        this.endPosition = endPosition;
    }

    private int[] startPosition = new int[2];
    private int[] endPosition = new int[2];

    public VoteRequest(String command, int[] startPosition, int[] endPosition, int voteInitiator) {
        super.setTAG("VoteRequest");
        this.command = command;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.voteInitiator = voteInitiator;
    }

    public int getVoteInitiator() {
        return voteInitiator;
    }

    public void setVoteInitiator(int voteInitiator) {
        this.voteInitiator = voteInitiator;
    }

    private int voteInitiator;
}
