package app.Protocols.RaftProtocol;
import app.Protocols.ScrabbleProtocol;

public class RegisterProtocol extends ScrabbleProtocol {
    private String clientName;
    private String hostAddress;
    private String hostPort;

    public RegisterProtocol(String clientName, String hostAddress, String hostPort) {
        super.setTAG("RegisterProtocol");
        this.clientName = clientName;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public String getHostPort() {
        return this.hostPort;
    }
}
