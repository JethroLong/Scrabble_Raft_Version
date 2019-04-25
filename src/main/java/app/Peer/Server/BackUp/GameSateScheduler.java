package app.Peer.Server.BackUp;

import java.util.Timer;


public class GameSateScheduler implements Runnable{
    private Timer timer;

    public GameSateScheduler() {
        this.timer = new Timer();
    }

    @Override
    public void run() {
        timer.scheduleAtFixedRate(new GameStateRecordTask(),1000,2000);
    }
}
