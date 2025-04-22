package com.rodrigoappelt.sistemasdistribuidos.interfaces;

import java.util.ArrayList;

/**
 * Interface compartilhada do servidor de chat
 */
public interface IServerChat extends java.rmi.Remote {
    public ArrayList<String> getRooms();
    public void createRoom(String roomName);
}