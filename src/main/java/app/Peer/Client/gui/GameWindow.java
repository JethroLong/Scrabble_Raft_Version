package app.Peer.Client.gui;

import app.Models.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow implements Runnable {

    private JFrame frame;
    private GameGridPanel gridPanel;
    private GameAlphabet alphabetPanel;
    private PlayerPanel playerPanel;
    private JButton passBtn, voteBtn;

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    private Player[] players;

    /*
    public static class GameWindowHolder {
        private static final GameWindow INSTANCE = new GameWindow();
    }

    public static final GameWindow get() {
        return GameWindowHolder.INSTANCE;
    }
    */

    private GameWindow() {
        gridPanel = GameGridPanel.get();
        alphabetPanel = GameAlphabet.get();
        playerPanel = PlayerPanel.get();
        initialize();
    }

    private volatile static GameWindow instance = null;

    public static synchronized GameWindow get() {
        if (instance == null) {
            synchronized (GameWindow.class) {
                instance = new GameWindow();
            }
        }
        return instance;
    }

    private void setToNull() {
        instance = null;
    }

    @Override
    public void run() {
        this.frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        gridPanel.delLastMoveValue();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GuiController.get().sendQuitMsg();
            }
        });
        frame.setTitle("Scrabble Game "+GuiController.get().getUsername());
        frame.setSize(860, 740);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        gridPanel.setBounds(20, 20, 600, 600);
        frame.getContentPane().add(gridPanel);

        alphabetPanel.setBounds(125, 640, 390, 60);
        frame.getContentPane().add(alphabetPanel);

        playerPanel.setBounds(640, 20, 200, 300);
        frame.getContentPane().add(playerPanel);

        passBtn = new JButton("PASS");
        passBtn.setBounds(640, 640, 100, 60);
        voteBtn = new JButton("VOTE");
        voteBtn.setBounds(740, 640, 100, 60);
        frame.add(passBtn);
        frame.add(voteBtn);

//      gridPanel.setAllowDrag(true);

        passBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] lastMove = gridPanel.getLastMove();
                if (lastMove[0] != -1 && lastMove[1] != -1) {
                    // Placing but pass
                    //System.err.println("sendPass: " + gridPanel.getCharacter(lastMove[0], lastMove[1]));
                    GuiController.get().sendPass(lastMove, gridPanel.getCharacter(lastMove[0], lastMove[1]));
                    gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                    gridPanel.delLastMoveValue();
                }
                else {
                    // No Placing
                    GuiController.get().sendPass(lastMove, '0');
                }
                gridPanel.setAllowDrag(false);
                //System.err.println("set to false 3");
            }
        });

        voteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //System.out.println("Click Vote!!!!");
                int[] lastMove = gridPanel.getLastMove();
                gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                gridPanel.getSelectArea();
//                gridPanel.delLastMoveValue();
                gridPanel.setAllowDrag(false);
                //System.err.println("set to false 2");
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                frame.dispose();
                clearGameWindow();
            }
        });
    }


    void clearGameWindow() {
        gridPanel.clearGrid();
        playerPanel.clearPlayerList();
    }


    void startOneTurn() {
        //System.err.println("set to true 1");
        gridPanel.setAllowDrag(true);
    }

    void sendSelect(int[] lastMove, int sx, int sy, int ex, int ey) {
        char c = gridPanel.getCharacter(lastMove[0], lastMove[1]);
        GuiController.get().sendVote(lastMove, c, sx, sy, ex, ey);
    }

    synchronized void updatePlayerList(Player[] playerList) {
        playerPanel.updatePlayerList(playerList);
    }

    void updateBoard(char[][] board) {
        gridPanel.updateBoard(board);
    }

    void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
    }

    void showVoteRequest(int inviterId, int[] startPosition, int[] endPosition) {
        String inviterName = playerPanel.getPlayerName(inviterId);
        String word = gridPanel.getWord(startPosition, endPosition);
        int confirmed = JOptionPane.showConfirmDialog(null, inviterName+"'s Vote:\n" + "Do you agree " + word + " is a word?"
                ,"Vote", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            GuiController.get().sendVoteResponse(true);
        }
        else {
            GuiController.get().sendVoteResponse(false);
        }
    }

    void showWinners(Player[] players) {
        String message = new String();
        message = "Winner: \n";
        for (Player player: players) {
            message = message + player.getUser().getUserName() + "  ";
        }
        showDialog(message);
        frame.dispose();
        clearGameWindow();
    }

    public void setGameTurnTitle(int title){
        for (Player player : players){
            if(player.getInGameSequence()==title){
                //System.err.println("frame: " + frame);
                frame.setTitle("I am "+GuiController.get().getUsername()+"    Current player: "+player.getUser().getUserName());
                break;
            }
        }

    }

    void shutDown(){
        frame.dispose();
        clearGameWindow();
    }
}
