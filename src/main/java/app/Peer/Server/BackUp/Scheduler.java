package app.Peer.Server.BackUp;

import app.Models.PeerSockets;
import app.Protocols.Pack;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable{
    private Timer timer;
    private ArrayList<PeerSockets> peerSockets;

    public Scheduler(ArrayList<PeerSockets> peerSockets) {
        this.peerSockets = peerSockets;
        this.timer = new Timer();
    }

    public Scheduler() {
        this.peerSockets = new ArrayList<PeerSockets>();
        this.timer = new Timer();
    }

//    private volatile static Scheduler scheduler;


    @Override
    public void run() {
        timer.scheduleAtFixedRate(new BackUpTask(),1000,15000);
    }
}
