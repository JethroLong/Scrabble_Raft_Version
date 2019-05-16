package app.Peer.Client.blockingqueue;

import app.Peer.Client.gui.GuiListener;

import java.util.concurrent.BlockingQueue;

public class GuiGetMsg implements Runnable{
    public GuiGetMsg(BlockingQueue<String> fromCenter) {
        this.fromCenter = fromCenter;
        GuiListener.get().addBlockingQueue(fromCenter);
    }
    private BlockingQueue<String> fromCenter;


    @Override
    public void run() {

        while (true){
            String temp;
            try {
                temp = fromCenter.take();
//                GuiController.get().receiveMsgFromCenter(temp);
                synchronized (GuiListener.get()){
                    GuiListener.get().addMessage(temp);
                }
                System.err.println("GuiGetMsg: receive msg from server: " + temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
