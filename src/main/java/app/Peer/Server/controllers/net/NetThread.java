package app.Peer.Server.controllers.net;


import app.Protocols.GamingProtocol.GamingOperationProtocol;
import app.Protocols.Pack;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

public class NetThread implements Runnable {
    private boolean flag = true;
    private Socket client;
    private Hashtable clientDataHash;
    private Hashtable clientNameHash;
    private boolean isClientClosed = false;
    private final BlockingQueue<Pack> toNetPutMsg;
    private int clientID;

    public NetThread(Socket client, Hashtable clientDataHash, Hashtable clientNameHash, BlockingQueue toNetPutMsg, int clientID) {
        this.client = client;
        this.clientDataHash = clientDataHash;
        this.clientNameHash = clientNameHash;
        this.toNetPutMsg = toNetPutMsg;
        this.clientID = clientID;
    }

    @Override

    // keep listening to one client socket, and read messages and pass it to center
    public void run() {
//        DataInputStream inputStream;
        BufferedReader inputStream;
        synchronized (clientDataHash){
            System.out.println("Current users number:"+clientDataHash.size());
        }
        try {
//            inputStream = new DataInputStream(client.getInputStream());
            inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (flag){
                if(client.isClosed()==false&&client.isConnected()==true){
                    String message = inputStream.readLine();
                    if(message!=null||!message.equals("")){
                        // toNetPutMsg -- from client to net
                        toNetPutMsg.put(new Pack(clientID,bouncyCastleBase64(message)));
                    }else {
                        flag=false;
                    }
                }else {
                    flag=false;
                    client.close();
                    break;
                }
                if(client.isConnected()==false||client.isClosed()==true){
                    flag=false;
                    client.close();
                    break;
                }
            }

        }catch (Exception e){
            closeClient();
        }
    }
    private void closeClient(){
        try {
            client.close();
            System.out.println("client "+clientID+" is closed!");
            Net.getInstance().getClientDataHsh().remove(client);
            Net.getInstance().getClientNameHash().remove(clientID);
            toNetPutMsg.put(new Pack(clientID, JSON.toJSONString(new GamingOperationProtocol("disconnect"))));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  String bouncyCastleBase64 (String cipher) {

        byte[] decodeBytes = Base64.getDecoder().decode(cipher);
        return new String(decodeBytes);
    }
}
