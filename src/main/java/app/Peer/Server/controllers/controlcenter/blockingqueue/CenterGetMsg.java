
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
    private final BlockingQueue<Pack> toRaft;

    public CenterGetMsg(BlockingQueue<Pack> fromNet, BlockingQueue<Pack> toEngine,
                        BlockingQueue<Pack> toRaft) {
        this.fromNet = fromNet;
        this.toEngine = toEngine;
        this.toRaft = toRaft;
    }

    @Override
    public void run() {
        while (true){
            try {
                checkProtocols(fromNet.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkProtocols(Pack packedMsg) {
        try {
            ScrabbleProtocol temp;
            String msg = packedMsg.getMsg();
            if (!msg.equals("null")) {
                temp = JSON.parseObject(msg, ScrabbleProtocol.class);
                String type = temp.getTAG();
                switch (type) {
                    case "RaftProtocol":
                        toRaft.put(packedMsg);
                        break;
                    default:
                        toEngine.put(packedMsg);
                        break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
