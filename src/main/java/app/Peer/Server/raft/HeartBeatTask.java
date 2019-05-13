package app.Peer.Server.raft;

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
            HeartBeatProtocol heartbeat = new HeartBeatProtocol("PENG PENG PENG!");
//            String jsonStr = JSON.toJSONString(heartbeat);
//            Pack temp = Packing();
//            System.out.println("New HeartBeat Pack: "+temp);
            RaftController.getInstance().sendMsg(heartbeat, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Pack Packing() {
        HeartBeatProtocol heartbeat = new HeartBeatProtocol("PENG PENG PENG!");
        String jsonStr = JSON.toJSONString(heartbeat);
        return new Pack(0, jsonStr);
    }
}
