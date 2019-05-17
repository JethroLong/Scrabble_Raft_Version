package app.Protocols;

public class Pack {
    private int userId;
    private String msg;
    private int[] recipient;
    private String userName;

    public Pack(int userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }

    public Pack(String userName, String msg) {
        this.userName = userName;
        this.msg = msg;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int[] getRecipient() {
        return recipient;
    }

    public void setRecipient(int[] recipient) {
        this.recipient = recipient;
    }

    public int getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return String.format("USERNAME: %s, ID: %d, MSG: %s", userName, userId, msg);
    }
}
