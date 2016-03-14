package interaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public class ClientConsole extends AbstractClient {

    public ClientConsole() throws RemoteException {
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        System.out.println(message.getNameSender() + "-> " + message.getContent());
    }

    /**
     * Launch main program for client console.
     *
     * @param args .
     */
    public static void main(String[] args) {
        try {
            // Connect to the server
            Registry registry = Network.getRegistry();
            Server server = (Server) registry.lookup(ServerConsole.SERVER_NAME);
            Scanner sc = new Scanner(System.in);
            String name = askName(sc);
            ClientConsole client = new ClientConsole();
            registry.rebind(name, (Client) client);
            server.connect(name);
            // 
            client.sendFile(name, ServerConsole.SERVER_NAME, new File("test.pdf"));
            server.sendFileToAll(name, new File("test.pdf"));
            /*while (true) {
                String message = sc.nextLine();
                if (message.equals("exit")) {
                    sc.close();
                    System.exit(0);
                }
                server.sendMessage(name, message);
            }*/
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(ClientConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String askName(Scanner sc) {
        System.out.print("Your name : ");
        return sc.nextLine();
    }

    @Override
    public void setInfosServer() throws RemoteException {
    }

}
