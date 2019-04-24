package app.Peer.Server.controllers.gameEngine;


import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class GameCore implements Runnable{
    private final BlockingQueue<String> blockingQueue;
    private volatile boolean flag;
    public GameCore(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        flag = false;
    }

    //test get message from net layer
    public String getMessageFromNet(){
        String message = "";
        try {
            String packet = blockingQueue.take();
            message = packet;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!message.equals("")){
//            ScrabbleProtocol scrabbleProtocol = JSON.parseObject(message,ScrabbleProtocol.class);
//            System.out.println(scrabbleProtocol.getTAG());
        }else {
            System.out.println("error");
        }
        return message;
    }

    public void shutDown(){
        flag=true;
    }

    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String str;
        while (!flag){

            str = getMessageFromNet();
            if(str.equals("shutdown")){
                shutDown();
            }
            System.out.println(str);
        }
    }
}
