import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ipTest {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(7777);
            System.out.println("Server Host: "+ InetAddress.getLocalHost().getHostAddress());
            Socket client;
            while(true){
                client = server.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
