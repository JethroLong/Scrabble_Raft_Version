package app.Peer.Server.raft;

import app.Peer.Client.gui.GuiController;
import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Peer.Server.raft.Blockingqueue.RaftGetMsg;
import app.Peer.Server.raft.Blockingqueue.RaftPutMsg;
import app.Protocols.Pack;
import app.Protocols.ScrabbleProtocol;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;

public class RaftController implements Runnable {
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> fromRaft;
    private BlockingQueue<Pack> toNet;
    private ExecutorService pool;
    private volatile static RaftController instance;

    // Election status can be either "LEADER", "FOLLOWER" or "CANDIDATE".
    private String status;
    public String getStatus(){return this.status;}
    public void setStatus(String status){
        this.status = status;
        if(status.equals("LEADER")) GuiController.get().setLeader(true);
        else GuiController.get().setLeader(false);
    }

    private int term = 0;
    public int getTerm(){return term;}
    public void increaseTerm(){this.term = term++;}

    private int voteCount = 0;
    public int getVoteCount(){return this.voteCount;}
    public void increaseVoteCount(){this.voteCount++;}
    public void resetVoteCount(){this.voteCount = 0;}

    private int ticketCount = 0;
    public int getTicketCount(){return this.ticketCount;}
    public void increaseTicketCount(){this.ticketCount++;}
    public void resetTicketCount(){this.ticketCount = 0;}

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
        pool = new ThreadPoolExecutor(2,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new ThreadFactoryBuilder().setNameFormat("ControlCenter-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new RaftGetMsg(fromCenter));
        pool.execute(new RaftPutMsg(fromRaft, toNet));
        broadcastHeartBeat();

    }


    public <T extends ScrabbleProtocol> void sendMsg(T protocol, int recipient){
        // This method is used to send massages to any peer.
        // The first parameter should be of any subtype of ScrabbleProtocol.
        // The second parameter could be either:
        // 0 - which lets the net to broadcast the massage to all peers;
        // peerId - which lets the net to unicast the massage to the peer with given peerId.
        String jsonStr = JSON.toJSONString(protocol);
        Pack pack = new Pack(recipient, jsonStr);
        try{
            fromRaft.put(pack);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    // use this method to broadcast msg to all the other peers
    void broadcast(Pack packedMsg) {
        try{
//            System.out.println("xxxxxxxxxxxxxx"+fromRaft);
            fromRaft.put(packedMsg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void switchProtocols(Pack packedMsg) {
        System.out.println(packedMsg.getMsg());
    }

    public void broadcastHeartBeat() {
        if (GuiController.get().isLeader()) {
            new Thread(HeartBeatScheduler.getInstance()).start();
        }
    }
}
