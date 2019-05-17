package app.Peer.Client.Net.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class ClientNetPutMsg implements Runnable{
    private final BlockingQueue<String> toCenter; //put msg to center
    private final BlockingQueue<String> fromNetThread;
    private boolean flag = true;
    public ClientNetPutMsg(BlockingQueue<String> toCenter, BlockingQueue<String> fromNetThread) {
        this.toCenter = toCenter;
        this.fromNetThread = fromNetThread;
    }

    @Override
    public void run() {
        while (flag){
            try {
                String temp = fromNetThread.take();
                System.err.println("ClientNetPugMsg receive msg: " + temp);
                toCenter.put(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown(){
        flag = false;
    }
}
