import app.Peer.Server.BackUp.backupScheduler;

public class TimerTest {
    public static void main(String[] args) {
        new Thread(new backupScheduler()).start();
    }
}
