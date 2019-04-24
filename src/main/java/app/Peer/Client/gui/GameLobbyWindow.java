package app.Peer.Client.gui;


import app.Models.Users;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameLobbyWindow implements Runnable {

    private JFrame frame;
    private JTable userList;
    private JTable playerList;

    private JButton btnInvite;
    private JButton btnStart;
    private JButton btnLeave;

    private NonEditableModel userTableModel = new NonEditableModel();
    private NonEditableModel playerTableModel = new NonEditableModel();

    public static class LobbyWindowHolder {
        private static final GameLobbyWindow INSTANCE = new GameLobbyWindow();
    }

    private GameLobbyWindow() {

    }

    public JFrame getFrame(){
        return this.frame;
    }

    public static final GameLobbyWindow get() {
        return LobbyWindowHolder.INSTANCE;
    }

    void setModel() {
        userTableModel.addColumn("Id");
        userTableModel.addColumn("Name");
        userTableModel.addColumn("Status");
        userTableModel.addColumn("Win");
        playerTableModel.addColumn("Id");
        playerTableModel.addColumn("Name");
        playerTableModel.addColumn("Status");
    }

    public void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void run() {
        initialize();
        this.frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 460, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        DefaultTableCellRenderer renderer  =  new  DefaultTableCellRenderer();   //set column align center
        renderer.setHorizontalAlignment(JTextField.CENTER);

        userList = new JTable(userTableModel);
        userList.getColumnModel().getColumn(0).setCellRenderer(renderer);
        userList.getColumnModel().getColumn(1).setCellRenderer(renderer);
        userList.getColumnModel().getColumn(2).setCellRenderer(renderer);
        userList.getColumnModel().getColumn(3).setCellRenderer(renderer);
        userList.setBounds(20, 40, 200, 300);
        userList.getColumnModel().getColumn(0).setPreferredWidth(28);
        userList.getColumnModel().getColumn(1).setPreferredWidth(72);
        userList.getColumnModel().getColumn(2).setPreferredWidth(60);
        userList.getColumnModel().getColumn(3).setPreferredWidth(40);
//        userList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        frame.getContentPane().add(userList);

        JScrollPane spUserList = new JScrollPane(userList);
        spUserList.setBounds(20, 40, 200, 300);
        spUserList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(spUserList);

        btnInvite = new JButton("Invite");
        btnInvite.setBounds(75, 350, 90, 29);
        frame.getContentPane().add(btnInvite);

        btnInvite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] selection = userList.getSelectedRows();
                if (selection.length < 2) {
                    showDialog("You must select more than one players.");
                }
                else {
                    String[] players = new String[selection.length];
                    //System.out.printf("%d%n", selection[0]);
                    int num = 0;
                    boolean valid = false;
                    for (int i : selection) {
                        System.out.printf("row index: %d%n", i);
                        if (userList.getValueAt(i, 0).toString().equals(GuiController.get().getId())) {
                            valid = true;
                        }
                        players[num++] = userList.getValueAt(i, 1).toString(); // include the inviter
                    }
                    if (!valid) {
                        showDialog("You must select yourself.");
                    }
                    else {
                        GuiController.get().invitePlayers(players);
                    }
                }
            }
        });

        JLabel lblNewLabel, lblNewLabel1;
        lblNewLabel = new JLabel("Online users:");
        lblNewLabel.setBounds(20, 20, 103, 16);
        frame.getContentPane().add(lblNewLabel);

        playerList = new JTable(playerTableModel);
        playerList.setBounds(240, 40, 200, 300);
        frame.getContentPane().add(playerList);

        JScrollPane spPlayerList = new JScrollPane(playerList);
        spPlayerList.setBounds(240, 40, 200, 150);
        spPlayerList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(spPlayerList);

        btnStart = new JButton("Start");
        btnStart.setBounds(240, 200, 90, 29);
        frame.getContentPane().add(btnStart);

        btnLeave = new JButton("Leave");
        btnLeave.setBounds(350, 200, 90, 29);
        frame.getContentPane().add(btnLeave);

        btnLeave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiController.get().sendLeaveMsg();
            }
        });

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (playerList.getRowCount() < 1) {
                    showDialog("You must have more than one player");
                }
                else {
                    /*
                    String[] players = new String[playerList.getRowCount()];
                    for (int i = 0; i < playerList.getRowCount(); i++) {
                        players[i] = playerList.getValueAt(i, 0).toString();
                    }
                    */
                    GuiController.get().startGame();
                }
            }
        });

        lblNewLabel1 = new JLabel("Current Team Members:");
        lblNewLabel1.setBounds(240, 20, 150, 16);
        frame.getContentPane().add(lblNewLabel1);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                GuiController.get().logoutGame();
                frame.dispose();
                System.exit(0);
            }
        });

        frame.setTitle(GuiController.get().getUsername());
    }

    public class NonEditableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    int getIndexInUserList(int id) {
        String strId = Integer.toString(id);
        for (int i = 0; i < userList.getRowCount(); i++) {
            if (userList.getValueAt(i, 0).toString().equals(strId)) {
                return i;
            }
        }
        return -1;
    }

    /*
    void updateUserList(int id, String username, String status) {
        String strId = Integer.toString(id);
        int index = getIndexInUserList(strId);
        if (index == -1) {
            addToUserList(strId, username, status);
        }
        else {
            userList.setValueAt(username, index, 1);
            userList.setValueAt(status, index, 2);
        }
    }
    */

    void clearUserList() {
        int rowCount = userTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            userTableModel.removeRow(i);
        }
    }

    void updateUserList(Users[] userList) {
        clearUserList();
        for (Users user : userList)
            addToUserList(user.getUserID(), user.getUserName(), user.getStatus(), user.getNumWin());
    }

    void addToUserList(int id, String name, String status, int numWin) {
        String strId = Integer.toString(id);
        String strNumWin = Integer.toString(numWin);
        userTableModel.addRow(new Object[]{strId, name, status, strNumWin});
    }

    void clearPlayerList() {
        int rowCount = playerTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            playerTableModel.removeRow(i);
        }
    }

    //update playerList and update related players in userList
    void updatePlayerList(Users[] playerList) {
        clearPlayerList();
        for (Users player : playerList) {
            addToPlayerList(player.getUserID(), player.getUserName(), player.getStatus());
            updatePlayerInUserList(player);
        }
    }

    void updatePlayerInUserList(Users player) {
        int index = getIndexInUserList(player.getUserID());
        userList.setValueAt(player.getStatus(), index, 2);
    }

    void addToPlayerList(int id, String name, String status) {
        String strId = Integer.toString(id);
        playerTableModel.addRow(new Object[]{strId, name, status});
    }

    void showInviteMessage(int inviterId, String inviterName) {
        int confirmed = JOptionPane.showConfirmDialog(null, inviterName+" ask you to join a game, yes or no?",
                "Invite", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            GuiController.get().sendInviteResponse(true, inviterId);
        }
        else {
            GuiController.get().sendInviteResponse(false, inviterId);
        }
    }

    void showRefuseInvite(int id) {
        int index = getIndexInUserList(id);
        showDialog(userList.getValueAt(index, 1) + " has refused you.");
    }
}
