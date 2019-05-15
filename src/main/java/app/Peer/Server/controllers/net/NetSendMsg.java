package app.Peer.Server.controllers.net;


import app.Models.PeerHosts;
import app.Peer.Client.gui.GuiController;
import app.Protocols.Pack;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class NetSendMsg implements Runnable {
    private Hashtable clientNameTable;
    private Socket client;
    private Pack message;
    private ArrayList<PeerHosts> peerSockets;
    public NetSendMsg(Pack message, Hashtable clientNameTable) {
        this.message=message;
        this.clientNameTable=clientNameTable;
    }
    @Override
    public void run() {

//            System.err.println("NetSendMsg called");
//            System.err.println("massage in NetSendMsg: "+message);
            if(message.getRecipient()==null){ // (message) Pack : msg + recipientID

//                System.err.println("GetRecipient null");
                if (message.getUserName()!=null){
                    if(message.getUserName().equals("broadcast")) broadcast(message.getMsg());
                    else sendToPeerSocket(message.getMsg(), message.getUserName());
                } else if(message.getUserId()==0){
//                    System.err.println("Userid 0, brocasting");
                    sendBroadcastMsg(message.getMsg()); //broadcast
                }else {
//                    System.err.println("Unicast");
                    sendToPeer(message.getMsg(),message.getUserId()); // unicast
                }
            }else {
//                System.err.println("GetRecipient: "+message.getRecipient());
                int peerNum = message.getRecipient().length;
                for(int i=0;i<peerNum;i++){
                    sendToPeer(message.getMsg(),message.getRecipient()[i]); //multi-cast
                }
            }

    }

    private void sendBroadcastMsg(String msg){
        synchronized (clientNameTable){
            for(Enumeration enu = clientNameTable.elements(); enu.hasMoreElements();){
                client = (Socket)enu.nextElement();
//                System.err.println("SERVER: Connected socket: "+client);
                sendMsgOperation(msg);
            }
        }
    }

    private void sendToPeer(String msg, int clientId){
        client = (Socket)clientNameTable.get(clientId);
//        System.out.println("sendToPeer id: "+clientId);
        sendMsgOperation(msg);
    }

    private void sendToPeerSocket(String msg, String clientName){
        client = Net.getInstance().getClientNameSocketMap().get(clientName);
//        if(client != null){
        try{
            if (!client.isClosed()) {
//                System.out.println("NegSendMsg Send to Peer() .." + client);
                sendMsgOperation(msg);
            }
//        }
        }catch (Exception e){
            System.err.println("Exception happened when sending to client: "+clientName);
            System.err.println("socket: "+client);
            System.err.println("message: "+msg);
            e.printStackTrace();
        }

    }

    private void broadcast(String msg){
        HashMap<String, Socket> clientNameSocketMap = new HashMap<>(Net.getInstance().getClientNameSocketMap());
//        System.out.println("MY USERNAME: "+GuiController.get().getUsername());
        for(String name: clientNameSocketMap.keySet()){
            if(!name.equals(GuiController.get().getUsername())) sendToPeerSocket(msg, name);
        }
    }

    private void sendMsgOperation(String msg){
        try {
            PrintWriter printWriter = new PrintWriter(new DataOutputStream(client.getOutputStream()));
            printWriter.println(bouncyCastleBase64(msg));
            printWriter.flush();
//            System.err.println("2: "+client);

        } catch (Exception e) {
//            System.out.println("Welcome back!");
            e.printStackTrace();
            System.out.println("client: "+client);
            System.out.println("msg: "+msg);
        }
    }

    private String bouncyCastleBase64 (String message) {
        byte[] encodeBytes = Base64.getEncoder().encode(message.getBytes());
        return new String (encodeBytes);
    }
}
