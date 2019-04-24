package app.Peer.Server.controllers.net;


import app.Models.PeerSockets;
import app.Protocols.Pack;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;

public class NetSendMsg implements Runnable {
    private Hashtable clientNameTable;
    private Socket client;
    private Pack message;
    private ArrayList<PeerSockets> peerSockets;
    public NetSendMsg(Pack message, Hashtable clientNameTable) {
        this.message=message;
        this.clientNameTable=clientNameTable;
    }
    @Override
    public void run() {

            if(message.getRecipient()==null){
                if(message.getUserId()==0){
                    sendBroadcastMsg(message.getMsg());
                }else {
                    sendToPeer(message.getMsg(),message.getUserId());
                }
            }else {
                int peerNum = message.getRecipient().length;
                for(int i=0;i<peerNum;i++){
                    sendToPeer(message.getMsg(),message.getRecipient()[i]);
                }
            }

    }

    private void sendBroadcastMsg(String msg){
        synchronized (clientNameTable){
            for(Enumeration enu = clientNameTable.elements(); enu.hasMoreElements();){
                client = (Socket)enu.nextElement();
                sendMsgOperation(msg);
            }
        }
    }

    private void sendToPeer(String msg, int clientId){
        client = (Socket)clientNameTable.get(clientId);
        sendMsgOperation(msg);
    }

    private void sendMsgOperation(String msg){
        try {
            PrintWriter printWriter = new PrintWriter(new DataOutputStream(client.getOutputStream()));
            printWriter.println(bouncyCastleBase64(msg));
            printWriter.flush();
        } catch (Exception e) {
            System.out.println("Welcome back!");
        }
    }

    private String bouncyCastleBase64 (String message) {
        byte[] encodeBytes = Base64.getEncoder().encode(message.getBytes());
        return new String (encodeBytes);
    }
}
