package app.Peer.Client.Net;

import app.Models.PeerHosts;
import app.Peer.Client.Gui;
import app.Peer.Client.Net.blockingqueue.ClientNetGetMsg;
import app.Peer.Client.Net.blockingqueue.ClientNetPutMsg;
import app.Peer.Client.gui.GuiController;
import app.Peer.Client.gui.LoginWindow;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

    public int getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    private int leaderID;
    private String ipAddr;
    private int portNum;
    private Socket leaderSocket;
    private String userName;

    private ArrayList<PeerHosts> peerHosts;

    public ArrayList<Socket> getConnectedPeerSockets() {
        return connectedPeerSockets;
    }

    public void setConnectedPeerSockets(ArrayList<Socket> connectedPeers) {
        this.connectedPeerSockets = connectedPeers;
    }

    private ArrayList<Socket> connectedPeerSockets;
    private ArrayList<PeerHosts> connectedPeerHosts;

    public Socket getLeaderSocket() {
        return leaderSocket;
    }

    public void setLeaderSocket(Socket leaderSocket) {
        this.leaderSocket = leaderSocket;
    }


    public ArrayList<PeerHosts> getPeerHosts() {
        return peerHosts;
    }

    public void setPeerHosts(ArrayList<PeerHosts> peerHosts) {
        this.peerHosts = peerHosts;
    }


    public ClientNet(BlockingQueue fromNet, BlockingQueue toNet, String ipAddr, int portNum, String userName) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
        toNetPutMsg = new LinkedBlockingQueue<>();
        this.ipAddr = ipAddr;
        this.portNum = portNum;
        peerHosts = new ArrayList<PeerHosts>();
        connectedPeerSockets = new ArrayList<Socket>();
        connectedPeerHosts = new ArrayList<PeerHosts>();
    }

    private ServerSocket server;

    private volatile static ClientNet net;

    private ClientNet() {
        fromCenter = new LinkedBlockingQueue<>();
        toCenter = new LinkedBlockingQueue<>();
        toNetPutMsg = new LinkedBlockingQueue<>();

        peerHosts = new ArrayList<PeerHosts>();
        connectedPeerSockets = new ArrayList<Socket>();
        connectedPeerHosts = new ArrayList<PeerHosts>();
    }


    public static ClientNet getInstance() {
        if (net == null) {
            synchronized (ClientNet.class) {
                if (net == null) {
                    net = new ClientNet();
                }
            }
        }
        return net;
    }

    public static ClientNet getInstance(BlockingQueue fromNet, BlockingQueue toNet, String ipAddr, int portNum, String userName) {
        if (net == null) {
            synchronized (ClientNet.class) {
                if (net == null) {
                    net = new ClientNet(fromNet, toNet, ipAddr, portNum, userName);
                }
            }
        }
        return net;
    }

    private void initialServer(Socket leaderSocket, BlockingQueue toNetPutMsg) {
        pool.execute(new ClientNetThread(leaderSocket, toNetPutMsg));
    }


    public void connectToNewPeers() {
        //extract the hosts of all connected peers

        //check if the updated peerServers contains new unconnected peers
        // if yes, establish a new connection to the peer (with peer's local server port)
//        String localHostAddr = "";
//        try {
//            localHostAddr = InetAddress.getLocalHost().getHostAddress();
//        } catch (Exception e) {
//
//        }
//        String localServerPort = GuiController.get().getLocalServerPort();
//        String leaderAddr = LoginWindow.get().getLeaderAddr();
//        String leaderPort = LoginWindow.get().getLeaderPortStr();
        for (PeerHosts peer : peerHosts) {
            int count = 0;
            for(PeerHosts connected : connectedPeerHosts){
                if(peer.getPeerHost().equals(connected.getPeerHost())
                && peer.getPeerPort().equals(connected.getPeerPort())){
                    break;
                }
                count++;
            }
            if(count == connectedPeerHosts.size()){
                String addr = peer.getPeerHost();
                int portNum = Integer.parseInt(peer.getPeerPort());
                System.out.println("new peer detected, start connection");
                startConnection(addr, portNum);
            }

//            if (!(peer.getPeerHost().equals(localHostAddr)
//                    && peer.getPeerPort().equals(localServerPort)
//                    && !(peer.getPeerHost().equals(leaderAddr)
//                    && peer.getPeerPort().equals(leaderPort)))) {
//                System.out.println("new peer detected, start connection");
//                startConnection(addr, portNum);
//            }
        }
    }

    private void startConnection(String Addr, int port) {
        try {
            Socket newPeer = new Socket(Addr, port);

            System.out.println("connection succ! ");
            connectedPeerHosts.add(new PeerHosts(Addr, Integer.toString(port)));
            connectedPeerSockets.add(newPeer);


            // open new net for new peer
            initialServer(newPeer, toNetPutMsg);
        } catch (IOException e) {
            System.out.println("peer connection exception");
        }
    }


    public void shutdown() {
        flag = false;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            if (leaderSocket == null) {
                socket = new Socket(ipAddr, portNum);
                connectedPeerSockets.add(socket);
                connectedPeerHosts.add(new PeerHosts(ipAddr, Integer.toString(portNum)));
                if (GuiController.get().isLeader()) {
                    leaderSocket = socket;
                }
            } else {
                socket = leaderSocket;
            }
            String localServerPort = GuiController.get().getLocalServerPort();
            GuiController.get().loginGame(localServerPort);
            threadForSocket = new ThreadFactoryBuilder()
                    .setNameFormat("Net-pool-%d").build();
            pool = new ThreadPoolExecutor(10, 50, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024), threadForSocket, new ThreadPoolExecutor.AbortPolicy());
            pool.execute(new ClientNetGetMsg(fromCenter, socket));
            pool.execute(new ClientNetPutMsg(toCenter, toNetPutMsg));
            initialServer(socket, toNetPutMsg);
        } catch (Exception e) {
            System.out.println("I am ClientNet, Help me! Please re-input!");
            net = null;
            LoginWindow.get().closeWindow();
            LoginWindow.get().reInitial();
        }
    }
}
