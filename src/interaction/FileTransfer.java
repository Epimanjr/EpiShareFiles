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

    public FileTransfer() throws RemoteException {
    }

    public void beginReceiveFile(String senderName, String targetName, String filename) throws RemoteException, FileNotFoundException {
        output = new FileOutputStream(new File(targetName + "/" + filename));
        System.out.println("Receiving " + filename + " from " + senderName + " ... ");
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
            System.out.println("File transfer complete.");

        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void sendFile(String senderName, String targetName, String filename) throws RemoteException {
        Registry registry = Network.getRegistry();
        try {
            // Get target client
            ExchangeClient client = (ExchangeClient) registry.lookup(targetName);
            client.beginReceiveFile(senderName, targetName, filename);

            try {
                System.out.println("Send " + filename + " to " + targetName + " ... ");
                // Send file
                input = new FileInputStream(new File(senderName + "/" + filename));
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
