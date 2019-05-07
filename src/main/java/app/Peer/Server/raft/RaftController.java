package app.Peer.Server.raft;

import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Peer.Server.raft.Blockingqueue.RaftGetMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;

public class RaftController implements Runnable {
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> toCenter;
    private boolean term = false;
    private ExecutorService pool;

    private volatile static RaftController instance;

    private RaftController(BlockingQueue<Pack> fromCenter, BlockingQueue<Pack> toCenter) {
        this.fromCenter = fromCenter;
        this.toCenter = toCenter;
    }

    public static RaftController getInstance(BlockingQueue<Pack> fromCenter, BlockingQueue<Pack> toCenter) {
        if (instance == null) {
            synchronized (RaftController.class) {
                if (instance == null) {
                    instance = new RaftController(fromCenter, toCenter);
                }
            }
        }
        return instance;
    }

    public static RaftController getInstance() {
        return instance;
    }

    /**
     * TO-DO:
     * 接收来自ServerControlCenter的raft相关msg
     * 发送来自raft相关的msg
     */

    public void run() {
        pool = new ThreadPoolExecutor(2,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new ThreadFactoryBuilder().setNameFormat("ControlCenter-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new RaftGetMsg(fromCenter));
    }

    // use this method to broadcast msg to all the other peers
    public void broadcast(Pack packedMsg) {
        try{
            toCenter.put(packedMsg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void switchProtocols(Pack packedMsg) {
        System.out.println(packedMsg.getMsg());
    }
}
