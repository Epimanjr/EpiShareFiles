
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

    public ServerConsole() throws RemoteException {
    }
    
    @Override
    public void sendMessage(String nameSender, String message) throws RemoteException {
        System.out.println(nameSender + "-> " + message);
        Registry registry = LocateRegistry.getRegistry(3212);
        // Clients loop
        for(String str : this.getListClients()) {
            try {
                Client client = (Client)registry.lookup(str);
                client.receiveMessage(nameSender, message);
            } catch (NotBoundException | AccessException ex) {
                Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void connect(String nameClient) throws RemoteException {
        this.sendMessage("MainServer", nameClient + " is now connected.");
        this.addClient(nameClient);
    }
    
    /**
     * Launch main program for server console.
     *
     * @param args .
     */
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(3212);
            registry.rebind("MainServer", (Server)new ServerConsole());
            System.out.println("Server is now ready.");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
