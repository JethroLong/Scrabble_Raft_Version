package app.Peer.Client.Net;

import app.Peer.Client.Net.blockingqueue.ClientNetGetMsg;
import app.Peer.Client.Net.blockingqueue.ClientNetPutMsg;
import app.Peer.Client.gui.GuiController;
import app.Peer.Client.gui.LoginWindow;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ClientNet implements Runnable {
    private String tag = "Net";
    private static Logger logger = Logger.getLogger(ClientNet.class);
    private final BlockingQueue<String> fromCenter;
    private final BlockingQueue<String> toCenter;
    private final BlockingQueue<String> toNetPutMsg;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    private String ipAddr;
    private int portNum;
    private Socket leaderSocket;
    private String userName;
    private ArrayList<String> peerHosts;
    private ArrayList<Socket> connectedPeers;

    public Socket getLeaderSocket() {
        return leaderSocket;
    }

    public void setLeaderSocket(Socket leaderSocket) {
        this.leaderSocket = leaderSocket;
    }

    public ArrayList<String> getPeerHosts() {
        return peerHosts;
    }

    public void setPeerHosts(ArrayList<String> peerHosts) {
        this.peerHosts = peerHosts;
    }

    public ClientNet(BlockingQueue fromNet, BlockingQueue toNet, String ipAddr, int portNum, String userName) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        toNetPutMsg = new LinkedBlockingQueue<>();
        this.ipAddr=ipAddr;
        this.portNum=portNum;
        peerHosts = new ArrayList<String>();
        connectedPeers = new ArrayList<Socket>();
    }

    private ServerSocket server;

    private volatile static ClientNet net;
    private ClientNet(){
        fromCenter = new LinkedBlockingQueue<>();
        toCenter = new LinkedBlockingQueue<>();
        toNetPutMsg = new LinkedBlockingQueue<>();
        peerHosts = new ArrayList<String>();
        connectedPeers = new ArrayList<Socket>();
    }


    public static ClientNet getInstance(){
        if (net == null){
            synchronized (ClientNet.class){
                if (net == null){
                    net = new ClientNet();
                }
            }
        }
        return net;
    }

    public static ClientNet getInstance (BlockingQueue fromNet, BlockingQueue toNet,String ipAddr, int portNum,String userName){
        if (net == null){
            synchronized (ClientNet.class){
                if (net == null){
                    net = new ClientNet(fromNet,toNet,ipAddr,portNum,userName);
                }
            }
        }
        return net;
    }

    private void initialServer(Socket leaderSocket, BlockingQueue toNetPutMsg){
        pool.execute(new ClientNetThread(leaderSocket,toNetPutMsg));
    }


    public void connectToNewPeers(){
        ArrayList<String> connectedHosts = new ArrayList<String>();

        //extract the hosts of all connected peers
        for(Socket connected : connectedPeers){
            String connectedAddr = connected.getInetAddress().getHostAddress();
            connectedHosts.add(connectedAddr);
        }

        //check if the updated peerServers contains new unconnected peers
        // if yes, establish a new connection to the peer (default port 6666)
        for(String peerHost : peerHosts){
            if (!connectedHosts.contains(peerHost)){
                try {
                    Socket newPeer = new Socket(peerHost, 6666);
                    connectedPeers.add(newPeer);
                    connectedHosts.add(peerHost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown(){
        flag = false;
    }

    @Override
    public void run() {
//        Socket socket = null;
        try {
            if (leaderSocket == null) {
                leaderSocket = new Socket(ipAddr, portNum);
                connectedPeers.add(leaderSocket);
            }
            GuiController.get().loginGame();
            threadForSocket = new ThreadFactoryBuilder()
                    .setNameFormat("Net-pool-%d").build();
            pool = new ThreadPoolExecutor(3,50,0L,TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
            pool.execute(new ClientNetGetMsg(fromCenter,leaderSocket));
            pool.execute(new ClientNetPutMsg(toCenter,toNetPutMsg));
            initialServer(leaderSocket,toNetPutMsg);
        } catch (Exception e) {
            System.out.println("I am ClientNet, Help me! Please re-input!");
            net = null;
            LoginWindow.get().closeWindow();
            LoginWindow.get().reInitial();
        }
    }
}
