package com.rodrigoappelt.sistemasdistribuidos.interfaces;

import java.rmi.RemoteException;

/**
 * Interface compartilhada de um
 * usuario presente no chat.
 */
public interface IUserChat extends java.rmi.Remote {
    public void deliverMsg(String senderName, String msg) throws RemoteException;
}