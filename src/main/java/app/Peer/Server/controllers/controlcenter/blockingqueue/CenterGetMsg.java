
package app.Peer.Server.controllers.controlcenter.blockingqueue;

import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class CenterGetMsg implements Runnable {
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toEngine;
    private final BlockingQueue<Pack> fromEngine;
    private final BlockingQueue<Pack> toNet;

    public CenterGetMsg(BlockingQueue<Pack> fromNet, BlockingQueue<Pack> toEngine, BlockingQueue<Pack> fromEngine, BlockingQueue<Pack> toNet) {
        this.fromNet = fromNet;
        this.toEngine = toEngine;
        this.fromEngine = fromEngine;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        while (true){
            try {
                toEngine.put(fromNet.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
