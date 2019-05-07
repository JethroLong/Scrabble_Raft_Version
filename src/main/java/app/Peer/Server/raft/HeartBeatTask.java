package app.Peer.Server.raft;

import app.Models.GameState;
import app.Models.PeerHosts;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EnginePutMsg;
import app.Peer.Server.controllers.net.Net;
import app.Protocols.Pack;
import app.Protocols.RaftProtocol.HeartBeatProtocol;
import app.Protocols.ScrabbleProtocol;
import app.Protocols.ServerResponse.BackupProtocol;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.TimerTask;

public class HeartBeatTask extends TimerTask implements Runnable {
    public HeartBeatTask() {}

    @Override
    public void run() {
        broadCastHeartBeat();
    }

    private void broadCastHeartBeat() {
        try {
            Pack temp = Packing();
            System.out.println("New HeartBeat Pack: "+temp);
            RaftController.getInstance().broadcast(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Pack Packing() {
        ArrayList<PeerHosts> peerHosts = Net.getInstance().getPeerSockets();
        if (peerHosts != null) {
            PeerHosts[] peerHosts_List = new PeerHosts[peerHosts.size()];
            peerHosts_List = peerHosts.toArray(peerHosts_List);
            HeartBeatProtocol heartbeat = new HeartBeatProtocol(peerHosts_List);
            //lower down the likelihood that the clientID distribution meets a collision
            heartbeat.setInitialClientID(Net.getInstance().getClientNumber());
            String jsonStr = JSON.toJSONString(heartbeat);
            return new Pack(0, jsonStr);
        } else {
            return new Pack(0, JSON.toJSONString("No sockets"));
        }
    }
}
