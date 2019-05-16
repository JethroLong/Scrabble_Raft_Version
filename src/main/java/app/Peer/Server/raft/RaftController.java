package app.Peer.Server.raft;

import app.Models.PeerHosts;
import app.Peer.Client.Net.ClientNet;
import app.Peer.Client.gui.GuiController;
import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Peer.Server.controllers.net.Net;
import app.Peer.Server.raft.Blockingqueue.RaftGetMsg;
import app.Peer.Server.raft.Blockingqueue.RaftPutMsg;
import app.Protocols.Pack;
import app.Protocols.ScrabbleProtocol;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.*;

public class RaftController implements Runnable {
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> fromRaft;
    private BlockingQueue<Pack> toNet;
    private ExecutorService pool;
    private volatile static RaftController instance;

    public String getMyName(){return GuiController.get().getUsername();}

    private String leaderName;
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    public String getLeaderName() { return leaderName; }

    public void removeOldLeader(){
        Socket leaderSocket = getPeers().get(leaderName);
        ClientNet.getInstance().getConnectedPeerSockets().remove(leaderSocket);
        getPeers().remove(leaderName);
        leaderName = null;
    }

    public void setNewLeader(String name){
        leaderName = name;
        if(!name.equals(getMyName())){
            Socket leaderSocket = ClientNet.getInstance().getPeerNameSocketMap().get(name);
            ClientNet.getInstance().setLeaderSocket(leaderSocket);
            setStatus("FOLLOWER");
        }
    }

    public HashMap<String, Socket> getPeers(){
        return Net.getInstance().getClientNameSocketMap();
    }

    // Election status can be either "LEADER", "FOLLOWER" or "CANDIDATE".
    private String status;
    public String getStatus(){return this.status;}
    public void setStatus(String status){
        this.status = status;
        if(status.equals("LEADER")) {
            GuiController.get().setLeader(true);
            broadcastHeartBeat();
            setLeaderName(GuiController.get().getUsername());

            // re
            GuiController.get().serverRecovery();
        }
        else GuiController.get().setLeader(false);
    }

    // Indicate the current election term.
    private int term = 0;
    public int getTerm(){return term;}
    public void increaseTerm(){
        this.term++;
        resetTicketCount();
        resetVoteCount();
        setHasVoted(false);
    }
    public void setTerm(int term){
        this.term = term;
        resetTicketCount();
        resetVoteCount();
        setHasVoted(false);
    }

    // A received ElectionProtocol is a ticket.
    private int ticketCount = 0;
    public int getTicketCount(){return this.ticketCount;}
    public void increaseTicketCount(){this.ticketCount++;}
    public void resetTicketCount(){this.ticketCount = 0;}

    // Counts how many tickets vote for me.
    private int voteCount = 0;
    public int getVoteCount(){return this.voteCount;}
    public void increaseVoteCount(){this.voteCount++;}
    public void resetVoteCount(){this.voteCount = 0;}

    // Records that whether I have voted or not in a term.
    private boolean hasVoted = false;
    public boolean getHasVoted(){return hasVoted;}
    public void setHasVoted(boolean hasVoted){this.hasVoted = hasVoted;}

    // constructor
    private RaftController(BlockingQueue<Pack> toRaft, BlockingQueue<Pack> fromRaft, BlockingQueue<Pack> toNet) {
        this.fromCenter = toRaft;
        this.fromRaft = fromRaft;
        this.toNet = toNet;

    }

    private RaftController(){}

    public static RaftController getInstance(
            BlockingQueue<Pack> toRaft, BlockingQueue<Pack> fromRaft, BlockingQueue<Pack> toNet) {
        if (instance == null) {
            synchronized (RaftController.class) {
                if (instance == null) {
                    instance = new RaftController(toRaft, fromRaft, toNet);
                }
            }
        }
        return instance;
    }

    public static RaftController getInstance() {
        if (instance == null) {
            synchronized (RaftController.class) {
                if (instance == null) {
                    instance = new RaftController();
                }
            }
        }
        return instance;
    }

    /**
     * TO-DO:
     * 接收来自ServerControlCenter的raft相关msg
     * 发送来自raft相关的msg
     */
    @Override
    public void run() {
        pool = new ThreadPoolExecutor(20,100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new ThreadFactoryBuilder().setNameFormat("ControlCenter-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new RaftGetMsg(fromCenter));
        pool.execute(new RaftPutMsg(fromRaft, toNet));
        broadcastHeartBeat();

    }


    public <T extends ScrabbleProtocol> void sendMsg(T protocol, String username){
        /**
            This method is used to send massages to any peer.
            The first parameter should be of any subtype of ScrabbleProtocol.
            The second parameter could be either:
            0 - which lets the net to broadcast the massage to all peers(INCLUDING myself);
            peerId - which lets the net to unicast the massage to the peer with given peerId.
         **/
        String jsonStr = JSON.toJSONString(protocol);
        Pack pack = new Pack(username, jsonStr);
        try{
            fromRaft.put(pack);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    public <T extends ScrabbleProtocol> void xBroadcast(T protocol) {
        /**
         This method is used to broadcast a message to peers EXCLUDING myself.
         **/
        sendMsg(protocol, "broadcast");

    }


    public void switchProtocols(Pack packedMsg) {
    }

    public void broadcastHeartBeat() {
        if (GuiController.get().isLeader()) {
            new Thread(HeartBeatScheduler.getInstance()).start();
        }
    }
}
