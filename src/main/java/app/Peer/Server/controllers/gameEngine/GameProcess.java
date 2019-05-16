package app.Peer.Server.controllers.gameEngine;


import app.Models.*;
import app.Peer.Client.gui.GuiController;
import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EnginePutMsg;
import app.Peer.Server.controllers.net.Net;
import app.Protocols.GamingProtocol.BrickPlacing;
import app.Protocols.GamingProtocol.GamingOperationProtocol;
import app.Protocols.NonGamingProtocol.NonGamingProtocol;
import app.Protocols.Pack;
import app.Protocols.ScrabbleProtocol;
import app.Protocols.ServerResponse.*;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class GameProcess {
    private final int ID_PLACEHOLDER = -1;
    private final int BOARD_SIZE = 20;
    private final int INITIAL_SEQ = 1;

    private int gameHost = ID_PLACEHOLDER;
    private boolean gameStart = false;
    private int whoseTurn;
    private int numVoted;
    private int agree;
    private int disagree;
    private int voteInitiator;
    private boolean voteSuccess;
    private int numPass;
    private int gameLoopStartSeq;
    private char[][] board;
    private GamingOperationProtocol latestBrickPlacing;

    public GameState getGameState() {
        updateGameState();
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private GameState gameState;

//    private int currentUserID;
//    private String msg;

    private ArrayList<Users> userList;
    private ArrayList<ArrayList<Users>> teamsInWait;
    private ArrayList<Player> playerList;
    private int[] playersID;
//    private int teamNum;

    private ConcurrentHashMap<Integer, String> db;
    private ConcurrentHashMap<Integer, ArrayList<Users>> teams;
    private BlockingQueue<Pack> queue;

    public int getIdByUserName(String username){
        for(Map.Entry entry : db.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());
            if(entry.getValue().equals(username)) return (Integer) entry.getValue();
        }
        return 0;
    }
    public String getUserNameById(int id){
        return db.get(id);
    }

    private volatile static GameProcess gameProcess;


    public GameProcess() {
        teamsInWait = new ArrayList<>();
        userList = new ArrayList<>();
        db = new ConcurrentHashMap<>();
        teams = new ConcurrentHashMap<>();
        board = new char[BOARD_SIZE][BOARD_SIZE];
        gameState = new GameState();
    }

    //Singleton GameProcess
    public static GameProcess getInstance() {
        if (gameProcess == null) {
            synchronized (GameProcess.class) {
                if (gameProcess == null) {
                    gameProcess = new GameProcess();
                }
            }
        }
        return gameProcess;
    }

    public void addBlockingQueue(BlockingQueue queue) {
        this.queue = queue;
    }


    public void switchProtocols(int currentUserID, String msg) {
//        updateGameState();

        ScrabbleProtocol temp = null;
        if (!msg.equals("null")) {
            temp = JSON.parseObject(msg, ScrabbleProtocol.class);
            String type = temp.getTAG();
            switch (type) {
                case "NonGamingProtocol":
                    NonGamingProtocol parsedObj = JSON.parseObject(msg, NonGamingProtocol.class);
                    nonGamingOperation(currentUserID, parsedObj);
                    break;
                case "GamingOperationProtocol":
                    gamingOperation(currentUserID, JSON.parseObject(msg, GamingOperationProtocol.class));
                    break;
                default:
                    userListToClient();
                    break;
            }
        }

    }

    private void nonGamingOperation(int currentUserID, NonGamingProtocol nonGamingProtocol) {
        //command: start,login, logout, invite(inviteOperation, inviteResponse), recovery, peerLogin
        String command = nonGamingProtocol.getCommand();
        String[] userList = nonGamingProtocol.getUserList();

        String clientHost = nonGamingProtocol.getLocalHostAddress();
        String clientLocalServerPort = nonGamingProtocol.getLocalServerPort();

        // add peer
        addPeerHost(currentUserID, clientHost, clientLocalServerPort);
        boolean isAccept = nonGamingProtocol.isInviteAccepted();
        int hostID = nonGamingProtocol.getHostID();
        nonGamingOperationExecutor(currentUserID, command, userList, isAccept, hostID);
    }

    private void addPeerHost(int clientNumber, String hostAddr, String port){
        PeerHosts newPeerHost = new PeerHosts(clientNumber, hostAddr, port);
        Net.getInstance().getPeerHosts().add(newPeerHost);
    }


    private void gamingOperation(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        //command: vote, voteResponse, disconnect
        String command = gamingOperationProtocol.getCommand();
        switch (command) {
            case "vote":
                if (gameStart && (whoseTurn == playerList
                        .get(playerIndexSearch(currentUserID))
                        .getInGameSequence())) {
                    if (gamingOperationProtocol.isVote()) {
                        hasVote(currentUserID, gamingOperationProtocol);
                    } else {
                        hasNotVote(currentUserID, gamingOperationProtocol);
                    }
                } else {
                    //ignore
                }
                break;
            case "voteResponse":
                if (gameStart) {
                    numVoted++;
                    playerVoteResponse(gamingOperationProtocol.isVote());
                }
                break;
            case "disconnect":
                disconnect(currentUserID);
                break;
            default:
                break;

        }

    }

    private synchronized void userRemove(Users user) {
        if (userList.contains(user)) {
            db.remove(user.getUserID(), user.getUserName());
            userList.remove(user);
        }
    }

    private void disconnect(int currentUserID) {
        if (gameStart == true) {
            if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                playerList.remove(playerIndexSearch(currentUserID));

//                win(currentUserID);  disconnection should not terminate the game
            }
            //remove disconnected users
//            if (db.containsKey(currentUserID)) {
//                db.remove(currentUserID);
//                userList.remove(userIndexSearch(currentUserID));
//            }
            userRemove(userList.get(userIndexSearch(currentUserID)));
            userListToClient();

            //reset gameEndCheck parameters
//            numPass = 0;
//            gameLoopStartSeq = 0;
        } else if (db.containsKey(currentUserID)) {
            //remove disconnected users
            userRemove(userList.get(userIndexSearch(currentUserID)));
//            if (db.containsKey(currentUserID)) {
//                db.remove(currentUserID);
//                userList.remove(userIndexSearch(currentUserID));
//            }
            userListToClient();
        } else {
            //Not exist
        }

        // remove peerHost
        Net.getInstance().getPeerHosts().remove(currentUserID);
    }

    private void recovery(){
        recoverGlobalParas(GuiController.get().getGameState());
        userListToClient();
        if (gameStart){
            if (voteInitiator == ID_PLACEHOLDER){ // if not in a voting progress previously
//                boardUpdate(playersID);
                hasNotVote(ID_PLACEHOLDER, latestBrickPlacing);
            }else{ // if in a voting progress
                hasVote(voteInitiator, latestBrickPlacing);
//                int[] start = latestBrickPlacing.getStartPosition();
//                int[] end = latestBrickPlacing.getEndPosition();
//                voting(voteInitiator,start, end);
            }
        }else{
            userListToClient();
        }
        enableBackup();
    }

    private void voting(int initiator, int[] start, int[] end){
        boardUpdate(initiator);
        voteOperation(initiator, start, end);
        waitVoting();

        voteResult(start, end);
        gameTurnControl();
        boardUpdate(initiator);

        //reset voteSuccess
        voteSuccess = false;
    }

    private void enableBackup(){
        GameEngine.getInstance().startBackup();
    }

    private void voteResult(int[] start, int[] end) {
        if ((double) agree / numVoted > 0.5) {
            //success
            int i;
            voteSuccess = true;
            int index = playerIndexSearch(voteInitiator);
            int currentPoints = playerList.get(index).getPoints();
            if (start[0] == end[0]) {
                playerList.get(index).setPoints(end[1] - start[1] + 1 + currentPoints);
            } else if (start[1] == end[1]) {
                playerList.get(index).setPoints(end[0] - start[0] + 1 + currentPoints);
            }
        } else {
            //failure
            voteSuccess = false;
        }

        //reset agree/disagree num
        voteInitiator = ID_PLACEHOLDER;
        numVoted = 0;
        agree = 0;
        disagree = 0;
    }

    //search player instance according to userID
    private int playerIndexSearch(int currentUserID) {
        int index;
        for (index = 0; index < playerList.size(); index++) {
            if (playerList.get(index).getUser().getUserID() == currentUserID) {
                break;
            }
        }
        return index;
    }

    private void hasVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        latestBrickPlacing = gamingOperationProtocol;
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        int[] start = gamingOperationProtocol.getStartPosition();
        int[] end = gamingOperationProtocol.getEndPosition();

        //reset gameEndCheck parameters
        numPass = 0;
