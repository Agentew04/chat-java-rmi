package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IUserChat;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserChat extends UnicastRemoteObject implements IUserChat, Serializable {
    protected UserChat() throws RemoteException {
    }

    @Override
    public void deliverMsg(String senderName, String msg) {
        System.out.println("Message from " + senderName + ": " + msg);

    }
}
