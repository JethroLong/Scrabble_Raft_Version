package app.Peer.Client.gui;

import app.Peer.Client.ClientCenter.ClientControlCenter;
import app.Peer.Client.Net.ClientNet;
import app.Peer.Server.gui.MonitorGui;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Random;

public class LoginWindow implements Runnable {
    public ClientControlCenter getCenter() {
        return center;
    }

    public void setCenter(ClientControlCenter center) {
        this.center = center;
    }

    private ClientControlCenter center;
    private JFrame frame;
    private JTextField userName;
    private JTextField ip;
    private JTextField leaderPort;
    private JCheckBox mode;
    private JTextArea inviteURL;
    private String address;
    private JTextField localPort;

    public String getAddress() {
        return address;
    }

    public String getPortStr() {
        return leaderPortStr;
    }

    public String getUserNameStr() {
        return userNameStr;
    }

    private String leaderPortStr;
    private String localPortStr;
    private String userNameStr;


    public LoginWindow() {
    }

    //    public static class LoginWindowHolder {
//        private static final LoginWindow INSTANCE = new LoginWindow();
//    }
    private static LoginWindow loginWindow;


    public static final LoginWindow get() {
        if (loginWindow == null) {
            return loginWindow = new LoginWindow();
        } else {
            return loginWindow;
        }
    }


    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void run() {
        initialize();
    }

    public void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
        closeWindow();
    }

    public void closeWindow() {
        frame.dispose();
    }

    public void reInitial() {
        initialize();
        JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
        this.frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 350, 240);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 23, 66, 16);
        frame.getContentPane().add(lblUsername);

        JLabel lblNewLabel = new JLabel("IP address:");
        lblNewLabel.setBounds(30, 62, 68, 16);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Leader Port:");
        lblNewLabel_1.setBounds(30, 95, 75, 16);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Local Port:");
        lblNewLabel_2.setBounds(30, 125, 68, 16);
        frame.getContentPane().add(lblNewLabel_2);

        userName = new JTextField();
        userName.setBounds(125, 18, 160, 26);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        ip = new JTextField();
        ip.setBounds(125, 57, 160, 26);
        frame.getContentPane().add(ip);
        ip.setColumns(10);

        leaderPort = new JTextField();
        leaderPort.setBounds(125, 95, 160, 26);
        frame.getContentPane().add(leaderPort);
        leaderPort.setColumns(10);

        localPort = new JTextField();
        localPort.setBounds(125, 125, 160, 26);
        frame.getContentPane().add(localPort);
        localPort.setColumns(10);

        mode = new JCheckBox("   Login as leader");
        frame.getContentPane().add(mode);
        mode.setBounds(90, 155, 200, 30);

        JButton login = new JButton("Login");
        login.setBounds(90, 185, 80, 30);
        frame.getContentPane().add(login);

        JButton other = new JButton("Other Login");
        other.setBounds(190, 185, 105, 30);
        frame.getContentPane().add(other);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    checkIfServer();
                }
            }
        };

        login.addKeyListener(keyListener);
        login.addActionListener((ActionEvent arg0) -> {
            try {
                checkIfServer(); // if start as server
            } catch (Exception e) {
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
            }
        });

        other.addKeyListener(keyListener);
        other.addActionListener((ActionEvent arg0) -> {
            try {
                changeView();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
            }
        });
        this.frame.setVisible(true);
    }

//    void loginAction() {
//        if(!userNameStr.trim().isEmpty()) {
//            center.openNet(address, Integer.parseInt(portStr), userNameStr);
//            //clientManager.openSocket(address, portStr, userNameStr);
//            GuiController.get().setUserName(userNameStr);
//
//        }else{
//            showDialog("Invalid username, please try again!");
//            run();
//        }
//    }

    // execute when user click the login button
    public void loginAction(String userName, String ipAddr, String portNum) {
        if (!userName.isEmpty()) {
            center.openNet(ipAddr, Integer.parseInt(portNum), userName);
            //clientManager.openSocket(address, portStr, userNameStr);
            GuiController.get().setUserName(userName);
        } else {
            showDialog("Invalid username, please try again!");
            run();
        }
    }

    public void changeView() {
        closeWindow();
        frame = new JFrame();
        frame.setBounds(100, 100, 350, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(47, 23, 66, 16);
        frame.getContentPane().add(lblUsername);

        JLabel lblURL = new JLabel("Link:");
        lblURL.setBounds(47, 50, 66, 16);
        frame.getContentPane().add(lblURL);

        userName = new JTextField();
        userName.setBounds(125, 18, 160, 26);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        inviteURL = new JTextArea();
        inviteURL.setBounds(125, 50, 160, 80);
        inviteURL.setLineWrap(true);
        frame.getContentPane().add(inviteURL);

        JButton login = new JButton("Login");
        login.setBounds(125, 138, 80, 30);
        frame.getContentPane().add(login);

        JButton back = new JButton("Back");
        back.setBounds(210, 138, 80, 30);
        frame.getContentPane().add(back);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    checkIfServer();
                }
            }
        };
        login.addKeyListener(keyListener);
        back.addKeyListener(keyListener);
        login.addActionListener((ActionEvent arg0) -> {
            try {
                //decryption
                JSONArray inviteURLText = JSON.parseArray(bouncyCastleBase64(inviteURL.getText()));
                loginAction(userName.getText(), inviteURLText.getString(0), inviteURLText.getString(1));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
            }
        });

        back.addActionListener((ActionEvent arg0) -> {
            closeWindow();
            initialize();
        });


        this.frame.setVisible(true);
    }

    private String bouncyCastleBase64(String cipher) {

        byte[] decodeBytes = Base64.getDecoder().decode(cipher);
        return new String(decodeBytes);
    }

    private void checkIfServer() {
        address = ip.getText();
        leaderPortStr = leaderPort.getText();
        localPortStr = localPort.getText();
        userNameStr = userName.getText();
        if (mode.isSelected()) {
            try {
                address = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (localPortStr.equals("")){
                localPortStr = "6666";
            }
            leaderPortStr = localPortStr;
            GuiController.get().setLocalServerPort(leaderPortStr);
            GuiController.get().setLeader(true);
            new MonitorGui(Integer.parseInt(localPortStr)); //start server process as leader
            loginAction(userNameStr, address, leaderPortStr);
        } else {
            int localPortInt = 0;
            if (localPortStr.equals("")){
                localPortInt = (int)(Math.random() * (65535 - 1023) + 1024);
                localPortStr = Integer.toString(localPortInt);
            }else{
                localPortInt = Integer.parseInt(localPortStr);
            }
            GuiController.get().setLocalServerPort(localPortStr);

            GuiController.get().setLeader(false);
            loginAction(userNameStr, address, leaderPortStr);
            new MonitorGui(localPortInt); // start server process
        }
    }

}