/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.io.File;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    private TextField tfNickname;
    @FXML
    private TextField tfMessage;
    @FXML
    private Button butConnect;

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

    public void initConnect() {
        try {
            // Connect to the server
            receiveMessage(new Message("Try to connect to the server ... ", ServerGraphic.SERVER_NAME));
            Registry registry = Network.getRegistry();
            server = (Server) registry.lookup(ServerGraphic.SERVER_NAME);
            receiveMessage(new Message("Success", ServerGraphic.SERVER_NAME));
            //butConnect.visibleProperty().bindBidirectional(new SimpleObjectProperty<>(connect));
        } catch (RemoteException | NotBoundException ex) {
            try {
                receiveMessage(new Message("Echec", ServerGraphic.SERVER_NAME));
            } catch (RemoteException ex1) {
                Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void actionQuit(ActionEvent event) {
        System.exit(0);
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
            server.sendFileToAll(currentNickname, file);
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

    public void actionConnect(ActionEvent event) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //System.out.println("*** Appel du service de connexion ***");
                        try {
                            //System.out.print("Récupération du registre ... ");
                            Registry registry = Network.getRegistry();
                            //System.out.println("OK");
                            String nickName = tfNickname.getText();
                            //System.out.print("Enregistrement ... ");
                            registry.rebind(nickName, (Client) returnThis());
                            //System.out.println("OK");
                            //System.out.print("Appel du serveur ... ");
                            server.connect(nickName);
                            majGuiAfterConnect(nickName);
                            //System.out.println("OK");
                        } catch (RemoteException ex) {
                            Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        return null;
                    }
                };
            }
        };
        service.start();
        service.setOnSucceeded((WorkerStateEvent event1) -> {
            System.out.println("OK, vous êtes maintenant connecté.");
            connect = !true;
        });
    }

    public void majGuiAfterConnect(String nickname) {
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(1), (ActionEvent actionEvent) -> {
            currentNickname = nickname;
            butConnect.setVisible(false);
            labConnectStatus.setText("Connected as " + nickname);
        }));
        timeline1.setCycleCount(1);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);
        animation.play();

    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        Text textSender = new Text(message.getNameSender() + "-> ");
        textSender.setFont(new Font(14));
        textSender.setFill(Color.BLACK);
        Text textMessage = new Text(message.getContent() + "\n");
        textMessage.setFont(new Font(message.getFont()));
        textMessage.setFill(Color.web(message.getColor()));
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(1), (ActionEvent actionEvent) -> {
            chatBox.getChildren().addAll(textSender, textMessage);
        }));
        timeline1.setCycleCount(1);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);
        animation.play();

    }

    public TextFlow getChatBox() {
        return chatBox;
    }
}
