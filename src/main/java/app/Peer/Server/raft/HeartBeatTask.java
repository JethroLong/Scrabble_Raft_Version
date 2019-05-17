package app.Peer.Server.raft;

import app.Peer.Client.gui.GuiController;
import app.Protocols.Pack;
import app.Protocols.RaftProtocol.HeartBeatProtocol;
import com.alibaba.fastjson.JSON;
import java.util.TimerTask;

public class HeartBeatTask extends TimerTask implements Runnable {
    public HeartBeatTask() {}

    @Override
    public void run() {
        broadCastHeartBeat();
    }

    private void broadCastHeartBeat() {
        try {
            HeartBeatProtocol heartbeat = new HeartBeatProtocol(RaftController.getInstance().getMyName());
            RaftController.getInstance().xBroadcast(heartbeat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
