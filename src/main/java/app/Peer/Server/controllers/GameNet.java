package app.Peer.Server.controllers;

import app.Peer.Server.controllers.gameEngine.GameCore;
import app.Peer.Server.controllers.net.NetCore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
* Merlin Lee
* 21/09/2018
* BlockingQueue
* */
//Singleton Design Pattern
public class GameNet {
    private BlockingQueue<String> blockingQueue;

    public GameNet() {
        this.blockingQueue = new LinkedBlockingQueue<String>(10);
    }

    public BlockingQueue<String> getBlockingQueue() {
        return blockingQueue;
    }

    //test
    public static void main(String[] args){
        GameNet gameNet = new GameNet();
        new Thread(new NetCore(gameNet.getBlockingQueue())).start();
        new Thread(new GameCore(gameNet.getBlockingQueue())).start();
    }
}
