
package app.Peer.Server.controllers.controlcenter.blockingqueue;

import app.Protocols.GamingProtocol.GamingOperationProtocol;
import app.Protocols.NonGamingProtocol.NonGamingProtocol;
import app.Protocols.Pack;
import app.Protocols.ScrabbleProtocol;
import com.alibaba.fastjson.JSON;

import java.util.concurrent.BlockingQueue;

public class CenterGetMsg implements Runnable {
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toEngine;
    private final BlockingQueue<Pack> fromEngine;
    private final BlockingQueue<Pack> toNet;
    private final BlockingQueue<Pack> toRaft;

    public CenterGetMsg(BlockingQueue<Pack> fromNet, BlockingQueue<Pack> toEngine,
                        BlockingQueue<Pack> fromEngine, BlockingQueue<Pack> toNet,
                        BlockingQueue<Pack> toRaft) {
        this.fromNet = fromNet;
        this.toEngine = toEngine;
        this.fromEngine = fromEngine;
        this.toNet = toNet;
        this.toRaft = toRaft;
    }

    @Override
    public void run() {
        while (true){
            try {
                switchProtocols(fromNet.take().getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchProtocols(String msg) {
        try {
            ScrabbleProtocol temp = null;
            if (!msg.equals("null")) {
                temp = JSON.parseObject(msg, ScrabbleProtocol.class);
                String type = temp.getTAG();
                switch (type) {
                    case "RaftProtocol":
                        toRaft.put(fromNet.take());
                        break;
                    default:
                        toEngine.put(fromNet.take());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
