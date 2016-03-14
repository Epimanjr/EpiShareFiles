/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import interaction.Network;
import interaction.Server;
import interaction.ServerConsole;
import interaction.ServerGraphic;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Maxime BLAISE
 */
public class ServerController implements Initializable {

    @FXML
    private Label labServerStatus;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String messageForLabServerStatus = "";
        try {
            Registry registry = Network.createRegistry();
            registry.rebind(LaunchServer.SERVER_NAME, (Server)new ServerGraphic());
            
            messageForLabServerStatus = "Server is now ready.";
        } catch (RemoteException ex) {
            messageForLabServerStatus = "Error: the server is not initialized correctly.";
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            labServerStatus.setText(messageForLabServerStatus);
        }
    }    
    
}
