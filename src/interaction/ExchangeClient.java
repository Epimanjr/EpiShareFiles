package interaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for Client and Server.
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public interface ExchangeClient extends Remote {

    /**
     * Begin of receiving file. Initialize OutputStream in the client folder.
     *
     * @param senderName The name of the user who send the file
     * @param targetName The name of the user who receive the file
     * @param file The file sent
     * @param pathToSave Folder name in which will be saved this file
     * @throws java.rmi.RemoteException If there is network problem
     * @throws java.io.FileNotFoundException If the target folder doesn't exist
     */
    public abstract void beginReceiveFile(String senderName, String targetName, File file, String pathToSave) throws RemoteException, FileNotFoundException;

    /**
     * Receiving the content of the file. Depends of the buffer size. This
     * method is called when the sender buffer is full.
     *
     * @param buf Buffer (array of bytes)
     * @param bytesRead number of bytes read
     * @throws java.rmi.RemoteException If there is network problem
     * @throws java.io.IOException If outputstream not initialized
     */
    public abstract void receiveContentFile(byte[] buf, int bytesRead) throws RemoteException, IOException;

    /**
     * End of receiving file. Close the OutputSteam.
     *
     * @throws java.rmi.RemoteException If there is network problem
     * @throws java.io.IOException If outputstream not initialized
     */
    public abstract void endReceiveFile() throws RemoteException, IOException;

    /**
     * Receive a message. This message will be shown in the chatbox.
     *
     * @param message Instance of message, with content and some information.
     * @throws RemoteException If there is network problem
     */
    public abstract void receiveMessage(Message message) throws RemoteException;

}
