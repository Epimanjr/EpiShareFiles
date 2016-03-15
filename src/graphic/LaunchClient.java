
package graphic;

import interaction.ClientGraphic;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Maxime BLAISE
 */
public class LaunchClient extends Application  {
    
    public static final String APPLICATION_NAME = "mShisKus 2.0";
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientConnection.fxml"));
        Scene scene = new Scene((Pane) loader.load());
        
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("Client.fxml"));
        Scene scene2 = new Scene((Pane) loader2.load());

        ClientGraphic controleur2 = loader2.getController();
        /*if (controleur2.getChatBox() == null) {
            System.err.println("Error: chatbox server null at start.");
        } else {
            controleur2.initConnect();
        }*/
        
        ClientConnectionController controleur = loader.getController();
        controleur.setClient(controleur2);
        controleur.setScene(scene2);
        controleur.setStage(primaryStage);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
