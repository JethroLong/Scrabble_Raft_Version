package app.Peer.Server.controllers.net;


import app.Models.PeerSockets;
import app.Peer.Client.gui.LoginWindow;
import app.Peer.Server.BackUp.BackUpTask;
import app.Peer.Server.BackUp.Scheduler;
import app.Peer.Server.controllers.net.blockingqueue.NetGetMsg;
import app.Peer.Server.controllers.net.blockingqueue.NetPutMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Net implements Runnable{
    private String tag = "Net";
    private static Logger logger = Logger.getLogger(String.valueOf(Net.class));

    public ArrayList<PeerSockets> getPeerSockets() {
        return peerSockets;
    }

    private ArrayList<PeerSockets> peerSockets; // to store peer sockets
    private final BlockingQueue<Pack> fromCenter;
    private final BlockingQueue<Pack> toCenter;
    private int portNumber = 6666;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public Net(BlockingQueue fromNet, BlockingQueue toNet) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        peerSockets = new ArrayList<PeerSockets>();
    }

    public Net(BlockingQueue fromNet, BlockingQueue toNet, int portNumber) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        this.portNumber=portNumber;
        peerSockets = new ArrayList<PeerSockets>();
    }

    public Hashtable getClientDataHsh() {
        return clientDataHsh;
    }

    public Hashtable getClientNameHash() {
        return clientNameHash;
    }

    private Hashtable clientDataHsh = new Hashtable(50);
    private Hashtable clientNameHash = new Hashtable(50);

    public ServerSocket getServer() {
        return server;
    }

    private ServerSocket server;

    private volatile static Net net;
    private Net(){
        fromCenter = new LinkedBlockingQueue<>();
        toCenter = new LinkedBlockingQueue<>();
    }
    public static Net getInstance(){
        if (net == null){
            synchronized (Net.class){
                if (net == null){
                    net = new Net();
                }
            }
        }
        return net;
    }

    public static Net getInstance (BlockingQueue fromNet, BlockingQueue toNet){
        if (net == null){
            synchronized (Net.class){
                if (net == null){
                    net = new Net(fromNet,toNet);
                }
            }
        }
        return net;
    }

    public static Net getInstance (BlockingQueue fromNet, BlockingQueue toNet, int portNumber){
        if (net == null){
            synchronized (Net.class){
                if (net == null){
                    net = new Net(fromNet,toNet,portNumber);
                }
            }
        }
        return net;
    }

    private void initialServer(int port, BlockingQueue toNetPutMsg){
        Socket client;
        int clientNumber = 1;
        try {
            server = new ServerSocket(port);
            LoginWindow loginWindow = LoginWindow.get();
            loginWindow.loginAction(loginWindow.getUserNameStr(),loginWindow.getAddress(),loginWindow.getPortStr());
            while (flag){
                client = server.accept();
                peerSockets.add(new PeerSockets(clientNumber, client));
                DataOutputStream dataOutputStream = new DataOutputStream(client
                            .getOutputStream());

                if (clientNumber == 1){
                    new Thread(new Scheduler()).start();
                }
                // UserID distribution
                clientDataHsh.put(client,dataOutputStream);
                clientNameHash.put(clientNumber++,client);

                // allocate a worker thread for the new client.
                pool.execute(new NetThread(client,clientDataHsh,clientNameHash,toNetPutMsg,clientNumber-1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        flag = false;
    }

    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("Net-pool-%d").build();
        pool = new ThreadPoolExecutor(10,20,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        BlockingQueue<Pack> toNetPutMsg = new LinkedBlockingQueue<>();
        pool.execute(new NetGetMsg(fromCenter,clientNameHash));
        pool.execute(new NetPutMsg(toCenter,toNetPutMsg));
        initialServer(portNumber,toNetPutMsg);
    }
}
