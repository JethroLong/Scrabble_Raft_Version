package app.Peer.Server.raft;

import java.util.Timer;

public class DetectHeartBeatScheduler {
    private Timer timer;
    private DetectHeartBeatTask task;
    public DetectHeartBeatScheduler() {
        this.timer = new Timer();
        this.task = new DetectHeartBeatTask();
    }

    public void startTask(){
        timer.schedule(task,10000);
    }

    public void restart(){
        task.cancel();
        this.task = new DetectHeartBeatTask();
        startTask();
    }
}
