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
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
    String currentSenderName = "";

    public FileTransfer() throws RemoteException {
    }

    public void notifyStateTransfer(File file, int sendOrReceive, int state) {
        String content = "File Transfer Complete";
        if (state == 0) {
            content = (sendOrReceive == 1) ? "Receiving " + file.getName() + " from " + currentSenderName + " ... " : "Send " + file.getName() + " to " + currentTargetName + " ... ";
        }

        // 0 = send ; 1 = receive
        String str = (sendOrReceive == 0) ? currentSenderName : currentTargetName;

        
        try {
            ExchangeClient exchange = (ExchangeClient) Network.getRegistry().lookup(str);
            sendFileTransferMessage(exchange, content, currentSenderName);
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void beginReceiveFile(String senderName, String targetName, File file, String pathToSave) throws RemoteException, FileNotFoundException {
        output = new FileOutputStream(pathToSave + "/" + file.getName());
        currentTargetName = targetName;
        currentSenderName = senderName;
        notifyStateTransfer(file, 1, 0);
    }

    public void receiveContentFile(byte[] buf, int bytesRead) throws RemoteException, IOException {
        if (output != null) {
            output.write(buf, 0, bytesRead);
        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void receiveContentFile(ArrayList<byte[]> bytes, ArrayList<Integer> byteread) throws RemoteException, IOException {
        if (output != null) {
            for (int i = 0; i < bytes.size(); i++) {
                output.write(bytes.get(i), 0, byteread.get(i));
            }
        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void endReceiveFile() throws RemoteException, IOException {
        if (output != null) {
            output.close();

            notifyStateTransfer(null, 1, 1);
        } else {
            System.err.println("Error: OutputStream is null.");
        }
    }

    public void sendFile(String senderName, String targetName, File file) throws RemoteException {
        sendFile(senderName, targetName, file, targetName);
    }

    public void sendFile(String senderName, String targetName, File file, String pathToSave) throws RemoteException {
        Registry registry = Network.getRegistry();
        currentSenderName = senderName;
        currentTargetName = targetName;
        try {
            // Get target client
            ExchangeClient client = (ExchangeClient) registry.lookup(targetName);
            client.beginReceiveFile(senderName, targetName, file, pathToSave);
            client.receiveMessage(new Message("File size : " + ((int) (file.length() / 1000)) + "Ko", senderName, "#00ff00", 14));

            try {
                notifyStateTransfer(file, 0, 0);
                // Send file
                input = new FileInputStream(file);
                sendFileHelper(client, file);

                input.close();
                client.endReceiveFile();
                notifyStateTransfer(file, 0, 1);
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NotBoundException | AccessException | FileNotFoundException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendFileHelper(ExchangeClient client, File file) throws RemoteException {
        try {
            int bufferSize = 65536;
            byte[] buf = new byte[bufferSize];
            int bytesRead;
            int totalState = ((int) (file.length() / bufferSize))+1;
            int it = 1;
            while ((bytesRead = input.read(buf)) > 0) {
                client.receiveContentFile(buf, bytesRead);
                int percent = (it * 100) / totalState;
                int totalBytesRead = ((it-1) * bufferSize) + bytesRead;
                String content = totalBytesRead / 1000 + "/" + file.length() / 1000 + "KO => " + percent + "%";
                sendFileTransferMessage(client,content , currentSenderName);
                it++;
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendFileTransferMessage(ExchangeClient client, String content, String senderName) {
        Message message = new Message(content, senderName, "#00ff00", 14);
        try {
            client.receiveMessage(message);
        } catch (RemoteException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
