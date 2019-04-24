package app.Protocols.NonGamingProtocol;

import app.Protocols.ScrabbleProtocol;

public class NonGamingProtocol extends ScrabbleProtocol {
    // to server: start, login, logout, invite, inviteResponse
    // from server: userUpdate, invite, inviteACK
    private String command;
    private String[] userList;

    public boolean isInviteAccepted() {
        return inviteAccepted;
    }

    public void setInviteAccepted(boolean inviteAccepted) {
        this.inviteAccepted = inviteAccepted;
    }

    private boolean inviteAccepted;
    private int hostID;

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getUserList() {
        return userList;
    }

    public void setUserList(String[] userList) {
        this.userList = userList;
    }

    public NonGamingProtocol(String command, String[] userList) {
        super.setTAG("NonGamingProtocol");
        this.command = command;
        this.userList = userList;
    }

    public NonGamingProtocol() {
        super.setTAG("NonGamingProtocol");
    }
}
