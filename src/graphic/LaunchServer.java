package graphic;

import interaction.Network;
import interaction.Server;
import interaction.ServerGraphic;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Maxime BLAISE
 */
public class LaunchServer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("Server.fxml"));
        Scene scene = new Scene((Pane) loader.load());

        ServerGraphic controleur = loader.getController();
        if (controleur.getChatBox() == null) {
            System.err.println("Error: chatbox server null at start.");
        } else {
            String messageForLabServerStatus = "";
            try {
                Registry registry = Network.createRegistry();
                registry.rebind(ServerGraphic.SERVER_NAME, (Server) controleur);

                messageForLabServerStatus = "Server is now ready.";
            } catch (RemoteException ex) {
                messageForLabServerStatus = "Error: the server is not initialized correctly.";
                Logger.getLogger(ServerGraphic.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                controleur.setTextToLabel(messageForLabServerStatus);
            }
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle(LaunchClient.APPLICATION_NAME + " Server");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
