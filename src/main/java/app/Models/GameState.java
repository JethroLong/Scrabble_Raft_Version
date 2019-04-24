package app.Models;

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

    private ArrayList<Users> userList;
    private ArrayList<ArrayList<Users>> teamsInWait;
    private ArrayList<Player> playerList;
    private int[] playersID;

    private ConcurrentHashMap<Integer, String> db;
    private ConcurrentHashMap<Integer, ArrayList<Users>> teams;
    private BlockingQueue<Pack> queue;

    public GameState() {
    }

}
