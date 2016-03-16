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
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.util.Duration;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class FileTransfer extends UnicastRemoteObject {

    InputStream input = null;
    OutputStream output = null;
    String currentTargetName = "";
    String currentSenderName = "";
    ExchangeClient receiver = null;

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
            sendFileTransferMessage(exchange, content);
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
        currentSenderName = senderName;
        currentTargetName = targetName;

        // File transfer prepare
        Service service = contactReceiver();
        SequentialTransition animation = createAnimation(5000, 5000);

        animation.setOnFinished((ActionEvent event1) -> {
            sendFileTransferMessage((ExchangeClient) this, "TimeOut: unable to contact the receiver.", true);
            service.cancel();
        });
        service.setOnSucceeded((Event event1) -> {
            animation.stop();
            sendFileTransferMessage((ExchangeClient) this, "Connection with " + currentTargetName + " OK.");
            try {
                sendFileHelper(file, pathToSave);
            } catch (RemoteException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        service.setOnRunning((Event event1) -> {
            animation.play();
            sendFileTransferMessage((ExchangeClient) this, "Trying to connect with " + currentTargetName + " ... ");
        });
        service.start();
    }

    public static SequentialTransition createAnimation(int totalMillis, int duration) {
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(duration), (ActionEvent actionEvent) -> {

        }));
        timeline1.setCycleCount(totalMillis / duration);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);
        return animation;
    }

    private Service contactReceiver() {
        return new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Registry registry = Network.getRegistry();
                        receiver = (ExchangeClient) registry.lookup(currentTargetName);
                        return null;
                    }
                };
            }
        };
    }

    private void sendFileHelper(File file, String pathToSave) throws RemoteException {
        try {

            receiver.beginReceiveFile(currentSenderName, currentTargetName, file, pathToSave);
            receiver.receiveMessage(new Message("File size : " + ((int) (file.length() / 1000)) + "Ko", currentSenderName, "#00ff00", 14));

            try {
                notifyStateTransfer(file, 0, 0);
                // Send file
                input = new FileInputStream(file);
                sendContentFileHelper(receiver, file);

                input.close();
                receiver.endReceiveFile();
                notifyStateTransfer(file, 0, 1);
            } catch (IOException ex) {
                Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (AccessException | FileNotFoundException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendContentFileHelper(ExchangeClient client, File file) throws RemoteException {
        try {
            // Init fields
            byte[] buf = new byte[computeBufferSize((int) file.length())];
            int bytesRead, totalBytesRead = 0;
            // Loop to read all the content of the file
            while ((bytesRead = input.read(buf)) > 0) {
                // Bytes transfer
                client.receiveContentFile(buf, bytesRead);
                totalBytesRead += bytesRead;
                // Give infos to sender and receiver
                String content = computeContent(totalBytesRead, (int) file.length());
                sendFileTransferMessage(client, content);
                sendFileTransferMessage((ExchangeClient) this, content);
            }
        } catch (IOException ex) {
            // Give infos to sender and receiver
            String content = "Error: IO Exception.";
            sendFileTransferMessage(client, content, true);
            sendFileTransferMessage((ExchangeClient) this, content, true);
            //Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String computeContent(int totalBytesRead, int fileLength) {
        int percent = (totalBytesRead * 100) / fileLength;
        return totalBytesRead / 1000 + "/" + fileLength / 1000 + "KO => " + percent + "%";
    }

    private int computeBufferSize(int fileLength) {
        if (fileLength > 40000000) {
            return 4194304;
        }
        if (fileLength > 20000000) {
            return 2097152;
        }
        if (fileLength > 10000000) {
            return 1048576;
        }
        if (fileLength > 5000000) {
            return 524288;
        }
        if (fileLength > 2000000) {
            return 262144;
        }
        if (fileLength > 1000000) {
            return 131072;
        }
        return 65536;
    }

    private void sendFileTransferMessage(ExchangeClient client, String content) {
        sendFileTransferMessage(client, content, false);
    }

    private void sendFileTransferMessage(ExchangeClient client, String content, boolean error) {
        String color = (error) ? "#ff0000" : "#00ff00";
        Message message = new Message(content, currentSenderName, color, 14);
        try {
            client.receiveMessage(message);
        } catch (RemoteException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
