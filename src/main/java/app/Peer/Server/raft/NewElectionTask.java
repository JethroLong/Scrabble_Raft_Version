package app.Peer.Server.raft;

import app.Models.PeerHosts;
import app.Peer.Client.Net.ClientNet;
import app.Peer.Client.gui.GuiController;
import app.Peer.Client.gui.GuiSender;
import app.Peer.Server.controllers.net.Net;
import app.Protocols.Pack;
import app.Protocols.RaftProtocol.HeartBeatProtocol;
import app.Protocols.RaftProtocol.StartElectionProtocol;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
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
            // Update the connected peer hosts list. Remove the old leader from it.
            ArrayList<PeerHosts> newPeerHosts = new ArrayList<>(ClientNet.getInstance().getPeerHosts());
            newPeerHosts.removeIf(peerHosts -> peerHosts.getPeerID() == ClientNet.getInstance().getLeaderID());
            ClientNet.getInstance().setPeerHosts(newPeerHosts);
            RaftController.getInstance().getPeers().remove(RaftController.getInstance().getLeaderName());
            // Set my status to be "CANDIDATE", set my election term to be 0,
            // and broadcast a start-election request.
            RaftController.getInstance().setStatus("CANDIDATE");
            RaftController.getInstance().setTerm(this.term);
            StartElectionProtocol msg = new StartElectionProtocol(
                    RaftController.getInstance().getTerm(),
                    GuiController.get().getUsername());
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
