package app.Peer.Server.BackUp;


import app.Models.GameState;
import app.Models.PeerHosts;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EnginePutMsg;
import app.Peer.Server.controllers.net.Net;
import app.Protocols.ServerResponse.BackupProtocol;
import app.Protocols.Pack;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.TimerTask;

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
        ArrayList<PeerHosts> peerHosts = Net.getInstance().getPeerSockets();
        if (peerHosts != null) {
            PeerHosts[] peerHosts_List = new PeerHosts[peerHosts.size()];
            peerHosts_List = peerHosts.toArray(peerHosts_List);

            GameState gameState = GameProcess.getInstance().getGameState();

            BackupProtocol backup = new BackupProtocol(gameState, peerHosts_List);
            //lower down the likelihood that the clientID distribution meets a collision
            backup.setInitialClientID(Net.getInstance().getClientNumber());
            String jsonStr = JSON.toJSONString(backup);
            return new Pack(0, jsonStr);
        } else {
            return new Pack(0, JSON.toJSONString("No sockets"));
        }
    }
}