//        gameLoopStartSeq = 0;

        voteInitiator = currentUserID;
        board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getBrick().charAt(0));

        voting(currentUserID, start, end);
    }

    private void hasNotVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        //initial check gameEnd conditions (1. if every player had a turn -- sequence loop check  2. num of direct pass)
        if (bp.getBrick() != null) {
            //reset gameEndCheck parameters
            numPass = 0;

            board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getBrick().charAt(0));
            gameTurnControl();
            boardUpdate(currentUserID);
        } else {
            numPass++;
            if (!gameEndCheck()) {
                gameTurnControl();
                boardUpdate(currentUserID);
            } else {
                win(currentUserID);
                //reset gameEndCheck parameters
                numPass = 0;
            }
        }
    }


    private boolean gameEndCheck() {
//        int index = playerIndexSearch(currentUserID);
        if (numPass == playerList.size()) {
            return true;  // game should be terminated
        } else {
            return false;  // game should continue
        }
    }

    private int findMaxSequence(){
        int max = ID_PLACEHOLDER;
        for(int i=0; i<playerList.size()-1; i++){
            max = Math.max(playerList.get(i).getInGameSequence(), playerList.get(i+1).getInGameSequence());
        }
        return max;
    }

    // check if the player represented by the next sequence exists in playerList
    private boolean checkSequence(int nextSequence){
        for(Player player : playerList){
            if(player.getInGameSequence() == nextSequence){
                return true;
            }
        }
        return false;
    }


    private void gameTurnControl() {
        int maxSequence = findMaxSequence();
        if (whoseTurn < maxSequence) {
            whoseTurn++;
            while(true){
                if(!checkSequence(whoseTurn)){
                    whoseTurn++;
                }else{
                    break;
                }
            }
        } else {
            whoseTurn = 1;
            while(true){
                if(!checkSequence(whoseTurn)){
                    whoseTurn++;
                }else{
                    break;
                }
            }
        }
    }

    public void waitVoting() {
        System.out.println("START WAITING: " + numVoted);
        while (numVoted != (playerList.size())) {
            Pack temp;
            try {
                temp = queue.take();
                System.out.println(temp.getMsg());
                switchProtocols(temp.getUserId(), temp.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("WAITING END: " + numVoted);
    }

    private void playerVoteResponse(boolean isVote) {
        if (isVote) {
            agree++;
        } else {
            disagree++;
        }
    }

    private void voteOperation(int voteInitiator, int[] start, int[] end) {
        String command = "voteRequest";
        Pack vote = new Pack(voteInitiator, JSON.toJSONString(new VoteRequest(command, start, end, voteInitiator)));
        vote.setRecipient(playersID);
        EnginePutMsg.getInstance().putMsgToCenter(vote);
    }

    private void nonGamingOperationExecutor(int currentUserID, String command, String[] peerList, boolean isAccept, int hostID) {
        switch (command.trim()) {
            case "start":
                start(currentUserID);
                break;
            case "login":
                login(currentUserID, peerList[0]);
                break;
            case "peerLogin":
                peerLogin(currentUserID, peerList[0]);
                break;
            case "logout":
                logout(currentUserID);
                break;
            case "inviteOperation":
                inviteOperation(currentUserID, peerList);
                break;
            case "inviteResponse":
                inviteResponse(currentUserID, hostID, isAccept);
                break;
            case "quit":
                if (gameStart == true) {
                    if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                        playerList.remove(playerIndexSearch(currentUserID));
                        teams.get(gameHost).remove(playerIndexSearch(currentUserID));
//                        win(currentUserID);
                    }
                }
            case "leave":
                leaveTeam(currentUserID, hostID);
                break;
            case "recovery":
                recovery();
            default:
                error(currentUserID, "Unknown Error", "lobby");
                break;
        }
    }

    private void leaveTeam(int currentUserID, int hostID) {
        int index = userIndexSearch(currentUserID);
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("ready")) {
            if (currentUserID == hostID) {
                //team host leaves
                for (Users member : teams.get(currentUserID)) {
                    teamUpdate(member.getUserID());
                }
                teamStatusUpdate(teams.get(currentUserID), "available");
                userListToClient();
                teamsInWait.remove(teams.get(currentUserID));
                teams.remove(currentUserID);
            } else {
                // other team members leave
                if (teams.containsKey(hostID)) {
                    ArrayList<Users> team = teams.get(hostID);
                    if (team.contains(userList.get(index))) {
                        team.remove(userList.get(index));
                        userList.get(index).setStatus("available");
                    }
                    Users[] temp = team.toArray(new Users[team.size()]);

                    teamUpdate(currentUserID);
                    teamUpdate(temp, hostID, false);
                    userListToClient();
                } else {
                    if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("ready")) {
                        error(currentUserID, "Unknown team", "lobby");
                    }
                    userListToClient();
                }
            }
        }
    }

    //search the index of a user at local memory
    private int userIndexSearch(String userName) {
        int index = 0;
        for (Users user : userList) {
            if (userName.trim().equals(user.getUserName())) {
                break;
            }
            index++;
        }
        if (index < userList.size()){
            return index;
        }else {
            return -1; // not found
        }
    }

    private int userIndexSearch(int userID) {
        int index = 0;
        for (Users user : userList) {
            if (user.getUserID() == userID) {
                break;
            }
            index++;
        }

        if (index < userList.size()){
            return index;
        }else {
            return -1; // not found
        }
    }

    private void boardInitiation() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void start(int currentUserID) {
        // a game can be started only when team exists (same as Host check) and there is no game in process
        if (teamsInWait.contains(teams.get(currentUserID)) && !gameStart) {

            //initiate game board
            boardInitiation();
            gameHost = currentUserID;
            ArrayList<Users> team = null;
            try {
                team = onlineCheck(teams.get(gameHost));
            } catch (Exception e) {

            }

            //playerID assigned here
            if (addPlayers(team)) {
                teamStatusUpdate(team, "in-game");

                gameStart = true;
                whoseTurn = 1;

                boardUpdate(playersID);
                boardUpdate(currentUserID);
            } else {
                error(currentUserID, "Start Failed! Active team members should be no less than 2", "lobby");
                userListToClient();
            }

            //broadcast to all online users to update status
            userListToClient();
        } else {
            error(currentUserID, "Not authorized to start game", "lobby");
        }
    }

    private synchronized ArrayList<Users> onlineCheck(ArrayList<Users> team) {
        for (Users member : team) {
            if (member == null || (!userList.contains(member))) {
                team.remove(member);
            }
        }
        teamUpdate(team.toArray(new Users[team.size()]), ID_PLACEHOLDER, false);
        return team;
    }

    private void login(int currentUserID, String userName) {
        // same username, replicated login requests not allowed
        if (!db.contains(userName) && db.get(currentUserID) == null) {
            db.put(currentUserID, userName);
            userList.add(new Users(currentUserID, userName));
            //send currentUserList back to client
            userListToClient();
        } else {
            error(currentUserID, "User already Exists", "login");
        }

    }

    // Follower handles login request comes from other peers -- binding userName & ID locally
    private void peerLogin(int currentUserID, String userName){
        if (!db.contains(userName) && db.get(currentUserID) == null) {
            db.put(currentUserID, userName);
            userList.add(new Users(currentUserID, userName));
        } else {
            error(currentUserID, "User already Exists", "Peerlogin");
        }
    }

    private void logout(int currentUserID) {
        if (db.get(currentUserID) != null) {
            if (gameStart) {
//                userList.remove(userIndexSearch(currentUserID));
//                db.remove(currentUserID);
                if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                    playerList.remove(playerIndexSearch(currentUserID));
                    win(currentUserID);
                }
                userRemove(userList.get(userIndexSearch(currentUserID)));
                userListToClient();
            } else {
//                userList.remove(userIndexSearch(currentUserID));
//                db.remove(currentUserID);
                userRemove(userList.get(userIndexSearch(currentUserID)));
                userListToClient();
            }
        } else {
            error(currentUserID, "No such user", "lobby");
        }
    }


    private void inviteOperation(int currentUserID, String[] peerList) {
        // initial check the status of user if he or she feels like inviting others
        // also check if he or she has already created a team
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("available") || teams.containsKey(currentUserID)) {
            if (!teams.containsKey(currentUserID)) {
                ArrayList<Users> team = new ArrayList<>();  // allow multiple teams in wait
                team.add(userList.get(userIndexSearch(currentUserID)));
                userList.get(userIndexSearch(currentUserID)).setStatus("ready"); // status changed when team created
                int hostID = currentUserID;
                teamsInWait.add(team);
                teams.put(hostID, team);
            }

            //make envelope and start inviting
            for (String peer : peerList) {
                if (userList.get(userIndexSearch(peer.trim())).getStatus().equals("available")) {
                    makeEnvelope(currentUserID, peer);
                }
            }
        } else {
            //error, no access error
            error(currentUserID, "No Access to invite others", "lobby");
            userListToClient();
        }
    }

    private synchronized void inviteResponse(int currentUserID, int hostID, boolean isAccept) {
        String command = "inviteACK";
        if (isAccept) {
            Users temp = userList.get(userIndexSearch(db.get(currentUserID)));
            if (!teams.get(hostID).contains(temp) && temp.getStatus().equals("available")) {
                teams.get(hostID).add(temp);
                temp.setStatus("ready");
            }
            int size = teams.get(hostID).size();
            Users[] teamList = teams.get(hostID).toArray(new Users[size]);
            inviteACK(command, currentUserID, hostID, isAccept, teamList); //ACK to inviteInitiator

            //broadcast to all members of a team
            teamUpdate(teamList, hostID, isAccept);

            //broadcast to all users to update status
            userListToClient();
        } else {
            if (teams.containsKey(hostID)) {
                int size = teams.get(hostID).size();
                Users[] teamList = teams.get(hostID).toArray(new Users[size]);
                inviteACK(command, currentUserID, hostID, isAccept, teamList);
            }
            userListToClient();
        }
    }

    private void teamUpdate(Users[] teamList, int hostID, boolean isAccept) {
        String command = "teamUpdate";
        for (int i = 0; i < teamList.length; i++) {
            inviteACK(command, hostID, teamList[i].getUserID(), isAccept, teamList);
        }
    }

    private void teamUpdate(int currentUserID) {
        String command = "teamUpdate";
        inviteACK(command, currentUserID, currentUserID, false, null);
    }


    private void inviteACK(String command, int currentUserID, int hostID, boolean isAccept, Users[] teamList) {
        Pack ACK = new Pack(hostID, JSON.toJSONString(new InviteACK(currentUserID, command, isAccept, teamList)));
        ACK.setRecipient(null);  //peer-to-peer
        EnginePutMsg.getInstance().putMsgToCenter(ACK);
    }


    private void error(int currentUserID, String msg, String type) {
        Pack errorMsg = new Pack(currentUserID, JSON.toJSONString(new ErrorProtocol(msg, type)));
        EnginePutMsg.getInstance().putMsgToCenter(errorMsg);
    }

    private void makeEnvelope(int currentUserID, String peerName) {
        if (db.contains(peerName)) {
            String command = "invite";
            Users[] inviteInitiator = new Users[]{userList.get(userIndexSearch(currentUserID))};
            int recipient = ID_PLACEHOLDER;
            for (Users user : userList) {
                if (user.getUserName().equals(peerName)) {
                    recipient = user.getUserID();
                    break;
                }
            }
            String envelope = JSON.toJSONString(new NonGamingResponse(inviteInitiator, command));
            Pack inviteEnvelope = new Pack(recipient, envelope);
            EnginePutMsg.getInstance().putMsgToCenter(inviteEnvelope);
        }
    }

    private void userListToClient() {
        Users[] current = new Users[userList.size()];
        current = userList.toArray(current);
        String command = "userUpdate";
        Pack list = new Pack(0, JSON.toJSONString(new NonGamingResponse(current, command)));
        EnginePutMsg.getInstance().putMsgToCenter(list);
    }

    private void userListToClient(int userID) {
        Users[] current = new Users[userList.size()];
        current = userList.toArray(current);
        String command = "userUpdate";
        Pack list = new Pack(userID, JSON.toJSONString(new NonGamingResponse(current, command)));
        EnginePutMsg.getInstance().putMsgToCenter(list);
    }

    private void teamStatusUpdate(ArrayList<Users> user, String status) {
        if (user != null) {
            for (Users member : user) {
                if (member != null) {
                    member.setStatus(status);
                }
            }
        }
    }

    private boolean addPlayers(ArrayList<Users> readyUser) {
        if (readyUser.size() >= 2) {
            int sequence = INITIAL_SEQ;
            playerList = new ArrayList<>();
            playersID = new int[readyUser.size()];
            for (Users member : readyUser) {
                playerList.add(new Player(member, sequence));
                playersID[sequence - 1] = member.getUserID();
                sequence++;
            }
            return true;
        } else {
            return false;
        }

    }

    private void boardUpdate(int currentUserID) {
        String command = "update";
//            Player[] playerArr = playerList.toArray(new Player[playerList.size()]);
        if (playerList != null) {
            int size = playerList.size();
            Player[] temp = playerList.toArray(new Player[size]);
            Pack update = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
            update.setRecipient(playersID);
            EnginePutMsg.getInstance().putMsgToCenter(update);
        }

    }

    private void boardUpdate(int[] playersID) {
        String command = "start";
//            Player[] playerArr = playerList.toArray(new Player[playerList.size()]);
        if (playerList != null) {
            int size = playerList.size();
            Player[] temp = playerList.toArray(new Player[size]);
            Pack update = new Pack(ID_PLACEHOLDER, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
            update.setRecipient(playersID);
            EnginePutMsg.getInstance().putMsgToCenter(update);
            boardUpdate(ID_PLACEHOLDER);
        }

    }

    private void win(int currentUserID) {
        String command = "win";
        Collections.sort(playerList);
        int hi = playerList.size() - 1;
        int i;
        for (i = hi - 1; i >= 0; i--)
            if (playerList.get(hi).getPoints() != playerList.get(i).getPoints()) {
                break;
            }
        ArrayList<Player> winner = new ArrayList<>();
        for (int j = hi; j > i; j--) {
            int numWin = playerList.get(j).getUser().getNumWin();
            playerList.get(j).getUser().setNumWin(++numWin);
            winner.add(playerList.get(j));
        }
        int size = winner.size();
        Player[] temp = winner.toArray(new Player[size]);
        Pack win = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
        win.setRecipient(playersID);   //multi-cast
        EnginePutMsg.getInstance().putMsgToCenter(win);
        teamStatusUpdate(teams.get(gameHost), "available");
        userListToClient();

        //terminate game, reset parameters
        resetGameParameters();

        boardInitiation();

        if (teams.containsKey(gameHost)) {
            teamsInWait.remove(teams.get(gameHost));
            teams.remove(gameHost, teams.get(gameHost));
            gameHost = ID_PLACEHOLDER;
        }
    }

    private void updateGameState(){
        gameState.setBoard(board);
        gameState.setAgree(agree);
//        gameState.setDb(db);
        gameState.setGameStart(gameStart);
        gameState.setNumPass(numPass);
        gameState.setNumVoted(numVoted);
//        gameState.setTeams(teams);
        gameState.setWhoseTurn(whoseTurn);
        gameState.setVoteSuccess(voteSuccess);
        gameState.setVoteInitiator(voteInitiator);
        gameState.setPlayersID(playersID); // for multi-cast
        gameState.setLatestBrickPlacing(latestBrickPlacing);

        if(playerList != null) {
            Player[] players = new Player[playerList.size()];
            players = playerList.toArray(players);
            gameState.setPlayerList(players);
        }
        if(userList != null) {
            Users[] users = new Users[userList.size()];
            users = userList.toArray(users);
            gameState.setUserList(users);
        }

        if(teamsInWait != null) {
            Team[] teams_list = new Team[teamsInWait.size()];
            for (int i = 0; i < teams_list.length; i++) {
                teams_list[i] = new Team(teamsInWait.get(i).get(0).getUserID(), teamsInWait.get(i));
            }
            gameState.setTeamsInWait(teams_list);
        }
    }

    // recover userList stats
    private void recoverUserList(Users[] oldUserList){
        for(Users oldUser : oldUserList){
            int index = userIndexSearch(oldUser.getUserName());
            // recovery user stats from old userList (old leader's)
            if (index != -1){
                userList.get(index).setNumWin(oldUser.getNumWin());
                userList.get(index).setStatus(oldUser.getStatus());
            }
        }
    }

    // recover teams
    private void recoverTeams(Team[] oldTeams){
        for(Team oldTeam : oldTeams){
            int hostID = oldTeam.getHostID();
            ArrayList<Users> newTeam = new ArrayList<>();
            teamsInWait.add(newTeam);
            teams.put(hostID, newTeam);
            for (Users oldMember : oldTeam.getTeamMember()){
                int newMemberIndex = userIndexSearch(oldMember.getUserName());
                Users newMember = userList.get(newMemberIndex);
                newTeam.add(newMember);
            }
        }
    }

    // recover playerList by reconstructing according to userName
    private void recoverPlayerList(Player[] oldPlayerList){
        for(Player oldPlayer : oldPlayerList){
            int oldPoints = oldPlayer.getPoints();
            int inGameSequence = oldPlayer.getInGameSequence();
            String oldPlayerName = oldPlayer.getUser().getUserName();
            int newPlayerUserIndex = userIndexSearch(oldPlayerName);
            Player newPlayer = new Player(userList.get(newPlayerUserIndex), inGameSequence);
            newPlayer.setPoints(oldPoints);
            playerList.add(newPlayer);
        }
    }

    // map vote initiator ID from old leader's record
    private int mapVoteInitiator(int oldInitiator, Users[] oldUserList){
        int newInitiatorID = ID_PLACEHOLDER;
        for(Users oldUser : oldUserList){
            if (oldUser.getUserID() == oldInitiator){
                int newIndex = userIndexSearch(oldUser.getUserName());
                newInitiatorID = userList.get(newIndex).getUserID();
                break;
            }
        }
        return newInitiatorID;
    }

    // map playersID from old playersID -- playersID is for multi-casting
    private int[] mapPlayersID(int[] oldPlayersID, Users[] oldUserList){
        int[] newPlayersID = new int[oldPlayersID.length];
        int arrayIndex = 0;
        for(int oldPlayerID : oldPlayersID){
            // find old User in oldUserList by oldPlayerID
            for(Users oldUser : oldUserList){
                if (oldPlayerID == oldUser.getUserID()){
                    int newIndex = userIndexSearch(oldUser.getUserName());
                    int newPlayerID = userList.get(newIndex).getUserID();
                    newPlayersID[arrayIndex++] = newPlayerID;
                }
            }
        }
        return newPlayersID;
    }

    // map userID field in brickPlacing (lastBrickPlacing)
    private GamingOperationProtocol mapBrickPlacing(GamingOperationProtocol oldlatestBrickPlacing, Users[] oldUserList){
        int oldID = oldlatestBrickPlacing.getBrickPlacing().getUserID();
        int newID = ID_PLACEHOLDER;
        for(Users oldUser : oldUserList){
            if(oldID == oldUser.getUserID()){
                newID = userIndexSearch(oldUser.getUserName());
                break;
            }
        }
        oldlatestBrickPlacing.getBrickPlacing().setUserID(newID);
        return oldlatestBrickPlacing;
    }

    // map gameHost ID from oldUserList
    private int mapGameHost(int oldGameHost, Users[] oldUserList){
        for(Users oldUser : oldUserList){
            if(oldUser.getUserID() == oldGameHost){
                int newIndex = userIndexSearch(oldUser.getUserName());
                return userList.get(newIndex).getUserID();
            }
        }
        return ID_PLACEHOLDER;
    }


    private void recoverGlobalParas(GameState newGameState){
        board = newGameState.getBoard();
        agree = newGameState.getAgree();
        gameStart = newGameState.isGameStart();
        numPass = newGameState.getNumPass();
        numVoted = newGameState.getNumVoted();
        whoseTurn = newGameState.getWhoseTurn(); // inGameSequence -- no mapping
        voteSuccess = newGameState.isVoteSuccess();

        // update userList first -- numWin, status
        if (newGameState.getUserList() != null){
            recoverUserList(newGameState.getUserList());
        }

        // then update teams
        if (newGameState.getTeamsInWait() != null) {
            recoverTeams(newGameState.getTeamsInWait());
        }

        // update playerList
        if (newGameState.getPlayerList() !=null){
            recoverPlayerList(newGameState.getPlayerList());
        }

        // mapping voteInitiator ID
        voteInitiator = mapVoteInitiator(newGameState.getVoteInitiator(), newGameState.getUserList()); // needs username id mapping
        gameHost = mapGameHost(newGameState.getGameHost(), newGameState.getUserList());

        // update playersID
        if (newGameState.getPlayersID() != null){
            playersID = mapPlayersID(newGameState.getPlayersID(), newGameState.getUserList());
        }

        // recovery lastBrickPlacing status
        if (newGameState.getLatestBrickPlacing() != null){
            latestBrickPlacing = mapBrickPlacing(newGameState.getLatestBrickPlacing(), newGameState.getUserList());
        }
    }

    private void resetGameParameters(){
        //terminate game, reset parameters
        gameStart = false;
        playersID = null;
        playerList.clear();
        whoseTurn = INITIAL_SEQ;
        boardInitiation();
    }

}
