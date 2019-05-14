package app.Peer.Server.controllers.net.blockingqueue;

import app.Peer.Server.controllers.net.NetSendMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Hashtable;
import java.util.concurrent.*;

public class NetGetMsg implements Runnable {
    private Hashtable clientName;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    public NetGetMsg(BlockingQueue<Pack> fromCenter, Hashtable clientName) {
        this.fromCenter = fromCenter;
        this.clientName = clientName;
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("NetGetMsg-pool-%d").build();
        pool = new ThreadPoolExecutor(10,100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
    }

    private final BlockingQueue<Pack> fromCenter;
    @Override
    public void run() {
        while (flag){
            try {
                Pack message = fromCenter.take();
                System.err.println("NetGetMsg: send msg: " + message.getMsg());

                pool.execute(new NetSendMsg(message, clientName));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void shutdown(){
        flag = false;
    }
}
