package interaction;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
        for(String str: this.listClients) {
            if(!str.equals(senderName)) {
                sendFile(senderName, str, file);
            }
        }
        System.out.println("All send complete.");
    }
    
    @Override
    public void sendMessage(Message message) throws RemoteException {
        notificationForServer(message);
        Registry registry = LocateRegistry.getRegistry(3212);
        // Clients loop
        for(String str : this.getListClients()) {
            try {
                Client client = (Client)registry.lookup(str);
                client.receiveMessage(message);
            } catch (NotBoundException | AccessException ex) {
                Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
   

    @Override
    public void connect(String nameClient) throws RemoteException {   
        this.addClient(nameClient);
        this.sendMessage(new Message(nameClient + " is now connected.", ServerConsole.SERVER_NAME));
    }
    
    @Override
    public void disconnect(String nameClient) throws RemoteException {   
        this.removeClient(nameClient);
        this.sendMessage(new Message(nameClient + " is now disconnected.", ServerConsole.SERVER_NAME));
    }
    
    public static ArrayList<File> askListFiles(String folderName) {
        ArrayList<File> list = new ArrayList<>();
        File file = new File(folderName);
        list.addAll(Arrays.asList(file.listFiles()));
        return list;
    }

    public String getFolderName() {
        return folderName;
    }

    public final void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    
}
