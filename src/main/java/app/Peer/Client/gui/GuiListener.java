package app.Peer.Client.gui;


import app.Models.Player;
import app.Models.Users;
import app.Protocols.ScrabbleProtocol;
import app.Protocols.ServerResponse.*;
import com.alibaba.fastjson.JSON;

import java.util.concurrent.BlockingQueue;

public class GuiListener {

    private volatile static GuiListener instance;
    private BlockingQueue<String> queue;

    private GuiListener() {

    }

    public static synchronized GuiListener get() {
        if (instance == null) {
            synchronized (GuiListener.class) {
                instance = new GuiListener();
            }
        }
        return instance;
    }

    public void addBlockingQueue(BlockingQueue queue) {
        this.queue = queue;
    }

    public synchronized void addMessage(String str) {
        //System.err.println("MSG FROM S" + str);
        ScrabbleProtocol scrabbleProtocol = JSON.parseObject(str, ScrabbleProtocol.class);
        String tag = scrabbleProtocol.getTAG();
        switch (tag) {
            case "NonGamingResponse":
                processNonGamingResonse(str);
                break;
            case "InviteACK":
                processInviteACK(str);
                break;
            case "GamingSync":
                processGamingSync(str);
                break;
            case "VoteRequest":
                processVoteRequest(str);
                break;
            case "ErrorProtocol":
                processError(str);
                break;
            case "BackupProtocol":
                processBackup(str);
            default:
                break;
        }
    }

    private void processBackup(String str){

    }

    private void processVoteRequest(String str) {
        VoteRequest respond = JSON.parseObject(str, VoteRequest.class);
        int id = respond.getVoteInitiator();
        int[] startPosition = respond.getStartPosition();
        int[] endPosition = respond.getEndPosition();
        GuiController.get().showVoteRequest(id, startPosition, endPosition);
    }

    private void processGamingSync(String str) {
        GamingSync respond = JSON.parseObject(str, GamingSync.class);
        String command = respond.getCommand();
        char[][] board;
        Player[] players;
        switch (command) {
            case "update":
                players = respond.getPlayerList();
                GuiController.get().updatePlayerListInGame(players);
                int nextTurn = respond.getNextTurn();
                GuiController.get().checkIfStartATurn(nextTurn);
                board = respond.getBoard();
                GuiController.get().updateBoard(board);
                break;
            case "win":
                board = respond.getBoard();
                GuiController.get().updateBoard(board);
                players = respond.getPlayerList();
                GuiController.get().showWinners(players);

                //remove team
                GameLobbyWindow.get().clearPlayerList();

                //reset game parameters
                GuiController.get().resetGame();
                break;
            case "start":
                // update team status
                players = respond.getPlayerList();
                Users[] users = new Users[players.length];
                int i = 0;
                for (Player user : players) {
                    users[i] = user.getUser();
                    i++;
                }
                GuiController.get().updatePlayerListInLobby(users);
                GuiController.get().runGameWindow();
                break;
            default:
                break;
        }
    }

    private void processInviteACK(String str) {
        InviteACK respond = JSON.parseObject(str, InviteACK.class);
        String command = respond.getCommand();
        Users[] users = respond.getTeamList();
        switch (command) {
            case "inviteACK":
                boolean ac = respond.isAccept();
                if (!ac) {
                    GuiController.get().showInviteACK(respond.getId());
                }

                GuiController.get().updatePlayerListInLobby(users);
                break;
            case "teamUpdate":
                if (users != null) {
                    GuiController.get().updatePlayerListInLobby(users);
                } else {
                    GameLobbyWindow.get().clearPlayerList();
                }
                break;
            default:
                break;
        }
    }

    private synchronized void processNonGamingResonse(String str) {
        NonGamingResponse respond = JSON.parseObject(str, NonGamingResponse.class);
        String command = respond.getCommand();
        switch (command) {
            case "userUpdate":
                Users[] users = respond.getUsersList();
                synchronized (GuiController.get()) {
                    GuiController.get().updateUserList(users);
                }

                break;
            case "invite":
                int inviterId = respond.getUsersList()[0].getUserID();
                String inviterName = respond.getUsersList()[0].getUserName();
                GuiController.get().showInviteMessage(inviterId, inviterName);
                break;
            default:
                break;
        }
        //Users[] users = respond.getUsersList();
        //String status = respond.getStatus();
        //GuiController.get().showLoginRespond(users, "Free");
    }

    private void processError(String str) {
        ErrorProtocol respond = JSON.parseObject(str, ErrorProtocol.class);
        String command = respond.getErrorType();
        String errorMsg = respond.getErrorMsg();
        switch (command) {
            case "login":
                LoginWindow.get().showDialog(errorMsg);
                LoginWindow.get().run();
                break;
            case "lobby":
                if (GameLobbyWindow.get()!= null){
                    GameLobbyWindow.get().showDialog(errorMsg);
                }else{
                    LoginWindow.get().showDialog(errorMsg);
                }
                break;
            case "other":
                if (GameLobbyWindow.get()!= null){
                    GameLobbyWindow.get().showDialog(errorMsg);
                }else{
                    LoginWindow.get().showDialog(errorMsg);
                }
                GuiController.get().shutdown();
                break;
            default:
                break;
        }
    }


}
