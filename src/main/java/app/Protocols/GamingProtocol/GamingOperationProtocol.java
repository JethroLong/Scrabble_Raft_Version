package app.Protocols.GamingProtocol;

import app.Protocols.ScrabbleProtocol;

public class GamingOperationProtocol extends ScrabbleProtocol {
    private boolean vote;

    public GamingOperationProtocol() {
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    private String command;  //vote, voteResponse
    public GamingOperationProtocol(String command){
        super.setTAG("GamingOperationProtocol");
        setCommand(command);
    }
    public GamingOperationProtocol(String command, boolean vote, BrickPlacing brickPlacing, int[] startPosition, int[] endPosition) {
        super.setTAG("GamingOperationProtocol");
        this.command = command;
        this.vote = vote;
        this.brickPlacing = brickPlacing;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public BrickPlacing getBrickPlacing() {

        return brickPlacing;
    }

    public void setBrickPlacing(BrickPlacing brickPlacing) {
        this.brickPlacing = brickPlacing;
    }

    private BrickPlacing brickPlacing;

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
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
}

