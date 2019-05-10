package app.Peer.Server.controllers.gameEngine.blockingqueque;


import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class EngineGetMsg implements Runnable {
    public EngineGetMsg(BlockingQueue<Pack> fromCenter) {
        this.fromCenter = fromCenter;
        GameProcess.getInstance().addBlockingQueue(fromCenter);
    }
    private BlockingQueue<Pack> fromCenter;


    @Override
    public void run() {

        while (true){
            Pack temp;
            try {
                temp = fromCenter.take();
//                GameProcess.getInstance().addData(temp.getUserId(), temp.getMsg());
//                GameProcess.getInstance().switchProtocols(temp.getUserId(), temp.getMsg());
                if(temp.getMsg()!=null){
                    GameProcess.getInstance().switchProtocols(temp.getUserId(), temp.getMsg());
                }
//                System.out.println(temp.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
