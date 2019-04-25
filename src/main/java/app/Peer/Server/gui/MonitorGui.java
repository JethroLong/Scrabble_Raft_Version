package app.Peer.Server.gui;

import app.Peer.Server.controllers.controlcenter.ControlCenter;
import com.alibaba.fastjson.JSON;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;

public class MonitorGui {
    private JFrame frame;
    private int portNum;

    public MonitorGui() {
        this.portNum = 6666;
        new Thread(new ControlCenter()).start();
        initialize();
    }

    public MonitorGui(int port) {
        this.portNum = port;
        new Thread(new ControlCenter(port)).start();
        initialize();
    }
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Server Monitor");
        JLabel status = new JLabel("Server is running....");
        status.setBounds(50, 20, 130, 16);
        frame.getContentPane().add(status);

        JLabel ipAddr = null;
        try {
            ipAddr = new JLabel("IP: "+ InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ipAddr.setBounds(50, 50, 130, 16);
        JLabel port = new JLabel("Port: "+portNum);
        port.setBounds(50, 65, 130, 16);

        frame.getContentPane().add(ipAddr);
        frame.getContentPane().add(port);

        JButton shutdown = new JButton("shutdown");
        shutdown.setBounds(50, 92, 95, 30);
        frame.getContentPane().add(shutdown);

        JButton invite = new JButton("invite");
        invite.setBounds(150, 92, 95, 30);
        frame.getContentPane().add(invite);

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
                    shutdown();
                }
            }
        };
        shutdown.addKeyListener(keyListener);
        invite.addKeyListener(keyListener);
        shutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    shutdown();
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });

        invite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    setClipboardText(Toolkit.getDefaultToolkit().getSystemClipboard(), JSON.toJSONString(new String[]{
                            InetAddress.getLocalHost().getHostAddress(), String.valueOf(portNum)}, true));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
        this.frame.setVisible(true);
    }

    public void shutdown(){
        System.exit(0);
    }


    private void setClipboardText(Clipboard clip, String writeMe) {
        Transferable tText = new StringSelection(bouncyCastleBase64(writeMe));
        clip.setContents(tText, null);
    }

    private String bouncyCastleBase64 (String message) {
        byte[] encodeBytes = Base64.getEncoder().encode(message.getBytes());
        return new String (encodeBytes);
    }
}
