package com.rodrigoappelt.sistemasdistribuidos.interfaces;

/**
 * Interface para uma sala de chat.
 * Compartilhada entre todos os
 * trabalhos.
 */
public interface IRoomChat extends java.rmi.Remote {
    public void sendMsg(String usrName, String msg);
    public void joinRoom(String userName, IUserChat user);
    public void leaveRoom(String usrName);
    public String getRoomName();
    public void closeRoom();
}
