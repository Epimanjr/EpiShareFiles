package interaction;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private String folderName;

    private String serverHostName;

    public AbstractServer() throws RemoteException {
        try {
            serverHostName = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    /**
     * Remove a new client to the list.
     *
     * @param nameClient Name of the client
     */
    public void removeClient(String nameClient) {
        this.listClients.remove(nameClient);
    }

    @Override
    public ArrayList<String> askListConnectedUsers() throws RemoteException {
        return listClients;
    }

    @Override
    public void sendFileToAll(String senderName, File file) throws RemoteException {
        System.out.println("Send " + file.getName() + " to all clients : ");
        for (String str : this.listClients) {
            if (!str.equals(senderName)) {
                sendFile(senderName, str, file);
            }
        }
        System.out.println("All send complete.");
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        receiveMessage(message);
        Registry registry = LocateRegistry.getRegistry(3212);
        // Clients loop
        for (String str : this.getListClients()) {
            try {
                Client client = (Client) registry.lookup(str);
                client.receiveMessage(message);
            } catch (NotBoundException | AccessException ex) {
                Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void connect(String nameClient, Client client) throws RemoteException {
        Network.getRegistry().rebind(nameClient, client);
        this.addClient(nameClient);
        this.sendMessage(new Message(nameClient + " is now connected.", folderName));
        modifyListView();
    }
    
    abstract void modifyListView() throws RemoteException;

    @Override
    public void disconnect(String nameClient) throws RemoteException {
        this.removeClient(nameClient);
        this.sendMessage(new Message(nameClient + " is now disconnected.", ServerConsole.SERVER_NAME));
        modifyListView();
    }

    public static ArrayList<File> askListFiles(String folderName) {
        ArrayList<File> list = new ArrayList<>();
        File file = new File(folderName);
        list.addAll(Arrays.asList(file.listFiles()));
        return list;
    }

    @Override
    public void askFile(String askerName, File file, String pathToSave) throws RemoteException {
        sendFile(folderName, askerName, file, pathToSave);
    }

    public String getFolderName() {
        return folderName;
    }

    public final void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getServerHostName() {
        return serverHostName;
    }

}
