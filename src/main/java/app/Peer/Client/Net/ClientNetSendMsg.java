package app.Peer.Client.Net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class ClientNetSendMsg implements Runnable{

    private Socket client;
    private String message;
    private String msg;
    private Socket socket;

    public ClientNetSendMsg(String message,Socket socket) {
        this.message=message;
        this.socket=socket;
    }
    @Override
    public void run() {
        sendToPeer(message);
    }
    private void sendToPeer(String msg){

        sendMsgOperation(msg,socket);
    }

    private void sendMsgOperation(String msg, Socket socket){
        try {
//            System.out.println(socket);
            if(!socket.isClosed()){
                PrintWriter out= new PrintWriter(socket.getOutputStream());
                out.println(bouncyCastleBase64(msg));
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String bouncyCastleBase64 (String message) {
        byte[] encodeBytes = Base64.getEncoder().encode(message.getBytes());
        return new String (encodeBytes);
    }
}
