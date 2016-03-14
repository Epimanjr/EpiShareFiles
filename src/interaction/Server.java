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
     * @param message Message
     * @throws java.rmi.RemoteException .
     */
    public abstract void sendMessage(Message message) throws RemoteException;

    /**
     * Connect to the server.
     *
     * @param nameClient Name of the client who wants to connect to the server.
     * @throws RemoteException .
     */
    public abstract void connect(String nameClient) throws RemoteException;
    
    public abstract void sendFileToAll(String senderName, String filename) throws RemoteException;
}
