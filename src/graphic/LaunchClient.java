
package graphic;

import interaction.ClientGraphic;
import interaction.Message;
import interaction.Network;
import interaction.Server;
import interaction.ServerGraphic;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Maxime BLAISE
 */
public class LaunchClient extends Application  {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        Scene scene = new Scene((Pane) loader.load());

        ClientGraphic controleur = loader.getController();
        if (controleur.getChatBox() == null) {
            System.err.println("Error: chatbox server null at start.");
        } else {
            controleur.initConnect();
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
