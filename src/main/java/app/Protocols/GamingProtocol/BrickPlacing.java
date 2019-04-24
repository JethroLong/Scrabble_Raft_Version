package app.Protocols.GamingProtocol;

import app.Protocols.ScrabbleProtocol;

public class BrickPlacing extends ScrabbleProtocol {
    private int userID;

    private String brick;
    private int[] position = new int[2];

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getBrick() {
        return brick;
    }

    public void setBrick(String character) {
        this.brick = character;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }
}
