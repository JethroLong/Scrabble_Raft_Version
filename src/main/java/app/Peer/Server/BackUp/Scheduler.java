package app.Peer.Server.BackUp;

import java.util.Timer;


public class Scheduler implements Runnable{
    private Timer timer;

    public Scheduler() {
        this.timer = new Timer();
    }

//    private volatile static Scheduler scheduler;


    @Override
    public void run() {
        timer.scheduleAtFixedRate(new BackUpTask(),5000,15000);
    }
}

