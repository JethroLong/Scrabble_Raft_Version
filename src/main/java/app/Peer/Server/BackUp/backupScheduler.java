package app.Peer.Server.BackUp;

import java.util.Timer;


public class backupScheduler implements Runnable{
    private Timer timer;

    public backupScheduler() {
        this.timer = new Timer();
    }

//    private volatile static backupScheduler scheduler;


    @Override
    public void run() {
        timer.scheduleAtFixedRate(new BackUpTask(),5000,15000);
    }
}

