package app.Peer.Server.controllers.gameEngine;


import app.Peer.Client.gui.GuiController;
import app.Peer.Server.BackUp.GameSateScheduler;
import app.Peer.Server.BackUp.backupScheduler;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EngineGetMsg;
import app.Peer.Server.controllers.gameEngine.blockingqueque.EnginePutMsg;
import app.Protocols.Pack;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class GameEngine implements Runnable{
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;


    public GameEngine(BlockingQueue<Pack> toEngine, BlockingQueue<Pack> fromEngine) {
        this.fromCenter = toEngine;
        this.toCenter = fromEngine;
    }


    //Singleton GameEngine
    private volatile static GameEngine gameEngine;
    public GameEngine(){}
    public static GameEngine getInstance(){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine();
                }
            }
        }
        return gameEngine;
    }

    public static GameEngine getInstance(BlockingQueue<Pack> toEngine, BlockingQueue<Pack> fromEngine){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine(toEngine, fromEngine);
                }
            }
        }
        return gameEngine;
    }

    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(20,100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new EngineGetMsg(fromCenter));
        startBackup();
        EnginePutMsg.getInstance(toCenter);
    }

    public void shutdown(){
        flag = false;
    }

    public void startBackup(){
        if(GuiController.get().isLeader()){
            new Thread(new backupScheduler()).start();
            new Thread(new GameSateScheduler()).start();
        }
    }
}
