
package interaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Maxime
 */
public interface ExchangeClient extends Remote {
    
    public abstract void beginReceiveFile(String senderName, String targetName, File file, String pathToSave) throws RemoteException, FileNotFoundException;

    public abstract void receiveContentFile(byte[] buf, int bytesRead) throws RemoteException, IOException;

    public abstract void endReceiveFile() throws RemoteException, IOException;
}
