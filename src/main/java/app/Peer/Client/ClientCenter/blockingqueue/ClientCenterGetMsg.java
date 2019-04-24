package app.Peer.Client.ClientCenter.blockingqueue;


import java.util.concurrent.BlockingQueue;

public class ClientCenterGetMsg implements Runnable {
    private final BlockingQueue<String> fromNet;
    private final BlockingQueue<String> toGui;
    private final BlockingQueue<String> fromGui;
    private final BlockingQueue<String> toNet;

    public ClientCenterGetMsg(BlockingQueue<String> fromNet, BlockingQueue<String> toGui, BlockingQueue<String> fromGui, BlockingQueue<String> toNet) {
        this.fromNet = fromNet;
        this.toGui = toGui;
        this.fromGui = fromGui;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        while (true){
            try {
                toGui.put(fromNet.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
