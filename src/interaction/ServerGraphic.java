/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.io.File;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
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
    private TextFlow chatBox;
    @FXML
    private ScrollPane chatBoxContainer;
    @FXML
    private ListView listUsersConnected;

    ExchangeClient currentUser;

    public ServerGraphic() throws RemoteException {
        setFolderName("ServerGraphic");
    }

    /**
     * Initializes the controller class.
     *
     * @param url .
     * @param rb .
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // For auto-scroll
        chatBox.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    chatBox.layout();
                    chatBoxContainer.layout();
                    chatBoxContainer.setVvalue(1.0f);
                }));

        try {
            // Show ip
            receiveMessage(new Message("Adresse IP = " + getServerHostName(), SERVER_NAME));
        } catch (RemoteException ex) {
            Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Bind
        // new SimpleListProperty(FXCollections.observableArrayList(getListClients())).bind(listUsersConnected.itemsProperty());
        //listUsersConnected.itemsProperty().bind(new SimpleListProperty(FXCollections.observableArrayList(getListClients())));
        // Send infos to clients
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(5000), (ActionEvent actionEvent) -> {
            Platform.runLater(() -> {
                getListClients().stream().forEach((str) -> {
                    try {
                        Client client = (Client) Network.getRegistry().lookup(str);
                        client.setInfosServer();
                    } catch (RemoteException | NotBoundException ex) {
                        Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });

        }));
        timeline1.setCycleCount(Timeline.INDEFINITE);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);
        animation.play();
    }

    public void setTextToLabel(String message) {
        labServerStatus.setText(message);
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
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

    @Override
    void modifyListView() throws RemoteException {
        Platform.runLater(() -> {
            listUsersConnected.setItems(FXCollections.observableArrayList(getListClients()));
        });
    }

    /*@FXML
    public void quickUser(ActionEvent event) {
        int indice = listUsersConnected.getSelectionModel().getSelectedIndex();
        SequentialTransition animation = FileTransfer.createAnimation(5000, 5000);
        String strUser = getListClients().get(indice);
        Service service = contactUser(strUser);
        animation.setOnFinished((ActionEvent event1) -> {
            receiveQuickMessage("TimeOut: unable to contact " + strUser + ".");
            service.cancel();
        });
        service.setOnSucceeded((Event event1) -> {
            animation.stop();
            receiveQuickMessage( "Connection with " + strUser + " OK.");
            try {
                // Quick !
                //currentUser.quick();
                removeClient(strUser);
                modifyListView();
            } catch (RemoteException ex) {
                Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        service.setOnRunning((Event event1) -> {
            animation.play();
            receiveQuickMessage("Trying to connect with " + strUser + " ... ");
        });
        service.start();
    }

    private void receiveQuickMessage(String content) {
        try {
            receiveMessage(new Message(content, getFolderName()));
        } catch (RemoteException ex) {
            Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Service contactUser(String user) {
        return new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Registry registry = Network.getRegistry();
                        currentUser = (ExchangeClient) registry.lookup(user);
                        return null;
                    }
                };
            }
        };
    }*/
}
