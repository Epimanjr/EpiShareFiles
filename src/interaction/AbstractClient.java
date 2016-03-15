/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.rmi.RemoteException;

/**
 *
 * @author Maxime BLAISE
 * @version 1.0
 */
public abstract class AbstractClient extends FileTransfer implements Client {

    public AbstractClient() throws RemoteException {
    }

    @Override
    public abstract void disconnect() throws RemoteException;

}
