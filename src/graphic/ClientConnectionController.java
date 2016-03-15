/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import static graphic.LaunchClient.APPLICATION_NAME;
import interaction.Client;
import interaction.ClientGraphic;
import interaction.Network;
import interaction.Server;
import interaction.ServerGraphic;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Maxime
 */
public class ClientConnectionController implements Initializable {

    @FXML
    private TextField tfHostname;
    @FXML
    private TextField tfNickname;
    @FXML
    private TextField tfPort;

    @FXML
    private Label labResultConnection;

    @FXML
    private ProgressBar connectionProgress;

    ClientGraphic client = null;
    Scene scene = null;
    Stage actuel = null;

    Float progressValue = 0.2f;

    Server server = null;

    /**
     * Initializes the controller class.
     *
     * @param url .
     * @param rb .
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void actionConnect(ActionEvent event) {
        // Get values
        String hostname = tfHostname.getText();
        String nickname = tfNickname.getText();
        // Connection
        Network.hostname = hostname;
        try {
            Network.port = new Integer(tfPort.getText());
        } catch (Exception e) {
            Network.port = 3212;
        }

        contactServer(nickname);

        /*Platform.runLater(() -> {
        try {
        Registry registry = Network.getRegistry();
        Server server = (Server) registry.lookup(ServerGraphic.SERVER_NAME);
        //registry.rebind(nickname, (Client) client);
        server.connect(nickname, (Client) client);
        actuel.close();
        Stage stage = new Stage();
        stage.setScene(scene);
        client.actionConnect(nickname, server);
        stage.setTitle(APPLICATION_NAME);
        stage.show();
        } catch (RemoteException | NotBoundException ex) {
        Logger.getLogger(ClientConnectionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        });*/
    }

    private void launchClientGUI(String nickname) {
        Stage stage = new Stage();
        stage.setScene(scene);
        client.actionConnect(nickname, server);
        stage.setTitle(APPLICATION_NAME);

        actuel.close();
        // Lors de la fermeture
        stage.setOnCloseRequest((WindowEvent event) -> {
            try {
                server.disconnect(nickname);
            } catch (RemoteException ex) {
                Logger.getLogger(ClientGraphic.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                System.exit(0);
            }
        });
        stage.show();
    }

    private void connectServer(String nickname) {
        try {
            labResultConnection.setText("Trying to connect ...");
            server.connect(nickname, (Client) client);
            launchClientGUI(nickname);
        } catch (RemoteException ex) {
            labResultConnection.setText("Don't able to connect to server.");
        }
    }

    private void contactServer(String nickname) {
        Timeline timeline1 = new Timeline();
        progressValue = 0.02f;
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), (ActionEvent actionEvent) -> {
            connectionProgress.setProgress(progressValue);
            progressValue += 0.02f;
        }));
        timeline1.setCycleCount(50);
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(timeline1);

        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Registry registry = Network.getRegistry();
                        server = (Server) registry.lookup(ServerGraphic.SERVER_NAME);
                        return null;
                    }
                };
            }
        };

        animation.setOnFinished((ActionEvent event1) -> {
            labResultConnection.setText("TimeOut -> Don't able to connect");
            service.cancel();
        });
        service.setOnSucceeded((Event event1) -> {
            animation.stop();
            connectionProgress.setProgress(1f);
            labResultConnection.setText("Connected ! ");
            connectServer(nickname);
        });
        service.setOnRunning((Event event1) -> {
            labResultConnection.setText("Trying to contact server at " + Network.hostname + " ... ");
        });

        animation.play();
        service.start();
    }

    public void setClient(ClientGraphic client) {
        this.client = client;
    }

    void setScene(Scene scene2) {
        this.scene = scene2;
    }

    void setStage(Stage stage) {
        this.actuel = stage;
    }

}
