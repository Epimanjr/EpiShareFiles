package interaction;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface for Server.
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface Server extends ExchangeClient {

    /**
     * Send a message. Generally to all client, but it can be only to one if the
     * message begins with @nickname
     *
     * @param message Instance of Message
     * @throws java.rmi.RemoteException If there is network problem
     */
    public abstract void sendMessage(Message message) throws RemoteException;

    /**
     * Connect to the server.
     *
     * @param nameClient Name of the client who wants to connect to the server.
     * @param client Client Interface (for rebind)
     * @throws RemoteException .
     */
    public abstract void connect(String nameClient, Client client) throws RemoteException;

    /**
     * Disconnect to the server.
     *
     * @param nameClient Name of the client who wants to disconnect to the
     * server.
     * @throws RemoteException If there is network problem
     */
    public abstract void disconnect(String nameClient) throws RemoteException;

    /**
     * Allow to users to get the list of conncted users.
     *
     * @return List of connected Users
     * @throws RemoteException If there is network problem
     */
    public abstract ArrayList<String> askListConnectedUsers() throws RemoteException;

    /**
     * Allow to users to get the list files in server directory.
     *
     * @return List of files
     * @throws RemoteException If there is network problem
     */
    public ArrayList<File> askListFiles() throws RemoteException;

    /**
     * Allow user to download a file.
     *
     * @param askerName Name of the asker.
     * @param file Target file the client wants
     * @param pathToSave Folder name in which will be saved this file
     * @throws RemoteException If there is network problem
     */
    public abstract void askFile(String askerName, File file, String pathToSave) throws RemoteException;

}
