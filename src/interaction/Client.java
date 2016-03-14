package interaction;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Client extends ExchangeClient {

    /**
     * Receive a message from server.
     *
     * @param message Message
     * @throws RemoteException .
     */
    public abstract void receiveMessage(Message message) throws RemoteException;
}
