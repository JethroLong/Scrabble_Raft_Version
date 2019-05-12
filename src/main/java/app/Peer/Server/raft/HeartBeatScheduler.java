package app.Peer.Server.raft;

import java.util.Timer;

public class HeartBeatScheduler implements Runnable {

    private Timer timer;

    private HeartBeatScheduler() {
        this.timer = new Timer();
    }

    private static HeartBeatScheduler instance = new HeartBeatScheduler();

    public static HeartBeatScheduler getInstance() {
        return instance;
    }

    @Override
    public void run() {
        timer.scheduleAtFixedRate(new HeartBeatTask(),1000,4000);
    }

}
