
package interaction;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Maxime BLAISE
 */
public class Network {
    
    public static String hostname = "";
    public static int port = 3212;
    
    public static Registry getRegistry() throws RemoteException {
        return LocateRegistry.getRegistry(3212);
    }
}
