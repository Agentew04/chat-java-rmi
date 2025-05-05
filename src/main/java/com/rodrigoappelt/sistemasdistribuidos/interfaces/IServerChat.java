package com.rodrigoappelt.sistemasdistribuidos.interfaces;

import java.net.MalformedURLException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface compartilhada do servidor de chat
 */
public interface IServerChat extends Remote {
    ArrayList<String> getRooms() throws RemoteException;
    void createRoom(String roomName) throws RemoteException;
}