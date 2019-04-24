package app.Peer.Server.BackUp;


import java.util.TimerTask;

public class BackUpTask extends TimerTask {
    @Override
    public void run() {
        backUpBcast();
    }

    public void backUpBcast(){
        // send gameState to all
    }
}
