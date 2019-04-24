package app.Protocols.ServerResponse;

import app.Models.Users;
import app.Protocols.ScrabbleProtocol;

public class NonGamingResponse extends ScrabbleProtocol {

    public NonGamingResponse(Users[] usersList, String command) {
        super.setTAG("NonGamingResponse");
        this.usersList = usersList;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Users[] getUsersList() {
        return usersList;
    }

    public void setUsersList(Users[] usersList) {
        this.usersList = usersList;
    }

    private Users[] usersList;
    private String command;

}
