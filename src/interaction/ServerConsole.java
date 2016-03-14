package interaction;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public class ServerConsole extends AbstractServer {

    /**
     * Server name in console.
     */
    public static final String SERVER_NAME = "ServerConsole";

    public ServerConsole() throws RemoteException {
    }

    /**
     * Launch main program for server console.
     *
     * @param args .
     */
    public static void main(String[] args) {
        try {
            Registry registry = Network.createRegistry();
            registry.rebind(ServerConsole.SERVER_NAME, (Server) new ServerConsole());
            System.out.println("Server is now ready.");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void notificationForServer(Message message) {
        System.out.println(message.getNameSender() + "-> " + message.getContent());
    }
}
