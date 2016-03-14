package interaction;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Server extends ExchangeClient {

    /**
     * Send a message to all clients.
     *
     * @param nameSender Name of this message's sender.
     * @param message Message's content.
     * @throws java.rmi.RemoteException .
     */
    public abstract void sendMessage(String nameSender, String message) throws RemoteException;

    /**
     * Connect to the server.
     *
     * @param nameClient Name of the client who wants to connect to the server.
     * @throws RemoteException .
     */
    public abstract void connect(String nameClient) throws RemoteException;
}
