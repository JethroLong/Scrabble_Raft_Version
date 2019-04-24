package app.Peer.Server.BackUp;


import app.Models.GameState;
import app.Models.PeerSockets;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EnginePutMsg;
import app.Peer.Server.controllers.net.Net;
import app.Protocols.NonGamingProtocol.BackupProtocol;
import app.Protocols.Pack;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackUpTask extends TimerTask implements Runnable {


    public BackUpTask() {}


    @Override
    public void run() {
        backUpBcast();
//        task(); //test case
    }

    void backUpBcast() {
        // send gameState to all
        try {
            Pack temp = Packing();
            System.out.println("Pack: "+temp);
            EnginePutMsg.getInstance().putMsgToCenter(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Pack Packing() {
        ArrayList<PeerSockets> peerSockets = Net.getInstance().getPeerSockets();
        if (peerSockets != null) {
            PeerSockets[] peerSockets_List = new PeerSockets[peerSockets.size()];
            peerSockets_List = peerSockets.toArray(peerSockets_List);
            GameState gameState = GameProcess.getInstance().getGameState();
            String jsonStr = JSON.toJSONString(new BackupProtocol(gameState, peerSockets_List));
            return new Pack(0, jsonStr);
        } else {
            return new Pack(0, JSON.toJSONString("No sockets"));
        }
    }

    public void task() {
        System.out.println("task at" + scheduledExecutionTime());
    }
}
