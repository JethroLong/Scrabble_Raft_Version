package app.Models;

import app.Protocols.GamingProtocol.GamingOperationProtocol;
import app.Protocols.Pack;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private int gameHost;
    private boolean gameStart;
    private int whoseTurn;
    private int numVoted;
    private int agree;
    private int voteInitiator;
    private boolean voteSuccess;
    private int numPass;
    private char[][] board;

    private Users[] userList;
    private Team[] teamsInWait;
    private Player[] playerList;
    private int[] playersID;

    public GamingOperationProtocol getLatestBrickPlacing() {
        return latestBrickPlacing;
    }

    public void setLatestBrickPlacing(GamingOperationProtocol latestBrickPlacing) {
        this.latestBrickPlacing = latestBrickPlacing;
    }

    private GamingOperationProtocol latestBrickPlacing;


    public int getGameHost() {
        return gameHost;
    }

    public void setGameHost(int gameHost) {
        this.gameHost = gameHost;
    }

    public boolean isGameStart() {
        return gameStart;
    }

    public void setGameStart(boolean gameStart) {
        this.gameStart = gameStart;
    }

    public int getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(int whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public int getNumVoted() {
        return numVoted;
    }

    public void setNumVoted(int numVoted) {
        this.numVoted = numVoted;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public int getVoteInitiator() {
        return voteInitiator;
    }

    public void setVoteInitiator(int voteInitiator) {
        this.voteInitiator = voteInitiator;
    }

    public boolean isVoteSuccess() {
        return voteSuccess;
    }

    public void setVoteSuccess(boolean voteSuccess) {
        this.voteSuccess = voteSuccess;
    }

    public int getNumPass() {
        return numPass;
    }

    public void setNumPass(int numPass) {
        this.numPass = numPass;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public Users[] getUserList() {
        return userList;
    }

    public void setUserList(Users[] userList) {
        this.userList = userList;
    }

    public Team[] getTeamsInWait() {
        return teamsInWait;
    }

    public void setTeamsInWait(Team[] teamsInWait) {
        this.teamsInWait = teamsInWait;
    }

    public Player[] getPlayerList() {
        return playerList;
    }

    public void setPlayerList(Player[] playerList) {
        this.playerList = playerList;
    }

    public int[] getPlayersID() {
        return playersID;
    }

    public void setPlayersID(int[] playersID) {
        this.playersID = playersID;
    }

    public GameState() {
    }

}
