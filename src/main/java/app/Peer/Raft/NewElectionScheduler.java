package app.Peer.Raft;

import java.util.Random;
import java.util.Timer;

public class NewElectionScheduler {
    /**
     This class defines a scheduler to detect the failure of the leader based on heartbeat messages
     and assign the corresponding tasks.
     **/

    private Timer timer;
    private NewElectionTask task;
    public NewElectionScheduler(int term) {
        this.timer = new Timer();
        this.task = new NewElectionTask(term);
    }

    public void startTask(int delay){
        Random rand = new Random();
        int randomWaitTime = rand.nextInt(5) * 1000; // Obtain a time between [0 - 5] secs.
        System.out.println("Random period: "+randomWaitTime/1000+" secs.");
        timer.schedule(task,delay * 1000 +randomWaitTime);
    }

    public void restart(){
        // Cancel the old task and start a new one.
        task.cancel();
        this.task = new NewElectionTask(0);
        startTask(7);
    }

}
