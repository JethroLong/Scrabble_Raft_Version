package app.Peer.Server.raft;

import app.Peer.Client.gui.GuiController;
import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Peer.Server.raft.Blockingqueue.RaftGetMsg;
import app.Peer.Server.raft.Blockingqueue.RaftPutMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;

public class RaftController implements Runnable {
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> fromRaft;
    private BlockingQueue<Pack> toNet;
    private boolean term = false;
    private ExecutorService pool;
    private volatile static RaftController instance;

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
        broatcastHeartBeat();

    }

    // use this method to broadcast msg to all the other peers
    void broadcast(Pack packedMsg) {
        try{
            fromRaft.put(packedMsg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void switchProtocols(Pack packedMsg) {
        System.out.println(packedMsg.getMsg());
    }

    public void broatcastHeartBeat() {
        if (GuiController.get().isLeader()) {
            new Thread(HeartBeatScheduler.getInstance()).start();;
        }
    }
}
