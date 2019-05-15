package app.Peer.Client.gui;


import app.Models.GameState;
import app.Models.PeerHosts;
import app.Models.Player;
import app.Models.Users;
import app.Peer.Client.Gui;
import app.Peer.Client.Net.ClientNet;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Peer.Server.raft.NewElectionScheduler;
import app.Peer.Server.raft.ElectionTask;
import app.Peer.Server.raft.RaftController;
import app.Protocols.RaftProtocol.ElectedProtocol;
import app.Protocols.RaftProtocol.ElectionProtocol;
import app.Protocols.RaftProtocol.HeartBeatProtocol;
import app.Protocols.RaftProtocol.StartElectionProtocol;
import app.Protocols.ScrabbleProtocol;
import app.Protocols.ServerResponse.*;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class GuiListener {

    private volatile static GuiListener instance;
    private BlockingQueue<String> queue;
    private NewElectionScheduler newElectionScheduler;
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
        ScrabbleProtocol scrabbleProtocol = JSON.parseObject(str, ScrabbleProtocol.class);
        String tag = scrabbleProtocol.getTAG();
//        System.out.println(tag);
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
                break;

            // RAFT SECTION: protocol to process Raft algorithms.
            case "RaftProtocol":
                processRaft(str);
                break;
            case "HeartBeatProtocol":
                processHeartBeat(str);
                break;
            case "StartElectionProtocol":
                if(RaftController.getInstance().getStatus().equals("CANDIDATE"))processElectionRequest(str);
                break;
            case "ElectionProtocol":
                if(RaftController.getInstance().getStatus().equals("CANDIDATE")) processElection(str);
                break;
            case "ElectedProtocol":
                if(RaftController.getInstance().getStatus().equals("CANDIDATE")) processElected(str);
                break;
            // END OF RAFT SECTION

            default:
                break;
        }
    }

    private void processRaft(String str) {
        // do some when receive raft related msg
        // case 1: leader request(with newLeaderID)
        // case 2: heartbeat(use timer)
        // case 3:
    }

    /** RAFT SECTION: methods to process Raft algorithms. **/
    private void processHeartBeat(String str){
        /**
            This method is used to process heartbeat messages:
            1. Start the timertask when receive the heartbeat message for the first time.
            2. Restart the timertask every time after the first time.
         **/
        HeartBeatProtocol heartbeat = JSON.parseObject(str, HeartBeatProtocol.class);
        if(RaftController.getInstance().getLeaderName() == null) RaftController.getInstance().setNewLeader(heartbeat.getInitiator());

        if(newElectionScheduler == null){
            // Start a timer for the heartbeat messages from the leader.
            this.newElectionScheduler = new NewElectionScheduler(0);
            // Every follower should wait at least 7 secs.
            // After that, wait another random period(between 0 - 5 secs) and broadcast election request to every peer alive.
            newElectionScheduler.startTask(7);
        }else {
            newElectionScheduler.restart();
        }

    }


    private void processElectionRequest(String str){
        /** This method is used to decide whether to vote for a candidate or not. **/
        StartElectionProtocol request = JSON.parseObject(str, StartElectionProtocol.class);
        ElectionTask electionTask = new ElectionTask(RaftController.getInstance().getMyName(),
                RaftController.getInstance().getTerm());
        // Vote under two cases:
        // 1. The request has a term larger than mine.
        // 2. The request has a term as large as mine,
        //    I have not voted in this election term,
        //    and my status is candidate.
        if(request.getTerm() > RaftController.getInstance().getTerm()
                || request.getTerm() == RaftController.getInstance().getTerm()
                & !RaftController.getInstance().getHasVoted()
                & RaftController.getInstance().getStatus().equals("CANDIDATE")){
            // If so, vote for the candidate. Set the term and set hasVoted to be true.
            electionTask.vote(request.getCandidate(), true);
            RaftController.getInstance().setTerm(request.getTerm());
            RaftController.getInstance().setHasVoted(true);
        }else{
            // If not, do not vote for the candidate.
            electionTask.vote(request.getCandidate(), false);
        }
    }

    private void processElection(String str){
        /** This method is used to determine the result of an election term. **/
        ElectionProtocol ticket = JSON.parseObject(str, ElectionProtocol.class); // Get the election ticket.
        RaftController.getInstance().increaseTicketCount(); // Increase ticket count.
        // If this ticket voted for me, increase my vote count.
        if(ticket.getVote()) RaftController.getInstance().increaseVoteCount();

        // Get the number of current peers(including myself).
        int numPeers = RaftController.getInstance().getPeers().size();

        if(RaftController.getInstance().getVoteCount() * 2 > numPeers){
            // If I have got the majority votes, broadcast a elected message and change my status to be "LEADER".
            System.out.println(String.format(
                    "Received %d votes out of %d tickets from %d peers. I won the election.",
                    RaftController.getInstance().getVoteCount(),
                    RaftController.getInstance().getTicketCount(),
                    RaftController.getInstance().getPeers().size()
            ));

            ElectedProtocol electedProtocol = new ElectedProtocol(RaftController.getInstance().getMyName());
            RaftController.getInstance().xBroadcast(electedProtocol);
            RaftController.getInstance().setStatus("LEADER");
        }else if(RaftController.getInstance().getTicketCount() >= numPeers){
            // If I received the max ticket count without getting majority count,
            // increase my election term, request a new election term.
            System.out.println(String.format(
                    "Received %d votes out of %d tickets from %d peers. A split vote happened.",
                    RaftController.getInstance().getVoteCount(),
                    RaftController.getInstance().getTicketCount(),
                    RaftController.getInstance().getPeers().size()
                    ));
            RaftController.getInstance().increaseTerm();
            NewElectionScheduler newElectionScheduler = new NewElectionScheduler(RaftController.getInstance().getTerm());
            newElectionScheduler.startTask(0);
        }
    }

    private void processElected(String str){
        /** This method is used to deal the case that a new leader is elected. **/
        ElectedProtocol electedProtocol = JSON.parseObject(str, ElectedProtocol.class);
        String leaderName = electedProtocol.getNewLeader();
        RaftController.getInstance().setTerm(0);
        RaftController.getInstance().setNewLeader(leaderName);
    }

    /** END OF RAFT SECTION **/

    private void processBackup(String str) {
        // update local backups -- GameState & peerSockets
        BackupProtocol backup = JSON.parseObject(str, BackupProtocol.class);

        // extract
        PeerHosts[] peerHosts = backup.getPeerHosts();
        GameState gameState = backup.getGameState();
        int leaderID = backup.getLeaderID();

        // update Game state
        GuiController.get().updateLocalGameState(gameState);

        // update leaderID
        ClientNet.getInstance().setLeaderID(leaderID);

        // convert to array list

        ArrayList<PeerHosts> newPeerHosts = new ArrayList<PeerHosts>();
        for(PeerHosts peer : peerHosts){
            newPeerHosts.add(peer);
        }
        // set new peerHosts from server and establish new connections
        ClientNet.getInstance().setPeerHosts(newPeerHosts);
        ClientNet.getInstance().connectToNewPeers();
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

//                GuiController.get().shutdown();


//                int newLeaderID = raft election method() // raft algorithm -- leader election
//                GameState agreedGameState = raft commonsense algorithm -- decide the new State
//                GuiController.get().updateGameState(agreedGameState);
//
//                if (GuiController.get().getId() == newLeaderID){
//                      GuiController.get().setLeader(true)  // mark self as new leader
//                      look up the socket from connectedPeers using peerID
//                      ClientNet.getInstance().setLeaderSocket(leaderSocket); //set leaderSocket
//                      ClientNet.getInstance().run(); // restart net to new leader
//                      //recover state from backup
//                      GuiController.get().serverRecovery();

//                }else{
//                      look up the socket from connectedPeers such that peer.hostAddr == new leader's Address
//                      ClientNet.getInstance().setLeaderSocket(leaderSocket);
//                      ClientNet.getInstance().run(); // restart net to new leader
//                      // peers wait for msg from new leader
//                }

//
                break;
            default:
                break;
        }
    }


}
