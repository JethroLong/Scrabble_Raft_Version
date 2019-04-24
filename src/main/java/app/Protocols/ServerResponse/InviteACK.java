package app.Protocols.ServerResponse;

import app.Models.Users;
import app.Protocols.ScrabbleProtocol;

public class InviteACK extends ScrabbleProtocol {
    private String command; //inviteACK, playerUpdate
    private int userID;
    private Users[] teamList;

    public InviteACK() {
        super.setTAG("InviteACK");
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Users[] getTeamList() {
        return teamList;
    }

    public void setTeamList(Users[] teamList) {
        this.teamList = teamList;
    }

    public int getId() {
        return userID;
    }

    public void setId(int userID) {
        this.userID = userID;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    //accept, reject
    private boolean isAccept;

    public InviteACK(int userID, String command, boolean isAccept, Users[] teamList) {
        super.setTAG("InviteACK");
        this.teamList = teamList;
        this.command = command;
        this.userID = userID;
        this.isAccept = isAccept;
    }
}