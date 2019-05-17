package app.Peer.Raft.Blockingqueue;

import app.Peer.Raft.RaftController;
import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class RaftGetMsg implements Runnable{

    private BlockingQueue<Pack> fromCenter;

    public RaftGetMsg(BlockingQueue<Pack> fromCenter){
        this.fromCenter = fromCenter;
    }

    @Override
    public void run() {
        while (true){
            Pack packedMsg;
            try {
                packedMsg = fromCenter.take();
                if(packedMsg.getMsg()!=null){
                    // ***** do something with packedMsg(temp.getUserId(), temp.getMsg()); ***
                    RaftController.getInstance().switchProtocols(packedMsg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
