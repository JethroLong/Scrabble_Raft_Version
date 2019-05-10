package app.Peer.Client.gui;


import app.Peer.Client.blockingqueue.GuiPutMsg;
import app.Protocols.ScrabbleProtocol;
import com.alibaba.fastjson.JSON;

public class GuiSender {

    private static GuiSender instance = null;

    private GuiSender() {

    }

    public static synchronized GuiSender get() {
        if (instance == null) {
            instance = new GuiSender();
        }
        return instance;
    }

    public void sendToCenter(ScrabbleProtocol scrabbleProtocol) {
        try {
            String json = JSON.toJSONString(scrabbleProtocol);
            //System.out.println("Trans before send");
            //NonGamingProtocol protocol = (NonGamingProtocol) scrabbleProtocol;
            //System.out.println(JSON.toJSONString(protocol));
            //output.println(json);
            //output.flush();
            GuiPutMsg.getInstance().putMsgToCenter(json);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
