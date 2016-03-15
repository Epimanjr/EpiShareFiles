/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import com.sun.javafx.perf.PerformanceTracker;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    ClientGraphic client = null;
    Scene scene = null;
    Stage actuel = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void actionQuit(ActionEvent event) {
        System.exit(0);
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
        Platform.runLater(() -> {
            try {
                Registry registry = Network.getRegistry();
                Server server = (Server) registry.lookup(ServerGraphic.SERVER_NAME);
                //registry.rebind(nickname, (Client) client);
                server.connect(nickname, (Client)client);
                actuel.close();
                Stage stage = new Stage();
                stage.setScene(scene);
                client.actionConnect(nickname, server);
                stage.setTitle(APPLICATION_NAME);
                stage.show();
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(ClientConnectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
