package app.Peer.Server.raft;

import java.util.Timer;

public class HeartBeatScheduler implements Runnable {
    private Timer timer;

    public HeartBeatScheduler() {
        this.timer = new Timer();
    }

//    private volatile static Scheduler scheduler;


    @Override
    public void run() {
        //timer.scheduleAtFixedRate(new BackUpTask(),1000,15000);
    }
}
