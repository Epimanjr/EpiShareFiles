package interaction;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class AbstractServer extends FileTransfer implements Server {

    /**
     * List of clients connected to the server.
     */
    private final ArrayList<String> listClients = new ArrayList<>();

    public AbstractServer() throws RemoteException {
    }

    public ArrayList<String> getListClients() {
        return listClients;
    }

    /**
     * Add a new client to the list.
     *
     * @param nameClient Name of the client
     */
    public void addClient(String nameClient) {
        this.listClients.add(nameClient);
    }
}
