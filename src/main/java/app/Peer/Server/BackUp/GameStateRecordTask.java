package app.Peer.Server.BackUp;

import app.Peer.Client.gui.GuiController;
import app.Peer.Server.controllers.gameEngine.GameProcess;

import java.util.TimerTask;

public class GameStateRecordTask extends TimerTask implements Runnable{
    public GameStateRecordTask() {
    }

    @Override
    public void run() {
        if (GuiController.get().isLeader()) {
            GameProcess.getInstance().getGameState();
        }//call updateGameState()
    }
}
