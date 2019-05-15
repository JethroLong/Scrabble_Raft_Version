package app.Peer.Server.controllers.net;


import app.Models.PeerHosts;
import app.Peer.Client.gui.GuiController;
import app.Peer.Server.BackUp.Scheduler;
import app.Peer.Server.controllers.net.blockingqueue.NetGetMsg;
import app.Peer.Server.controllers.net.blockingqueue.NetPutMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Net implements Runnable{
    private String tag = "Net";
    private static Logger logger = Logger.getLogger(String.valueOf(Net.class));

    public ArrayList<PeerHosts> getPeerSockets() {
        return peerHosts;
    }
    private ArrayList<PeerHosts> peerHosts; // to store peer hosts
    private final BlockingQueue<Pack> fromCenter;
    private final BlockingQueue<Pack> toCenter;
    private int portNumber = 6666;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    private Hashtable clientDataHsh = new Hashtable(50);
    private Hashtable clientNameHash = new Hashtable(50);
    private HashMap<String, Socket> clientNameSocketMap= new HashMap<String, Socket>();

    public HashMap<String, Socket> getClientNameSocketMap() {
        return clientNameSocketMap;
    }

    public void putClientNameSocketMap(String clientName, Socket socket) {
        this.clientNameSocketMap.put(clientName, socket);
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    private int clientNumber;

    public Net(BlockingQueue fromNet, BlockingQueue toNet) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        peerHosts = new ArrayList<PeerHosts>();
    }

    public Net(BlockingQueue fromNet, BlockingQueue toNet, int portNumber) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        this.portNumber=portNumber;
        peerHosts = new ArrayList<PeerHosts>();
    }

    public Hashtable getClientDataHsh() {
        return clientDataHsh;
    }

    public Hashtable getClientNameHash() {
        return clientNameHash;
    }

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

        //check if the peer has an allocated clientID already.
        if (GuiController.get().getId().equals("None")){
            clientNumber = 1;
        }

        try {
            server = new ServerSocket(port);
//            LoginWindow loginWindow = LoginWindow.get();
//            loginWindow.loginAction(loginWindow.getUserNameStr(),loginWindow.getAddress(),loginWindow.getPortStr());

            while (flag){

                client = server.accept();
                String clientHost = client.getInetAddress().getHostAddress();
                if (clientHost.equals("127.0.0.1")){
                    clientHost = InetAddress.getLocalHost().getHostAddress();
                }
                peerHosts.add(new PeerHosts(clientNumber, clientHost));
                DataOutputStream dataOutputStream = new DataOutputStream(client
                            .getOutputStream());

//                if (GuiController.get().isLeader()){
//                    new Thread(new Scheduler()).start(); //leader starts back up task
//                }
                // UserID -- socket binding && put in hashtable for quick access
                clientDataHsh.put(client,dataOutputStream);
                clientNameHash.put(clientNumber++,client);
                System.err.println("clientName: "+client);
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
        System.err.println("peerHosts: "+peerHosts);
    }
}
