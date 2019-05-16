package app.Peer.Client.Net.blockingqueue;

import app.Peer.Client.Net.ClientNet;
import app.Peer.Client.Net.ClientNetSendMsg;
import app.Peer.Server.controllers.gameEngine.GameProcess;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.*;

public class ClientNetGetMsg implements Runnable{
    private Socket socket;
    private Hashtable clientName;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    private BlockingQueue<String> fromCenter;

    private volatile static ClientNetGetMsg instance;

    private ClientNetGetMsg(BlockingQueue<String> fromCenter, Socket socket) {
        this.fromCenter = fromCenter;
        this.socket = socket;
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("NetGetMsg-pool-%d").build();
        pool = new ThreadPoolExecutor(20,100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
    }

    public static ClientNetGetMsg getInstance() {
        if (instance == null) {
            synchronized (ClientNetGetMsg.class) {
                if (instance == null) {
                    instance = new ClientNetGetMsg();
                }
            }
        }
        return instance;
    }

    private ClientNetGetMsg() {}

    public static ClientNetGetMsg getInstance(BlockingQueue<String> fromCenter, Socket socket) {
        if (instance == null) {
            synchronized (ClientNetGetMsg.class) {
                if (instance == null) {
                    instance = new ClientNetGetMsg(fromCenter, socket);
                }
            }
        }

        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (flag){
            try {
                String message = fromCenter.take();
                pool.execute(new ClientNetSendMsg(message,socket));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown(){
        flag = false;
    }

}
