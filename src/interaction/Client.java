package interaction;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Client extends Remote {

    /**
     * Receive a message from server.
     *
     * @param nameSender Name of this message's sender.
     * @param message Message's content.
     * @throws RemoteException .
     */
    public abstract void receiveMessage(String nameSender, String message) throws RemoteException;
}
