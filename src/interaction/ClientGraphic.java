/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Maxime BLAISE
 */
public class ClientGraphic extends AbstractClient implements Initializable {

    @FXML
    private TextFlow chatBox;
    @FXML
    private Label labConnectStatus;
    @FXML
    private TextField tfMessage;

    @FXML
    private ListView listUsers;
    @FXML
    private ListView listFiles;

    Server server = null;
    boolean connect = !false;

    String currentNickname;

    public ClientGraphic() throws RemoteException {
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /*ClientConsole client = new ClientConsole();
            registry.rebind(name, (Client) client);
            server.connect(name);*/
    }

    @FXML
    public void actionQuit(ActionEvent event) {
        try {
            server.disconnect(currentNickname);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.exit(0);
        }
        
    }

    public ClientGraphic returnThis() {
        return this;
    }

    public void actionSendFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the file to send");
        File file = fileChooser.showOpenDialog(null);
        try {
            sendFile(currentNickname, ServerGraphic.SERVER_NAME, file);
            //server.sendFileToAll(currentNickname, file);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actionSendMessage(ActionEvent event) {
        try {
            server.sendMessage(new Message(tfMessage.getText(), currentNickname));
            tfMessage.setText("");
        } catch (RemoteException ex) {
            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actionConnect(String nickname, Server server) {
        this.server = server;
        currentNickname = nickname;
        Platform.runLater(() -> {
            labConnectStatus.setText("Connected as " + nickname);
        });
        try {
            setInfosServer();
        } catch (RemoteException ex) {
            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setInfosServer() throws RemoteException {
        Platform.runLater(() -> {
            try {
                // List users
                ArrayList<String> listTmp = server.askListConnectedUsers();
                listUsers.setItems(FXCollections.observableArrayList(listTmp));
                // List files
                ArrayList<String> nameFiles = new ArrayList<>();
                server.askListFiles().stream().forEach((f) -> {
                    nameFiles.add(f.getName());
                });
                listFiles.setItems(FXCollections.observableArrayList(nameFiles));
            } catch (RemoteException ex) {
                Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        Text textSender = new Text(message.getNameSender() + "-> ");
        textSender.setFont(new Font(14));
        textSender.setFill(Color.BLACK);
        Text textMessage = new Text(message.getContent() + "\n");
        textMessage.setFont(new Font(message.getFont()));
        textMessage.setFill(Color.web(message.getColor()));
        Platform.runLater(() -> {
            chatBox.getChildren().addAll(textSender, textMessage);
        });
    }

    public TextFlow getChatBox() {
        return chatBox;
    }
}
