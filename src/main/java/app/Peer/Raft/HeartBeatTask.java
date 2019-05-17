package app.Peer.Raft;

import app.Protocols.RaftProtocol.HeartBeatProtocol;

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
