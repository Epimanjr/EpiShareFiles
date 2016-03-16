package interaction;

import java.rmi.RemoteException;

/**
 * Interface for Client.
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Client extends ExchangeClient {

    /**
     * Get the server information (list of users connected and list of files)
     * and update the GUI.
     *
     * @throws RemoteException If there is network problem
     */
    public abstract void setInfosServer() throws RemoteException;

    //public abstract void disconnect() throws RemoteException;
}
