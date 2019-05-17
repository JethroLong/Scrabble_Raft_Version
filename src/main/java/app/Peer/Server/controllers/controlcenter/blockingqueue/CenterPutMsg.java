
package app.Peer.Server.controllers.controlcenter.blockingqueue;


import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class CenterPutMsg implements Runnable {
    private final BlockingQueue<Pack> fromEngine;

    private final BlockingQueue<Pack> toNet;

    public CenterPutMsg(BlockingQueue<Pack> fromEngine, BlockingQueue<Pack> toNet) {
        this.fromEngine = fromEngine;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        while (true){
            try {
                Pack packFromEngine = fromEngine.take();
                toNet.put(packFromEngine);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
