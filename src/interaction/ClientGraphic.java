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
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
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
    private Label labClientsConnected;
    @FXML
    private TextField tfMessage;
    @FXML
    private Button butDownload;
    @FXML
    private ScrollPane chatBoxContainer;

    @FXML
    private ListView listUsers;
    @FXML
    private ListView listFiles;

    Server server = null;
    boolean connect = !false;

    String currentNickname = "";

    ArrayList<File> arraylistFiles = null;

    public ClientGraphic() throws RemoteException {
    }

    /**
     * Initializes the controller class.
     *
     * @param url .
     * @param rb .
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#0000ff"));
        //Adding the shadow when the mouse cursor is on
        butDownload.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            butDownload.setEffect(shadow);
        });
        //Removing the shadow when the mouse cursor is off
        butDownload.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            butDownload.setEffect(null);
        });
        Image imageDecline = new Image(getClass().getResourceAsStream("dl.png"), 20, 20, true, true);
        butDownload.setPadding(new Insets(0));
        butDownload.setGraphic(new ImageView(imageDecline));

        listFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // For auto-scroll
        chatBox.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    chatBox.layout();
                    chatBoxContainer.layout();
                    chatBoxContainer.setVvalue(1.0f);
                }));

        // For private message
        listUsers.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    if (!tfMessage.getText().startsWith("@")) {
                        tfMessage.setText("@" + listUsers.getSelectionModel().getSelectedItem().toString() + " " + tfMessage.getText());
                    }
                }
            }
        });

    }

    public void actionDownload(ActionEvent event) {
        //butDownload.set
        Platform.runLater(() -> {
            butDownload.setDisable(true);
            butDownload.setText("Waiting ... ");
        });

        // Choose folder
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(null);
        String pathToSave = (selectedDirectory != null) ? selectedDirectory.getAbsolutePath() : "";

        ArrayList<Integer> indices = new ArrayList<>(listFiles.getSelectionModel().getSelectedIndices());
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<Void>() {
                    @Override
                    public Void call() {
                        indices.stream().forEach((Integer i) -> {
                            try {
                                server.askFile(currentNickname, arraylistFiles.get(i), pathToSave);
                            } catch (RemoteException ex) {
                                Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                        return null;
                    }
                };
            }
        };
        service.start();

        Platform.runLater(() -> {
            butDownload.setText("Download");
            butDownload.setDisable(false);
        });

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
                labClientsConnected.setText(listTmp.size() + " client(s) connected.");
                // List files
                ArrayList<String> nameFiles = new ArrayList<>();
                arraylistFiles = server.askListFiles();
                arraylistFiles.stream().forEach((f) -> {
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
