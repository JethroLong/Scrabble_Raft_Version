package app.Protocols;

public class Pack {
    private int userId;
    private String msg;

    public int[] getRecipient() {
        return recipient;
    }

    public void setRecipient(int[] recipient) {
        this.recipient = recipient;
    }

    private int[] recipient;

    public int getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }

    public Pack(int userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }
}
