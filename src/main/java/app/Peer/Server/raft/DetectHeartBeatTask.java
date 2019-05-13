package app.Peer.Server.raft;

import app.Peer.Client.gui.GuiController;
import app.Peer.Client.gui.GuiSender;
import app.Protocols.Pack;
import app.Protocols.RaftProtocol.HeartBeatProtocol;
import app.Protocols.RaftProtocol.StartElectionProtocol;
import com.alibaba.fastjson.JSON;

import java.util.TimerTask;

public class DetectHeartBeatTask extends TimerTask {
    public void run(){
        System.err.println("No heartbeat from leader detected!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        GuiSender.get().sendToCenter(new StartElectionProtocol("Start election request."));
        broadcastRequest();
    }


    private void broadcastRequest() {
        try {
            // Set my election term to be 0 and broadcast a start-election request.
            RaftController.getInstance().setTerm(0);
            StartElectionProtocol msg = new StartElectionProtocol();
            RaftController.getInstance().sendMsg(msg, 0);;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
