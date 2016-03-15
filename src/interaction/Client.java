package interaction;

import java.rmi.RemoteException;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Client extends ExchangeClient {
    
    public abstract void setInfosServer() throws RemoteException;
}
