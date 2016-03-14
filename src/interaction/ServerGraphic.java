/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import graphic.LaunchServer;
import interaction.Network;
import interaction.Server;
import interaction.ServerConsole;
import interaction.ServerGraphic;
import java.io.File;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Maxime BLAISE
 */
public class ServerGraphic extends AbstractServer implements Initializable {

    /**
     * Server name in graphic.
     */
    public static final String SERVER_NAME = "ServerGraphic";

    @FXML
    private Label labServerStatus;
    @FXML
    private Button butQuit;
    @FXML
    private TextFlow chatBox;

    public ServerGraphic() throws RemoteException {
        setFolderName("ServerGraphic");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(5000), (ActionEvent actionEvent) -> {
            getListClients().stream().forEach((str) -> {
                try {
                    Client client = (Client) Network.getRegistry().lookup(str);
                    client.setInfosServer();
                } catch (RemoteException | NotBoundException ex) {
                    Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }));
        timeline1.setCycleCount(Timeline.INDEFINITE);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);
        animation.play();
    }

    public void actionQuit(ActionEvent event) {
        System.exit(0);
    }

    public void setTextToLabel(String message) {
        labServerStatus.setText(message);
    }

    @Override
    public void notificationForServer(Message message) throws RemoteException {
        Text textMessage = new Text(message.getNameSender() + "-> " + message.getContent() + "\n");
        textMessage.setFont(new Font(message.getFont()));
        textMessage.setFill(Color.web(message.getColor()));
        if (getChatBox() == null) {
            System.err.println("TextFlow NULL");
        } else {
            Platform.runLater(() -> {
                getChatBox().getChildren().add(textMessage);
            });   
        }
    }

    public TextFlow getChatBox() {
        return chatBox;
    }

    @Override
    public ArrayList<File> askListFiles() {
        return AbstractServer.askListFiles(ServerGraphic.SERVER_NAME);
    }

}
