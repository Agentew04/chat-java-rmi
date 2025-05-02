package com.rodrigoappelt.sistemasdistribuidos.interfaces;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface compartilhada do servidor de chat
 */
public interface IServerChat extends java.rmi.Remote {
    public ArrayList<String> getRooms();
    public void createRoom(String roomName) throws MalformedURLException, RemoteException;
}