package app.Peer.Client.Net;

import app.Protocols.ServerResponse.ErrorProtocol;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

public class ClientNetThread implements Runnable {
    private Socket peerServer;
    private Hashtable clientDataHash;
    private Hashtable clientNameHash;
    private boolean isClientClosed = false;
    private final BlockingQueue<String> toNetPutMsg;
    private boolean flag = true;

    public ClientNetThread(Socket server, BlockingQueue toNetPutMsg) {
        this.peerServer = server;
        this.toNetPutMsg = toNetPutMsg;
    }

    @Override
    public void run() {

        BufferedReader inputStream;
        try {
            inputStream = new BufferedReader(new InputStreamReader(peerServer.getInputStream()));
            while (flag){
                if(peerServer.isClosed()==false && peerServer.isConnected()==true){
                    String message = inputStream.readLine();
                    if(message==null){
                        flag=false;
                    }else {
                        // Server Net to client
                        toNetPutMsg.put(bouncyCastleBase64(message));
                    }
//                Pack msg = new Pack(-1,message);
                    //System.out.println(message);

                }else {
                    closeClient();
                }
            }

        }catch (Exception e){
            System.out.println("Connection Closed!");
        }finally {
            if (!isClientClosed){
                closeClient();
            }
        }
    }
    private void closeClient(){
        try {
            toNetPutMsg.put(JSON.toJSONString(new ErrorProtocol("The leader has been shutdown", "other")));
            peerServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("System shutdown!");
    }

    private  String bouncyCastleBase64 (String cipher) {

        byte[] decodeBytes = Base64.getDecoder().decode(cipher);
        return new String(decodeBytes);
    }
}
