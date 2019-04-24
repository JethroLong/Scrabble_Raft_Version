
package app.Peer.Server.controllers.controlcenter.blockingqueue;


import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class CenterPutMsg implements Runnable {
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toEngine;
    private final BlockingQueue<Pack> fromEngine;
    private final BlockingQueue<Pack> toNet;

    public CenterPutMsg(BlockingQueue<Pack> fromNet, BlockingQueue<Pack> toEngine, BlockingQueue<Pack> fromEngine, BlockingQueue<Pack> toNet) {
        this.fromNet = fromNet;
        this.toEngine = toEngine;
        this.fromEngine = fromEngine;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        while (true){
            try {
                Pack pack=null;
                pack = fromEngine.take();
                toNet.put(pack);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
