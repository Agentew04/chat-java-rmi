package com.rodrigoappelt.sistemasdistribuidos.interfaces;

import java.rmi.RemoteException;

/**
 * Interface para uma sala de chat.
 * Compartilhada entre todos os
 * trabalhos.
 */
public interface IRoomChat extends java.rmi.Remote {
    public void sendMsg(String usrName, String msg) throws RemoteException;
    public void joinRoom(String userName, IUserChat user) throws RemoteException;
    public void leaveRoom(String usrName) throws RemoteException;
    public String getRoomName() throws RemoteException;
    public void closeRoom() throws RemoteException;
}
