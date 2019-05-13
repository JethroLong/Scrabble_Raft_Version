package app.Peer.Server.raft;

import java.util.Random;
import java.util.Timer;

public class DetectHeartBeatScheduler {
    private Timer timer;
    private DetectHeartBeatTask task;
    public DetectHeartBeatScheduler() {
        this.timer = new Timer();
        this.task = new DetectHeartBeatTask();
    }

    public void startTask(){
        // Start a timer for the heartbeat messages from the leader.
        // Every follower should wait at least 7 secs.
        // After that, wait another random period (between 0 - 5 secs) and broadcast election request to every peer alive.
        Random rand = new Random();
        int randomWaitTime = rand.nextInt(5) * 1000; // Obtain a time between [0 - 10] secs.
        timer.schedule(task,7000+randomWaitTime);
    }

    public void restart(){
        task.cancel();
        this.task = new DetectHeartBeatTask();
        startTask();
    }
}
