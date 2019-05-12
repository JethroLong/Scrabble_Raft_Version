package app.Protocols.RaftProtocol;

import app.Protocols.ScrabbleProtocol;

public class StartElectionProtocol extends ScrabbleProtocol {

    public StartElectionProtocol(){
        super.setTAG("StartElectionProtocol");
    }
    public StartElectionProtocol(String str){
        super.setTAG("HeartBeatProtocol");
    }
}
