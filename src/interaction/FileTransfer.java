package interaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class FileTransfer extends UnicastRemoteObject {

    InputStream input = null;
    OutputStream output = null;
    boolean outputBusy = false;
    String currentTargetName = "";

    public FileTransfer() throws RemoteException {
    }

    public void notifyStateTransfer(File file, int state) {
        try {
            String content = (state == 1) ? "File Transfer Complete" : "Receiving " + file.getName() + " from " + currentTargetName + " ... ";
            Message message = new Message(content, currentTargetName, "#00ff00", 14);
            if (currentTargetName.equals(ServerGraphic.SERVER_NAME) || currentTargetName.equals(ServerConsole.SERVER_NAME)) {
                Server server = (Server) Network.getRegistry().lookup(currentTargetName);
                server.notificationForServer(message);
            } else {
                Client client = (Client) Network.getRegistry().lookup(currentTargetName);
                client.receiveMessage(message);
            }
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void beginReceiveFile(String senderName, String targetName, File file) throws RemoteException, FileNotFoundException {
        output = new FileOutputStream(targetName + "/" + file.getName());
        currentTargetName = targetName;
        notifyStateTransfer(file, 0);
    }

    public void receiveContentFile(byte[] buf, int bytesRead) throws RemoteException, IOException {
        if (output != null) {
            output.write(buf, 0, bytesRead);
        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void endReceiveFile() throws RemoteException, IOException {
        if (output != null) {
            output.close();

            notifyStateTransfer(null, 1);
        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void sendFile(String senderName, String targetName, File file) throws RemoteException {
        Registry registry = Network.getRegistry();
        try {
            // Get target client
            ExchangeClient client = (ExchangeClient) registry.lookup(targetName);
            client.beginReceiveFile(senderName, targetName, file);

            try {
                System.out.println("Send " + file.getName() + " to " + targetName + " ... ");
                // Send file
                input = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    client.receiveContentFile(buf, bytesRead);
                }
                input.close();
                client.endReceiveFile();
                System.out.println("File transfer complete.");
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NotBoundException | AccessException | FileNotFoundException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
