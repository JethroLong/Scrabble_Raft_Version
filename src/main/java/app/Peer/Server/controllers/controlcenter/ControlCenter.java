package app.Peer.Server.controllers.controlcenter;


import app.Peer.Server.controllers.controlcenter.blockingqueue.CenterGetMsg;
import app.Peer.Server.controllers.controlcenter.blockingqueue.CenterPutMsg;
import app.Peer.Server.controllers.gameEngine.GameEngine;
import app.Peer.Server.controllers.net.Net;
import app.Peer.Raft.RaftController;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class ControlCenter implements Runnable{
    private String tag = "ControlCenter";
    private static Logger logger = Logger.getLogger(String.valueOf(ControlCenter.class));
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toEngine;
    private final BlockingQueue<Pack> fromEngine;
    private final BlockingQueue<Pack> toNet;
    private final BlockingQueue<Pack> toRaft;
    private final BlockingQueue<Pack> fromRaft;
    private GameEngine gameEngine;
    private int portNumber;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    private volatile static ControlCenter instance;

    private ControlCenter() {
        this.toRaft = new LinkedBlockingQueue<>();
        this.fromRaft = new LinkedBlockingQueue<>();
        this.fromNet = new LinkedBlockingQueue<>();
        toEngine = new LinkedBlockingQueue<>();
        fromEngine = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
        initialServer();
        logger.info(tag+" Initial ControlCenter Complete!");
    }

    private ControlCenter(int port) {
        this.toRaft = new LinkedBlockingQueue<>();
        this.fromRaft = new LinkedBlockingQueue<>();
        this.fromNet = new LinkedBlockingQueue<>();
        toEngine = new LinkedBlockingQueue<>();
        fromEngine = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
        portNumber=port;
        initialServer();
        logger.info(tag+" Initial ControlCenter Complete!");
    }

    public static ControlCenter get() {
        if (instance == null) {
            synchronized (ControlCenter.class) {
                if (instance == null) {
                    instance = new ControlCenter();
                }
            }
        }
        return instance;
    }

    public static ControlCenter get(int portNum) {
        if (instance == null) {
            synchronized (ControlCenter.class) {
                if (instance == null) {
                    instance = new ControlCenter(portNum);
                }
            }
        }
        return instance;
    }


    public void initialServer(){
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("Server-ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(20,100,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        if(portNumber==0){
            pool.execute(Net.getInstance(fromNet,toNet));
        }else {
            pool.execute(Net.getInstance(fromNet,toNet,portNumber));
        }
        pool.execute(GameEngine.getInstance(toEngine,fromEngine));
        pool.execute(RaftController.getInstance(toRaft, fromRaft, toNet));
        logger.info(tag+" Initial Server Completed");
    }
    @Override
    public void run() {
        pool.execute(new CenterGetMsg(fromNet,toEngine,toRaft)); // from net to to Raft or engine
        pool.execute(new CenterPutMsg(fromEngine,toNet)); // from Raft, engine to net
    }


    public void shutdown(){
        flag = false;
    }

}
