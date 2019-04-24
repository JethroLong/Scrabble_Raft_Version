package app.Peer.Client.gui;

import app.Peer.Client.ClientCenter.ClientControlCenter;
import app.Peer.Server.gui.MonitorGui;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Base64;

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
    private JTextField port;
    private JCheckBox mode;
    private JTextArea inviteURL;
    private String address;

    public String getAddress() {
        return address;
    }

    public String getPortStr() {
        return portStr;
    }

    public String getUserNameStr() {
        return userNameStr;
    }

    private String portStr;
    private String userNameStr;


    public LoginWindow() {
    }

//    public static class LoginWindowHolder {
//        private static final LoginWindow INSTANCE = new LoginWindow();
//    }
    private static LoginWindow loginWindow;



    public static final LoginWindow get() {
        if(loginWindow==null){
            return loginWindow=new LoginWindow();
        }else {
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

    public void reInitial(){
        initialize();
        JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
        this.frame.setVisible(true);
    }
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 350, 235);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(47, 23, 66, 16);
        frame.getContentPane().add(lblUsername);

        JLabel lblNewLabel = new JLabel("IP address:");
        lblNewLabel.setBounds(45, 62, 68, 16);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Port:");
        lblNewLabel_1.setBounds(84, 100, 29, 16);
        frame.getContentPane().add(lblNewLabel_1);

        userName = new JTextField();
        userName.setBounds(125, 18, 160, 26);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        ip = new JTextField();
        ip.setBounds(125, 57, 160, 26);
        frame.getContentPane().add(ip);
        ip.setColumns(10);

        port = new JTextField();
        port.setBounds(125, 95, 160, 26);
        frame.getContentPane().add(port);
        port.setColumns(10);

        mode = new JCheckBox("   Login as leader");
        frame.getContentPane().add(mode);
        mode.setBounds(90, 125, 200, 30);

        JButton login = new JButton("Login");
        login.setBounds(90, 155, 80, 30);
        frame.getContentPane().add(login);

        JButton other = new JButton("Other Login");
        other.setBounds(190, 155, 105, 30);
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
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    checkIfServer(); // if start server
                }catch (Exception e) {
                    System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });

        other.addKeyListener(keyListener);
        other.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    changeView();
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
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

    public void loginAction(String userName, String ipAddr, String portNum) {
        if(!userName.isEmpty()) {
            center.openNet(ipAddr, Integer.parseInt(portNum), userName);
            //clientManager.openSocket(address, portStr, userNameStr);
            GuiController.get().setUserName(userName);
        }else{
            showDialog("Invalid username, please try again!");
            run();
        }
    }

    public void changeView(){
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
        inviteURL.setBounds(125,50,160,80);
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
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    //decryption
                    JSONArray inviteURLText = JSON.parseArray(bouncyCastleBase64(inviteURL.getText()));
                    loginAction(userName.getText(),inviteURLText.getString(0),inviteURLText.getString(1));
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                closeWindow();
                initialize();
            }
        });


        this.frame.setVisible(true);
    }

    private String bouncyCastleBase64 (String cipher) {

        byte[] decodeBytes = Base64.getDecoder().decode(cipher);
        return new String(decodeBytes);
    }

    private void checkIfServer(){
        address = ip.getText();
        portStr = port.getText();
        userNameStr = userName.getText();
        if (mode.isSelected()){
            if (address.equals("")) {
                address = "localhost";
            }
            new MonitorGui(Integer.parseInt(portStr));
            System.out.println("login action start");
//            loginAction(userNameStr, address, portStr);
        }else {
            loginAction(userNameStr, address, portStr);
        }
    }

}