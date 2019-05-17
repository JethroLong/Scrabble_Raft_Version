package app.Peer.Raft;

import app.Protocols.RaftProtocol.StartElectionProtocol;

import java.util.TimerTask;

public class NewElectionTask extends TimerTask {
    /**
        This class defines the tasks a follower should do under the case that the leader has failed.
    **/
    private int term;
    public void run(){
        if(!RaftController.getInstance().getHasVoted()){
            System.err.println("Sending new election request with term: "+term);
            broadcastRequest();
        }

    }

    public NewElectionTask(int term){this.term = term;}

    private void broadcastRequest() {
        try {
            // Remove the old leader from my RaftController.
            RaftController.getInstance().removeOldLeader();
            // Set my status to be "CANDIDATE", set my election term to be 0,
            // and broadcast a start-election request.
            RaftController.getInstance().setStatus("CANDIDATE");
            RaftController.getInstance().setTerm(this.term);
            StartElectionProtocol msg = new StartElectionProtocol(
                    RaftController.getInstance().getTerm(),
                    RaftController.getInstance().getMyName());
            RaftController.getInstance().xBroadcast(msg);
            // Vote for myself.
            RaftController.getInstance().increaseVoteCount();
            RaftController.getInstance().increaseTicketCount();
            RaftController.getInstance().setHasVoted(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
