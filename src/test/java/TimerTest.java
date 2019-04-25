import app.Peer.Server.BackUp.Scheduler;

import java.util.Timer;

public class TimerTest {
    public static void main(String[] args) {
        new Thread(new Scheduler()).start();
    }
}
