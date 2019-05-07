package app.Peer.Server.raft;

import app.Peer.Server.controllers.controlcenter.ControlCenter;
import app.Protocols.Pack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class RaftController {
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> toCenter;
    private boolean flag = true;
    private ExecutorService pool;

    private RaftController(BlockingQueue<Pack> fromCenter, BlockingQueue<Pack> toCenter) {
        this.fromCenter = fromCenter;
        this.toCenter = toCenter;
    }


}
