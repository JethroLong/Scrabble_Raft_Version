package app.Peer.Raft.Blockingqueue;

import java.util.concurrent.BlockingQueue;
import app.Protocols.Pack;
import org.apache.log4j.Logger;

public class RaftPutMsg implements Runnable {
    private final String TAG = "RaftPutMsg";
    private static Logger logger = Logger.getLogger(RaftPutMsg.class);

    private final BlockingQueue<Pack> fromRaft;
    private final BlockingQueue<Pack> toNet;


    public RaftPutMsg(BlockingQueue<Pack> fromRaft, BlockingQueue<Pack> toNet) {
        logger.info(TAG + " Initialize");
        this.fromRaft = fromRaft;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Pack pack = fromRaft.take();
                toNet.put(pack);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
