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
import javafx.application.Platform;

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

        Message message = new Message(content, currentSenderName, "#00ff00", 14);
        try {
            ExchangeClient exchange = (ExchangeClient) Network.getRegistry().lookup(str);
            exchange.receiveMessage(message);
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
                output.write(bytes.get(i), 0, byteread.get(i).intValue());
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

                byte[] buf = new byte[1024];
                int bytesRead;
                ArrayList<byte[]> listBytes = new ArrayList<>();
                ArrayList<Integer> listByteRead = new ArrayList<>();
                int iterator = 1;

                int numberOfState = ((int) (file.length() / 1024));
                while ((bytesRead = input.read(buf)) > 0) {
                    //client.receiveContentFile(buf, bytesRead);
                    listBytes.add(buf);
                    listByteRead.add(bytesRead);
                    if (listBytes.size() > (numberOfState / 10)) {
                        int percent = (iterator * 100) / numberOfState;
                        int begin = ((iterator * 1024) / 1000);
                        int end = ((int) (file.length() / 1000));
                        if (begin <= end) {
                            String messContent = begin + "Ko/" + end + "Ko => " + percent + "% / 100%";
                            client.receiveMessage(new Message(messContent, senderName, "#00ff00", 14));
                        }
                        client.receiveContentFile(listBytes, listByteRead);
                        listBytes = new ArrayList<>();
                        listByteRead = new ArrayList<>();
                    }
                    iterator++;
                }
                if (!listBytes.isEmpty()) {
                    client.receiveContentFile(listBytes, listByteRead);
                }
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
}
