package app.Peer.Server.raft;

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
        broadRequest();
    }


    private void broadRequest() {
        try {
            Pack temp = Packing();
            System.out.println("New election request: "+temp);
            RaftController.getInstance().broadcast(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Pack Packing() {
        StartElectionProtocol msg = new StartElectionProtocol();
        String jsonStr = JSON.toJSONString(msg);
        return new Pack(0, jsonStr);
    }

}
