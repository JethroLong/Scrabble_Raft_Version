package app.Peer.Server.controllers.gameEngine.blockingqueque;


import app.Protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class EnginePutMsg  {
    public EnginePutMsg(){}
    public EnginePutMsg(BlockingQueue<Pack> toCenter) {
        this.toCenter = toCenter;
    }

    private BlockingQueue<Pack> toCenter;


    //Singleton GameEngine
    private volatile static EnginePutMsg enginePutMsg;
    public static EnginePutMsg getInstance(){
        if (enginePutMsg == null ){
            synchronized (EnginePutMsg.class){
                if (enginePutMsg == null){
                    enginePutMsg = new EnginePutMsg();
                }
            }
        }
        return enginePutMsg;
    }

    public static EnginePutMsg getInstance(BlockingQueue<Pack> toCenter){
        if (enginePutMsg == null ){
            synchronized (EnginePutMsg.class){
                if (enginePutMsg == null){
                    enginePutMsg = new EnginePutMsg(toCenter);
                }
            }
        }
        return enginePutMsg;
    }


    public void putMsgToCenter(Pack msg){
        try{
            toCenter.put(msg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
