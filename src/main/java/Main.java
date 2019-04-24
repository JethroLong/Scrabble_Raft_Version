import app.Peer.Client.ClientCenter.ClientControlCenter;
import org.apache.log4j.PropertyConfigurator;

public class Main {
    public static void main(String[] args){
        PropertyConfigurator.configure(Main.class.getResourceAsStream("log4j.properties"));
        try{
            new Thread(new ClientControlCenter()).start();
        }catch (Exception e){
            System.err.println("Try Again Please, my boy/girl!");
        }
    }
}