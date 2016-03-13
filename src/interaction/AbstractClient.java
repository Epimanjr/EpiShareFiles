/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class AbstractClient extends UnicastRemoteObject implements Client {

    
    public AbstractClient() throws RemoteException {
    }

    
}
