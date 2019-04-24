package app.Models;

public class Users {
    private int userID;
    private String userName;

    public Users() {
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumWin() {
        return numWin;
    }

    public void setNumWin(int numWin) {
        this.numWin = numWin;
    }

    public Users(int userID, String userName){
        this.userID = userID;
        this.userName= userName;
        this.status = "available";
        this.numWin = 0;
    }

    //ready, in-game, available
    private String status;
    private int numWin;
}