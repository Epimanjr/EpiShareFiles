package interaction;

import java.rmi.RemoteException;

/**
 * Abstract Client. For ClientConsole and ClientGraphic.
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class AbstractClient extends FileTransfer implements Client {

    /**
     * The server in which the client is connected.
     */
    private Server server = null;

    /**
     * The current nickname of the client.
     */
    private String currentNickname;

    /**
     * Empty constructeur.
     *
     * @throws RemoteException If there is network problem
     */
    public AbstractClient() throws RemoteException {
    }

    /**
     * Method called if the client wants to disconnect and close the
     * application.
     *
     * @throws RemoteException If there is network problem
     */
    public void disconnect() throws RemoteException {
        if (server == null) {
            System.err.println("Error: the server might not initialized.");
        } else {
            server.disconnect(currentNickname);
        }
        System.exit(0);
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getCurrentNickname() {
        return currentNickname;
    }

    public void setCurrentNickname(String currentNickname) {
        this.currentNickname = currentNickname;
    }

}
