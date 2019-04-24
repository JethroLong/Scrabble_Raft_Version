package app.Peer.Server.controllers.net;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class NetCore implements Runnable{
    private final BlockingQueue<String> blockingQueue;
    private volatile boolean flag;

    public NetCore(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        flag=false;
    }

    //test put string
    public void netToGame(String message){
        try {
            blockingQueue.put(message);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String str;
        while (!flag){

            str = reader.nextLine();
            if(str.equals("shutdown")){
                shutDown();
            }
            netToGame(str);
        }
    }

    public void shutDown(){
        flag=true;
    }
}
