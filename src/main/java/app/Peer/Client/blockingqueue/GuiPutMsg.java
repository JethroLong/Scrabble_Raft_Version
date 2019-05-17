package app.Peer.Client.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class GuiPutMsg {
    public GuiPutMsg(){}
    public GuiPutMsg(BlockingQueue<String> toCenter) {
        this.toCenter = toCenter;
    }

    private BlockingQueue<String> toCenter;


    //Singleton GameEngine
    private volatile static GuiPutMsg guiPutMsg;
    public static GuiPutMsg getInstance(){
        if (guiPutMsg == null ){
            synchronized (GuiPutMsg.class){
                if (guiPutMsg == null){
                    guiPutMsg = new GuiPutMsg();
                }
            }
        }
        return guiPutMsg;
    }

    public static GuiPutMsg getInstance(BlockingQueue<String> toCenter){
        if (guiPutMsg == null ){
            synchronized (GuiPutMsg.class){
                if (guiPutMsg == null){
                    guiPutMsg = new GuiPutMsg(toCenter);
                }
            }
        }
        return guiPutMsg;
    }


    public synchronized void putMsgToCenter(String msg){
        try{
//            System.err.println("GuiPutMsg: Send msg to Server: " + msg);
            toCenter.put(msg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
