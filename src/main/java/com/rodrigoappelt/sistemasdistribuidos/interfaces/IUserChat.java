package com.rodrigoappelt.sistemasdistribuidos.interfaces;

/**
 * Interface compartilhada de um
 * usuario presente no chat.
 */
public interface IUserChat extends java.rmi.Remote {
    public void deliverMsg(String senderName, String msg);
}