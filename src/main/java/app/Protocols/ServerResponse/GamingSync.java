package app.Protocols.ServerResponse;

import app.Models.Player;
import app.Protocols.ScrabbleProtocol;

public class GamingSync extends ScrabbleProtocol {
    //    private int userID;     -----overlapped with Player
    private String FLAG;

    public GamingSync() {
        super.setTAG("GamingSync");
    }

    public Player[] getPlayerList() {
        return playerList;
    }

    public void setPlayerList(Player[] playerList) {
        this.playerList = playerList;
    }

    public int getNextTurn() {
        return nextTurn;
    }


    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    private String command;   // new add -- 9.28

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public boolean getVoteSuccess() {
        return voteSuccess;
    }

    public void setVoteSuccess(boolean voteSuccess) {
        this.voteSuccess = voteSuccess;
    }

    //first cell is about successful packet transmission
    //Second cell is about vote result.
    private boolean voteSuccess;

    /*Contain score....
     *
     * */
    private Player[] playerList;  //previously be Users[] currentUserList

    // new add  -- 9.28
    private char[][] board;



    // whose turn??  int turn
    private int nextTurn;

    public GamingSync(String command, Player[] playerList,int nextTurn, char[][] board, boolean voteSuccess) {
        super.setTAG("GamingSync");
        this.nextTurn = nextTurn;
        this.command = command;
        this.playerList = playerList;
        this.board = board;
        this.voteSuccess = voteSuccess;
    }
}